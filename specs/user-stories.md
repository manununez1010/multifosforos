# Historias de usuario

> Cada historia se vincula con los requerimientos funcionales de
> `requirements.md` (columna "Historia(s) de usuario").
>
> Estas historias cubren el **juego base** (una partida de a dos). El
> torneo todavía no está especificado: el material relevado está en
> `relevamiento/torneo.md`. Las historias nuevas siguen la numeración
> (HU-08 en adelante).

## HU-01: Crear una partida

**Como** jugador **quiero** crear una partida eligiendo cuántos fósforos
hay y cuántos se pueden sacar por turno **para** jugar con las reglas que
quiera.

**Criterios de aceptación:**
- Si no elijo nada, la partida tiene 15 fósforos, se saca de 1 a 3 y es
  entre dos personas.
- Puedo elegir entre 5 y 50 fósforos y un máximo por turno entre 2 y 5.
  Valores fuera de esos rangos se rechazan.
- Puedo elegir jugar contra otra persona o contra la máquina.
- La partida recibe un id único que puedo compartir.

**Requerimientos:** RF-01, RF-02

## HU-02: Ver partidas disponibles

**Como** jugador **quiero** ver las partidas que esperan rival **para**
unirme sin que me pasen el id.

**Criterios de aceptación:**
- Se listan solo las partidas entre personas que están en "ESPERANDO".

**Requerimientos:** RF-03

## HU-03: Unirme a una partida

**Como** jugador **quiero** unirme a una partida con mi nombre **para**
jugar contra otra persona.

**Criterios de aceptación:**
- Me uno indicando el id de partida, mi id de jugador y mi nombre.
- No me puedo unir si ya estoy en otra partida que no terminó.
- Una partida tiene exactamente dos jugadores. Con el segundo, la partida
  pasa a "EN_JUEGO" y empieza el primero que entró.

**Requerimientos:** RF-04, RF-05

## HU-04: Sacar fósforos

**Como** jugador **quiero** sacar fósforos en mi turno **para** dejar al
rival en una mala posición.

**Criterios de aceptación:**
- Solo puedo jugar en mi turno y en una partida en juego.
- Saco entre 1 y el máximo de la partida, y nunca más de los que quedan.
- Después de mi jugada le toca al rival, y la jugada queda en el
  historial.

**Requerimientos:** RF-06, RF-07

## HU-05: Terminar la partida

**Como** jugador **quiero** saber quién ganó **para** cerrar la partida
y poder jugar otra.

**Criterios de aceptación:**
- Pierde quien se lleva el último fósforo; gana el otro.
- Una partida terminada no acepta más jugadas.
- Al terminar, los dos jugadores quedan libres para unirse a otra
  partida.

**Requerimientos:** RF-08, RF-09

## HU-06: Jugar contra la máquina

**Como** jugador **quiero** jugar contra la máquina **para** practicar
contra un rival que juega perfecto.

**Criterios de aceptación:**
- Al unirme a una partida contra la máquina, la partida empieza
  enseguida y juego yo primero.
- Después de cada jugada mía, la máquina responde sola con la jugada que
  elige MIN-MAX.
- Si la máquina está en posición perdedora, saca 1 fósforo.

**Requerimientos:** RF-10, RF-11

## HU-07: Seguir la partida en tiempo real

**Como** jugador **quiero** ver cada jugada apenas ocurre **para** no
tener que recargar la página.

**Criterios de aceptación:**
- Puedo consultar el estado completo de una partida por su id.
- Cada cambio en la partida (alguien se une, alguien juega, termina) les
  llega a todos los que la están mirando.
- Si mi jugada se rechaza, me llega el motivo.

**Requerimientos:** RF-12, RF-13

---

# Torneo por absorción

> Historias derivadas de `relevamiento/torneo.md` y de las respuestas
> del cliente del 14/10/2026. Cada criterio cita los fragmentos (F-xx)
> de los que sale.

## HU-08: Crear un torneo

**Como** docente **quiero** crear un torneo **para** que todo el curso
se inscriba y juegue a la vez.

**Criterios de aceptación:**
- El torneo se crea en estado de inscripción, con un id único que puedo
  compartir con el curso (F-02).
- Quien crea el torneo es quien lo administra: recibe una clave que
  queda guardada en su navegador, y solo con esa clave se puede iniciar
  (F-02, aclaración del cliente).

**Requerimientos:** RF-14

## HU-09: Inscribirme en el torneo

**Como** alumno **quiero** inscribirme en el torneo con mi nombre
**para** participar.

**Criterios de aceptación:**
- Me inscribo indicando el id del torneo y mi nombre, mientras el
  torneo está en inscripción (F-02).
- No se aceptan dos inscriptos con el mismo nombre (sin distinguir
  mayúsculas), ni el mismo alumno dos veces.
