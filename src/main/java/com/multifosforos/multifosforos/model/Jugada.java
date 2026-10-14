package com.multifosforos.multifosforos.model;

import java.time.LocalDateTime;

public class Jugada {

    private final String jugadorId;
    private final int cantidad;
    private final int fosforosAntes;
    private final LocalDateTime momento;

    public Jugada(String jugadorId, int cantidad, int fosforosAntes) {
        this.jugadorId = jugadorId;
        this.cantidad = cantidad;
        this.fosforosAntes = fosforosAntes;
        this.momento = LocalDateTime.now();
    }

    public String getJugadorId() { return jugadorId; }
    public int getCantidad() { return cantidad; }
    public int getFosforosAntes() { return fosforosAntes; }
    public int getFosforosDespues() { return fosforosAntes - cantidad; }
    public LocalDateTime getMomento() { return momento; }
}
