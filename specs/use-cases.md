# Casos de uso

> Cada caso de uso indica las historias de usuario (`user-stories.md`)
> que cubre. Los casos de uso del torneo siguen la numeración (UC-06 en
> adelante).

## UC-01: Crear partida

**Historias de usuario:** HU-01

**Actor:** Jugador anfitrión

**Precondición:** ninguna.

**Flujo principal:**
1. El jugador envía la cantidad de fósforos, el máximo por turno y el
   modo (HUMANOS o CONTRA_MAQUINA). Puede omitir cualquiera.
2. El sistema completa los valores omitidos con los valores por defecto
   (15, 3, HUMANOS) (RF-01).
3. El sistema verifica que los valores estén en rango (RF-02).
4. El sistema crea la partida en estado "ESPERANDO", sin jugadores, con
   un id único, y la devuelve.

**Flujo(s) alternativo(s):**
- 3a. Si algún valor está fuera de rango, el sistema rechaza el pedido
  con el motivo y no crea nada.

**Postcondición:** existe una partida en "ESPERANDO" sin jugadores.

**Relaciones con otros casos de uso:**

| Relación | Caso de uso relacionado | Tipo | Justificación |
|----------|--------------------------|------|----------------|
| — | UC-02 Unirse a partida | — | No hay include/extend: crear no requiere que alguien se una (el frontend une al creador en un segundo pedido), y unirse no requiere crear en la misma operación. |

## UC-02: Unirse a partida

**Historias de usuario:** HU-02, HU-03, HU-06

**Actor:** Jugador

**Precondición:** existe una partida en "ESPERANDO" (UC-01) y el jugador
no está en otra partida sin terminar.

**Flujo principal:**
1. El jugador envía el id de la partida, su id de jugador y su nombre.
2. El sistema verifica que la partida exista y esté en "ESPERANDO".
3. El sistema verifica que el jugador no esté en otra partida sin
   terminar ni ya en esta (RF-04).
4. El sistema agrega al jugador.
5. Si es el segundo jugador, la partida pasa a "EN_JUEGO" y el turno es
   del primero (RF-05).
6. El sistema notifica el estado actualizado a quienes siguen la partida
   (RF-13).

**Flujo(s) alternativo(s):**
- 2a/3a. Si alguna verificación falla, el sistema rechaza el pedido con
  un motivo y no modifica nada.
- 5a. Si la partida es CONTRA_MAQUINA, el sistema agrega a la máquina
  como segundo jugador; la partida pasa a "EN_JUEGO" con el turno del
  humano (RF-10).

**Postcondición:** el jugador queda en la partida.

**Relaciones con otros casos de uso:**

| Relación | Caso de uso relacionado | Tipo | Justificación |
|----------|--------------------------|------|----------------|
| requiere | UC-01 Crear partida | include | Unirse siempre necesita una partida ya creada: el flujo no tiene sentido sin ella. |

## UC-03: Realizar jugada

**Historias de usuario:** HU-04, HU-07

**Actor:** Jugador

**Precondición:** el jugador está en una partida.

**Flujo principal:**
1. El jugador envía cuántos fósforos saca.
2. El sistema valida la jugada según la tabla de decisión
   (`decision-table.md`, RF-06).
3. El sistema descuenta los fósforos, registra la jugada y pasa el turno
   (RF-07).
4. El sistema notifica el estado actualizado (RF-13).

**Flujo(s) alternativo(s):**
- 2a. Si la jugada se rechaza, el sistema notifica el motivo al jugador
  y no modifica la partida.
- 3a. Si quedan 0 fósforos, se ejecuta UC-04.
- 3b. Si la partida es CONTRA_MAQUINA y no terminó, se ejecuta UC-05
  antes de notificar.

**Postcondición:** la partida refleja la jugada.

**Relaciones con otros casos de uso:**

| Relación | Caso de uso relacionado | Tipo | Justificación |
|----------|--------------------------|------|----------------|
| extendido por | UC-04 Finalizar partida | extend | Solo ocurre bajo una condición (quedan 0 fósforos); la jugada está completa sin él. |
| extendido por | UC-05 Respuesta de la máquina | extend | Solo ocurre en modo CONTRA_MAQUINA y si la partida sigue; la jugada del humano está completa sin él. |

## UC-04: Finalizar partida

**Historias de usuario:** HU-05

**Actor:** Sistema (disparado por UC-03)

**Precondición:** una jugada aceptada dejó 0 fósforos.

