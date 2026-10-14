package com.multifosforos.multifosforos.dto;

/** Mensaje WebSocket que manda un jugador para sacar fósforos. */
public record JugadaRequest(String partidaId, String jugadorId, int cantidad) { }
