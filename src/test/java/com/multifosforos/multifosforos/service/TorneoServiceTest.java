package com.multifosforos.multifosforos.service;

import com.multifosforos.multifosforos.dto.TorneoDTO;
import com.multifosforos.multifosforos.model.EstadoPartida;
import com.multifosforos.multifosforos.model.EstadoTorneo;
import com.multifosforos.multifosforos.model.Equipo;
import com.multifosforos.multifosforos.model.Inscripto;
import com.multifosforos.multifosforos.model.ModoPartida;
import com.multifosforos.multifosforos.model.Partida;
import com.multifosforos.multifosforos.model.PartidaTorneo;
import com.multifosforos.multifosforos.model.ResultadoInicio;
import com.multifosforos.multifosforos.model.ResultadoInscripcion;
import com.multifosforos.multifosforos.model.ResultadoVoto;
import com.multifosforos.multifosforos.model.Ronda;
import com.multifosforos.multifosforos.model.Torneo;
import com.multifosforos.multifosforos.model.Votacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

/** Casos de prueba del torneo (specs/test-cases.md, TC-30 en adelante) sobre TorneoService. */
class TorneoServiceTest {

    /** Guarda las tareas programadas para correrlas a mano. */
    static class ProgramadorFalso implements Programador {
        record Tarea(Runnable tarea, long demoraMs) { }
        final List<Tarea> tareas = new ArrayList<>();

        @Override
        public void programar(Runnable tarea, long demoraMs) {
            tareas.add(new Tarea(tarea, demoraMs));
        }

        /** Corre (una vez) las tareas pendientes con esa demora. */
        void correr(long demoraMs) {
            List<Tarea> a = tareas.stream().filter(t -> t.demoraMs() == demoraMs).toList();
            tareas.removeAll(a);
            a.forEach(t -> t.tarea().run());
        }
    }

    /** Random que siempre devuelve el mismo valor (acotado): sorteos predecibles. */
    static class RandomFijo extends Random {
        private final int valor;
        RandomFijo(int valor) { this.valor = valor; }
        @Override public int nextInt(int bound) { return Math.min(valor, bound - 1); }
        @Override public boolean nextBoolean() { return false; }
    }

    private final MinimaxService minimax = new MinimaxService();
    private ProgramadorFalso programador;
    private TorneoService service;

    @BeforeEach
    void setUp() {
        usarRandom(new Random(42));
    }

    private void usarRandom(Random random) {
        programador = new ProgramadorFalso();
        service = new TorneoService(minimax, programador, random, t -> { });
    }

    // ---------- ayudas ----------

    private Torneo torneoCon(int inscriptos) {
        Torneo t = service.crearTorneo();
        for (int i = 1; i <= inscriptos; i++) {
            assertEquals(ResultadoInscripcion.ACEPTADA, service.inscribir(t.getId(), "j" + i, "Alumno" + i));
        }
        return t;
    }

    private Torneo iniciadoCon(int inscriptos) {
        Torneo t = torneoCon(inscriptos);
        assertEquals(ResultadoInicio.INICIADO, service.iniciar(t.getId(), t.getClaveAdmin()));
        return t;
    }

    /** Todos los integrantes del equipo en turno votan q; la votación se cierra y se aplica. */
    private void jugarTurno(Torneo t, PartidaTorneo pt, int q) {
        Equipo e = pt.getEquipoEnTurno();
        for (Inscripto i : e.getIntegrantes()) {
            assertEquals(ResultadoVoto.ACEPTADO, service.votar(t.getId(), i.getId(), q));
        }
        programador.correr(Votacion.MUESTRA_MS);
    }

    /** Juega la partida hasta el final; la estrategia elige cuánto saca el equipo en turno. */
    private void jugarHastaTerminar(Torneo t, PartidaTorneo pt, BiFunction<Equipo, Partida, Integer> estrategia) {
        while (pt.getPartida().getEstado() == EstadoPartida.EN_JUEGO) {
            Equipo e = pt.getEquipoEnTurno();
            jugarTurno(t, pt, estrategia.apply(e, pt.getPartida()));
        }
    }

