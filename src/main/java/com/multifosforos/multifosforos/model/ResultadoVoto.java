package com.multifosforos.multifosforos.model;

/** Resultado de validar un voto. Ver specs/decision-table.md, «¿Se acepta un voto?». */
public enum ResultadoVoto {
    ACEPTADO,
    SIN_PARTIDA_EN_JUEGO,
    NO_ES_TURNO_DE_SU_EQUIPO,
    VOTACION_CERRADA,
    CANTIDAD_FUERA_DE_RANGO,
    MAS_QUE_LOS_RESTANTES
}
