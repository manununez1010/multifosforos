package com.multifosforos.multifosforos.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Ronda del torneo: todas sus partidas usan los mismos fósforos y el mismo máximo (RF-21). */
public class Ronda {

    private final int numero;
    private final int fosforos;
    private final int maxQuitar;
    private final boolean esFinal;
    private final List<PartidaTorneo> partidas = new ArrayList<>();

    public Ronda(int numero, int fosforos, int maxQuitar, boolean esFinal) {
        this.numero = numero;
        this.fosforos = fosforos;
        this.maxQuitar = maxQuitar;
        this.esFinal = esFinal;
    }

    public void agregarPartida(PartidaTorneo partida) {
        partidas.add(partida);
    }

    /** Una ronda termina cuando terminaron todas sus partidas (RF-29). */
    public boolean isTerminada() {
        return partidas.stream().allMatch(PartidaTorneo::isTerminada);
    }

    /** Partida en juego del equipo en esta ronda, o null. */
    public PartidaTorneo getPartidaEnJuegoDe(Equipo equipo) {
        return partidas.stream()
                .filter(p -> p.incluye(equipo) && p.getPartida().getEstado() == EstadoPartida.EN_JUEGO)
                .findFirst().orElse(null);
    }

    public int getNumero() { return numero; }
    public int getFosforos() { return fosforos; }
    public int getMaxQuitar() { return maxQuitar; }
    public boolean isFinal() { return esFinal; }
    public List<PartidaTorneo> getPartidas() { return Collections.unmodifiableList(partidas); }
}
