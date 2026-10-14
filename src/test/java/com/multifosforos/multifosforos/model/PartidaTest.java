package com.multifosforos.multifosforos.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Casos de prueba de specs/test-cases.md sobre el modelo Partida (tabla de decisión de la jugada). */
class PartidaTest {

    private Partida partida;

    @BeforeEach
    void setUp() {
        partida = new Partida(15, 3, ModoPartida.HUMANOS);
        partida.agregarJugador(new Jugador("j1", "Ana"));
        partida.agregarJugador(new Jugador("j2", "Beto"));
    }

    @Test
    void tc07_conElSegundoJugadorEmpiezaYJuegaElPrimero() {
        assertEquals(EstadoPartida.EN_JUEGO, partida.getEstado());
        assertEquals("j1", partida.getJugadorEnTurno().getId());
    }

    @Test
    void tc08_unTercerJugadorEsRechazado() {
        assertFalse(partida.agregarJugador(new Jugador("j3", "Caro")));
        assertEquals(2, partida.getJugadores().size());
    }

    @Test
    void tc11_elMismoJugadorNoEntraDosVeces() {
        Partida p = new Partida(15, 3, ModoPartida.HUMANOS);
        assertTrue(p.agregarJugador(new Jugador("j1", "Ana")));
        assertFalse(p.agregarJugador(new Jugador("j1", "Ana")));
        assertEquals(EstadoPartida.ESPERANDO, p.getEstado());
    }

    @Test
    void tc13_regla1_partidaQueNoEstaEnJuego() {
        Partida p = new Partida(15, 3, ModoPartida.HUMANOS);
        p.agregarJugador(new Jugador("j1", "Ana"));
        assertEquals(ResultadoJugada.PARTIDA_NO_EN_JUEGO, p.jugar("j1", 1));
        assertEquals(15, p.getFosforosRestantes());
    }

    @Test
    void tc14_regla2_jugadorQueNoPertenece() {
        assertEquals(ResultadoJugada.JUGADOR_NO_PERTENECE, partida.jugar("intruso", 1));
    }

    @Test
    void tc15_regla3_noEsSuTurno() {
        assertEquals(ResultadoJugada.NO_ES_SU_TURNO, partida.jugar("j2", 1));
        assertEquals(15, partida.getFosforosRestantes());
    }

    @Test
    void tc16_regla4_cantidadCero() {
        assertEquals(ResultadoJugada.CANTIDAD_FUERA_DE_RANGO, partida.jugar("j1", 0));
    }

    @Test
    void tc17_regla4_cantidadMayorAlMaximo() {
        assertEquals(ResultadoJugada.CANTIDAD_FUERA_DE_RANGO, partida.jugar("j1", 4));
        assertEquals(15, partida.getFosforosRestantes());
    }

    @Test
    void tc18_regla5_masQueLosRestantes() {
        jugarHastaDejar(2);                      // quedan 2, juega j1 o j2
        String enTurno = partida.getJugadorEnTurno().getId();
        assertEquals(ResultadoJugada.MAS_QUE_LOS_RESTANTES, partida.jugar(enTurno, 3));
        assertEquals(2, partida.getFosforosRestantes());
    }

    @Test
    void tc19_regla6_jugadaAceptadaDescuentaRegistraYPasaElTurno() {
        assertEquals(ResultadoJugada.ACEPTADA, partida.jugar("j1", 2));
        assertEquals(13, partida.getFosforosRestantes());
        assertEquals("j2", partida.getJugadorEnTurno().getId());
        assertEquals(1, partida.getHistorial().size());
        Jugada jugada = partida.getHistorial().get(0);
        assertEquals("j1", jugada.getJugadorId());
        assertEquals(2, jugada.getCantidad());
        assertEquals(15, jugada.getFosforosAntes());
        assertEquals(13, jugada.getFosforosDespues());
    }

    @Test
    void tc20_quienSacaElUltimoPierde() {
        jugarHastaDejar(1);
        String enTurno = partida.getJugadorEnTurno().getId();
        String otro = enTurno.equals("j1") ? "j2" : "j1";
        assertEquals(ResultadoJugada.ACEPTADA, partida.jugar(enTurno, 1));
        assertEquals(EstadoPartida.TERMINADA, partida.getEstado());
        assertEquals(enTurno, partida.getPerdedorId());
        assertEquals(otro, partida.getGanadorId());
        assertEquals(0, partida.getFosforosRestantes());
        assertNotNull(partida.getFechaFin());
        assertNull(partida.getJugadorEnTurno());
    }

    @Test
    void tc21_terminadaNoAceptaMasJugadas() {
        jugarHastaDejar(1);
        partida.jugar(partida.getJugadorEnTurno().getId(), 1);
        assertEquals(ResultadoJugada.PARTIDA_NO_EN_JUEGO, partida.jugar("j1", 1));
        assertEquals(ResultadoJugada.PARTIDA_NO_EN_JUEGO, partida.jugar("j2", 1));
    }

    /** Saca de a 1 alternando turnos hasta que queden 'restantes' fósforos. */
    private void jugarHastaDejar(int restantes) {
        while (partida.getFosforosRestantes() > restantes) {
            partida.jugar(partida.getJugadorEnTurno().getId(), 1);
        }
    }
}
