package com.multifosforos.multifosforos.config;

import com.multifosforos.multifosforos.service.Programador;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** Programador real: corre las tareas del torneo en un hilo aparte después de la demora. */
public class ProgramadorConScheduler implements Programador, DisposableBean {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    @Override
    public void programar(Runnable tarea, long demoraMs) {
        scheduler.schedule(() -> {
            try {
                tarea.run();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }, demoraMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void destroy() {
        scheduler.shutdownNow();
    }
}
