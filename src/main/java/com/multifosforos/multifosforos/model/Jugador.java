package com.multifosforos.multifosforos.model;

public class Jugador {

    public static final String ID_MAQUINA = "maquina";

    private final String id;
    private final String nombre;
    private final boolean maquina;

    public Jugador(String id, String nombre) {
        this(id, nombre, false);
    }

    private Jugador(String id, String nombre, boolean maquina) {
        this.id = id;
        this.nombre = nombre;
        this.maquina = maquina;
    }

    public static Jugador maquina() {
        return new Jugador(ID_MAQUINA, "Máquina", true);
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public boolean isMaquina() { return maquina; }
}
