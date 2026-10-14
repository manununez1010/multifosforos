package com.multifosforos.multifosforos.dto;

/** Mensaje WebSocket con el voto de un integrante. */
public record VotoRequest(String torneoId, String jugadorId, int cantidad) { }
