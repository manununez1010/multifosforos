package com.multifosforos.multifosforos.service;

/** Programa una tarea para dentro de un rato (cierre de la votación, RF-24; muestra de la jugada, RF-27). */
public interface Programador {
    void programar(Runnable tarea, long demoraMs);
}
