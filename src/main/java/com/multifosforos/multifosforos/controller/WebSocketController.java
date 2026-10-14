package com.multifosforos.multifosforos.controller;

import com.multifosforos.multifosforos.dto.GameMessage;
import com.multifosforos.multifosforos.dto.JugadaRequest;
import com.multifosforos.multifosforos.dto.PartidaDTO;
import com.multifosforos.multifosforos.model.Partida;
import com.multifosforos.multifosforos.model.ResultadoJugada;
import com.multifosforos.multifosforos.service.GameService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/** Jugadas en tiempo real. Entrada: /app/partida/jugada. Salida: /topic/partida/{id}. */
@Controller
public class WebSocketController {

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(GameService gameService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/partida/jugada")
    public void jugada(JugadaRequest req) {
        String topico = "/topic/partida/" + req.partidaId();
        ResultadoJugada resultado = gameService.jugar(req.jugadorId(), req.cantidad());
        if (resultado != ResultadoJugada.ACEPTADA) {
            messagingTemplate.convertAndSend(topico, GameMessage.error(req.jugadorId(), mensaje(resultado)));
            return;
        }
        Partida partida = gameService.obtenerPartida(req.partidaId());
        messagingTemplate.convertAndSend(topico, GameMessage.estado(PartidaDTO.desde(partida)));
    }

    private String mensaje(ResultadoJugada r) {
        return switch (r) {
            case PARTIDA_NO_EN_JUEGO -> "La partida no está en juego";
            case JUGADOR_NO_PERTENECE -> "No estás en esta partida";
            case NO_ES_SU_TURNO -> "No es tu turno";
            case CANTIDAD_FUERA_DE_RANGO -> "Cantidad fuera de rango";
            case MAS_QUE_LOS_RESTANTES -> "No quedan tantos fósforos";
            case ACEPTADA -> "";
        };
    }
}
