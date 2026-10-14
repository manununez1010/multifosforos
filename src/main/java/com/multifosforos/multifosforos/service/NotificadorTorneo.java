package com.multifosforos.multifosforos.service;

import com.multifosforos.multifosforos.model.Torneo;

/** Avisa que cambió el estado de un torneo (inscripción, votos, jugadas, rondas). */
public interface NotificadorTorneo {
    void estadoCambiado(Torneo torneo);
}
