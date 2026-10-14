# Casos de prueba

> Derivados de `requirements.md`, `use-cases.md` y `decision-table.md`.
> Cada caso referencia el RF, el caso de uso o la regla que lo sustenta,
> y el test que lo implementa (columna "Test"). Los casos del torneo
> siguen la numeración (TC-30 en adelante).

## UC-01: Crear partida

| ID | Objetivo | Entrada | Resultado esperado | Referencia | Test |
|----|----------|---------|----------------------|------------|------|
| TC-01 | Crear sin indicar valores | `crearPartida(null, null, null)` | 15 fósforos, máximo 3, HUMANOS, "ESPERANDO", sin jugadores, id no nulo | RF-01 | `GameServiceTest.tc01_crearConValoresPorDefecto` |
| TC-02 | Crear con valores elegidos | `crearPartida(21, 4, CONTRA_MAQUINA)` | La partida tiene esos tres valores | RF-01 | `GameServiceTest.tc02_crearConValoresElegidos` |
| TC-03 | Fósforos fuera de rango | 4 y 51 (y los bordes 5 y 50) | 4 y 51 se rechazan; 5 y 50 se aceptan | RF-02 | `GameServiceTest.tc03_fosforosFueraDeRangoSeRechazan` |
| TC-04 | Máximo por turno fuera de rango | 1 y 6 (y los bordes 2 y 5) | 1 y 6 se rechazan; 2 y 5 se aceptan | RF-02 | `GameServiceTest.tc04_maxQuitarFueraDeRangoSeRechaza` |
| TC-05 | Ids únicos | 100 partidas | 100 ids distintos | RF-01 | `GameServiceTest.tc05_cadaPartidaTieneIdUnico` |

## UC-02: Unirse a partida

| ID | Objetivo | Precondición | Entrada | Resultado esperado | Referencia | Test |
|----|----------|---------------|---------|----------------------|------------|------|
| TC-06 | Se une el primer jugador | partida nueva | `unirJugador(id, "j1", "Ana")` | `true`; sigue "ESPERANDO" con 1 jugador | RF-04 | `GameServiceTest.tc06_primerJugadorSeUneYLaPartidaSigueEsperando` |
| TC-07 | Se une el segundo jugador | partida con 1 jugador | segundo jugador | "EN_JUEGO"; el turno es del primero | RF-05 | `PartidaTest.tc07_conElSegundoJugadorEmpiezaYJuegaElPrimero` |
| TC-08 | Tercer jugador | partida con 2 jugadores | tercer jugador | Rechazado; siguen 2 jugadores | RF-05 | `PartidaTest.tc08_unTercerJugadorEsRechazado` |
| TC-09 | Partida inexistente | ninguna | `unirJugador("no-existe", …)` | `false`; el jugador no queda en ninguna partida | RF-04 | `GameServiceTest.tc09_unirseAPartidaInexistente` |
| TC-10 | Jugador en otra partida sin terminar | "j1" en la partida A | unirse a la B | `false`; "j1" sigue solo en A | RF-04 | `GameServiceTest.tc10_jugadorEnOtraPartidaActivaNoSePuedeUnir` |
| TC-11 | El mismo jugador dos veces | "j1" ya en la partida | "j1" otra vez | Rechazado; sigue "ESPERANDO" | RF-04 | `PartidaTest.tc11_elMismoJugadorNoEntraDosVeces` |
| TC-12 | Listar disponibles | una en espera, una llena, una contra la máquina | `listarDisponibles()` | Solo la que está en espera entre humanos | RF-03 | `GameServiceTest.tc12_disponiblesSoloEsperandoYEntreHumanos` |

## UC-03: Realizar jugada (tabla de decisión)

| ID | Objetivo | Entrada | Resultado esperado | Referencia | Test |
|----|----------|---------|----------------------|------------|------|
| TC-13 | Partida que no está en juego | partida con 1 jugador | `PARTIDA_NO_EN_JUEGO`; no se descuenta nada | Regla 1 | `PartidaTest.tc13_regla1_partidaQueNoEstaEnJuego` |
| TC-14 | Jugador que no pertenece | `jugar("intruso", 1)` | `JUGADOR_NO_PERTENECE` | Regla 2 | `PartidaTest.tc14_regla2_jugadorQueNoPertenece` |
| TC-15 | Fuera de turno | juega "j2" cuando le toca a "j1" | `NO_ES_SU_TURNO`; no se descuenta nada | Regla 3 | `PartidaTest.tc15_regla3_noEsSuTurno` |
| TC-16 | Cantidad 0 | `jugar("j1", 0)` | `CANTIDAD_FUERA_DE_RANGO` | Regla 4 | `PartidaTest.tc16_regla4_cantidadCero` |
| TC-17 | Cantidad mayor al máximo | máximo 3, saca 4 | `CANTIDAD_FUERA_DE_RANGO` | Regla 4 | `PartidaTest.tc17_regla4_cantidadMayorAlMaximo` |
| TC-18 | Más que los restantes | quedan 2, saca 3 | `MAS_QUE_LOS_RESTANTES`; siguen 2 | Regla 5 | `PartidaTest.tc18_regla5_masQueLosRestantes` |
| TC-19 | Jugada válida | 15 fósforos, "j1" saca 2 | Quedan 13, turno de "j2", historial con (j1, 2, 15 → 13) | Regla 6, RF-07 | `PartidaTest.tc19_regla6_jugadaAceptadaDescuentaRegistraYPasaElTurno` |

