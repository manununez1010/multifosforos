package com.multifosforos.multifosforos.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Equipo del torneo. Arranca con un solo integrante, que es su capitán (RF-17, RF-26),
 * y crece al absorber a los equipos que le gana (RF-28). La máquina participa como un
 * equipo sin integrantes (RF-34).
 */
public class Equipo {

    private final String id;
    private final Inscripto capitan;
    private final List<Inscripto> integrantes;
    private final boolean maquina;

    private Equipo(String id, Inscripto capitan, List<Inscripto> integrantes, boolean maquina) {
        this.id = id;
        this.capitan = capitan;
        this.integrantes = integrantes;
        this.maquina = maquina;
    }

    /** Equipo inicial de un inscripto: él solo, como capitán. */
    public static Equipo de(Inscripto inscripto) {
        List<Inscripto> integrantes = new ArrayList<>();
        integrantes.add(inscripto);
        return new Equipo("eq-" + inscripto.getId(), inscripto, integrantes, false);
    }

    public static Equipo maquina() {
        return new Equipo(Jugador.ID_MAQUINA, null, new ArrayList<>(), true);
    }

    /** Los integrantes del otro equipo pasan a este. El capitán no cambia. */
    public void absorber(Equipo otro) {
        integrantes.addAll(otro.integrantes);
    }

    /** Copia con los integrantes de este momento (para el campeón, RF-31). */
    public Equipo copia() {
        return new Equipo(id, capitan, new ArrayList<>(integrantes), maquina);
    }

    public boolean tieneIntegrante(String jugadorId) {
        return integrantes.stream().anyMatch(i -> i.getId().equals(jugadorId));
    }

    /** El equipo visto como jugador de una Partida del juego base. */
    public Jugador comoJugador() {
        return maquina ? Jugador.maquina() : new Jugador(id, getNombre());
    }

    public String getNombre() {
        return maquina ? "Máquina" : "Equipo de " + capitan.getNombre();
    }

    public String getId() { return id; }
    public Inscripto getCapitan() { return capitan; }
    public List<Inscripto> getIntegrantes() { return Collections.unmodifiableList(integrantes); }
    public boolean isMaquina() { return maquina; }
}
