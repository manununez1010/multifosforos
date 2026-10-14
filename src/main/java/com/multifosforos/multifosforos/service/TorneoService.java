package com.multifosforos.multifosforos.service;

import com.multifosforos.multifosforos.model.EstadoPartida;
import com.multifosforos.multifosforos.model.EstadoTorneo;
import com.multifosforos.multifosforos.model.Equipo;
import com.multifosforos.multifosforos.model.Inscripto;
import com.multifosforos.multifosforos.model.Jugador;
import com.multifosforos.multifosforos.model.ModoPartida;
import com.multifosforos.multifosforos.model.Partida;
import com.multifosforos.multifosforos.model.PartidaTorneo;
import com.multifosforos.multifosforos.model.ResultadoInicio;
import com.multifosforos.multifosforos.model.ResultadoInscripcion;
import com.multifosforos.multifosforos.model.ResultadoVoto;
import com.multifosforos.multifosforos.model.Ronda;
import com.multifosforos.multifosforos.model.Torneo;
import com.multifosforos.multifosforos.model.Votacion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Torneo por absorción (specs: HU-08 a HU-16, RF-14 a RF-35, UC-06 a UC-14).
 * Cada torneo se modifica bajo su propio lock; los cierres de votación y las
 * jugadas elegidas se aplican con el Programador.
 */
@Service
public class TorneoService {

    private final Map<String, Torneo> torneos = new ConcurrentHashMap<>();
    private final MinimaxService minimaxService;
    private final Programador programador;
    private final Random random;
    private final NotificadorTorneo notificador;

    public TorneoService(MinimaxService minimaxService, Programador programador, Random random,
                         NotificadorTorneo notificador) {
        this.minimaxService = minimaxService;
        this.programador = programador;
        this.random = random;
        this.notificador = notificador;
    }

    // ---------- UC-06: crear ----------

    /** RF-14: torneo nuevo en INSCRIPCION. Quien lo crea recibe la clave para administrarlo. */
    public Torneo crearTorneo() {
        Torneo torneo = new Torneo();
        torneos.put(torneo.getId(), torneo);
        return torneo;
    }

    // ---------- UC-07: inscribirse ----------

    /** RF-15, RF-16. */
    public ResultadoInscripcion inscribir(String torneoId, String jugadorId, String nombre) {
        Torneo torneo = torneos.get(torneoId);
        if (torneo == null) {
            return ResultadoInscripcion.TORNEO_INEXISTENTE;
        }
        synchronized (torneo) {
            if (torneo.getEstado() != EstadoTorneo.INSCRIPCION) return ResultadoInscripcion.TORNEO_INICIADO;
            if (torneo.getInscripto(jugadorId) != null) return ResultadoInscripcion.YA_INSCRIPTO;
            if (torneo.hayInscriptoConNombre(nombre)) return ResultadoInscripcion.NOMBRE_REPETIDO;
            torneo.agregarInscripto(new Inscripto(jugadorId, nombre.trim()));
            notificador.estadoCambiado(torneo);
            return ResultadoInscripcion.ACEPTADA;
        }
    }

    // ---------- UC-08: iniciar ----------

    /** RF-16, RF-17, RF-18. */
    public ResultadoInicio iniciar(String torneoId, String claveAdmin) {
        Torneo torneo = torneos.get(torneoId);
        if (torneo == null) {
            return ResultadoInicio.TORNEO_INEXISTENTE;
        }
        synchronized (torneo) {
            if (!torneo.getClaveAdmin().equals(claveAdmin)) return ResultadoInicio.CLAVE_INCORRECTA;
            if (torneo.getEstado() != EstadoTorneo.INSCRIPCION) return ResultadoInicio.YA_INICIADO;
            if (torneo.getInscriptos().size() < Torneo.MIN_INSCRIPTOS) return ResultadoInicio.POCOS_INSCRIPTOS;
            torneo.setEstado(EstadoTorneo.EN_CURSO);
            for (Inscripto i : torneo.getInscriptos()) {
                torneo.agregarParticipante(Equipo.de(i));
            }
            armarRonda(torneo);
            notificador.estadoCambiado(torneo);
            return ResultadoInicio.INICIADO;
        }
    }

    // ---------- UC-09: armar ronda ----------

