package com.multifosforos.multifosforos.controller;

import com.multifosforos.multifosforos.dto.IniciarTorneoRequest;
import com.multifosforos.multifosforos.dto.InscripcionRequest;
import com.multifosforos.multifosforos.dto.TorneoDTO;
import com.multifosforos.multifosforos.model.ResultadoInicio;
import com.multifosforos.multifosforos.model.ResultadoInscripcion;
import com.multifosforos.multifosforos.model.Torneo;
import com.multifosforos.multifosforos.service.MinimaxService;
import com.multifosforos.multifosforos.service.TorneoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Torneo por REST: crear, inscribirse, iniciar y consultar. Los votos van por WebSocket. */
@RestController
@RequestMapping("/api/torneos")
public class TorneoController {

    private final TorneoService torneoService;
    private final MinimaxService minimaxService;

    public TorneoController(TorneoService torneoService, MinimaxService minimaxService) {
        this.torneoService = torneoService;
        this.minimaxService = minimaxService;
    }

    /** Devuelve el torneo y la clave para administrarlo (solo la recibe quien lo crea). */
    @PostMapping
    public Map<String, Object> crear() {
        Torneo t = torneoService.crearTorneo();
        return Map.of("torneo", TorneoDTO.desde(t, minimaxService), "claveAdmin", t.getClaveAdmin());
    }

    @GetMapping
    public List<TorneoDTO> todos() {
        return torneoService.listarTorneos().stream().map(t -> TorneoDTO.desde(t, minimaxService)).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TorneoDTO> obtener(@PathVariable String id) {
        Torneo t = torneoService.obtenerTorneo(id);
        return t != null ? ResponseEntity.ok(TorneoDTO.desde(t, minimaxService)) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/inscriptos")
    public ResponseEntity<?> inscribir(@PathVariable String id, @RequestBody InscripcionRequest req) {
        if (req == null || req.jugadorId() == null || req.nombre() == null || req.nombre().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Faltan el id o el nombre"));
        }
        ResultadoInscripcion r = torneoService.inscribir(id, req.jugadorId(), req.nombre());
        return switch (r) {
            case ACEPTADA -> ResponseEntity.ok(TorneoDTO.desde(torneoService.obtenerTorneo(id), minimaxService));
            case TORNEO_INEXISTENTE -> error(HttpStatus.NOT_FOUND, "El torneo no existe");
            case TORNEO_INICIADO -> error(HttpStatus.CONFLICT, "El torneo ya empezó: no se aceptan más inscripciones");
            case YA_INSCRIPTO -> error(HttpStatus.CONFLICT, "Ya estás inscripto");
            case NOMBRE_REPETIDO -> error(HttpStatus.CONFLICT, "Ya hay alguien inscripto con ese nombre");
        };
    }

    @PostMapping("/{id}/iniciar")
    public ResponseEntity<?> iniciar(@PathVariable String id, @RequestBody IniciarTorneoRequest req) {
        ResultadoInicio r = torneoService.iniciar(id, req != null ? req.claveAdmin() : null);
        return switch (r) {
            case INICIADO -> ResponseEntity.ok(TorneoDTO.desde(torneoService.obtenerTorneo(id), minimaxService));
            case TORNEO_INEXISTENTE -> error(HttpStatus.NOT_FOUND, "El torneo no existe");
            case CLAVE_INCORRECTA -> error(HttpStatus.FORBIDDEN, "Solo quien creó el torneo lo puede iniciar");
            case YA_INICIADO -> error(HttpStatus.CONFLICT, "El torneo ya empezó");
            case POCOS_INSCRIPTOS -> error(HttpStatus.CONFLICT, "Hacen falta al menos " + Torneo.MIN_INSCRIPTOS + " inscriptos");
        };
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(Map.of("error", mensaje));
    }
}
