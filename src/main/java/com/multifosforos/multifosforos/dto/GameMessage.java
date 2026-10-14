package com.multifosforos.multifosforos.dto;

/**
 * Mensaje que se publica en /topic/partida/{id}.
 * tipo: ESTADO (cambió la partida) o ERROR (jugada rechazada; jugadorId indica a quién va dirigido).
 */
public record GameMessage(String tipo, PartidaDTO partida, String jugadorId, String error) {

    public static GameMessage estado(PartidaDTO partida) {
        return new GameMessage("ESTADO", partida, null, null);
    }

    public static GameMessage error(String jugadorId, String error) {
        return new GameMessage("ERROR", null, jugadorId, error);
    }
}
