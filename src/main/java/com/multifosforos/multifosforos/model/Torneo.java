package com.multifosforos.multifosforos.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Torneo por absorción. Las reglas están en specs/ (HU-08 a HU-16, RF-14 a RF-35);
 * el armado de rondas y la votación los maneja TorneoService.
 */
public class Torneo {

    public static final int MIN_INSCRIPTOS = 2;
    public static final int MIN_FOSFOROS = 10;
    public static final int MAX_FOSFOROS = 17;
    public static final int MIN_QUITAR = 2;
    public static final int MAX_QUITAR = 5;

    private final String id;
    private final String claveAdmin;
    private final LocalDateTime fechaCreacion;
    private final Equipo maquina = Equipo.maquina();
    private final List<Inscripto> inscriptos = new ArrayList<>();
    /** Participantes que siguen en el torneo: equipos de alumnos y, si corresponde, la máquina. */
    private final List<Equipo> participantes = new ArrayList<>();
    private final List<Inscripto> eliminados = new ArrayList<>();
    private final List<Ronda> rondas = new ArrayList<>();

    private EstadoTorneo estado = EstadoTorneo.INSCRIPCION;
    private Equipo campeon;

    public Torneo() {
        this.id = UUID.randomUUID().toString();
        this.claveAdmin = UUID.randomUUID().toString();
        this.fechaCreacion = LocalDateTime.now();
    }

    public Inscripto getInscripto(String jugadorId) {
        return inscriptos.stream().filter(i -> i.getId().equals(jugadorId)).findFirst().orElse(null);
    }

    public boolean hayInscriptoConNombre(String nombre) {
        return inscriptos.stream().anyMatch(i -> i.getNombre().trim().equalsIgnoreCase(nombre.trim()));
    }

    /** Equipo de alumnos al que pertenece el jugador, o null. */
    public Equipo getEquipoDe(String jugadorId) {
        return participantes.stream().filter(e -> e.tieneIntegrante(jugadorId)).findFirst().orElse(null);
    }

    public Ronda getRondaActual() {
        return rondas.isEmpty() ? null : rondas.get(rondas.size() - 1);
    }

    public void agregarInscripto(Inscripto inscripto) { inscriptos.add(inscripto); }
    public void agregarParticipante(Equipo equipo) { if (!participantes.contains(equipo)) participantes.add(equipo); }
    public void quitarParticipante(Equipo equipo) { participantes.remove(equipo); }
    public void eliminar(List<Inscripto> alumnos) { eliminados.addAll(alumnos); }
    public void agregarRonda(Ronda ronda) { rondas.add(ronda); }
    public void setEstado(EstadoTorneo estado) { this.estado = estado; }
    public void setCampeon(Equipo campeon) { this.campeon = campeon; }

    public String getId() { return id; }
    public String getClaveAdmin() { return claveAdmin; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public Equipo getMaquina() { return maquina; }
    public EstadoTorneo getEstado() { return estado; }
    public List<Inscripto> getInscriptos() { return Collections.unmodifiableList(inscriptos); }
    public List<Equipo> getParticipantes() { return Collections.unmodifiableList(participantes); }
    public List<Inscripto> getEliminados() { return Collections.unmodifiableList(eliminados); }
    public List<Ronda> getRondas() { return Collections.unmodifiableList(rondas); }
    public Equipo getCampeon() { return campeon; }
}
