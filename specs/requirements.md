# Requerimientos

> La columna "Historia(s) de usuario" vincula cada requerimiento con su
> historia en `user-stories.md`. Los requerimientos del torneo siguen la
> numeración (RF-14 en adelante).

## Requerimientos funcionales

| ID | Requerimiento | Historia(s) de usuario |
|----|---------------|------------------------|
| RF-01 | El sistema debe crear una partida nueva con un id único, una cantidad inicial de fósforos, un máximo de fósforos por turno y un modo (HUMANOS o CONTRA_MAQUINA). Si no se indican, se usan 15 fósforos, máximo 3 y modo HUMANOS. | HU-01 |
| RF-02 | El sistema debe rechazar la creación de una partida con menos de 5 o más de 50 fósforos, o con un máximo por turno menor que 2 o mayor que 5. | HU-01 |
| RF-03 | El sistema debe listar las partidas disponibles: en estado "ESPERANDO" y en modo HUMANOS. | HU-02 |
| RF-04 | El sistema debe permitir que un jugador se una a una partida en estado "ESPERANDO" indicando id de partida, id de jugador y nombre, siempre que ese jugador no esté en otra partida que no haya terminado y que no esté ya en esa misma partida. | HU-03 |
| RF-05 | El sistema debe admitir exactamente dos jugadores por partida. Al unirse el segundo, la partida pasa a "EN_JUEGO" y el turno es del primero que se unió. | HU-03 |
| RF-06 | El sistema debe rechazar una jugada si la partida no está "EN_JUEGO", si el jugador no pertenece a la partida, si no es su turno, si la cantidad está fuera del rango de 1 al máximo de la partida, o si es mayor que los fósforos restantes. | HU-04 |
| RF-07 | Ante una jugada válida, el sistema debe descontar los fósforos, registrar la jugada en el historial (jugador, cantidad, fósforos antes y después) y pasar el turno al otro jugador. | HU-04 |
| RF-08 | El sistema debe terminar la partida (estado "TERMINADA") cuando una jugada deja 0 fósforos: el jugador que hizo esa jugada pierde y el otro gana. | HU-05 |
| RF-09 | El sistema debe liberar a los jugadores de una partida terminada, de modo que puedan unirse a otra. | HU-05 |
| RF-10 | En una partida CONTRA_MAQUINA, el sistema debe agregar a la máquina como segundo jugador en cuanto se une el primer humano; la partida pasa a "EN_JUEGO" y el turno es del humano. | HU-06 |
| RF-11 | En una partida CONTRA_MAQUINA, después de cada jugada válida del humano que no termine la partida, el sistema debe jugar por la máquina la cantidad que elige MIN-MAX; si todas las jugadas pierden, la máquina saca 1. | HU-06 |
| RF-12 | El sistema debe permitir consultar el estado completo de una partida por su id: fósforos iniciales y restantes, máximo por turno, modo, estado, jugadores, turno, ganador, perdedor e historial. | HU-07 |
| RF-13 | El sistema debe notificar el estado actualizado de la partida a todos los que la siguen cada vez que se une un jugador o se acepta una jugada, y notificar el motivo del rechazo cuando una jugada se rechaza. | HU-07 |
| RF-14 | El sistema debe permitir crear un torneo con un id único, en estado "INSCRIPCION" y sin inscriptos, y entregarle a quien lo crea una clave de administración (F-02). | HU-08 |
| RF-15 | El sistema debe permitir que un alumno se inscriba en un torneo en estado "INSCRIPCION" indicando el id del torneo y su nombre, y rechazar la inscripción si ya hay un inscripto con ese nombre (sin distinguir mayúsculas) o si ese alumno ya está inscripto (F-02). | HU-09 |
| RF-16 | El sistema debe rechazar toda inscripción en un torneo que ya fue iniciado (F-02). | HU-09, HU-10 |
| RF-17 | Al iniciar el torneo, el sistema debe formar un equipo de un solo integrante por cada inscripto; ese integrante es el capitán del equipo (F-01, F-03, F-07). | HU-10 |
| RF-18 | El sistema debe permitir iniciar el torneo solo con la clave de administración y con al menos 2 inscriptos, y al iniciarlo debe armar la primera ronda (F-02, F-04). | HU-10 |
| RF-19 | Al armar una ronda, el sistema debe emparejar al azar a los participantes de a dos y crear una partida por pareja; todas las partidas de la ronda se juegan a la vez, con las reglas del juego base (F-01, F-04). | HU-11 |
| RF-20 | Si al armar una ronda la cantidad de participantes es impar, el sistema debe elegir al azar un equipo de alumnos que queda libre y hacerlo jugar contra la máquina (F-11). | HU-11 |
| RF-21 | Al armar una ronda, el sistema debe sortear una cantidad de fósforos entre 10 y 17 y un máximo por turno entre 2 y 5, iguales para todas las partidas de la ronda; la combinación no puede repetir la de la ronda anterior (F-12). | HU-11 |
| RF-22 | En cada partida de la ronda, el sistema debe dar el primer turno al equipo con menos integrantes; si tienen la misma cantidad, debe sortearlo. Contra la máquina, empieza el equipo (F-13). | HU-11 |
| RF-23 | En el turno de un equipo, el sistema debe permitir que cada integrante vote una cantidad de fósforos a sacar, y que cambie su voto mientras la votación está abierta, según la tabla «¿Se acepta un voto?» de `decision-table.md` (F-06). | HU-12 |
| RF-24 | El sistema debe cerrar la votación de cada jugada a los 30 segundos, o antes si ya votaron todos los integrantes del equipo (F-09, F-10, resuelto por el cliente). | HU-12 |
| RF-25 | Al cerrar la votación, el sistema debe jugar la cantidad más votada; si nadie votó, debe sacar 1 (F-06). | HU-12 |
| RF-26 | Si la votación termina empatada, el sistema debe jugar la cantidad que votó el capitán; si el capitán no votó ninguna de las empatadas, la menor de las empatadas (F-07, resuelto por el cliente). | HU-12 |
| RF-27 | El sistema debe mostrar a cada integrante del equipo la jugada que ganó la votación durante 2 segundos antes de aplicarla (F-20). | HU-12 |
| RF-28 | Al terminar una partida del torneo entre dos equipos de alumnos (que no sea la final), el sistema debe pasar a todos los integrantes del equipo perdedor al equipo ganador; el capitán del ganador no cambia (F-03). | HU-13 |
| RF-29 | El sistema debe dar por terminada una ronda cuando terminaron todas sus partidas y recién entonces armar la siguiente (F-05). | HU-14 |
| RF-30 | Cuando una ronda se arma con exactamente dos participantes, el sistema debe marcarla como la final (F-04). | HU-14 |
| RF-31 | Al terminar la final, el sistema debe dar por terminado el torneo y mostrar al ganador como campeón, con sus integrantes tal como llegaron a la final, sin sumar a los perdedores (F-15, resuelto por el cliente). | HU-16 |
| RF-32 | El sistema debe ofrecer una vista con todas las partidas de la ronda en curso, que se actualiza en cada cambio (F-14). | HU-15 |
| RF-33 | En la vista de la ronda, el sistema debe indicar, para cada partida en juego, qué equipo está en posición ganadora según MIN-MAX (F-14). | HU-15 |
| RF-34 | Si un equipo pierde contra la máquina, el sistema debe eliminarlo (sus integrantes dejan de jugar) y la máquina debe seguir en el torneo como un participante más, sin integrantes. La máquina participa de la ronda siguiente si ganó al menos una partida en la ronda que terminó (aclaración del cliente sobre F-11). | HU-13 |
| RF-35 | Si al terminar una ronda queda un solo participante, el sistema debe dar por terminado el torneo con ese participante como campeón. | HU-14 |
| RF-36 | Mientras el torneo está en inscripción, la vista del proyector (y la página de quien administra el torneo) debe mostrar un código QR con la dirección para inscribirse en ese torneo (pedido del cliente, 14/10/2026). | HU-09 |

