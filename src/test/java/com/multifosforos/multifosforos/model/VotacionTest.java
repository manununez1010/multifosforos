package com.multifosforos.multifosforos.model;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Casos de prueba de la tabla «¿Qué se juega al cerrar la votación?» (specs/test-cases.md, UC-11). */
class VotacionTest {

    private static Map<String, Integer> votos(Object... pares) {
        Map<String, Integer> m = new LinkedHashMap<>();
        for (int i = 0; i < pares.length; i += 2) {
            m.put((String) pares[i], (Integer) pares[i + 1]);
        }
        return m;
    }

    @Test
    void tc55_r2_mayoriaClara() {
        assertEquals(2, Votacion.resolver(votos("a", 2, "b", 2, "c", 1), "c"));
    }

    @Test
    void tc56_r2_equipoDeUnIntegrante() {
        assertEquals(3, Votacion.resolver(votos("a", 3), "a"));
    }

    @Test
    void tc57_r2_ganaLaMasVotadaAunqueNoLlegueALaMitad() {
        assertEquals(1, Votacion.resolver(votos("a", 1, "b", 1, "c", 2, "d", 3, "e", 4), "e"));
    }

    @Test
    void tc58_r3_empateDecideElCapitan() {
        // el capitán votó una de las empatadas: se juega la suya
        assertEquals(2, Votacion.resolver(votos("cap", 2, "b", 1, "c", 1, "d", 2), "cap"));
        // el capitán votó otra cantidad: se juega la menor de las empatadas
        assertEquals(1, Votacion.resolver(votos("cap", 3, "b", 1, "c", 1, "d", 2, "e", 2), "cap"));
        // el capitán no votó: la menor de las empatadas
        assertEquals(1, Votacion.resolver(votos("b", 2, "c", 1), "cap"));
    }

    @Test
    void tc59_r1_siNadieVotaSeSaca1() {
        assertEquals(1, Votacion.resolver(votos(), "cap"));
    }
}