**Flujo principal:**
1. El sistema registra como perdedor al jugador que hizo la última
   jugada y como ganador al otro (RF-08).
2. El sistema pasa la partida a "TERMINADA" y registra la fecha de fin.
3. El sistema libera a los jugadores para que puedan unirse a otra
   partida (RF-09).

**Flujo(s) alternativo(s):** ninguno.

**Postcondición:** la partida no acepta más jugadas.

**Relaciones con otros casos de uso:**

| Relación | Caso de uso relacionado | Tipo | Justificación |
|----------|--------------------------|------|----------------|
| extiende a | UC-03 Realizar jugada | extend | Ver UC-03: se agrega al flujo de la jugada solo cuando quedan 0 fósforos. |

## UC-05: Respuesta de la máquina

**Historias de usuario:** HU-06

**Actor:** Sistema (la máquina)

**Precondición:** partida CONTRA_MAQUINA en juego y es el turno de la
máquina.

**Flujo principal:**
1. El sistema calcula con MIN-MAX la cantidad que deja al rival en la
   peor posición (RF-11).
2. Si ninguna jugada gana, la máquina saca 1.
3. El sistema aplica la jugada como en UC-03, pasos 2 y 3.

**Flujo(s) alternativo(s):**
- 3a. Si la jugada de la máquina deja 0 fósforos, se ejecuta UC-04 (la
  máquina pierde).

**Postcondición:** vuelve a ser el turno del humano, o la partida
terminó.

**Relaciones con otros casos de uso:**

| Relación | Caso de uso relacionado | Tipo | Justificación |
|----------|--------------------------|------|----------------|
| extiende a | UC-03 Realizar jugada | extend | Ver UC-03. |

---

# Torneo por absorción

## UC-06: Crear torneo

**Historias de usuario:** HU-08

**Actor:** Docente

**Precondición:** ninguna.

**Flujo principal:**
1. El docente pide crear un torneo.
2. El sistema crea el torneo en estado "INSCRIPCION", sin inscriptos y
   con un código único de 4 números (RF-14).
3. El sistema devuelve el código, para compartirlo con el curso, y la clave
   de administración, que queda guardada en el navegador del docente.

**Flujo(s) alternativo(s):** ninguno.

**Postcondición:** existe un torneo en "INSCRIPCION" sin inscriptos.

**Relaciones con otros casos de uso:**

| Relación | Caso de uso relacionado | Tipo | Justificación |
|----------|--------------------------|------|----------------|
| — | UC-07 Inscribirse en el torneo | — | No hay include/extend: crear no inscribe a nadie, e inscribirse no ejecuta la creación (el torneo creado es solo su precondición). |

## UC-07: Inscribirse en el torneo

**Historias de usuario:** HU-09

**Actor:** Alumno

**Precondición:** existe un torneo (UC-06).

**Flujo principal:**
1. El alumno envía el id del torneo, su id de jugador y su nombre.
2. El sistema verifica que el torneo exista y esté en "INSCRIPCION"
   (RF-15, RF-16).
3. El sistema verifica que el alumno no esté inscripto y que no haya
   otro inscripto con el mismo nombre (RF-15).
4. El sistema registra al alumno como inscripto y avisa el cambio.

**Flujo(s) alternativo(s):**
- 2a. Si el torneo no existe o ya fue iniciado, el sistema rechaza la
  inscripción con el motivo y no modifica nada.
- 3a. Si el alumno ya está inscripto o el nombre está repetido, el
  sistema rechaza la inscripción con el motivo.

**Postcondición:** el alumno figura entre los inscriptos.

**Relaciones con otros casos de uso:**

| Relación | Caso de uso relacionado | Tipo | Justificación |
|----------|--------------------------|------|----------------|
| — | UC-06 Crear torneo | — | Es precondición, no parte del flujo: inscribirse nunca ejecuta la creación. |

## UC-08: Iniciar torneo

**Historias de usuario:** HU-10

**Actor:** Docente

**Precondición:** el torneo está en "INSCRIPCION".

**Flujo principal:**
1. El docente pide iniciar el torneo, con su clave de administración.
2. El sistema verifica la clave y que haya al menos 2 inscriptos
   (RF-18).
3. El sistema cierra la inscripción: el torneo pasa a "EN_CURSO"
   (RF-16).
4. El sistema forma un equipo de un integrante por cada inscripto, que
   es su capitán (RF-17).
5. El sistema arma la primera ronda (UC-09, RF-18).

