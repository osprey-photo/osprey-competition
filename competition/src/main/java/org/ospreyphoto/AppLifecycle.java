package org.ospreyphoto;

import org.ospreyphoto.nativeui.UIMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.application.Platform;


@Singleton
public class AppLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppLifecycle.class);

    @Inject
    UIMain uimain;

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("The application is starting...");
       
        Platform.startup(new Runnable() {
            @Override
            public void run() {
                uimain.start();
            }

        });

    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application is stopping...");
    }
}