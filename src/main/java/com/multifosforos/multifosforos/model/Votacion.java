package com.multifosforos.multifosforos.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Votación de un equipo para una jugada (RF-23 a RF-27).
 * Ver specs/decision-table.md, «Votación de un equipo».
 */
public class Votacion {

    /** Duración de la votación (RF-24). */
    public static final long DURACION_MS = 30_000;
    /** Tiempo que se muestra la jugada elegida antes de aplicarla (RF-27). */
    public static final long MUESTRA_MS = 2_000;
    /** Lo que se juega si nadie votó (RF-25, Regla R1). */
    public static final int JUGADA_SI_NADIE_VOTA = 1;

    private final String equipoId;
    private final long cierraEn;
    private final Map<String, Integer> votos = new LinkedHashMap<>();
    private boolean cerrada;
    private boolean aplicada;
    private Integer jugadaElegida;

    public Votacion(String equipoId, long cierraEn) {
        this.equipoId = equipoId;
        this.cierraEn = cierraEn;
    }

    /** Registra el voto; si el integrante ya había votado, lo reemplaza (RF-23). */
    public void votar(String jugadorId, int cantidad) {
        votos.put(jugadorId, cantidad);
    }

    public void cerrar(int jugada) {
        cerrada = true;
        jugadaElegida = jugada;
    }

    public void marcarAplicada() { aplicada = true; }

    /**
     * Cantidad que se juega (tabla «¿Qué se juega al cerrar la votación?»):
     * nadie votó → 1 (R1); una sola cantidad más votada → esa (R2);
     * empate → la del capitán si votó una de las empatadas, si no la menor de las empatadas (R3).
     */
    public static int resolver(Map<String, Integer> votos, String capitanId) {
        if (votos.isEmpty()) {
            return JUGADA_SI_NADIE_VOTA;
        }
        Map<Integer, Integer> conteo = contar(votos);
        int maximo = Collections.max(conteo.values());
        List<Integer> empatadas = conteo.entrySet().stream()
                .filter(e -> e.getValue() == maximo)
                .map(Map.Entry::getKey)
                .sorted()
                .toList();
        if (empatadas.size() == 1) {
            return empatadas.get(0);
        }
        Integer delCapitan = capitanId != null ? votos.get(capitanId) : null;
        if (delCapitan != null && empatadas.contains(delCapitan)) {
            return delCapitan;
        }
        return empatadas.get(0);
    }

    /** Cantidad de votos por cada cantidad de fósforos, ordenado de menor a mayor. */
    public static Map<Integer, Integer> contar(Map<String, Integer> votos) {
        Map<Integer, Integer> conteo = new TreeMap<>();
        for (int c : votos.values()) {
            conteo.merge(c, 1, Integer::sum);
        }
        return conteo;
    }

    public String getEquipoId() { return equipoId; }
    public long getCierraEn() { return cierraEn; }
    public Map<String, Integer> getVotos() { return Collections.unmodifiableMap(votos); }
    public boolean isCerrada() { return cerrada; }
    public boolean isAplicada() { return aplicada; }
    public Integer getJugadaElegida() { return jugadaElegida; }
}