**Flujo(s) alternativo(s):**
- 2a. Si la clave no es la del torneo, si ya se inició o si hay menos
  de 2 inscriptos, el sistema rechaza el pedido con el motivo y no
  modifica nada.

**Postcondición:** el torneo está en curso, con la primera ronda en
juego.

**Relaciones con otros casos de uso:**

| Relación | Caso de uso relacionado | Tipo | Justificación |
|----------|--------------------------|------|----------------|
| incluye | UC-09 Armar ronda | include | Iniciar siempre termina armando la primera ronda (RF-18); no hay inicio sin ronda. |

## UC-09: Armar ronda

**Historias de usuario:** HU-11, HU-14

**Actor:** Sistema (disparado por UC-08 o UC-12)

**Precondición:** el torneo está en curso, hay al menos dos
participantes y no hay ninguna ronda en juego.

**Flujo principal:**
1. El sistema sortea los fósforos (10 a 17) y el máximo por turno (2 a
   5) de la ronda, sin repetir la combinación de la ronda anterior
   (RF-21).
2. El sistema empareja al azar a los participantes de a dos (RF-19).
3. Por cada pareja, crea una partida con los valores sorteados; todas
   quedan en juego a la vez (RF-19).
4. En cada partida, el primer turno es del equipo con menos
   integrantes; si tienen la misma cantidad, se sortea (RF-22).
5. Si se armó con exactamente dos participantes, la ronda queda marcada
   como la final (RF-30).
6. Se abre la votación del equipo que empieza en cada partida (UC-10).

**Flujo(s) alternativo(s):**
- 2a. Si la cantidad de participantes es impar, un equipo de alumnos
  elegido al azar queda libre y juega contra la máquina; empieza el
  equipo (RF-20, RF-22).

**Postcondición:** hay una ronda en juego con todas sus partidas.

**Relaciones con otros casos de uso:**

| Relación | Caso de uso relacionado | Tipo | Justificación |
|----------|--------------------------|------|----------------|
| incluido por | UC-08 Iniciar torneo | include | Ver UC-08. |
| extiende a | UC-12 Cerrar partida del torneo | extend | Solo ocurre bajo una condición (terminaron todas las partidas de la ronda, no era la final y queda más de un participante); cerrar la partida está completo sin él. |

## UC-10: Votar jugada

**Historias de usuario:** HU-12

**Actor:** Integrante de un equipo

**Precondición:** la partida del equipo está en juego, es el turno del
equipo y la votación está abierta.

**Flujo principal:**
1. El integrante envía la cantidad de fósforos que quiere sacar.
2. El sistema valida el voto según la tabla «¿Se acepta un voto?» de
   `decision-table.md` (RF-23).
3. El sistema registra el voto; si el integrante ya había votado,
   reemplaza su voto anterior.
4. El sistema avisa el cambio (cuántos votaron y el conteo).

**Flujo(s) alternativo(s):**
- 2a. Si el voto se rechaza, el sistema le informa el motivo al
  integrante y no registra nada.
- 3a. Si con este voto ya votaron todos los integrantes, se ejecuta
  UC-11 sin esperar el tiempo (RF-24).

**Postcondición:** el voto queda registrado para la jugada en curso.

**Relaciones con otros casos de uso:**

| Relación | Caso de uso relacionado | Tipo | Justificación |
|----------|--------------------------|------|----------------|
| extendido por | UC-11 Resolver votación | extend | Solo cuando el voto es el último que faltaba (RF-24); en cualquier otro caso votar termina al registrar el voto. |

## UC-11: Resolver votación

**Historias de usuario:** HU-12

**Actor:** Sistema (al vencer los 30 segundos, o por UC-10)

**Precondición:** se cerró la votación de una jugada (RF-24).

**Flujo principal:**
1. El sistema cuenta los votos.
2. El sistema elige la cantidad a jugar según la tabla «¿Qué se juega
   al cerrar la votación?» de `decision-table.md` (RF-25, RF-26).
3. El sistema muestra a cada integrante la jugada elegida durante 2
   segundos (RF-27).
4. El sistema aplica la jugada con las reglas del juego base (UC-03).
5. Si la partida sigue, se abre la votación del equipo rival.

**Flujo(s) alternativo(s):**
- 4a. Si la jugada deja 0 fósforos, se ejecuta UC-04 y, después,
  UC-12.
