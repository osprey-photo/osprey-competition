package org.ospreyphoto.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.management.RuntimeErrorException;

import org.ospreyphoto.model.CompetitionSettings;
import org.ospreyphoto.restendpoint.ImageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AppState {
    @Inject
    Config cfg;
    final Logger logger = LoggerFactory.getLogger(ImageResource.class);

    CompetitionSettings settings;

    public CompetitionSettings getSettings() {
        if (this.settings == null) {
            try {
                load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return this.settings;
    }

    private File getFile() throws IOException {
        var home = System.getProperty("user.home");
        Path p = Paths.get(home, ".osprey", "ospreycompetition");
        Files.createDirectories(p);

        File f =  p.resolve("data.json").toFile();
        if (!f.exists()){
            logger.info("Settings does not exist, creating");
            settings=CompetitionSettings.defaults();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(f, settings);
        }
        return f;
    }

 
    private void load() throws StreamReadException, DatabindException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        this.settings = mapper.readValue(getFile(), CompetitionSettings.class);
       
    }

    public AppState updateSettings(CompetitionSettings settings) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            this.settings = settings;
            logger.info("Writing "+ mapper.writeValueAsString(settings));
            mapper.writeValue(getFile(), settings);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return this;
    }
}
