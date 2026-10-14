package com.multifosforos.multifosforos.controller;

import com.multifosforos.multifosforos.dto.TorneoMessage;
import com.multifosforos.multifosforos.dto.VotoRequest;
import com.multifosforos.multifosforos.model.ResultadoVoto;
import com.multifosforos.multifosforos.service.TorneoService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/** Votos en tiempo real. Entrada: /app/torneo/voto. Salida: /topic/torneo/{id}. */
@Controller
public class TorneoWebSocketController {

    private final TorneoService torneoService;
    private final SimpMessagingTemplate messagingTemplate;

    public TorneoWebSocketController(TorneoService torneoService, SimpMessagingTemplate messagingTemplate) {
        this.torneoService = torneoService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/torneo/voto")
    public void voto(VotoRequest req) {
        ResultadoVoto r = torneoService.votar(req.torneoId(), req.jugadorId(), req.cantidad());
        if (r != ResultadoVoto.ACEPTADO) {
            messagingTemplate.convertAndSend("/topic/torneo/" + req.torneoId(),
                    TorneoMessage.error(req.jugadorId(), mensaje(r)));
        }
    }

    private String mensaje(ResultadoVoto r) {
        return switch (r) {
            case SIN_PARTIDA_EN_JUEGO -> "No tenés una partida en juego";
            case NO_ES_TURNO_DE_SU_EQUIPO -> "No es el turno de tu equipo";
            case VOTACION_CERRADA -> "La votación está cerrada";
            case CANTIDAD_FUERA_DE_RANGO -> "Cantidad fuera de rango";
            case MAS_QUE_LOS_RESTANTES -> "No quedan tantos fósforos";
            case ACEPTADO -> "";
        };
    }
}
