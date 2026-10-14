package com.multifosforos.multifosforos.controller;

import com.multifosforos.multifosforos.dto.CrearPartidaRequest;
import com.multifosforos.multifosforos.dto.GameMessage;
import com.multifosforos.multifosforos.dto.PartidaDTO;
import com.multifosforos.multifosforos.dto.UnirseRequest;
import com.multifosforos.multifosforos.model.ModoPartida;
import com.multifosforos.multifosforos.model.Partida;
import com.multifosforos.multifosforos.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Alta de partidas, unión de jugadores y consultas (REST). Las jugadas van por WebSocket. */
@RestController
@RequestMapping("/api/partidas")
public class PartidaController {

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    public PartidaController(GameService gameService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody(required = false) CrearPartidaRequest req) {
        try {
            ModoPartida modo = null;
            if (req != null && req.modo() != null && !req.modo().isBlank()) {
                modo = ModoPartida.valueOf(req.modo().toUpperCase());
            }
            Partida p = gameService.crearPartida(
                    req != null ? req.fosforos() : null,
                    req != null ? req.maxQuitar() : null,
                    modo);
            return ResponseEntity.ok(PartidaDTO.desde(p));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/jugadores")
    public ResponseEntity<?> unirse(@PathVariable String id, @RequestBody UnirseRequest req) {
        if (req == null || req.jugadorId() == null || req.nombre() == null || req.nombre().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Faltan el id o el nombre del jugador"));
        }
        if (!gameService.unirJugador(id, req.jugadorId(), req.nombre().trim())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "No te podés unir: la partida no existe, ya empezó o ya estás en otra"));
        }
        PartidaDTO dto = PartidaDTO.desde(gameService.obtenerPartida(id));
        messagingTemplate.convertAndSend("/topic/partida/" + id, GameMessage.estado(dto));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartidaDTO> obtener(@PathVariable String id) {
        Partida p = gameService.obtenerPartida(id);
        return p != null ? ResponseEntity.ok(PartidaDTO.desde(p)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/disponibles")
    public List<PartidaDTO> disponibles() {
        return gameService.listarDisponibles().stream().map(PartidaDTO::desde).toList();
    }

    @GetMapping
    public List<PartidaDTO> todas() {
        return gameService.listarPartidas().stream().map(PartidaDTO::desde).toList();
    }
}