- 5a. Si el rival es la máquina, juega la máquina (UC-05) y después se
  abre de nuevo la votación del equipo; si la jugada de la máquina
  termina la partida, se ejecuta UC-12.

**Postcondición:** la jugada elegida por el equipo quedó aplicada.

**Relaciones con otros casos de uso:**

| Relación | Caso de uso relacionado | Tipo | Justificación |
|----------|--------------------------|------|----------------|
| incluye | UC-03 Realizar jugada | include | La jugada votada siempre se aplica con las reglas del juego base (F-01); resolver la votación no tiene sentido sin aplicarla. |
| extiende a | UC-10 Votar jugada | extend | Ver UC-10. |

## UC-12: Cerrar partida del torneo

**Historias de usuario:** HU-13, HU-14

**Actor:** Sistema (disparado por UC-04)

**Precondición:** terminó una partida que pertenece a un torneo.

**Flujo principal:**
1. Si la partida era entre dos equipos de alumnos, el sistema pasa a
   todos los integrantes del equipo perdedor al equipo ganador (RF-28).
2. Si quedan partidas de la ronda en juego, el sistema espera.
3. Si terminaron todas las partidas de la ronda, la ronda termina
   (RF-29). La máquina queda como participante si ganó alguna partida
   de la ronda (RF-34).
4. Si queda más de un participante, el sistema arma la siguiente ronda
   (UC-09).

**Flujo(s) alternativo(s):**
- 1a. Si el equipo perdió contra la máquina, queda eliminado y sus
  integrantes dejan de jugar (RF-34). Si el equipo le ganó a la
  máquina, sigue igual.
- 1b. Si la partida era la final, se ejecuta UC-13 (sin absorción,
  RF-31).
- 4a. Si queda un solo participante, es el campeón: se ejecuta UC-13
  (RF-35).

**Postcondición:** los participantes quedan actualizados y, si
correspondía, hay una ronda nueva o un campeón.

**Relaciones con otros casos de uso:**

| Relación | Caso de uso relacionado | Tipo | Justificación |
|----------|--------------------------|------|----------------|
| extiende a | UC-04 Finalizar partida | extend | Solo se agrega cuando la partida es de un torneo; finalizar una partida del juego base está completo sin él. |
| extendido por | UC-09 Armar ronda | extend | Ver UC-09. |
| extendido por | UC-13 Mostrar campeón | extend | Ver UC-13. |

## UC-13: Mostrar campeón

**Historias de usuario:** HU-16

**Actor:** Sistema

**Precondición:** terminó la final, o terminó una ronda y quedó un solo
participante.

**Flujo principal:**
1. El sistema registra como campeón al ganador, con sus integrantes tal
   como llegaron a la final (RF-31, RF-35).
2. El torneo pasa a "TERMINADO".
3. El sistema muestra al campeón con la lista de sus integrantes.

**Flujo(s) alternativo(s):**
- 3a. Si el campeón es la máquina, se muestra la máquina.

**Postcondición:** el torneo terminó y hay un campeón.

**Relaciones con otros casos de uso:**

| Relación | Caso de uso relacionado | Tipo | Justificación |
|----------|--------------------------|------|----------------|
| extiende a | UC-12 Cerrar partida del torneo | extend | Solo ocurre cuando la partida que terminó era la final o dejó un único participante. |

## UC-14: Ver la ronda en el proyector

**Historias de usuario:** HU-15

**Actor:** Docente

**Precondición:** existe el torneo.

**Flujo principal:**
1. El docente abre la vista del proyector del torneo.
2. El sistema muestra todas las partidas de la ronda en curso, con sus
   equipos, los fósforos restantes, el estado y la votación en curso
   (RF-32).
3. Para cada partida en juego, el sistema calcula con MIN-MAX qué
   equipo está en posición ganadora y lo indica (RF-33).
4. La vista se actualiza sola en cada cambio del torneo (RF-32).

**Flujo(s) alternativo(s):**
- 2a. Si el torneo está en inscripción, se muestran los inscriptos, la
  dirección del juego y, en grande, el código del torneo (RF-36).
- 2b. Si el torneo terminó, se muestra el campeón (UC-13).

**Postcondición:** ninguna (solo consulta).

**Relaciones con otros casos de uso:**

| Relación | Caso de uso relacionado | Tipo | Justificación |
|----------|--------------------------|------|----------------|
| — | UC-05 Respuesta de la máquina | — | No hay include/extend: usa el mismo cálculo de MIN-MAX, pero no ejecuta una jugada de la máquina. |
