package org.ospreyphoto.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Configures logging file path dynamically at runtime.
 * This must be initialized before Quarkus starts to ensure the log file path is set correctly.
 */
public class LoggingConfig {

    private static final String LOG_BASE_DIR = "logs";
    private static final String LOG_FILE_NAME = "ospreycompetition.log";

    /**
     * Initializes the logging configuration with a random directory.
     * This method should be called as early as possible in the application lifecycle.
     */
    public static void initialize() {
        try {
            // Generate a random number for the directory name
            Random random = new Random();
            int randomNumber = random.nextInt(1000000);
            
            // Create the log directory path with random number
            String logDirPath = LOG_BASE_DIR + "/run-" + randomNumber;
            Path logDir = Paths.get(logDirPath);
            
            // Create the directory if it doesn't exist
            Files.createDirectories(logDir);
            
            // Set the full log file path
            String logFilePath = logDirPath + "/" + LOG_FILE_NAME;
            
            // Set system property for Quarkus logging
            System.setProperty("quarkus.log.file.path", logFilePath);
            
            System.out.println("Logging initialized. Log file: " + logFilePath);
            
        } catch (IOException e) {
            System.err.println("Failed to initialize logging directory: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
