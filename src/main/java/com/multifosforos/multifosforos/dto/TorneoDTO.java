package com.multifosforos.multifosforos.dto;

import com.multifosforos.multifosforos.model.EstadoPartida;
import com.multifosforos.multifosforos.model.Equipo;
import com.multifosforos.multifosforos.model.Inscripto;
import com.multifosforos.multifosforos.model.Jugador;
import com.multifosforos.multifosforos.model.Partida;
import com.multifosforos.multifosforos.model.PartidaTorneo;
import com.multifosforos.multifosforos.model.Ronda;
import com.multifosforos.multifosforos.model.Torneo;
import com.multifosforos.multifosforos.model.Votacion;
import com.multifosforos.multifosforos.service.MinimaxService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Estado de un torneo tal como lo ven los alumnos y el proyector. */
public record TorneoDTO(
        String id,
        String estado,
        int minInscriptos,
        List<InscriptoDTO> inscriptos,
        List<EquipoDTO> participantes,
        List<InscriptoDTO> eliminados,
        RondaDTO ronda,
        EquipoDTO campeon) {

    public record InscriptoDTO(String id, String nombre, int partidasJugadas) { }

    public record EquipoDTO(String id, String nombre, String capitanId, boolean maquina, List<InscriptoDTO> integrantes) { }

    public record RondaDTO(int numero, int fosforos, int maxQuitar, boolean esFinal, List<PartidaTorneoDTO> partidas) { }

    /** posicionGanadoraEquipoId: equipo en posición ganadora según MIN-MAX (RF-33); null si la partida terminó. */
    public record PartidaTorneoDTO(
            String id,
            EquipoDTO equipoA,
            EquipoDTO equipoB,
            int fosforosIniciales,
            int fosforosRestantes,
            int maxQuitar,
            String estado,
            String turnoEquipoId,
            String ganadorEquipoId,
            String posicionGanadoraEquipoId,
            VotacionDTO votacion,
            List<JugadaDTO> historial) { }

    /** conteo: votos por cantidad. votantes: ids de quienes ya votaron. */
    public record VotacionDTO(String equipoId, Map<Integer, Integer> conteo, List<String> votantes,
                              long msRestantes, boolean cerrada, Integer jugadaElegida) { }

    public record JugadaDTO(String equipoId, String nombre, int cantidad, int fosforosDespues) { }

    public static TorneoDTO desde(Torneo t, MinimaxService minimax) {
        Ronda r = t.getRondaActual();
        RondaDTO ronda = r == null ? null : new RondaDTO(r.getNumero(), r.getFosforos(), r.getMaxQuitar(), r.isFinal(),
                r.getPartidas().stream().map(p -> partida(p, minimax)).toList());
        return new TorneoDTO(t.getId(), t.getEstado().name(), Torneo.MIN_INSCRIPTOS,
                inscriptos(t.getInscriptos()),
                t.getParticipantes().stream().map(TorneoDTO::equipo).toList(),
                inscriptos(t.getEliminados()),
                ronda,
                t.getCampeon() != null ? equipo(t.getCampeon()) : null);
    }

    private static List<InscriptoDTO> inscriptos(List<Inscripto> lista) {
        return lista.stream().map(i -> new InscriptoDTO(i.getId(), i.getNombre(), i.getPartidasJugadas())).toList();
    }

    private static EquipoDTO equipo(Equipo e) {
        return new EquipoDTO(e.getId(), e.getNombre(), e.getCapitan() != null ? e.getCapitan().getId() : null,
                e.isMaquina(), inscriptos(e.getIntegrantes()));
    }

    private static PartidaTorneoDTO partida(PartidaTorneo pt, MinimaxService minimax) {
        Partida p = pt.getPartida();
        Jugador enTurno = p.getJugadorEnTurno();
        String posicionGanadora = null;
        if (p.getEstado() == EstadoPartida.EN_JUEGO && enTurno != null) {
            String rival = enTurno.getId().equals(pt.getEquipoA().getId()) ? pt.getEquipoB().getId() : pt.getEquipoA().getId();
            posicionGanadora = minimax.esPosicionGanadora(p.getFosforosRestantes(), p.getMaxQuitar())
                    ? enTurno.getId() : rival;
        }
        Votacion v = pt.getVotacion();
        VotacionDTO votacion = v == null ? null : new VotacionDTO(v.getEquipoId(), Votacion.contar(v.getVotos()),
                new ArrayList<>(v.getVotos().keySet()),
                Math.max(0, v.getCierraEn() - System.currentTimeMillis()),
                v.isCerrada(), v.getJugadaElegida());
        List<JugadaDTO> historial = p.getHistorial().stream().map(j -> {
            Equipo e = pt.getEquipo(j.getJugadorId());
            return new JugadaDTO(j.getJugadorId(), e != null ? e.getNombre() : j.getJugadorId(), j.getCantidad(), j.getFosforosDespues());
        }).toList();
        return new PartidaTorneoDTO(p.getId(), equipo(pt.getEquipoA()), equipo(pt.getEquipoB()),
                p.getFosforosIniciales(), p.getFosforosRestantes(), p.getMaxQuitar(), p.getEstado().name(),
                enTurno != null ? enTurno.getId() : null, p.getGanadorId(), posicionGanadora, votacion, historial);
    }
}
