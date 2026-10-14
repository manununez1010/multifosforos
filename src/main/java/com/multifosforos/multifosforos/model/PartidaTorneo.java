package com.multifosforos.multifosforos.model;

/**
 * Partida de una ronda del torneo: una Partida del juego base entre dos equipos.
 * El equipo A es el que empieza (RF-22).
 */
public class PartidaTorneo {

    private final Partida partida;
    private final Equipo equipoA;
    private final Equipo equipoB;
    private Votacion votacion;

    public PartidaTorneo(Partida partida, Equipo equipoA, Equipo equipoB) {
        this.partida = partida;
        this.equipoA = equipoA;
        this.equipoB = equipoB;
    }

    public boolean incluye(Equipo equipo) {
        return equipoA == equipo || equipoB == equipo;
    }

    public Equipo getEquipo(String equipoId) {
        if (equipoA.getId().equals(equipoId)) return equipoA;
        if (equipoB.getId().equals(equipoId)) return equipoB;
        return null;
    }

    /** Equipo que tiene el turno, o null si la partida no está en juego. */
    public Equipo getEquipoEnTurno() {
        Jugador j = partida.getJugadorEnTurno();
        return j != null ? getEquipo(j.getId()) : null;
    }

    public Equipo getGanador() {
        return partida.getGanadorId() != null ? getEquipo(partida.getGanadorId()) : null;
    }

    public Equipo getPerdedor() {
        return partida.getPerdedorId() != null ? getEquipo(partida.getPerdedorId()) : null;
    }

    public boolean isTerminada() {
        return partida.getEstado() == EstadoPartida.TERMINADA;
    }

    public void setVotacion(Votacion votacion) { this.votacion = votacion; }

    public Partida getPartida() { return partida; }
    public Equipo getEquipoA() { return equipoA; }
    public Equipo getEquipoB() { return equipoB; }
    public Votacion getVotacion() { return votacion; }
}