    private int perfecta(Equipo e, Partida p) {
        return minimax.mejorJugada(p.getFosforosRestantes(), p.getMaxQuitar());
    }

    /** Contra la máquina, el equipo pierde a propósito; entre equipos, juegan perfecto. */
    private int pierdeContraLaMaquina(Equipo e, Partida p) {
        if (p.getModo() != ModoPartida.CONTRA_MAQUINA) return perfecta(e, p);
        int r = p.getFosforosRestantes();
        for (int q = 1; q <= Math.min(p.getMaxQuitar(), r); q++) {
            if (r - q == 0 || minimax.esPosicionGanadora(r - q, p.getMaxQuitar())) return q;
        }
        return 1;
    }

    /** Juega todas las partidas de la ronda actual. */
    private void jugarRonda(Torneo t, BiFunction<Equipo, Partida, Integer> estrategia) {
        Ronda r = t.getRondaActual();
        for (PartidaTorneo pt : r.getPartidas()) {
            jugarHastaTerminar(t, pt, estrategia);
        }
    }

    /** 4 inscriptos, se juega la ronda 1 y queda armada la final entre dos equipos de 2. */
    private Torneo finalDeCuatro() {
        Torneo t = iniciadoCon(4);
        jugarRonda(t, this::perfecta);
        assertEquals(2, t.getRondas().size());
        return t;
    }

    // ---------- UC-06: crear torneo ----------

    @Test
    void tc30_crearTorneoQuedaEnInscripcion() {
        Torneo t = service.crearTorneo();
        assertTrue(t.getId().matches("\\d{4}"), "código de 4 números: " + t.getId());
        assertNotNull(t.getClaveAdmin());
        assertEquals(EstadoTorneo.INSCRIPCION, t.getEstado());
        assertTrue(t.getInscriptos().isEmpty());
    }

