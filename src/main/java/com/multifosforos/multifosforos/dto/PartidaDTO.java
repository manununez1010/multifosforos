package com.multifosforos.multifosforos.dto;

import com.multifosforos.multifosforos.model.Jugada;
import com.multifosforos.multifosforos.model.Jugador;
import com.multifosforos.multifosforos.model.Partida;

import java.util.List;

/** Estado de una partida tal como lo ve el frontend. */
public record PartidaDTO(
        String id,
        int fosforosIniciales,
        int fosforosRestantes,
        int maxQuitar,
        String modo,
        String estado,
        List<JugadorDTO> jugadores,
        String turnoJugadorId,
        String ganadorId,
        String perdedorId,
        List<JugadaDTO> historial) {

    public record JugadorDTO(String id, String nombre, boolean maquina) { }

    public record JugadaDTO(String jugadorId, String nombre, int cantidad, int fosforosDespues) { }

    public static PartidaDTO desde(Partida p) {
        List<JugadorDTO> jugadores = p.getJugadores().stream()
                .map(j -> new JugadorDTO(j.getId(), j.getNombre(), j.isMaquina()))
                .toList();
        List<JugadaDTO> historial = p.getHistorial().stream()
                .map(jg -> new JugadaDTO(jg.getJugadorId(), nombre(p, jg), jg.getCantidad(), jg.getFosforosDespues()))
                .toList();
        Jugador enTurno = p.getJugadorEnTurno();
        return new PartidaDTO(p.getId(), p.getFosforosIniciales(), p.getFosforosRestantes(), p.getMaxQuitar(),
                p.getModo().name(), p.getEstado().name(), jugadores,
                enTurno != null ? enTurno.getId() : null,
                p.getGanadorId(), p.getPerdedorId(), historial);
    }

    private static String nombre(Partida p, Jugada jg) {
        Jugador j = p.getJugador(jg.getJugadorId());
        return j != null ? j.getNombre() : jg.getJugadorId();
    }
}