## Fragmentos que NO son requerimientos funcionales

| Aspecto | Categoría | Justificación |
|---------|-----------|----------------|
| Alta de partidas y unión por REST (`/api/partidas`), jugadas por WebSocket/STOMP (`/ws`, `/app/partida/jugada`, `/topic/partida/{id}`) | Restricción técnica | Es la tecnología de comunicación elegida, no una función visible para el negocio. |
| MIN-MAX con memoización por (fósforos, máximo, turno) | Detalle técnico | Describe cómo elige la máquina, no el resultado observable, que ya está en RF-11. |
| Logging por consola (`System.out.println`) de eventos de partida | Dato de implementación | No es una función solicitable ni verificable como requerimiento de negocio. |
| Identidad del jugador guardada por pestaña del navegador (`sessionStorage`) | Detalle técnico | Permite probar con dos pestañas en la misma computadora; no es una regla de negocio. |
| Las librerías SockJS y STOMP se sirven desde el propio proyecto (`static/js/`) | Restricción técnica | Para que funcione sin internet en la red de la facultad. |
| La aplicación se sirve por HTTPS con un certificado autofirmado (`src/main/resources/keystore.p12`, configurado en `application.properties`) | Restricción técnica | Muchos celulares (Safari desde iOS 18.2) no abren páginas "http"; con HTTPS alcanza con aceptar una vez el aviso del navegador. No cambia ninguna función del juego. |
| El código QR se genera en el navegador con la librería qrcode-generator (MIT), guardada en `static/js/qrcode.js` | Restricción técnica | Igual que SockJS y STOMP: se sirve desde el proyecto para que funcione sin internet y sin instalar nada (F-16). |
| F-16 · «Lo tienen que hacer sobre el mismo proyecto y con el mismo WebSocket que ya usa el juego. No se puede instalar nada nuevo en las máquinas.» | Restricción técnica | Impone la plataforma (este proyecto, el WebSocket existente) y prohíbe instalar software; no describe una función. |
| F-17 · «Que sea rápido y que se pueda jugar cómodo desde el celular.» | Requisito no funcional (RNF) | Rendimiento y usabilidad sin métrica: «rápido» y «cómodo» no se pueden verificar con sí/no. |
| F-18 · «El objetivo es que todo el curso juegue por lo menos tres partidas en la clase.» | Objetivo/meta de negocio (KPI) | Es un resultado esperado de la clase («el objetivo es…»); el sistema contribuye, pero no es una función. |
| F-19 · «Hoy, cuando jugamos en el pizarrón, el que pierde se sienta y mira. Por eso se aburren.» | Dato de contexto | Describe el proceso manual actual; explica por qué existe F-03, pero no pide nada al sistema. |

