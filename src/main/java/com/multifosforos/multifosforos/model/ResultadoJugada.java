package com.multifosforos.multifosforos.model;

/** Resultado de validar una jugada. Ver specs/decision-table.md. */
public enum ResultadoJugada {
    ACEPTADA,
    PARTIDA_NO_EN_JUEGO,
    JUGADOR_NO_PERTENECE,
    NO_ES_SU_TURNO,
    CANTIDAD_FUERA_DE_RANGO,
    MAS_QUE_LOS_RESTANTES
}
