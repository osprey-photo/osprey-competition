package org.ospreyphoto;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logmanager.LogManager;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Main {

    public static void main(String... args) {
        System.out.println("Running main method");


            // Logger logger = Logger.getGlobal();
            // logger.addHandler(new FileHandler("c:\\Users\\matth\\log.log"));
            // LogManager.getLogManager().addLogger(logger);

            Quarkus.run(args);
      

    }

}