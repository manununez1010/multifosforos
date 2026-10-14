package com.multifosforos.multifosforos.service;

import com.multifosforos.multifosforos.model.EstadoPartida;
import com.multifosforos.multifosforos.model.Jugador;
import com.multifosforos.multifosforos.model.ModoPartida;
import com.multifosforos.multifosforos.model.Partida;
import com.multifosforos.multifosforos.model.ResultadoJugada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** Casos de prueba de specs/test-cases.md sobre GameService. */
class GameServiceTest {

    private GameService service;

    @BeforeEach
    void setUp() {
        service = new GameService(new MinimaxService());
    }

    @Test
    void tc01_crearConValoresPorDefecto() {
        Partida p = service.crearPartida(null, null, null);
        assertNotNull(p.getId());
        assertEquals(15, p.getFosforosIniciales());
        assertEquals(15, p.getFosforosRestantes());
        assertEquals(3, p.getMaxQuitar());
        assertEquals(ModoPartida.HUMANOS, p.getModo());
        assertEquals(EstadoPartida.ESPERANDO, p.getEstado());
        assertTrue(p.getJugadores().isEmpty());
    }

    @Test
    void tc02_crearConValoresElegidos() {
        Partida p = service.crearPartida(21, 4, ModoPartida.CONTRA_MAQUINA);
        assertEquals(21, p.getFosforosIniciales());
        assertEquals(4, p.getMaxQuitar());
        assertEquals(ModoPartida.CONTRA_MAQUINA, p.getModo());
    }

    @Test
    void tc03_fosforosFueraDeRangoSeRechazan() {
        assertThrows(IllegalArgumentException.class, () -> service.crearPartida(4, 3, null));
        assertThrows(IllegalArgumentException.class, () -> service.crearPartida(51, 3, null));
        assertDoesNotThrow(() -> service.crearPartida(5, 3, null));
        assertDoesNotThrow(() -> service.crearPartida(50, 3, null));
    }

    @Test
    void tc04_maxQuitarFueraDeRangoSeRechaza() {
        assertThrows(IllegalArgumentException.class, () -> service.crearPartida(15, 1, null));
        assertThrows(IllegalArgumentException.class, () -> service.crearPartida(15, 6, null));
        assertDoesNotThrow(() -> service.crearPartida(15, 2, null));
        assertDoesNotThrow(() -> service.crearPartida(15, 5, null));
    }