- Una vez iniciado el torneo, nadie más se puede inscribir (F-02).
- Puedo entrar al torneo escaneando un código QR que se muestra en el
  proyector, sin escribir la dirección (pedido del cliente, 14/10/2026).

**Requerimientos:** RF-15, RF-16, RF-36

## HU-10: Iniciar el torneo

**Como** docente **quiero** iniciar el torneo **para** cerrar la
inscripción y arrancar la primera ronda.

**Criterios de aceptación:**
- Hacen falta al menos 2 inscriptos para iniciar.
- Al iniciar, se cierra la inscripción (F-02).
- Cada inscripto arranca como un equipo de un solo integrante, que es
  su capitán (F-01, F-03; confirmado por el cliente).
- Al iniciar, se arma la primera ronda (F-04).

**Requerimientos:** RF-16, RF-17, RF-18

## HU-11: Jugar una ronda

**Como** equipo **quiero** que cada ronda me asigne un rival **para**
seguir jugando.

**Criterios de aceptación:**
- Los equipos se emparejan al azar en cada ronda. Las partidas de la
  ronda se juegan a la vez, de a dos, con las reglas del juego base:
  pierde quien se lleva el último (F-01, F-04).
- Todas las partidas de la ronda usan la misma cantidad de fósforos
  (entre 10 y 17) y el mismo máximo por turno (entre 2 y 5), sorteados.
  La combinación no se repite respecto de la ronda anterior (F-12).
- Si el número de participantes es impar, un equipo de alumnos elegido
  al azar queda libre y juega contra la máquina (F-11).
- Empieza el equipo con menos integrantes; si tienen los mismos, se
  sortea. Contra la máquina empieza siempre el equipo (F-13).

**Requerimientos:** RF-19, RF-20, RF-21, RF-22

## HU-12: Votar la jugada de mi equipo

**Como** integrante de un equipo **quiero** votar cuántos fósforos sacar
**para** decidir la jugada junto con mi equipo.

**Criterios de aceptación:**
- Solo voto en el turno de mi equipo y mientras la votación está
  abierta (F-06).
- Voto una cantidad que sería una jugada válida: entre 1 y el máximo, y
  no más que los fósforos restantes (F-01, F-06).
- Puedo cambiar mi voto mientras la votación está abierta.
- La votación dura 30 segundos, o termina antes si ya votaron todos los
  integrantes (aclaración del cliente: reemplaza los 20 s de F-09 y los
  15 s de F-10).
- Se juega la cantidad más votada; si nadie votó, se saca 1 (F-06).
- Si hay empate, decide el capitán (F-07).
- Antes de que se aplique, veo durante 2 segundos la jugada que ganó la
  votación (F-20).

**Requerimientos:** RF-23, RF-24, RF-25, RF-26, RF-27

## HU-13: Sumarme al equipo que me ganó

**Como** alumno que perdió una partida **quiero** sumarme al equipo que
me ganó **para** seguir jugando en lugar de quedarme mirando (F-03,
F-19).

**Criterios de aceptación:**
- Al terminar una partida entre dos equipos, todos los integrantes del
  equipo perdedor pasan al equipo ganador (F-03).
- Si un equipo pierde contra la máquina, queda eliminado y la máquina
  sigue en el torneo en su lugar (aclaración del cliente).

**Requerimientos:** RF-28, RF-34

## HU-14: Avanzar ronda por ronda hasta la final

**Como** docente **quiero** que las rondas se armen solas **para** que
el torneo avance hasta la final sin intervenir.

**Criterios de aceptación:**
- Una ronda termina cuando terminaron todas sus partidas; recién ahí se
  arma la siguiente (F-05).
- Cuando quedan dos participantes, esa ronda es la final (F-04).
- Si al terminar una ronda queda un solo participante, ese es el
  campeón.

**Requerimientos:** RF-29, RF-30, RF-35

## HU-15: Seguir la ronda en el proyector

**Como** docente **quiero** ver en el proyector todas las partidas de la
ronda y qué equipo está en posición ganadora según MIN-MAX **para**
usarlo en la clase (F-14).

**Criterios de aceptación:**
- Se ven todas las partidas de la ronda en curso, y la vista se
  actualiza sola en cada cambio.
- En cada partida en juego se indica qué equipo está en posición
  ganadora según MIN-MAX.

**Requerimientos:** RF-32, RF-33

## HU-16: Ver al equipo campeón

**Como** curso **queremos** ver al equipo campeón con todos sus
integrantes **para** cerrar el torneo (F-15).

**Criterios de aceptación:**
- Al terminar la final se muestra el equipo campeón con todos sus
  integrantes, tal como llegó a la final: sin sumar a los perdedores de
  la final (aclaración del cliente).
- Si el campeón es la máquina, se muestra la máquina.

**Requerimientos:** RF-31
