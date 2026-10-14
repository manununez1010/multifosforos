package com.multifosforos.multifosforos.config;

import com.multifosforos.multifosforos.dto.TorneoDTO;
import com.multifosforos.multifosforos.dto.TorneoMessage;
import com.multifosforos.multifosforos.service.MinimaxService;
import com.multifosforos.multifosforos.service.NotificadorTorneo;
import com.multifosforos.multifosforos.service.Programador;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Random;

/** Piezas del torneo: el reloj de las votaciones, el sorteo y el aviso por WebSocket (/topic/torneo/{id}). */
@Configuration
public class TorneoConfig {

    @Bean
    public Programador programador() {
        return new ProgramadorConScheduler();
    }

    @Bean
    public Random torneoRandom() {
        return new Random();
    }

    @Bean
    public NotificadorTorneo notificadorTorneo(SimpMessagingTemplate messagingTemplate, MinimaxService minimaxService) {
        return torneo -> messagingTemplate.convertAndSend("/topic/torneo/" + torneo.getId(),
                TorneoMessage.estado(TorneoDTO.desde(torneo, minimaxService)));
    }
}