    @Test
    void tc05_cadaPartidaTieneIdUnico() {
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            ids.add(service.crearPartida(null, null, null).getId());
        }
        assertEquals(100, ids.size());
    }

    @Test
    void tc06_primerJugadorSeUneYLaPartidaSigueEsperando() {
        Partida p = service.crearPartida(null, null, null);
        assertTrue(service.unirJugador(p.getId(), "j1", "Ana"));
        assertEquals(EstadoPartida.ESPERANDO, p.getEstado());
        assertEquals(1, p.getJugadores().size());
    }

    @Test
    void tc09_unirseAPartidaInexistente() {
        assertFalse(service.unirJugador("no-existe", "j1", "Ana"));
        assertNull(service.obtenerPartidaDeJugador("j1"));
    }

    @Test
    void tc10_jugadorEnOtraPartidaActivaNoSePuedeUnir() {
        Partida a = service.crearPartida(null, null, null);
        Partida b = service.crearPartida(null, null, null);
        assertTrue(service.unirJugador(a.getId(), "j1", "Ana"));
        assertFalse(service.unirJugador(b.getId(), "j1", "Ana"));
        assertEquals(a.getId(), service.obtenerPartidaDeJugador("j1").getId());
        assertTrue(b.getJugadores().isEmpty());
    }

    @Test
    void tc12_disponiblesSoloEsperandoYEntreHumanos() {
        Partida esperando = service.crearPartida(null, null, null);
        Partida llena = service.crearPartida(null, null, null);
        service.unirJugador(llena.getId(), "j1", "Ana");
        service.unirJugador(llena.getId(), "j2", "Beto");
        service.crearPartida(null, null, ModoPartida.CONTRA_MAQUINA);

        List<Partida> disponibles = service.listarDisponibles();
        assertEquals(1, disponibles.size());
        assertEquals(esperando.getId(), disponibles.get(0).getId());
    }

    @Test
    void tc22_alTerminarLosJugadoresQuedanLibres() {
        Partida p = service.crearPartida(5, 2, null);
        service.unirJugador(p.getId(), "j1", "Ana");
        service.unirJugador(p.getId(), "j2", "Beto");
        // 5 -> 3 -> 1 -> 0 : j1 saca el último y pierde
        assertEquals(ResultadoJugada.ACEPTADA, service.jugar("j1", 2));
        assertEquals(ResultadoJugada.ACEPTADA, service.jugar("j2", 2));
        assertEquals(ResultadoJugada.ACEPTADA, service.jugar("j1", 1));
        assertEquals(EstadoPartida.TERMINADA, p.getEstado());
        assertEquals("j2", p.getGanadorId());

        assertNull(service.obtenerPartidaDeJugador("j1"));
        Partida otra = service.crearPartida(null, null, null);
        assertTrue(service.unirJugador(otra.getId(), "j1", "Ana"));
        assertTrue(service.unirJugador(otra.getId(), "j2", "Beto"));
    }

    @Test
    void tc23_contraLaMaquinaEmpiezaConElPrimerHumano() {
        Partida p = service.crearPartida(15, 3, ModoPartida.CONTRA_MAQUINA);
        assertTrue(service.unirJugador(p.getId(), "j1", "Ana"));
        assertEquals(EstadoPartida.EN_JUEGO, p.getEstado());
        assertEquals(2, p.getJugadores().size());
        assertTrue(p.getJugadores().get(1).isMaquina());
        assertEquals("j1", p.getJugadorEnTurno().getId());
    }

    @Test
    void tc24_laMaquinaRespondeEnElMismoLlamado() {
        Partida p = service.crearPartida(15, 3, ModoPartida.CONTRA_MAQUINA);
        service.unirJugador(p.getId(), "j1", "Ana");
        assertEquals(ResultadoJugada.ACEPTADA, service.jugar("j1", 1));
        assertEquals(2, p.getHistorial().size());
        assertEquals(Jugador.ID_MAQUINA, p.getHistorial().get(1).getJugadorId());
        assertEquals("j1", p.getJugadorEnTurno().getId());
    }

    @Test
    void tc25_laMaquinaDejaAlRivalEnPosicionPerdedora() {
        Partida p = service.crearPartida(15, 3, ModoPartida.CONTRA_MAQUINA);
        service.unirJugador(p.getId(), "j1", "Ana");
        service.jugar("j1", 1);                       // quedan 14
        assertEquals(1, p.getHistorial().get(1).getCantidad());
        assertEquals(13, p.getFosforosRestantes());   // 13 = 1 (mod 4)
    }

    @Test
    void tc29_desdePosicionPerdedoraElHumanoNuncaLeGanaALaMaquina() {
        // 13 fósforos, hasta 3: el que empieza (el humano) está perdido. Se prueban TODAS sus estrategias.
        assertTrue(humanoNuncaGana(13, 3, List.of()));
        // 16 fósforos, hasta 4: 16 = 1 (mod 5), también perdedora
        assertTrue(humanoNuncaGana(16, 4, List.of()));
    }

    /** Recorre en profundidad todas las secuencias de jugadas del humano (backtracking sobre partidas nuevas). */
    private boolean humanoNuncaGana(int fosforos, int maxQuitar, List<Integer> prefijo) {
        for (int q = 1; q <= maxQuitar; q++) {
            GameService s = new GameService(new MinimaxService());
            Partida p = s.crearPartida(fosforos, maxQuitar, ModoPartida.CONTRA_MAQUINA);
            s.unirJugador(p.getId(), "h", "Humano");
            for (int jugada : prefijo) s.jugar("h", jugada);
            if (q > p.getFosforosRestantes()) continue;
            s.jugar("h", q);
            if (p.getEstado() == EstadoPartida.TERMINADA) {
                if ("h".equals(p.getGanadorId())) return false;
            } else {
                List<Integer> siguiente = new java.util.ArrayList<>(prefijo);
                siguiente.add(q);
                if (!humanoNuncaGana(fosforos, maxQuitar, siguiente)) return false;
            }
        }
        return true;
    }
}