## UC-04: Finalizar partida

| ID | Objetivo | Precondición | Resultado esperado | Referencia | Test |
|----|----------|---------------|----------------------|------------|------|
| TC-20 | Quien saca el último pierde | queda 1 fósforo | "TERMINADA"; pierde quien jugó, gana el otro; fecha de fin; nadie tiene el turno | RF-08, Regla A | `PartidaTest.tc20_quienSacaElUltimoPierde` |
| TC-21 | Terminada no acepta jugadas | partida terminada | `PARTIDA_NO_EN_JUEGO` para los dos | RF-08, Regla 1 | `PartidaTest.tc21_terminadaNoAceptaMasJugadas` |
| TC-22 | Jugadores liberados | partida terminada | Los dos se pueden unir a otra partida | RF-09 | `GameServiceTest.tc22_alTerminarLosJugadoresQuedanLibres` |

## UC-05: Respuesta de la máquina y MIN-MAX

| ID | Objetivo | Entrada | Resultado esperado | Referencia | Test |
|----|----------|---------|----------------------|------------|------|
| TC-23 | Empieza con el primer humano | partida CONTRA_MAQUINA, se une "j1" | "EN_JUEGO", 2 jugadores, el segundo es la máquina, turno de "j1" | RF-10 | `GameServiceTest.tc23_contraLaMaquinaEmpiezaConElPrimerHumano` |
| TC-24 | La máquina responde sola | "j1" saca 1 | 2 jugadas en el historial, la segunda de la máquina; vuelve a ser turno de "j1" | RF-11, Regla B | `GameServiceTest.tc24_laMaquinaRespondeEnElMismoLlamado` |
| TC-25 | La máquina juega bien | 15 fósforos, máximo 3; "j1" saca 1 | La máquina saca 1 y deja 13 | RF-11 | `GameServiceTest.tc25_laMaquinaDejaAlRivalEnPosicionPerdedora` |
| TC-26 | Posiciones perdedoras | n de 1 a 50, máximo k de 2 a 5 | Pierde el que tiene el turno exactamente cuando n ≡ 1 (mod k+1) | RF-11 | `MinimaxServiceTest.tc26_sePierdeExactamenteConNCongruenteA1ModuloKMas1` |
| TC-27 | Mejor jugada | mismos n y k | Saca (n−1) mod (k+1); si da 0, saca 1 | RF-11 | `MinimaxServiceTest.tc27_mejorJugadaDejaAlRivalEnPosicionPerdedora` |
| TC-28 | Costo con memoización | n = 50, k = 5 | A lo sumo 51 · 2 · 6 nodos visitados | RF-11 | `MinimaxServiceTest.tc28_conMemoLaCantidadDeNodosEsLineal` |
| TC-29 | La máquina no pierde desde una posición ganadora | 13 fósforos con máximo 3, y 16 con máximo 4; el humano empieza | Ninguna secuencia de jugadas del humano le gana | RF-11 | `GameServiceTest.tc29_desdePosicionPerdedoraElHumanoNuncaLeGanaALaMaquina` |

## Requerimientos sin test automático

- **RF-12 y RF-13** (consulta del estado y notificación en tiempo real)
  dependen del controlador y del WebSocket. Hoy se verifican a mano:
  abrir dos pestañas, crear la partida en una, unirse desde la otra y
  jugar (ver `README.md`).

---

# Torneo por absorción

## UC-06: Crear torneo

| ID | Objetivo | Entrada | Resultado esperado | Referencia | Test |
|----|----------|---------|----------------------|------------|------|
| TC-30 | Crear un torneo | `crearTorneo()` | Estado "INSCRIPCION", id y clave no nulos, sin inscriptos | RF-14 | `TorneoServiceTest.tc30_crearTorneoQuedaEnInscripcion` |
| TC-31 | Ids únicos | 100 torneos | 100 ids distintos | RF-14 | `TorneoServiceTest.tc31_cadaTorneoTieneIdUnico` |