    @Test
    void tc31_cadaTorneoTieneIdUnico() {
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 100; i++) ids.add(service.crearTorneo().getId());
        assertEquals(100, ids.size());
    }

    // ---------- UC-07: inscribirse ----------

    @Test
    void tc32_inscribirseEnInscripcion() {
        Torneo t = service.crearTorneo();
        assertEquals(ResultadoInscripcion.ACEPTADA, service.inscribir(t.getId(), "j1", "Ana"));
        assertEquals("Ana", t.getInscripto("j1").getNombre());
    }

    @Test
    void tc33_noSePuedeInscribirConElTorneoIniciado() {
        Torneo t = iniciadoCon(2);
        assertEquals(ResultadoInscripcion.TORNEO_INICIADO, service.inscribir(t.getId(), "j9", "Beto"));
        assertEquals(2, t.getInscriptos().size());
    }

    @Test
    void tc34_inscribirseEnTorneoInexistente() {
        assertEquals(ResultadoInscripcion.TORNEO_INEXISTENTE, service.inscribir("no-existe", "j1", "Ana"));
    }

    @Test
    void tc35_nombreRepetidoSeRechaza() {
        Torneo t = service.crearTorneo();
        service.inscribir(t.getId(), "j1", "Ana");
        assertEquals(ResultadoInscripcion.NOMBRE_REPETIDO, service.inscribir(t.getId(), "j2", " ana "));
        assertEquals(1, t.getInscriptos().size());
    }

    @Test
    void tc73_elMismoAlumnoNoSeInscribeDosVeces() {
        Torneo t = service.crearTorneo();
        service.inscribir(t.getId(), "j1", "Ana");
        assertEquals(ResultadoInscripcion.YA_INSCRIPTO, service.inscribir(t.getId(), "j1", "Otra"));
        assertEquals(1, t.getInscriptos().size());
    }

    // ---------- UC-08: iniciar ----------

    @Test
    void tc36_alIniciarCadaInscriptoEsUnEquipoDeUno() {
        Torneo t = iniciadoCon(4);
        assertEquals(EstadoTorneo.EN_CURSO, t.getEstado());
        assertEquals(4, t.getParticipantes().size());
        for (Inscripto i : t.getInscriptos()) {
            Equipo e = t.getEquipoDe(i.getId());
            assertEquals(1, e.getIntegrantes().size());
            assertSame(i, e.getCapitan());
        }
    }

    @Test
    void tc37_alIniciarSeArmaLaPrimeraRonda() {
        Torneo t = iniciadoCon(4);
        assertEquals(1, t.getRondas().size());
        assertEquals(2, t.getRondaActual().getPartidas().size());
        for (PartidaTorneo pt : t.getRondaActual().getPartidas()) {
            assertEquals(EstadoPartida.EN_JUEGO, pt.getPartida().getEstado());
        }
    }

    @Test
    void tc38_hacenFaltaDosInscriptosParaIniciar() {
        Torneo cero = torneoCon(0);
        assertEquals(ResultadoInicio.POCOS_INSCRIPTOS, service.iniciar(cero.getId(), cero.getClaveAdmin()));
        Torneo uno = torneoCon(1);
        assertEquals(ResultadoInicio.POCOS_INSCRIPTOS, service.iniciar(uno.getId(), uno.getClaveAdmin()));
        assertEquals(EstadoTorneo.INSCRIPCION, uno.getEstado());
        Torneo dos = torneoCon(2);
        assertEquals(ResultadoInicio.INICIADO, service.iniciar(dos.getId(), dos.getClaveAdmin()));
    }

    @Test
    void tc39_noSePuedeIniciarDosVeces() {
        Torneo t = iniciadoCon(4);
        Ronda ronda = t.getRondaActual();
        assertEquals(ResultadoInicio.YA_INICIADO, service.iniciar(t.getId(), t.getClaveAdmin()));
        assertSame(ronda, t.getRondaActual());
    }

    @Test
    void tc71_soloConLaClaveSeIniciaElTorneo() {
        Torneo t = torneoCon(3);
        assertEquals(ResultadoInicio.CLAVE_INCORRECTA, service.iniciar(t.getId(), "otra"));
        assertEquals(ResultadoInicio.CLAVE_INCORRECTA, service.iniciar(t.getId(), null));
        assertEquals(EstadoTorneo.INSCRIPCION, t.getEstado());
    }

    // ---------- UC-09: armar ronda ----------

    @Test
    void tc40_seEmparejaDeADos() {
        Torneo t = iniciadoCon(6);
        List<PartidaTorneo> partidas = t.getRondaActual().getPartidas();
        assertEquals(3, partidas.size());
        Set<Equipo> vistos = new HashSet<>();
        for (PartidaTorneo pt : partidas) {
            assertTrue(vistos.add(pt.getEquipoA()));
            assertTrue(vistos.add(pt.getEquipoB()));
        }
        assertEquals(new HashSet<>(t.getParticipantes()), vistos);
    }

    @Test
    void tc41_conNumeroImparElLibreJuegaContraLaMaquina() {
        Torneo t = iniciadoCon(5);
        List<PartidaTorneo> partidas = t.getRondaActual().getPartidas();
        assertEquals(3, partidas.size());
        long contraMaquina = partidas.stream().filter(p -> p.getEquipoB().isMaquina()).count();
        assertEquals(1, contraMaquina);
        assertEquals(2, partidas.stream().filter(p -> p.getPartida().getModo() == ModoPartida.HUMANOS).count());
    }

    @Test
    void tc42_todasLasPartidasDeLaRondaTienenLosMismosValores() {
        for (int seed = 0; seed < 20; seed++) {
            usarRandom(new Random(seed));
            Torneo t = iniciadoCon(6);
            Ronda r = t.getRondaActual();
            assertTrue(r.getFosforos() >= 10 && r.getFosforos() <= 17);
            assertTrue(r.getMaxQuitar() >= 2 && r.getMaxQuitar() <= 5);
            for (PartidaTorneo pt : r.getPartidas()) {
                assertEquals(r.getFosforos(), pt.getPartida().getFosforosIniciales());
                assertEquals(r.getMaxQuitar(), pt.getPartida().getMaxQuitar());
            }
        }
    }

    @Test
    void tc43_laCombinacionCambiaRespectoDeLaRondaAnterior() {
        usarRandom(new RandomFijo(0));        // siempre sortearía 10 fósforos y máximo 2
        int[] v = service.sortearValores(10, 2);
        assertFalse(v[0] == 10 && v[1] == 2);
        usarRandom(new RandomFijo(9));        // siempre sortearía 17 y 5
        v = service.sortearValores(17, 5);
        assertArrayEquals(new int[] { 17, 2 }, v);
        usarRandom(new Random(7));
        for (int i = 0; i < 1000; i++) {
            v = service.sortearValores(13, 3);
            assertFalse(v[0] == 13 && v[1] == 3);
            assertTrue(v[0] >= 10 && v[0] <= 17 && v[1] >= 2 && v[1] <= 5);
        }
    }

    @Test
    void tc44_empiezaElEquipoMasChico() {
        Equipo chico = Equipo.de(new Inscripto("a", "A"));
        Equipo grande = Equipo.de(new Inscripto("b", "B"));
        grande.absorber(Equipo.de(new Inscripto("c", "C")));
        grande.absorber(Equipo.de(new Inscripto("d", "D")));
        assertSame(chico, service.quienEmpieza(chico, grande));
        assertSame(chico, service.quienEmpieza(grande, chico));
    }

    @Test
    void tc45_conMismoTamanoSeSortea() {
        Equipo a = Equipo.de(new Inscripto("a", "A"));
        Equipo b = Equipo.de(new Inscripto("b", "B"));
        Set<Equipo> empezaron = new HashSet<>();
        for (int i = 0; i < 100; i++) empezaron.add(service.quienEmpieza(a, b));
        assertEquals(2, empezaron.size());
    }

    @Test
    void tc46_contraLaMaquinaEmpiezaElEquipo() {
        Torneo t = iniciadoCon(5);
        PartidaTorneo libre = t.getRondaActual().getPartidas().stream()
                .filter(p -> p.getEquipoB().isMaquina()).findFirst().orElseThrow();
        assertFalse(libre.getEquipoA().isMaquina());
        assertSame(libre.getEquipoA(), libre.getEquipoEnTurno());
        Equipo m = t.getMaquina();
        Equipo e = Equipo.de(new Inscripto("x", "X"));
        assertSame(e, service.quienEmpieza(m, e));
    }

    @Test
    void tc47_conDosEquiposLaRondaEsLaFinal() {
        Torneo t = iniciadoCon(2);
        assertTrue(t.getRondaActual().isFinal());
        Torneo t4 = iniciadoCon(4);
        assertFalse(t4.getRondaActual().isFinal());
    }

    // ---------- UC-10: votar (tabla «¿Se acepta un voto?») ----------

    @Test
    void tc48_v1_sinPartidaEnJuego() {
        Torneo t = iniciadoCon(4);
        PartidaTorneo p1 = t.getRondaActual().getPartidas().get(0);
        jugarHastaTerminar(t, p1, this::perfecta);
        String delPerdedor = p1.getPerdedor().getCapitan().getId();   // ahora está en el equipo ganador, que espera
        assertEquals(ResultadoVoto.SIN_PARTIDA_EN_JUEGO, service.votar(t.getId(), delPerdedor, 1));
        assertEquals(ResultadoVoto.SIN_PARTIDA_EN_JUEGO, service.votar(t.getId(), "intruso", 1));
    }

    @Test
    void tc49_v2_noEsElTurnoDeSuEquipo() {
        Torneo t = iniciadoCon(2);
        PartidaTorneo pt = t.getRondaActual().getPartidas().get(0);
        String rival = pt.getEquipoB().getCapitan().getId();
        assertEquals(ResultadoVoto.NO_ES_TURNO_DE_SU_EQUIPO, service.votar(t.getId(), rival, 1));
    }

    @Test
    void tc50_v3_votacionCerrada() {
        Torneo t = iniciadoCon(2);
        PartidaTorneo pt = t.getRondaActual().getPartidas().get(0);
        String id = pt.getEquipoA().getCapitan().getId();
        assertEquals(ResultadoVoto.ACEPTADO, service.votar(t.getId(), id, 1));   // equipo de uno: se cierra
        assertEquals(ResultadoVoto.VOTACION_CERRADA, service.votar(t.getId(), id, 2));
    }

    @Test
    void tc51_cambiarElVotoReemplazaAlAnterior() {
        Torneo t = finalDeCuatro();
        PartidaTorneo fin = t.getRondaActual().getPartidas().get(0);
        Equipo e = fin.getEquipoEnTurno();
        String id = e.getIntegrantes().get(0).getId();
        service.votar(t.getId(), id, 1);
        assertEquals(ResultadoVoto.ACEPTADO, service.votar(t.getId(), id, 2));
        assertEquals(1, fin.getVotacion().getVotos().size());
        assertEquals(2, fin.getVotacion().getVotos().get(id));
        assertFalse(fin.getVotacion().isCerrada());
    }

    @Test
    void tc52_v4_cantidadFueraDeRango() {
        usarRandom(new RandomFijo(0));           // 10 fósforos, máximo 2
        Torneo t = iniciadoCon(2);
        PartidaTorneo pt = t.getRondaActual().getPartidas().get(0);
        String id = pt.getEquipoA().getCapitan().getId();
        assertEquals(ResultadoVoto.CANTIDAD_FUERA_DE_RANGO, service.votar(t.getId(), id, 0));
        assertEquals(ResultadoVoto.CANTIDAD_FUERA_DE_RANGO, service.votar(t.getId(), id, 3));
    }

    @Test
    void tc53_v5_masQueLosRestantes() {
        usarRandom(new RandomFijo(0));           // 10 fósforos, máximo 2
        Torneo t = iniciadoCon(2);
        PartidaTorneo pt = t.getRondaActual().getPartidas().get(0);
        while (pt.getPartida().getFosforosRestantes() > 2) jugarTurno(t, pt, 2);   // 10 → 2
        jugarTurno(t, pt, 1);                                                      // 2 → 1
        String id = pt.getEquipoEnTurno().getCapitan().getId();
        assertEquals(ResultadoVoto.MAS_QUE_LOS_RESTANTES, service.votar(t.getId(), id, 2));
        assertEquals(1, pt.getPartida().getFosforosRestantes());
    }

    @Test
    void tc54_v6_votoValidoSeRegistraSinDescontar() {
        Torneo t = finalDeCuatro();
        PartidaTorneo fin = t.getRondaActual().getPartidas().get(0);
        int antes = fin.getPartida().getFosforosRestantes();
        String id = fin.getEquipoEnTurno().getIntegrantes().get(0).getId();
        assertEquals(ResultadoVoto.ACEPTADO, service.votar(t.getId(), id, 1));
        assertEquals(1, fin.getVotacion().getVotos().get(id));
        assertEquals(antes, fin.getPartida().getFosforosRestantes());
    }

    // ---------- UC-11: resolver votación ----------

    @Test
    void tc60_laVotacionDura30SegundosOMenosSiVotanTodos() {
        Torneo t = finalDeCuatro();
        PartidaTorneo fin = t.getRondaActual().getPartidas().get(0);
        assertTrue(programador.tareas.stream().anyMatch(x -> x.demoraMs() == 30_000));
        // solo vota uno de los dos: se cierra recién al vencer el tiempo
        String id = fin.getEquipoEnTurno().getIntegrantes().get(0).getId();
        service.votar(t.getId(), id, 1);
        assertFalse(fin.getVotacion().isCerrada());
        programador.correr(30_000);
        assertTrue(fin.getVotacion().isCerrada());
        assertEquals(1, fin.getVotacion().getJugadaElegida());
        // en el turno siguiente votan los dos: se cierra sin esperar
        programador.correr(Votacion.MUESTRA_MS);
        Equipo rival = fin.getEquipoEnTurno();
        for (Inscripto i : rival.getIntegrantes()) service.votar(t.getId(), i.getId(), 2);
        assertTrue(fin.getVotacion().isCerrada());
    }

    @Test
    void tc59_r1_siNadieVotaAlVencerElTiempoSeSaca1() {
        Torneo t = iniciadoCon(2);
        PartidaTorneo pt = t.getRondaActual().getPartidas().get(0);
        int antes = pt.getPartida().getFosforosRestantes();
        programador.correr(30_000);
        programador.correr(Votacion.MUESTRA_MS);
        assertEquals(antes - 1, pt.getPartida().getFosforosRestantes());
    }

    @Test
    void tc61_laJugadaElegidaSeMuestraAntesDeAplicarse() {
        Torneo t = iniciadoCon(2);
        PartidaTorneo pt = t.getRondaActual().getPartidas().get(0);
        int antes = pt.getPartida().getFosforosRestantes();
        Votacion v = pt.getVotacion();
        service.votar(t.getId(), pt.getEquipoA().getCapitan().getId(), 2);
        assertTrue(v.isCerrada());
        assertEquals(2, v.getJugadaElegida());
        assertEquals(antes, pt.getPartida().getFosforosRestantes());
        assertTrue(programador.tareas.stream().anyMatch(x -> x.demoraMs() == 2_000));
        programador.correr(Votacion.MUESTRA_MS);
        assertEquals(antes - 2, pt.getPartida().getFosforosRestantes());
    }

    // ---------- UC-12: cerrar partida del torneo ----------

    @Test
    void tc62_elPerdedorSeSumaAlEquipoGanador() {
        Torneo t = iniciadoCon(4);
        PartidaTorneo p1 = t.getRondaActual().getPartidas().get(0);
        jugarHastaTerminar(t, p1, this::perfecta);
        Equipo ganador = p1.getGanador();
        Equipo perdedor = p1.getPerdedor();
        assertEquals(2, ganador.getIntegrantes().size());
        assertTrue(ganador.tieneIntegrante(perdedor.getCapitan().getId()));
        assertFalse(t.getParticipantes().contains(perdedor));
        assertSame(ganador, t.getEquipoDe(perdedor.getCapitan().getId()));
    }

    @Test
    void tc63_siGanaContraLaMaquinaElEquipoSigueIgual() {
        usarRandom(new RandomFijo(1));        // 11 fósforos, máximo 3: el que empieza gana jugando bien
        Torneo t = iniciadoCon(3);
        PartidaTorneo libre = t.getRondaActual().getPartidas().stream()
                .filter(p -> p.getEquipoB().isMaquina()).findFirst().orElseThrow();
        Equipo equipo = libre.getEquipoA();
        jugarRonda(t, this::perfecta);
        assertSame(equipo, libre.getGanador());
        assertEquals(1, equipo.getIntegrantes().size());
        assertTrue(t.getParticipantes().contains(equipo));
        assertFalse(t.getParticipantes().contains(t.getMaquina()));
        assertTrue(t.getEliminados().isEmpty());
    }

    @Test
    void tc64_siPierdeContraLaMaquinaLaMaquinaSigueEnSuLugar() {
        Torneo t = iniciadoCon(3);
        PartidaTorneo libre = t.getRondaActual().getPartidas().stream()
                .filter(p -> p.getEquipoB().isMaquina()).findFirst().orElseThrow();
        Equipo equipo = libre.getEquipoA();
        jugarRonda(t, this::pierdeContraLaMaquina);
        assertTrue(libre.getGanador().isMaquina());
        assertFalse(t.getParticipantes().contains(equipo));
        assertTrue(t.getEliminados().contains(equipo.getCapitan()));
        // ronda 2: el ganador de la otra partida contra la máquina, que participa como un equipo más
        Ronda r2 = t.getRondaActual();
        assertEquals(2, r2.getNumero());
        assertTrue(r2.isFinal());
        assertTrue(t.getParticipantes().contains(t.getMaquina()));
        assertTrue(r2.getPartidas().get(0).incluye(t.getMaquina()));
    }

    @Test
    void tc65_laRondaEsperaQueTerminenTodasSusPartidas() {
        Torneo t = iniciadoCon(4);
        PartidaTorneo p1 = t.getRondaActual().getPartidas().get(0);
        PartidaTorneo p2 = t.getRondaActual().getPartidas().get(1);
        jugarHastaTerminar(t, p1, this::perfecta);
        assertEquals(1, t.getRondas().size());
        assertEquals(EstadoPartida.EN_JUEGO, p2.getPartida().getEstado());
    }

    @Test
    void tc66_alTerminarLaRondaSeArmaLaSiguiente() {
        Torneo t = finalDeCuatro();
        Ronda r2 = t.getRondaActual();
        assertEquals(2, r2.getNumero());
        assertTrue(r2.isFinal());
        assertEquals(1, r2.getPartidas().size());
        assertEquals(2, r2.getPartidas().get(0).getEquipoA().getIntegrantes().size());
        assertEquals(2, r2.getPartidas().get(0).getEquipoB().getIntegrantes().size());
    }

    // ---------- UC-13: campeón ----------

    @Test
    void tc67_elCampeonSeMuestraConSusIntegrantesAntesDeAbsorber() {
        Torneo t = finalDeCuatro();
        PartidaTorneo fin = t.getRondaActual().getPartidas().get(0);
        jugarHastaTerminar(t, fin, this::perfecta);
        assertEquals(EstadoTorneo.TERMINADO, t.getEstado());
        Equipo campeon = t.getCampeon();
        assertEquals(fin.getGanador().getId(), campeon.getId());
        assertEquals(2, campeon.getIntegrantes().size());
    }

    @Test
    void tc68_recorridoCompletoConOchoInscriptos() {
        Torneo t = iniciadoCon(8);
        List<Integer> partidasPorRonda = new ArrayList<>();
        while (t.getEstado() == EstadoTorneo.EN_CURSO) {
            partidasPorRonda.add(t.getRondaActual().getPartidas().size());
            jugarRonda(t, this::perfecta);
        }
        assertEquals(List.of(4, 2, 1), partidasPorRonda);
        assertEquals(4, t.getCampeon().getIntegrantes().size());
        for (Inscripto i : t.getInscriptos()) {
            assertEquals(3, i.getPartidasJugadas());
        }
    }

    @Test
    void tc72_siQuedaUnSoloParticipanteEsElCampeon() {
        Torneo t = iniciadoCon(5);
        jugarRonda(t, this::pierdeContraLaMaquina);   // ronda 1: el libre pierde contra la máquina
        assertEquals(3, t.getParticipantes().size()); // dos equipos y la máquina
        jugarRonda(t, this::pierdeContraLaMaquina);   // ronda 2: la máquina le gana a los dos equipos que enfrenta
        assertEquals(EstadoTorneo.TERMINADO, t.getEstado());
        assertTrue(t.getCampeon().isMaquina());
    }

    // ---------- UC-14: proyector ----------

    @Test
    void tc69_laVistaMuestraTodasLasPartidasDeLaRonda() {
        Torneo t = iniciadoCon(6);
        TorneoDTO dto = TorneoDTO.desde(t, minimax);
        assertEquals(3, dto.ronda().partidas().size());
        for (TorneoDTO.PartidaTorneoDTO p : dto.ronda().partidas()) {
            assertNotNull(p.equipoA());
            assertNotNull(p.equipoB());
            assertEquals(dto.ronda().fosforos(), p.fosforosRestantes());
            assertEquals("EN_JUEGO", p.estado());
        }
    }

    @Test
    void tc70_indicaQueEquipoEstaEnPosicionGanadora() {
        usarRandom(new RandomFijo(3));       // 13 fósforos, máximo 5: 13 ≡ 1 (mod 6), pierde el que tiene el turno
        Torneo t = iniciadoCon(2);
        TorneoDTO.PartidaTorneoDTO p = TorneoDTO.desde(t, minimax).ronda().partidas().get(0);
        assertEquals(13, p.fosforosRestantes());
        assertEquals(p.equipoB().id(), p.posicionGanadoraEquipoId());

        usarRandom(new RandomFijo(1));       // 11 fósforos, máximo 3: 11 ≡ 3 (mod 4), gana el que tiene el turno
        t = iniciadoCon(2);
        p = TorneoDTO.desde(t, minimax).ronda().partidas().get(0);
        assertEquals(11, p.fosforosRestantes());
        assertEquals(p.equipoA().id(), p.posicionGanadoraEquipoId());
        assertEquals(p.turnoEquipoId(), p.equipoA().id());
    }
}
