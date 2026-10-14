# Actividad: torneo de fósforos por absorción

El juego base ya funciona: dos personas (o una persona contra la
máquina) juegan una partida de fósforos. Hay que agregarle un **torneo**
en el que el que pierde se suma al equipo del que le ganó, y los equipos
juegan votando la jugada por mayoría.

La regla de la actividad: **specs primero**. Se trabaja con el
asistente de IA que usen, pero el asistente sigue las reglas de
`AGENTS.md`: no programa nada que no esté especificado, y no decide por
su cuenta lo que el cliente no definió.

## Fase 1 · Especificar el torneo (con IA)

A partir de `relevamiento/torneo.md`, agregar a `specs/`:

1. Las historias de usuario del torneo (desde HU-08).
2. Los requerimientos funcionales (desde RF-14), cada uno vinculado a su
   historia. Los fragmentos que no son RF van a la tabla de fragmentos
   no-RF con su categoría y su justificación.
3. Los casos de uso (desde UC-06), con sus relaciones include/extend
   justificadas.
4. La tabla de decisión de la votación de un equipo, con regla por
   defecto y observaciones.
5. Los casos de prueba (desde TC-30).

Cada conflicto o hueco del relevamiento queda marcado con
`[NEEDS CLARIFICATION: …]`, citando los fragmentos (F-xx). **No se
resuelve inventando un valor.**

**Entrega de la fase 1:** el diff de `specs/` y la lista de los
`[NEEDS CLARIFICATION]` que encontraron. El docente responde como
cliente.

## Fase 2 · Implementar (con IA)

Con las aclaraciones del cliente incorporadas a los specs (y las marcas
quitadas), implementar **de a un RF por vez**:

1. Pedirle al asistente el plan citando los RF y TC (por ejemplo:
   «implementá el RF-17, cubriendo TC-34 y TC-35»).
2. Revisar el plan y confirmar.
3. Verificar que los tests nuevos se llamen `tcNN_…` y que
   `test-cases.md` tenga la columna "Test" completa.
4. `mvn test` en verde antes de pasar al siguiente RF.

**Entrega de la fase 2:** el código, `mvn test` en verde y una frase:
«El RF que más nos costó especificar fue ___, porque ___».

## Lo que no vale

- Pedirle al asistente «hacé el torneo» y aceptar lo que salga.
- Un RF implementado sin su TC, o un test sin su TC en `test-cases.md`.
- Una regla que aparece en el código y no en los specs.