## UC-07: Inscribirse en el torneo

| ID | Objetivo | Precondición | Entrada | Resultado esperado | Referencia | Test |
|----|----------|---------------|---------|----------------------|------------|------|
| TC-32 | Inscribirse en inscripción | torneo en "INSCRIPCION" | `inscribir(id, "j1", "Ana")` | Aceptado; "Ana" figura entre los inscriptos | RF-15 | `TorneoServiceTest.tc32_inscribirseEnInscripcion` |
| TC-33 | Inscribirse con el torneo iniciado | torneo en "EN_CURSO" | `inscribir(id, "j9", "Beto")` | Rechazado; los inscriptos no cambian | RF-16 | `TorneoServiceTest.tc33_noSePuedeInscribirConElTorneoIniciado` |
| TC-34 | Torneo inexistente | ninguna | `inscribir("no-existe", …)` | Rechazado | RF-15 | `TorneoServiceTest.tc34_inscribirseEnTorneoInexistente` |
| TC-35 | Nombre repetido | "Ana" ya inscripta | `inscribir(id, "j2", " ana ")` | Rechazado (sin distinguir mayúsculas ni espacios); sigue 1 inscripto | RF-15 | `TorneoServiceTest.tc35_nombreRepetidoSeRechaza` |
| TC-73 | El mismo alumno dos veces | "j1" ya inscripto | `inscribir(id, "j1", "Otra")` | Rechazado; sigue 1 inscripto | RF-15 | `TorneoServiceTest.tc73_elMismoAlumnoNoSeInscribeDosVeces` |

## UC-08: Iniciar torneo

| ID | Objetivo | Precondición | Entrada | Resultado esperado | Referencia | Test |
|----|----------|---------------|---------|----------------------|------------|------|
| TC-36 | Al iniciar, equipos de uno | 4 inscriptos | `iniciar(id, clave)` | "EN_CURSO"; 4 equipos de 1 integrante, que es su capitán | RF-16, RF-17 | `TorneoServiceTest.tc36_alIniciarCadaInscriptoEsUnEquipoDeUno` |
| TC-37 | Al iniciar, se arma la primera ronda | 4 inscriptos | `iniciar(id, clave)` | Ronda 1 con 2 partidas "EN_JUEGO" | RF-18, RF-19 | `TorneoServiceTest.tc37_alIniciarSeArmaLaPrimeraRonda` |
| TC-38 | Mínimo de inscriptos | 0, 1 y 2 inscriptos | `iniciar(id, clave)` | Con 0 y 1 se rechaza y sigue en "INSCRIPCION"; con 2 se inicia | RF-18 | `TorneoServiceTest.tc38_hacenFaltaDosInscriptosParaIniciar` |
| TC-39 | Iniciar dos veces | torneo "EN_CURSO" | `iniciar(id, clave)` | Rechazado; la ronda en curso no cambia | RF-16 | `TorneoServiceTest.tc39_noSePuedeIniciarDosVeces` |
| TC-71 | Iniciar sin la clave | 3 inscriptos | `iniciar(id, "otra")` y `iniciar(id, null)` | Rechazado; sigue en "INSCRIPCION" | RF-14, RF-18 | `TorneoServiceTest.tc71_soloConLaClaveSeIniciaElTorneo` |

## UC-09: Armar ronda

| ID | Objetivo | Precondición | Resultado esperado | Referencia | Test |
|----|----------|---------------|----------------------|------------|------|
| TC-40 | Emparejar de a dos | 6 equipos | 3 partidas; cada equipo en exactamente una | RF-19 | `TorneoServiceTest.tc40_seEmparejaDeADos` |
| TC-41 | Número impar de equipos | 5 equipos | 2 partidas entre equipos y 1 de un equipo contra la máquina | RF-20 | `TorneoServiceTest.tc41_conNumeroImparElLibreJuegaContraLaMaquina` |
| TC-42 | Mismos valores en toda la ronda | 6 equipos, 20 sorteos distintos | Las 3 partidas tienen los mismos fósforos (10 a 17) y el mismo máximo (2 a 5) | RF-21 | `TorneoServiceTest.tc42_todasLasPartidasDeLaRondaTienenLosMismosValores` |
| TC-43 | La combinación cambia respecto de la ronda anterior | sorteo que repetiría la combinación anterior; 1000 sorteos | Nunca sale la misma combinación que la anterior, y los valores quedan en rango | RF-21 | `TorneoServiceTest.tc43_laCombinacionCambiaRespectoDeLaRondaAnterior` |
| TC-44 | Empieza el equipo más chico | equipo de 1 contra equipo de 3 | El turno es del equipo de 1 | RF-22 | `TorneoServiceTest.tc44_empiezaElEquipoMasChico` |
| TC-45 | Mismo tamaño: sorteo | 100 partidas entre dos equipos de 1 | Empiezan los dos alguna vez | RF-22 | `TorneoServiceTest.tc45_conMismoTamanoSeSortea` |
| TC-46 | Contra la máquina empieza el equipo | equipo libre contra la máquina | El turno es del equipo | RF-22 | `TorneoServiceTest.tc46_contraLaMaquinaEmpiezaElEquipo` |
| TC-47 | Dos participantes: final | 2 equipos (y 4 como control) | Con 2 la ronda es la final; con 4, no | RF-30 | `TorneoServiceTest.tc47_conDosEquiposLaRondaEsLaFinal` |

