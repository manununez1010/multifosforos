package com.multifosforos.multifosforos.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Casos de prueba de specs/test-cases.md sobre MIN-MAX. */
class MinimaxServiceTest {

    private MinimaxService minimax;

    @BeforeEach
    void setUp() {
        minimax = new MinimaxService();
    }

    @Test
    void tc26_sePierdeExactamenteConNCongruenteA1ModuloKMas1() {
        for (int k = 2; k <= 5; k++) {
            for (int n = 1; n <= 50; n++) {
                boolean perdedora = n % (k + 1) == 1;
                assertEquals(!perdedora, minimax.esPosicionGanadora(n, k), "n=" + n + ", k=" + k);
            }
        }
    }

    @Test
    void tc27_mejorJugadaDejaAlRivalEnPosicionPerdedora() {
        for (int k = 2; k <= 5; k++) {
            for (int n = 1; n <= 50; n++) {
                int esperada = (n - 1) % (k + 1);
                int jugada = minimax.mejorJugada(n, k);
                if (esperada == 0) {
                    assertEquals(1, jugada, "posición perdedora: saca 1. n=" + n + ", k=" + k);
                } else {
                    assertEquals(esperada, jugada, "n=" + n + ", k=" + k);
                }
            }
        }
    }

    @Test
    void tc28_conMemoLaCantidadDeNodosEsLineal() {
        minimax.reiniciarContador();
        minimax.esPosicionGanadora(50, 5);
        // sin memo serían millones de nodos; con memo, a lo sumo (n+1) * 2 estados * (k+1) llamadas
        assertTrue(minimax.getNodosVisitados() <= 51 * 2 * 6, "nodos: " + minimax.getNodosVisitados());
    }

    @Test
    void elValorDependeDelTurno() {
        // con 1 fósforo, el que tiene el turno pierde; visto desde MAX con turno de MIN, gana MAX
        assertEquals(-1, minimax.minimax(1, 3, true));
        assertEquals(+1, minimax.minimax(1, 3, false));
    }
}
