package com.multifosforos.multifosforos.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MIN-MAX para el juego de los fósforos (clase 11).
 *
 * MAX es el jugador que tiene el turno en la posición que se consulta.
 * Valores: +1 gana MAX, -1 gana MIN. No hay empates.
 *
 * El estado del juego es (fósforos, maxQuitar, turno): la memo se indexa
 * por los tres. Memoizar solo por la cantidad de fósforos da resultados
 * incorrectos, porque el mismo número de fósforos vale distinto según a
 * quién le toque.
 */
@Service
public class MinimaxService {

    private final Map<String, Integer> memo = new ConcurrentHashMap<>();
    private final AtomicLong nodosVisitados = new AtomicLong();

    /**
     * Valor MIN-MAX de la posición.
     * Sin memo, T(n) = T(n-1) + ... + T(n-k) + O(1): exponencial. Con memo, O(n·k).
     */
    public int minimax(int fosforos, int maxQuitar, boolean turnoMax) {
        nodosVisitados.incrementAndGet();
        String clave = fosforos + ":" + maxQuitar + ":" + turnoMax;
        Integer guardado = memo.get(clave);
        if (guardado != null) {
            return guardado;
        }

        int valor;
        if (fosforos == 0) {
            // el rival se llevó el último: gana el que tiene el turno
            valor = turnoMax ? +1 : -1;
        } else {
            valor = turnoMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            for (int quitar = 1; quitar <= maxQuitar && quitar <= fosforos; quitar++) {
                int hijo = minimax(fosforos - quitar, maxQuitar, !turnoMax);
                valor = turnoMax ? Math.max(valor, hijo) : Math.min(valor, hijo);
            }
        }
        memo.put(clave, valor);
        return valor;
    }

    /**
     * Cuántos fósforos conviene sacar al jugador que tiene el turno.
     * Si todas las jugadas pierden, devuelve 1 (la más chica).
     */
    public int mejorJugada(int fosforos, int maxQuitar) {
        int mejor = 1;
        int mejorValor = Integer.MIN_VALUE;
        for (int quitar = 1; quitar <= maxQuitar && quitar <= fosforos; quitar++) {
            int valor = minimax(fosforos - quitar, maxQuitar, false);   // después juega el rival (MIN)
            if (valor > mejorValor) {
                mejorValor = valor;
                mejor = quitar;
            }
        }
        return mejor;
    }

    /** true si el jugador que tiene el turno gana jugando bien, haga lo que haga el rival. */
    public boolean esPosicionGanadora(int fosforos, int maxQuitar) {
        return minimax(fosforos, maxQuitar, true) == +1;
    }

    public long getNodosVisitados() {
        return nodosVisitados.get();
    }

    public void reiniciarContador() {
        nodosVisitados.set(0);
    }
}