## UC-10: Votar jugada (tabla «¿Se acepta un voto?»)

| ID | Objetivo | Entrada | Resultado esperado | Referencia | Test |
|----|----------|---------|----------------------|------------|------|
| TC-48 | Sin partida en juego | vota un integrante cuyo equipo ya terminó su partida, y un no inscripto | Rechazado: "No tenés una partida en juego" | Regla V1 | `TorneoServiceTest.tc48_v1_sinPartidaEnJuego` |
| TC-49 | No es el turno de su equipo | vota un integrante del equipo rival | Rechazado: "No es el turno de tu equipo" | Regla V2 | `TorneoServiceTest.tc49_v2_noEsElTurnoDeSuEquipo` |
| TC-50 | Votación cerrada | equipo de uno: vota (se cierra) y vuelve a votar mientras se muestra la jugada | Rechazado: "Votación cerrada" | Regla V3 | `TorneoServiceTest.tc50_v3_votacionCerrada` |
| TC-51 | Cambiar el voto | un integrante vota 1 y después 2 | Se acepta; queda un solo voto, de 2; la votación sigue abierta | Regla V6, RF-23 | `TorneoServiceTest.tc51_cambiarElVotoReemplazaAlAnterior` |
| TC-52 | Cantidad fuera de rango | máximo 2, vota 0 y vota 3 | Rechazado: "Fuera de rango" | Regla V4 | `TorneoServiceTest.tc52_v4_cantidadFueraDeRango` |
| TC-53 | Más que los restantes | queda 1, vota 2 | Rechazado: "Más que los restantes"; sigue quedando 1 | Regla V5 | `TorneoServiceTest.tc53_v5_masQueLosRestantes` |
| TC-54 | Voto válido | integrante del equipo en turno vota 1 | Voto registrado; los fósforos no cambian todavía | Regla V6, RF-23 | `TorneoServiceTest.tc54_v6_votoValidoSeRegistraSinDescontar` |

## UC-11: Resolver votación (tabla «¿Qué se juega al cerrar la votación?»)

| ID | Objetivo | Entrada | Resultado esperado | Referencia | Test |
|----|----------|---------|----------------------|------------|------|
| TC-55 | Mayoría clara | 3 votos: 2, 2, 1 | Se juega 2 | Regla R2, RF-25 | `VotacionTest.tc55_r2_mayoriaClara` |
| TC-56 | Equipo de un integrante | 1 voto: 3 | Se juega 3 | Regla R2, RF-25 | `VotacionTest.tc56_r2_equipoDeUnIntegrante` |
| TC-57 | Más votada sin mayoría absoluta | 5 votos: 1, 1, 2, 3, 4 | Se juega 1 (mayoría simple) | Regla R2, RF-25 | `VotacionTest.tc57_r2_ganaLaMasVotadaAunqueNoLlegueALaMitad` |
| TC-58 | Empate | capitán vota 2 en un empate 2-2; capitán vota 3 en un empate entre 1 y 2; el capitán no vota | Se juega 2; se juega 1; se juega la menor de las empatadas | Reglas R3 y R4, RF-26 | `VotacionTest.tc58_r3_empateDecideElCapitan` |
| TC-59 | Nadie votó | 0 votos al cierre (también vencido el tiempo en una partida real) | Se saca 1 | Regla R1, RF-25 | `VotacionTest.tc59_r1_siNadieVotaSeSaca1`, `TorneoServiceTest.tc59_r1_siNadieVotaAlVencerElTiempoSeSaca1` |
| TC-60 | Duración de la votación | vota uno de dos integrantes; después votan los dos | Con un voto se cierra recién a los 30 s; con todos los votos se cierra enseguida | RF-24 | `TorneoServiceTest.tc60_laVotacionDura30SegundosOMenosSiVotanTodos` |
| TC-61 | Se muestra antes de aplicar | equipo de uno vota 2 | La votación queda cerrada con "sacar 2" y los fósforos no cambian; a los 2 s se descuentan | RF-27 | `TorneoServiceTest.tc61_laJugadaElegidaSeMuestraAntesDeAplicarse` |