## Conflictos u observaciones detectadas

- **Abandono y desconexión:** no hay forma de salir de una partida. Si
  un jugador cierra la página en medio de la partida, la partida queda
  "EN_JUEGO" para siempre y el otro jugador no puede unirse a ninguna
  otra. [NEEDS CLARIFICATION: definir qué pasa cuando un jugador
  abandona o se desconecta: ¿pierde, se cancela la partida, se espera
  un tiempo?]
- **Limpieza de memoria:** las partidas terminadas no se eliminan nunca;
  todo vive en memoria y se pierde al reiniciar. [NEEDS CLARIFICATION:
  ¿hace falta limpiar las partidas terminadas? ¿cuándo?]
- **Quién empieza:** siempre empieza el primero que se unió (RF-05). Con
  15 fósforos y máximo 3, el que empieza está en posición ganadora. Es
  el comportamiento actual y está confirmado para el juego base; el
  torneo puede definir otra regla.

### Torneo (RF-14 a RF-36)

Aclaraciones del cliente (14/10/2026), ya volcadas a los RF:

- **Desempate (RF-26):** decide el capitán (F-07); se descarta F-08.
- **Tiempo de votación (RF-24):** 30 segundos. No es ni el de F-09
  (20 s) ni el de F-10 (15 s); la votación se cierra antes si votaron
  todos.
- **Emparejamiento (RF-19):** al azar en todas las rondas.
- **Equipo libre (RF-20, RF-34):** se elige al azar; si pierde contra la
  máquina, queda eliminado y la máquina sigue en el torneo en su lugar.
- **Sorteo (RF-21):** fósforos entre 10 y 17, máximo entre 2 y 5; la
  combinación no repite la de la ronda anterior.
- **Votación (RF-23 a RF-25):** gana la más votada (mayoría simple), se
  puede cambiar el voto y, si nadie vota, se saca 1. Esto también
  resuelve el abandono: un equipo desconectado no traba la partida.
- **Campeón (RF-31):** se muestra antes de absorber a los perdedores
  de la final.
- **Equipos iniciales (RF-17):** cada inscripto arranca como un equipo
  de un integrante.
- **Valores por defecto aceptados por el cliente:** el torneo lo
  administra quien lo crea, con una clave guardada en su navegador
  (RF-14); no se aceptan nombres repetidos (RF-15); hacen falta 2
  inscriptos (RF-18); contra la máquina empieza el equipo (RF-22); la
  jugada elegida se muestra 2 segundos (RF-27); la vista del proyector
  se actualiza sola (RF-32).

Decisiones tomadas al especificar, para confirmar con el cliente:

- **RF-26:** si el capitán no votó ninguna de las cantidades empatadas,
  se juega la menor de las empatadas.
- **RF-34:** la máquina participa de la ronda siguiente si ganó al menos
  una partida en la ronda. Esto cubre el caso de una ronda en la que la
  máquina juega dos partidas: una como participante y otra contra el
  equipo libre.
- **RF-35:** como la máquina puede ganar varias partidas, puede quedar
  un único participante sin que haya habido final.

Otras observaciones:

- **F-18 (tres partidas por alumno):** con absorción, cada inscripto
  juega todas las rondas mientras su equipo no pierda contra la
  máquina. Con 8 inscriptos hay 3 rondas; con menos de 5 inscriptos no
  se llega a 3 partidas. No es un RF.
- **F-01 vs. F-12:** «las mismas reglas que el juego que ya tenemos» se
  interpreta como la regla de juego (pierde quien se lleva el último).
  Los valores de fósforos y máximo los fija F-12 por ronda.