    /** RF-19 a RF-22, RF-30. Se llama con el lock del torneo tomado. */
    void armarRonda(Torneo torneo) {
        Ronda anterior = torneo.getRondaActual();
        int[] valores = sortearValores(anterior != null ? anterior.getFosforos() : 0,
                anterior != null ? anterior.getMaxQuitar() : 0);

        List<Equipo> participantes = new ArrayList<>(torneo.getParticipantes());
        Collections.shuffle(participantes, random);
        Ronda ronda = new Ronda(torneo.getRondas().size() + 1, valores[0], valores[1], participantes.size() == 2);

        Equipo libre = null;
        if (participantes.size() % 2 == 1) {
            List<Equipo> alumnos = participantes.stream().filter(e -> !e.isMaquina()).toList();
            libre = alumnos.get(random.nextInt(alumnos.size()));
            participantes.remove(libre);
        }
        for (int i = 0; i + 1 < participantes.size(); i += 2) {
            crearPartida(ronda, participantes.get(i), participantes.get(i + 1));
        }
        if (libre != null) {
            crearPartida(ronda, libre, torneo.getMaquina());
        }
        torneo.agregarRonda(ronda);
        for (PartidaTorneo pt : ronda.getPartidas()) {
            abrirVotacion(torneo, pt);
        }
    }

    /**
     * RF-21: fósforos entre 10 y 17 y máximo entre 2 y 5. Si sale la misma combinación
     * que en la ronda anterior, se toma el máximo siguiente (vuelve a 2 después de 5).
     */
    int[] sortearValores(int fosforosAnterior, int maxAnterior) {
        int fosforos = Torneo.MIN_FOSFOROS + random.nextInt(Torneo.MAX_FOSFOROS - Torneo.MIN_FOSFOROS + 1);
        int max = Torneo.MIN_QUITAR + random.nextInt(Torneo.MAX_QUITAR - Torneo.MIN_QUITAR + 1);
        if (fosforos == fosforosAnterior && max == maxAnterior) {
            max = max == Torneo.MAX_QUITAR ? Torneo.MIN_QUITAR : max + 1;
        }
        return new int[] { fosforos, max };
    }

    /** RF-22: contra la máquina empieza el equipo; si no, el que tiene menos integrantes; si empatan, sorteo. */
    Equipo quienEmpieza(Equipo a, Equipo b) {
        if (a.isMaquina()) return b;
        if (b.isMaquina()) return a;
        int na = a.getIntegrantes().size();
        int nb = b.getIntegrantes().size();
        if (na != nb) return na < nb ? a : b;
        return random.nextBoolean() ? a : b;
    }

    private void crearPartida(Ronda ronda, Equipo x, Equipo y) {
        Equipo primero = quienEmpieza(x, y);
        Equipo segundo = primero == x ? y : x;
        ModoPartida modo = segundo.isMaquina() ? ModoPartida.CONTRA_MAQUINA : ModoPartida.HUMANOS;
        Partida partida = new Partida(ronda.getFosforos(), ronda.getMaxQuitar(), modo);
        partida.agregarJugador(primero.comoJugador());
        partida.agregarJugador(segundo.comoJugador());
        for (Equipo e : List.of(primero, segundo)) {
            e.getIntegrantes().forEach(Inscripto::sumarPartida);
        }
        ronda.agregarPartida(new PartidaTorneo(partida, primero, segundo));
    }

    // ---------- UC-10: votar ----------

    /** RF-23. El orden de los chequeos es el de la tabla «¿Se acepta un voto?». */
    public ResultadoVoto votar(String torneoId, String jugadorId, int cantidad) {
        Torneo torneo = torneos.get(torneoId);
        if (torneo == null) {
            return ResultadoVoto.SIN_PARTIDA_EN_JUEGO;
        }
        synchronized (torneo) {
            Equipo equipo = torneo.getEquipoDe(jugadorId);
            PartidaTorneo pt = torneo.getEstado() == EstadoTorneo.EN_CURSO && equipo != null
                    ? torneo.getRondaActual().getPartidaEnJuegoDe(equipo) : null;
            if (pt == null) return ResultadoVoto.SIN_PARTIDA_EN_JUEGO;                       // V1
            if (pt.getEquipoEnTurno() != equipo) return ResultadoVoto.NO_ES_TURNO_DE_SU_EQUIPO; // V2
            Votacion votacion = pt.getVotacion();
            if (votacion == null || votacion.isCerrada()) return ResultadoVoto.VOTACION_CERRADA; // V3
            Partida partida = pt.getPartida();
            if (cantidad < 1 || cantidad > partida.getMaxQuitar()) return ResultadoVoto.CANTIDAD_FUERA_DE_RANGO; // V4
            if (cantidad > partida.getFosforosRestantes()) return ResultadoVoto.MAS_QUE_LOS_RESTANTES;          // V5

            votacion.votar(jugadorId, cantidad);                                                 // V6
            if (votacion.getVotos().size() == equipo.getIntegrantes().size()) {
                cerrarVotacion(torneo, pt, votacion);   // RF-24: votaron todos
            } else {
                notificador.estadoCambiado(torneo);
            }
            return ResultadoVoto.ACEPTADO;
        }
    }

