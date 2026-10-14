# Tabla de decisión

> Detalla las reglas del caso de uso UC-03 (`use-cases.md`) y de la
> historia HU-04 (`user-stories.md`). Las tablas del torneo (por
> ejemplo, la votación) se agregan en este mismo archivo.

## ¿Se acepta una jugada?

| Condición / Acción | Regla 1 | Regla 2 | Regla 3 | Regla 4 | Regla 5 | Regla 6 | Regla por defecto |
|----------------------|---------|---------|---------|---------|---------|---------|---------------------|
| Partida en estado "EN_JUEGO" | No | Sí | Sí | Sí | Sí | Sí | - |
| El jugador pertenece a la partida | - | No | Sí | Sí | Sí | Sí | - |
| Es el turno del jugador | - | - | No | Sí | Sí | Sí | - |
| 1 ≤ cantidad ≤ máximo por turno | - | - | - | No | Sí | Sí | - |
| cantidad ≤ fósforos restantes | - | - | - | - | No | Sí | - |
| **Acción: Jugada aceptada** | No | No | No | No | No | Sí | No |
| **Acción: Motivo del rechazo** | Partida no en juego | No pertenece | No es su turno | Fuera de rango | Más que los restantes | — | Rechazada |
| **Acción: Fósforos descontados** | No | No | No | No | No | Sí | No |

**Regla por defecto:** cualquier combinación no cubierta por las reglas
1 a 6 se rechaza sin modificar la partida.

**Cómo se lee esta tabla:** las condiciones se evalúan en orden de
arriba hacia abajo y se corta en la primera que falla. Por eso, por
ejemplo, una jugada fuera de turno con una cantidad inválida se rechaza
por "No es su turno" (Regla 3) y no por la cantidad.

## ¿Qué pasa después de una jugada aceptada?

| Condición / Acción | Regla A | Regla B | Regla C | Regla por defecto |
|----------------------|---------|---------|---------|---------------------|
| Quedan 0 fósforos | Sí | No | No | - |
| Modo CONTRA_MAQUINA | - | Sí | No | - |
| **Acción: Partida TERMINADA, pierde quien jugó** | Sí | No | No | No |
| **Acción: Pasa el turno al otro jugador** | No | Sí | Sí | Sí |
| **Acción: La máquina juega con MIN-MAX** | No | Sí | No | No |

**Regla por defecto:** pasa el turno al otro jugador.

## Observaciones

- **Conflictos detectados:** ninguno en el juego base.
- **Huecos:** no está definido qué pasa si un jugador abandona en medio
  de la partida (ver `requirements.md`, observaciones).
- **Decisiones de negocio pendientes:** las del torneo, en
  `relevamiento/torneo.md`.

---

# Votación de un equipo (torneo)

> Detalla las reglas de UC-10 y UC-11 (`use-cases.md`), de la historia
> HU-12 y de los requerimientos RF-23 a RF-27.

## ¿Se acepta un voto?

| Condición / Acción | Regla V1 | Regla V2 | Regla V3 | Regla V4 | Regla V5 | Regla V6 | Regla por defecto |
|----------------------|----------|----------|----------|----------|----------|----------|---------------------|
| El votante tiene una partida del torneo en "EN_JUEGO" | No | Sí | Sí | Sí | Sí | Sí | - |
| Es el turno del equipo del votante | - | No | Sí | Sí | Sí | Sí | - |
| La votación está abierta | - | - | No | Sí | Sí | Sí | - |
| 1 ≤ cantidad ≤ máximo por turno de la ronda | - | - | - | No | Sí | Sí | - |
| cantidad ≤ fósforos restantes | - | - | - | - | No | Sí | - |
| **Acción: Voto registrado (reemplaza el anterior del mismo integrante)** | No | No | No | No | No | Sí | No |
| **Acción: Motivo del rechazo** | No tenés una partida en juego | No es el turno de tu equipo | Votación cerrada | Fuera de rango | Más que los restantes | — | Rechazado |
| **Acción: Si votaron todos los integrantes, se cierra la votación** | No | No | No | No | No | Sí | No |

**Regla por defecto:** cualquier combinación no cubierta por las reglas
V1 a V6 se rechaza sin registrar el voto.

**Cómo se lee esta tabla:** igual que la de la jugada, las condiciones
se evalúan de arriba hacia abajo y se corta en la primera que falla.
Un alumno no inscripto o eliminado no tiene partida (Regla V1). La
votación está cerrada si pasaron los 30 segundos o si ya se eligió la
jugada y se la está mostrando (RF-24, RF-27).

## ¿Qué se juega al cerrar la votación?

| Condición / Acción | Regla R1 | Regla R2 | Regla R3 | Regla R4 | Regla por defecto |
|----------------------|----------|----------|----------|----------|---------------------|
| Hay al menos un voto | No | Sí | Sí | Sí | - |
| Una sola cantidad es la más votada | - | Sí | No | No | - |
| El capitán votó una de las cantidades empatadas | - | - | Sí | No | - |
| **Acción: Cantidad que se juega** | 1 | La más votada | La del capitán | La menor de las empatadas | Ninguna |
| **Acción: Se muestra la jugada a los integrantes durante 2 s (RF-27)** | Sí | Sí | Sí | Sí | No |
| **Acción: Se aplica con la tabla «¿Se acepta una jugada?»** | Sí | Sí | Sí | Sí | No |

**Regla por defecto:** no se aplica ninguna jugada y la partida no se
modifica. Con las reglas R1 a R4 se cubren todas las combinaciones
posibles, así que la regla por defecto queda solo como resguardo.

**Nota:** la jugada elegida siempre es válida, porque cada voto se
validó con la tabla anterior y, si nadie votó, sacar 1 siempre es
posible (queda al menos un fósforo).

## Observaciones de la votación

- **Conflictos detectados y resueltos por el cliente (14/10/2026):**
  - Desempate: F-07 (capitán) vs. F-08 (MIN-MAX). Decide el capitán
    (Reglas R3 y R4).
  - Tiempo de votación: F-09 (20 s) vs. F-10 (15 s). El cliente fijó
    30 s; la votación se cierra antes si votaron todos.
- **Huecos resueltos por el cliente:** mayoría simple (gana la más
  votada); se puede cambiar el voto; si nadie vota, se saca 1
  (Regla R1); la jugada elegida se muestra 2 segundos.
- **Decisión pendiente de confirmar:** Regla R4 (el capitán no votó
  ninguna de las empatadas → la menor de las empatadas). La agregamos
  para que la tabla no deje casos sin regla.
- Las condiciones de rango (Reglas V4 y V5) se deducen de F-01 («las
  mismas reglas que el juego que ya tenemos»): un voto propone una
  jugada que tiene que ser válida.
