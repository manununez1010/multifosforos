# multifosforos

Juego de los fósforos multijugador en tiempo real (Programación III,
UADE). Backend Spring Boot (Java 17) con REST para crear partidas y
unirse, y WebSocket/STOMP para las jugadas; frontend en HTML/JS plano
servido como recursos estáticos. La máquina juega con MIN-MAX.

## Build y ejecución

- Compilar: `mvn compile` (hay `mvnw`/`mvnw.cmd`; si el wrapper no puede
  descargar el jar sin red, usar el `mvn` del sistema).
- Tests: `mvn test`.
- Correr: `mvn spring-boot:run` (levanta en `http://localhost:8080`).
- Páginas estáticas: `index.html`, `crear.html`, `unirse.html`,
  `juego.html` en `src/main/resources/static/`. SockJS y STOMP están en
  `static/js/` (no dependen de internet).

## Arquitectura

- `model/`: `Partida` (reglas de una partida de a dos y validación de
  la jugada), `Jugador`, `Jugada`, `EstadoPartida`, `ModoPartida`,
  `ResultadoJugada`. Estado en memoria: no hay base de datos.
- `service/GameService.java`: alta de partidas, unión de jugadores,
  jugadas, respuesta de la máquina y liberación de jugadores al terminar.
  Todo vive en `ConcurrentHashMap`; se pierde al reiniciar.
- `service/MinimaxService.java`: MIN-MAX con memoización por (fósforos,
  máximo, turno). `mejorJugada`, `esPosicionGanadora` y un contador de
  nodos.
- `controller/PartidaController.java`: REST en `/api/partidas` (crear,
  unirse, consultar, listar disponibles).
- `controller/WebSocketController.java` + `config/WebSocketConfig.java`:
  endpoint STOMP en `/ws`, jugadas en `/app/partida/jugada`, estado y
  errores en `/topic/partida/{id}`.
- `dto/`: forma de los pedidos y de los mensajes (`PartidaDTO`,
  `GameMessage`, `JugadaRequest`, etc.).

Reglas de negocio documentadas en `specs/` — ver ahí antes de asumir
cómo funciona algo: `user-stories.md`, `requirements.md` (RF, cada uno
vinculado a su historia), `use-cases.md`, `decision-table.md` y
`test-cases.md` (cada caso vinculado a su test).

## Cómo trabajar en este repo

Estas reglas aplican a cualquier asistente de IA que trabaje en este
repositorio (Claude, Gemini, Copilot, Cursor, Codex u otro).

### Specs primero — no se programa lo que no está especificado

- No escribir código de una funcionalidad que no esté en `specs/`: para
  implementar algo tiene que existir su historia (HU), su requerimiento
  (RF) y al menos un caso de prueba (TC) que lo cubra.
- Si el pedido no nombra un RF (por ejemplo, "hacé el torneo", "agregá
  la votación"), **no implementar**: responder qué RF faltan o cuáles
  aplican, y proponer especificarlos primero.
- Un RF, una fila de tabla de decisión o un caso de uso marcado
  `[NEEDS CLARIFICATION]` **no se implementa** hasta que el docente
  (cliente) lo resuelva y la marca se quite del spec.
- Todo plan de implementación empieza listando los RF y los TC que
  cubre. Todo test nuevo lleva el id del caso en el nombre del método
  (`tc31_…`) y se anota en la columna "Test" de `test-cases.md`.
- Si al implementar aparece una regla que el spec no define, se frena y
  se marca en el spec; no se decide en el código.
- El material relevado del torneo está en `relevamiento/torneo.md`.
  Pasarlo a specs se hace con las convenciones de `skill/SKILL.md`.

### Confirmación obligatoria antes de crear o modificar código

Antes de crear o modificar cualquier archivo, presentar primero un plan
breve: qué archivos se van a crear/modificar y el enfoque general (2-4
líneas). Esperar confirmación explícita del usuario ("dale", "sí",
"procedé") antes de escribir una sola línea de código. No asumir
aprobación por el solo hecho de que el pedido sonara claro.

### Alcance por defecto — no agregar sin pedirlo

- **Principio de Corte Vertical (Vertical Slice):** Salvo que se indique
  explícitamente "solo backend" o "solo tests unitarios", implementar un
  requerimiento funcional implica completar el corte vertical:
  Modelo → Servicio → Controller/WebSocket → Vista (HTML/JS) → Tests.
- Resolver únicamente lo que el pedido dice, ni una función más. Si el
  pedido es "implementá el RF-16", no tocar el RF-17 aunque esté al lado
  y parezca lo mismo.
- No agregar manejo de errores, validaciones, logging, comentarios ni
  documentación que no se hayan pedido explícitamente, aunque parezcan
  "buenas prácticas" obvias.
- No refactorizar, renombrar, reordenar ni "prolijar" código o specs
  existentes como parte de un pedido distinto — ni siquiera si están
  mal escritos.
- No crear archivos nuevos (specs, docs, configuración) que no fueron
  pedidos, aunque la convención de `skill/SKILL.md` los sugiera como
  recomendable.
- No agregar dependencias, librerías ni herramientas nuevas por
  iniciativa propia.
- Si al hacer el trabajo pedido aparece un problema aparte (un bug, un
  hueco, una inconsistencia), no corregirlo de oficio: reportarlo por
  separado y esperar que se pida explícitamente arreglarlo.
- Ante la duda de si algo entra en el alcance pedido, tratarlo como que
  NO entra, y preguntar antes de agregarlo.
- Un pedido amplio ("armá los specs") no habilita inventar secciones,
  columnas o convenciones nuevas que no estén ya en la plantilla — si
  falta algo para completar lo pedido, se marca como hueco, no se
  completa por cuenta propia.

### Puntos pendientes y conflictos

- `specs/requirements.md` y `specs/decision-table.md` tienen puntos
  marcados `[NEEDS CLARIFICATION]` que son decisiones de negocio
  pendientes, no bugs a "resolver" por cuenta propia.
- Si se detecta un conflicto de reglas o un comportamiento dudoso, **no
  elegir un valor arbitrariamente**: marcar el requerimiento, la fila de
  la tabla de decisión o el caso de uso afectado con
  `[NEEDS CLARIFICATION: <descripción breve del conflicto>]` y listarlo
  en la sección de observaciones del archivo correspondiente.

### Convenciones de specs

Para escribir o modificar specs (requerimientos, casos de uso, tablas
de decisión), seguir las convenciones completas de `skill/SKILL.md`.
