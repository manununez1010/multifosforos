package com.multifosforos.multifosforos.model;

/** Alumno inscripto en un torneo. */
public class Inscripto {

    private final String id;
    private final String nombre;
    private int partidasJugadas;

    public Inscripto(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public void sumarPartida() { partidasJugadas++; }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public int getPartidasJugadas() { return partidasJugadas; }
}
