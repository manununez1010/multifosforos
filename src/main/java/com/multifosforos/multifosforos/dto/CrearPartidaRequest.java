package com.multifosforos.multifosforos.dto;

/** Pedido para crear una partida. Los campos nulos toman el valor por defecto. */
public record CrearPartidaRequest(Integer fosforos, Integer maxQuitar, String modo) { }