## UC-12: Cerrar partida del torneo

| ID | Objetivo | Precondición | Resultado esperado | Referencia | Test |
|----|----------|---------------|----------------------|------------|------|
| TC-62 | Absorción | termina una partida entre dos equipos de 1 | El ganador tiene 2 integrantes; el perdedor deja de participar y su integrante está en el equipo ganador | RF-28 | `TorneoServiceTest.tc62_elPerdedorSeSumaAlEquipoGanador` |
| TC-63 | Gana contra la máquina | 11 fósforos, máximo 3; el equipo libre juega perfecto | Gana el equipo, sigue igual; la máquina no participa de la ronda siguiente | RF-20, RF-34 | `TorneoServiceTest.tc63_siGanaContraLaMaquinaElEquipoSigueIgual` |
| TC-64 | Pierde contra la máquina | el equipo libre pierde | El equipo queda eliminado; la máquina participa de la ronda siguiente (la final) | RF-34 | `TorneoServiceTest.tc64_siPierdeContraLaMaquinaLaMaquinaSigueEnSuLugar` |
| TC-65 | La ronda espera | terminó 1 de 2 partidas | No se arma la siguiente ronda; la otra sigue en juego | RF-29 | `TorneoServiceTest.tc65_laRondaEsperaQueTerminenTodasSusPartidas` |
| TC-66 | Ronda siguiente | terminan las 2 partidas de una ronda de 4 equipos | Se arma la ronda 2: final entre dos equipos de 2 | RF-29, RF-30 | `TorneoServiceTest.tc66_alTerminarLaRondaSeArmaLaSiguiente` |

## UC-13: Mostrar campeón

| ID | Objetivo | Precondición | Resultado esperado | Referencia | Test |
|----|----------|---------------|----------------------|------------|------|
| TC-67 | Campeón antes de absorber | termina la final entre dos equipos de 2 | Torneo "TERMINADO"; el campeón es el ganador de la final, con sus 2 integrantes | RF-31 | `TorneoServiceTest.tc67_elCampeonSeMuestraConSusIntegrantesAntesDeAbsorber` |
| TC-68 | Recorrido completo | 8 inscriptos, todas las partidas se juegan | 3 rondas (4, 2 y 1 partidas); campeón de 4 integrantes; cada inscripto jugó 3 partidas (F-18) | RF-17 a RF-19, RF-28 a RF-31 | `TorneoServiceTest.tc68_recorridoCompletoConOchoInscriptos` |
| TC-72 | Un solo participante | 5 inscriptos; la máquina le gana a todos los equipos que enfrenta | Al terminar la ronda 2 queda solo la máquina: es la campeona | RF-35 | `TorneoServiceTest.tc72_siQuedaUnSoloParticipanteEsElCampeon` |

## UC-14: Ver la ronda en el proyector

| ID | Objetivo | Precondición | Resultado esperado | Referencia | Test |
|----|----------|---------------|----------------------|------------|------|
| TC-69 | Todas las partidas de la ronda | ronda con 3 partidas | La vista lista las 3, con equipos, fósforos restantes y estado | RF-32 | `TorneoServiceTest.tc69_laVistaMuestraTodasLasPartidasDeLaRonda` |
| TC-70 | Posición ganadora según MIN-MAX | 13 fósforos con máximo 5, y 11 con máximo 3; turno del equipo A | Con 13 (≡ 1 mod 6) se indica el equipo B; con 11 (≡ 3 mod 4), el A | RF-33 | `TorneoServiceTest.tc70_indicaQueEquipoEstaEnPosicionGanadora` |

## Requerimientos del torneo sin test automático

- **RF-36 (código QR)** es solo de la página: se verifica a mano
  abriendo `proyector.html` con el torneo en inscripción y escaneando el
  código con un celular, que tiene que abrir `torneo.html?id=…` de ese
  torneo.

- **RF-27 (que se vea en el celular) y RF-32 (que el proyector se
  actualice solo)** dependen del WebSocket y de las páginas. Los tests
  cubren el estado que se publica (TC-61, TC-69). Lo que se ve se
  verifica a mano: crear el torneo en una pestaña, inscribirse desde
  otras, abrir `proyector.html` e iniciar (ver `README.md`).
