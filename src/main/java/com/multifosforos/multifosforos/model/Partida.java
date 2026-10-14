package com.multifosforos.multifosforos.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Una partida de fósforos entre dos jugadores.
 * Reglas: se alternan turnos, cada uno saca entre 1 y maxQuitar fósforos,
 * y PIERDE quien se lleva el último.
 */
public class Partida {

    public static final int MIN_FOSFOROS = 5;
    public static final int MAX_FOSFOROS = 50;
    public static final int MIN_QUITAR = 2;
    public static final int MAX_QUITAR = 5;
    public static final int JUGADORES_POR_PARTIDA = 2;

    private final String id;
    private final int fosforosIniciales;
    private final int maxQuitar;
    private final ModoPartida modo;
    private final LocalDateTime fechaCreacion;
    private final List<Jugador> jugadores = new ArrayList<>();
    private final List<Jugada> historial = new ArrayList<>();

    private int fosforosRestantes;
    private EstadoPartida estado;
    private int turno;              // índice en jugadores del que tiene que jugar
    private String ganadorId;
    private String perdedorId;
    private LocalDateTime fechaFin;

    public Partida(int fosforosIniciales, int maxQuitar, ModoPartida modo) {
        this.id = UUID.randomUUID().toString();
        this.fosforosIniciales = fosforosIniciales;
        this.fosforosRestantes = fosforosIniciales;
        this.maxQuitar = maxQuitar;
        this.modo = modo;
        this.estado = EstadoPartida.ESPERANDO;
        this.fechaCreacion = LocalDateTime.now();
    }

    /** Agrega un jugador. Con el segundo jugador la partida pasa a EN_JUEGO y empieza el primero que entró. */
    public synchronized boolean agregarJugador(Jugador jugador) {
        if (estado != EstadoPartida.ESPERANDO || jugadores.size() >= JUGADORES_POR_PARTIDA) {
            return false;
        }
        if (getJugador(jugador.getId()) != null) {
            return false;
        }
        jugadores.add(jugador);
        if (jugadores.size() == JUGADORES_POR_PARTIDA) {
            estado = EstadoPartida.EN_JUEGO;
            turno = 0;
        }
        return true;
    }

    /** Valida una jugada sin aplicarla. El orden de los chequeos es el de la tabla de decisión. */
    public synchronized ResultadoJugada validarJugada(String jugadorId, int cantidad) {
        if (estado != EstadoPartida.EN_JUEGO) return ResultadoJugada.PARTIDA_NO_EN_JUEGO;
        if (getJugador(jugadorId) == null) return ResultadoJugada.JUGADOR_NO_PERTENECE;
        if (!jugadores.get(turno).getId().equals(jugadorId)) return ResultadoJugada.NO_ES_SU_TURNO;
        if (cantidad < 1 || cantidad > maxQuitar) return ResultadoJugada.CANTIDAD_FUERA_DE_RANGO;
        if (cantidad > fosforosRestantes) return ResultadoJugada.MAS_QUE_LOS_RESTANTES;
        return ResultadoJugada.ACEPTADA;
    }

    /** Valida y, si corresponde, aplica la jugada: saca los fósforos, registra la jugada y pasa el turno o termina. */
    public synchronized ResultadoJugada jugar(String jugadorId, int cantidad) {
        ResultadoJugada resultado = validarJugada(jugadorId, cantidad);
        if (resultado != ResultadoJugada.ACEPTADA) {
            return resultado;
        }
        historial.add(new Jugada(jugadorId, cantidad, fosforosRestantes));
        fosforosRestantes -= cantidad;
        if (fosforosRestantes == 0) {
            // se llevó el último: pierde
            perdedorId = jugadorId;
            ganadorId = jugadores.get(1 - turno).getId();
            estado = EstadoPartida.TERMINADA;
            fechaFin = LocalDateTime.now();
        } else {
            turno = 1 - turno;
        }
        return ResultadoJugada.ACEPTADA;
    }

    public Jugador getJugador(String jugadorId) {
        for (Jugador j : jugadores) {
            if (j.getId().equals(jugadorId)) return j;
        }
        return null;
    }

    /** Jugador que tiene el turno, o null si la partida no está en juego. */
    public synchronized Jugador getJugadorEnTurno() {
        return estado == EstadoPartida.EN_JUEGO ? jugadores.get(turno) : null;
    }

    public boolean isActiva() {
        return estado != EstadoPartida.TERMINADA;
    }

    public String getId() { return id; }
    public int getFosforosIniciales() { return fosforosIniciales; }
    public int getFosforosRestantes() { return fosforosRestantes; }
    public int getMaxQuitar() { return maxQuitar; }
    public ModoPartida getModo() { return modo; }
    public EstadoPartida getEstado() { return estado; }
    public List<Jugador> getJugadores() { return Collections.unmodifiableList(jugadores); }
    public List<Jugada> getHistorial() { return Collections.unmodifiableList(historial); }
    public String getGanadorId() { return ganadorId; }
    public String getPerdedorId() { return perdedorId; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaFin() { return fechaFin; }
}