    // ---------- UC-11: resolver votación ----------

    private void abrirVotacion(Torneo torneo, PartidaTorneo pt) {
        Equipo enTurno = pt.getEquipoEnTurno();
        if (enTurno == null || enTurno.isMaquina()) {
            return;
        }
        Votacion votacion = new Votacion(enTurno.getId(), System.currentTimeMillis() + Votacion.DURACION_MS);
        pt.setVotacion(votacion);
        programador.programar(() -> cerrarVotacion(torneo, pt, votacion), Votacion.DURACION_MS);
    }

    /** RF-24 a RF-27: elige la jugada, la muestra y la aplica después de MUESTRA_MS. */
    void cerrarVotacion(Torneo torneo, PartidaTorneo pt, Votacion votacion) {
        synchronized (torneo) {
            if (pt.getVotacion() != votacion || votacion.isCerrada()) {
                return;   // ya se cerró (votaron todos antes del tiempo)
            }
            Equipo equipo = pt.getEquipo(votacion.getEquipoId());
            votacion.cerrar(Votacion.resolver(votacion.getVotos(), equipo.getCapitan().getId()));
            notificador.estadoCambiado(torneo);
            programador.programar(() -> aplicarJugada(torneo, pt, votacion), Votacion.MUESTRA_MS);
        }
    }

    /** Aplica la jugada elegida con las reglas del juego base (UC-03); si el rival es la máquina, responde con MIN-MAX (UC-05). */
    void aplicarJugada(Torneo torneo, PartidaTorneo pt, Votacion votacion) {
        synchronized (torneo) {
            if (pt.getVotacion() != votacion || !votacion.isCerrada() || votacion.isAplicada()) {
                return;
            }
            votacion.marcarAplicada();
            Partida partida = pt.getPartida();
            partida.jugar(votacion.getEquipoId(), votacion.getJugadaElegida());
            Jugador enTurno = partida.getJugadorEnTurno();
            if (enTurno != null && enTurno.isMaquina()) {
                partida.jugar(enTurno.getId(),
                        minimaxService.mejorJugada(partida.getFosforosRestantes(), partida.getMaxQuitar()));
            }
            if (partida.getEstado() == EstadoPartida.TERMINADA) {
                pt.setVotacion(null);
                cerrarPartida(torneo, pt);
            } else {
                abrirVotacion(torneo, pt);
            }
            notificador.estadoCambiado(torneo);
        }
    }

    // ---------- UC-12 y UC-13: cerrar partida, cerrar ronda, campeón ----------

    private void cerrarPartida(Torneo torneo, PartidaTorneo pt) {
        Ronda ronda = torneo.getRondaActual();
        Equipo ganador = pt.getGanador();
        Equipo perdedor = pt.getPerdedor();

        if (ronda.isFinal()) {
            torneo.setCampeon(ganador.copia());                  // RF-31: antes de absorber
            torneo.setEstado(EstadoTorneo.TERMINADO);
            return;
        }
        if (!perdedor.isMaquina() && !ganador.isMaquina()) {
            ganador.absorber(perdedor);                          // RF-28
            torneo.quitarParticipante(perdedor);
        } else if (!perdedor.isMaquina()) {
            torneo.quitarParticipante(perdedor);                 // RF-34: perdió contra la máquina
            torneo.eliminar(perdedor.getIntegrantes());
        }
        if (ronda.isTerminada()) {
            cerrarRonda(torneo, ronda);
        }
    }

    /** RF-29, RF-34, RF-35. */
    private void cerrarRonda(Torneo torneo, Ronda ronda) {
        Equipo maquina = torneo.getMaquina();
        boolean maquinaGano = ronda.getPartidas().stream().anyMatch(p -> p.getGanador() == maquina);
        torneo.quitarParticipante(maquina);
        if (maquinaGano) {
            torneo.agregarParticipante(maquina);
        }
        if (torneo.getParticipantes().size() == 1) {
            torneo.setCampeon(torneo.getParticipantes().get(0).copia());
            torneo.setEstado(EstadoTorneo.TERMINADO);
        } else {
            armarRonda(torneo);
        }
    }

    // ---------- consultas ----------

    public Torneo obtenerTorneo(String torneoId) {
        return torneoId != null ? torneos.get(torneoId) : null;
    }

    public List<Torneo> listarTorneos() {
        return torneos.values().stream()
                .sorted((a, b) -> a.getFechaCreacion().compareTo(b.getFechaCreacion()))
                .toList();
    }
}
