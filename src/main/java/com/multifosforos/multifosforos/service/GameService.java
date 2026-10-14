package com.multifosforos.multifosforos.service;

import com.multifosforos.multifosforos.model.EstadoPartida;
import com.multifosforos.multifosforos.model.Jugador;
import com.multifosforos.multifosforos.model.ModoPartida;
import com.multifosforos.multifosforos.model.Partida;
import com.multifosforos.multifosforos.model.ResultadoJugada;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    public static final int FOSFOROS_POR_DEFECTO = 15;
    public static final int MAX_QUITAR_POR_DEFECTO = 3;

    private final Map<String, Partida> partidas = new ConcurrentHashMap<>();
    private final Map<String, String> jugadorAPartida = new ConcurrentHashMap<>();
    private final MinimaxService minimaxService;

    public GameService(MinimaxService minimaxService) {
        this.minimaxService = minimaxService;
    }

    /**
     * Crea una partida. Valores nulos toman el valor por defecto (15 fósforos, sacar hasta 3, HUMANOS).
     * Valores fuera de rango se rechazan con IllegalArgumentException.
     */
    public Partida crearPartida(Integer fosforos, Integer maxQuitar, ModoPartida modo) {
        int f = fosforos != null ? fosforos : FOSFOROS_POR_DEFECTO;
        int k = maxQuitar != null ? maxQuitar : MAX_QUITAR_POR_DEFECTO;
        ModoPartida m = modo != null ? modo : ModoPartida.HUMANOS;
        if (f < Partida.MIN_FOSFOROS || f > Partida.MAX_FOSFOROS) {
            throw new IllegalArgumentException("La cantidad de fósforos debe estar entre "
                    + Partida.MIN_FOSFOROS + " y " + Partida.MAX_FOSFOROS);
        }
        if (k < Partida.MIN_QUITAR || k > Partida.MAX_QUITAR) {
            throw new IllegalArgumentException("El máximo a sacar por turno debe estar entre "
                    + Partida.MIN_QUITAR + " y " + Partida.MAX_QUITAR);
        }
        Partida partida = new Partida(f, k, m);
        partidas.put(partida.getId(), partida);
        System.out.println("Partida creada: " + partida.getId() + " (" + f + " fósforos, hasta " + k + ", " + m + ")");
        return partida;
    }

    /**
     * Une un jugador a una partida en ESPERANDO. En modo CONTRA_MAQUINA, la máquina entra
     * automáticamente después del humano y la partida empieza (juega primero el humano).
     */
    public boolean unirJugador(String partidaId, String jugadorId, String nombre) {
        Partida partida = partidas.get(partidaId);
        if (partida == null || partida.getEstado() != EstadoPartida.ESPERANDO) {
            return false;
        }
        if (jugadorAPartida.containsKey(jugadorId)) {
            return false;
        }
        if (!partida.agregarJugador(new Jugador(jugadorId, nombre))) {
            return false;
        }
        jugadorAPartida.put(jugadorId, partidaId);
        if (partida.getModo() == ModoPartida.CONTRA_MAQUINA) {
            partida.agregarJugador(Jugador.maquina());
        }
        System.out.println("Jugador " + nombre + " (" + jugadorId + ") se unió a " + partidaId);
        return true;
    }

    /**
     * Procesa la jugada de un jugador humano. Si la partida es contra la máquina y sigue en juego,
     * la máquina responde en el mismo llamado con la jugada que elige MIN-MAX.
     */
    public ResultadoJugada jugar(String jugadorId, int cantidad) {
        String partidaId = jugadorAPartida.get(jugadorId);
        if (partidaId == null) {
            return ResultadoJugada.JUGADOR_NO_PERTENECE;
        }
        Partida partida = partidas.get(partidaId);
        ResultadoJugada resultado = partida.jugar(jugadorId, cantidad);
        if (resultado != ResultadoJugada.ACEPTADA) {
            return resultado;
        }
        Jugador enTurno = partida.getJugadorEnTurno();
        if (enTurno != null && enTurno.isMaquina()) {
            int q = minimaxService.mejorJugada(partida.getFosforosRestantes(), partida.getMaxQuitar());
            partida.jugar(enTurno.getId(), q);
        }
        if (partida.getEstado() == EstadoPartida.TERMINADA) {
            liberarJugadores(partida);
        }
        return resultado;
    }

    /** Al terminar la partida, los jugadores quedan libres para unirse a otra. */
    private void liberarJugadores(Partida partida) {
        for (Jugador j : partida.getJugadores()) {
            jugadorAPartida.remove(j.getId(), partida.getId());
        }
        System.out.println("Partida terminada: " + partida.getId() + " — ganó " + partida.getGanadorId());
    }

    public Partida obtenerPartida(String partidaId) {
        return partidas.get(partidaId);
    }

    public Partida obtenerPartidaDeJugador(String jugadorId) {
        String partidaId = jugadorAPartida.get(jugadorId);
        return partidaId != null ? partidas.get(partidaId) : null;
    }

    /** Partidas entre humanos que esperan un segundo jugador. */
    public List<Partida> listarDisponibles() {
        return partidas.values().stream()
                .filter(p -> p.getEstado() == EstadoPartida.ESPERANDO)
                .filter(p -> p.getModo() == ModoPartida.HUMANOS)
                .sorted((a, b) -> a.getFechaCreacion().compareTo(b.getFechaCreacion()))
                .toList();
    }

    public List<Partida> listarPartidas() {
        return partidas.values().stream()
                .sorted((a, b) -> a.getFechaCreacion().compareTo(b.getFechaCreacion()))
                .toList();
    }
}
