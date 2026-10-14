package com.multifosforos.multifosforos.dto;

/**
 * Mensaje que se publica en /topic/torneo/{id}.
 * tipo: ESTADO (cambió el torneo) o ERROR (voto rechazado; jugadorId indica a quién va dirigido).
 */
public record TorneoMessage(String tipo, TorneoDTO torneo, String jugadorId, String error) {

    public static TorneoMessage estado(TorneoDTO torneo) {
        return new TorneoMessage("ESTADO", torneo, null, null);
    }

    public static TorneoMessage error(String jugadorId, String error) {
        return new TorneoMessage("ERROR", null, jugadorId, error);
    }
}
