# Relevamiento: torneo de fósforos por absorción

> Material relevado en entrevistas con distintas personas, **tal como lo
> dijeron**. No es una especificación: hay fragmentos que no son
> requerimientos, huecos y contradicciones. Pasarlo a `specs/` con las
> convenciones de `skill/SKILL.md`.
>
> Cada fragmento tiene un id (F-01, F-02…) para poder citarlo desde los
> specs y desde las marcas `[NEEDS CLARIFICATION]`.

## Interlocutores

- **Docente:** dicta la materia y es el cliente del torneo.
- **Ayudante:** coordinó la prueba del torneo en papel.
- **Delegado:** representa a los alumnos del curso.
- **Coordinación técnica:** administra las computadoras y la red de la
  facultad.

## Fragmentos

**F-01 · Docente:** «Queremos un torneo en el que juegue todo el curso a
la vez. Cada partida es de a dos, con las mismas reglas que el juego que
ya tenemos.»

**F-02 · Docente:** «El torneo lo creo yo, y los alumnos se inscriben con
su nombre. Cuando lo inicio, ya no se puede inscribir nadie más.»

**F-03 · Docente:** «Cuando alguien pierde no queda afuera: se suma al
equipo del que le ganó. Así el equipo ganador va creciendo.»

**F-04 · Docente:** «Los equipos se siguen enfrentando entre sí, ronda
tras ronda, hasta que quedan dos. Esa es la final.»

**F-05 · Docente:** «Una ronda termina cuando terminaron todas sus
partidas. Recién ahí se arma la siguiente.»

**F-06 · Ayudante:** «En un equipo de varios, cada integrante vota
cuántos fósforos sacar, y se juega lo que vote la mayoría.»

**F-07 · Ayudante:** «Si hay empate en la votación, decide el capitán
del equipo, que es el que arrancó ganando.»

**F-08 · Delegado:** «Nosotros pensamos que si empatan, mejor que decida
la máquina con MIN-MAX. Así nadie se enoja con el capitán.»

**F-09 · Ayudante:** «Para votar hay un tiempo fijo: 20 segundos por
jugada.»

**F-10 · Delegado:** «En la práctica, con 15 segundos alcanza. En la
prueba en papel usamos 15 y anduvo bien.»

**F-11 · Docente:** «Si en una ronda queda un número impar de equipos,
el que queda libre juega contra la máquina.»

**F-12 · Docente:** «Cada ronda arranca con una cantidad de fósforos y un
máximo por turno sorteados, distintos en cada ronda, para que nadie
juegue de memoria con la fórmula del módulo.»

**F-13 · Ayudante:** «Quiero que empiece cada partida el equipo que
tiene menos integrantes, para compensar. Si tienen los mismos, que sea
por sorteo.»

**F-14 · Docente:** «En la pantalla del proyector queremos ver todas las
partidas de la ronda y, en cada una, qué equipo está en posición
ganadora según MIN-MAX. Es la parte que más me interesa para la clase.»

**F-15 · Docente:** «Al final se muestra el equipo campeón con todos sus
integrantes.»

**F-16 · Coordinación técnica:** «Lo tienen que hacer sobre el mismo
proyecto y con el mismo WebSocket que ya usa el juego. No se puede
instalar nada nuevo en las máquinas.»

**F-17 · Delegado:** «Que sea rápido y que se pueda jugar cómodo desde
el celular.»

**F-18 · Docente:** «El objetivo es que todo el curso juegue por lo menos
tres partidas en la clase.»

**F-19 · Ayudante:** «Hoy, cuando jugamos en el pizarrón, el que pierde
se sienta y mira. Por eso se aburren.»

**F-20 · Ayudante:** «Cada integrante ve en su celular la jugada que
ganó la votación antes de que se aplique.»
