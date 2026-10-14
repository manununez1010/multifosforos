# multifosforos

Juego de los fósforos multijugador en tiempo real. Dos personas (o una
persona contra la máquina) sacan por turno entre 1 y un máximo de
fósforos de una pila; **pierde quien se lleva el último**. La máquina
juega con MIN-MAX (clase 11 de Programación III).

- **Backend:** Spring Boot (Java 17). REST para crear partidas y unirse;
  WebSocket/STOMP para las jugadas.
- **Frontend:** HTML/JS plano, servido como recursos estáticos. SockJS y
  STOMP vienen incluidos en `static/js/`, así que funciona sin internet.

## Requisitos

- Java 17
- Maven (el wrapper `mvnw` necesita conexión a internet la primera vez;
  si no hay red, usar el `mvn` instalado en el sistema)

## Cómo compilar, correr y testear

```bash
mvn compile            # compilar
mvn test               # correr los tests (76)
mvn spring-boot:run    # levantar la aplicación
```

La aplicación queda disponible en <https://localhost:8080>. Para jugar
desde otra computadora o el celular, entrar a `https://<IP-de-la-PC>:8080`
(puede hacer falta habilitar el puerto 8080 en el firewall).
La aplicación usa HTTPS con un certificado propio: la primera vez, el
navegador avisa que la conexión no es privada y hay que tocar «Visitar
sitio igualmente» (o «Avanzado → Continuar»).

## Cómo jugar

1. Abrir <https://localhost:8080> (`index.html`).
2. «Crear partida» (`crear.html`): nombre, fósforos (5 a 50), máximo por
   turno (2 a 5) y rival (otra persona o la máquina).
3. El rival entra por «Unirse a una partida» (`unirse.html`), eligiendo
   la partida de la lista o pegando el id.
4. En `juego.html`, en tu turno, elegís cuántos sacar.

Para probar solo, abrí dos pestañas: cada pestaña es un jugador
distinto.

## Cómo jugar el torneo

1. El docente entra a «Torneo» (`torneo.html`) y toca «Crear torneo».
   Ese navegador queda como administrador del torneo.
2. Abre «Ver proyector» (`proyector.html?id=…`) en la pantalla grande:
   ahí aparece la dirección para inscribirse.
3. Cada alumno entra desde el celular, pone su nombre y se inscribe.
4. Con al menos 2 inscriptos, el docente toca «Iniciar torneo».
5. En cada partida, cuando le toca a su equipo, cada integrante vota
   cuántos fósforos sacar (30 segundos, o hasta que voten todos). Se
   juega la más votada; si hay empate, decide el capitán (★); si nadie
   vota, se saca 1.
6. Quien pierde se suma al equipo que le ganó. Si el número de equipos
   es impar, uno juega contra la máquina; si pierde, queda eliminado y
   la máquina sigue en el torneo. Con dos participantes se juega la
   final, y el proyector muestra al campeón.

Para probar solo: una pestaña crea el torneo y cada pestaña nueva es un
alumno distinto.

## Estructura

```
src/main/java/com/multifosforos/multifosforos/
  model/        Partida, Jugador, Jugada, EstadoPartida, ModoPartida, ResultadoJugada
                Torneo, Equipo, Inscripto, Ronda, PartidaTorneo, Votacion (torneo)
  service/      GameService (partidas y jugadores), MinimaxService (MIN-MAX),
                TorneoService (torneo por absorción)
  controller/   PartidaController (REST /api/partidas), WebSocketController (/app/partida/jugada),
                TorneoController (REST /api/torneos), TorneoWebSocketController (/app/torneo/voto)
  config/       WebSocketConfig (endpoint STOMP /ws), TorneoConfig (reloj, sorteo, avisos)
  dto/          Forma de los pedidos y mensajes
src/main/resources/static/   Páginas HTML del juego
src/test/                    Tests unitarios (cada test lleva el id de su caso de prueba)
specs/                       Especificación funcional del juego base
relevamiento/                Material relevado para el torneo (todavía sin especificar)
skill/                       Convenciones para escribir specs
```

## API

| Método | Ruta | Qué hace |
|--------|------|----------|
| POST | `/api/partidas` | Crea una partida. Cuerpo: `{"fosforos": 15, "maxQuitar": 3, "modo": "HUMANOS"}` (todo opcional) |
| POST | `/api/partidas/{id}/jugadores` | Une un jugador. Cuerpo: `{"jugadorId": "…", "nombre": "…"}` |
| GET | `/api/partidas/{id}` | Estado de la partida |
| GET | `/api/partidas/disponibles` | Partidas entre humanos que esperan rival |
| GET | `/api/partidas` | Todas las partidas |
| STOMP SEND | `/app/partida/jugada` | `{"partidaId": "…", "jugadorId": "…", "cantidad": 2}` |
| STOMP SUBSCRIBE | `/topic/partida/{id}` | Mensajes `{"tipo": "ESTADO", "partida": …}` o `{"tipo": "ERROR", "jugadorId": …, "error": …}` |
| POST | `/api/torneos` | Crea un torneo. Devuelve `{"torneo": …, "claveAdmin": "…"}` |
| GET | `/api/torneos` / `/api/torneos/{id}` | Todos los torneos / estado de uno |
| POST | `/api/torneos/{id}/inscriptos` | Inscribe a un alumno. Cuerpo: `{"jugadorId": "…", "nombre": "…"}` |
| POST | `/api/torneos/{id}/iniciar` | Inicia el torneo. Cuerpo: `{"claveAdmin": "…"}` |
| STOMP SEND | `/app/torneo/voto` | `{"torneoId": "…", "jugadorId": "…", "cantidad": 2}` |
| STOMP SUBSCRIBE | `/topic/torneo/{id}` | Mensajes `{"tipo": "ESTADO", "torneo": …}` o `{"tipo": "ERROR", "jugadorId": …, "error": …}` |

## Especificación

En `specs/`:

| Archivo | Contenido |
|---------|-----------|
| `user-stories.md` | Historias de usuario con criterios de aceptación |
| `requirements.md` | Requerimientos funcionales, vinculados a las historias |
| `use-cases.md` | Casos de uso |
| `decision-table.md` | Reglas de validación de una jugada y qué pasa después |
| `test-cases.md` | Casos de prueba, cada uno vinculado a su test |

La actividad del torneo está en `ACTIVIDAD.md`.
