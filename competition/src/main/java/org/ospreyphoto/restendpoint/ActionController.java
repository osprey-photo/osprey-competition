package org.ospreyphoto.restendpoint;

import org.ospreyphoto.config.AppState;
import org.ospreyphoto.config.Config;
import org.ospreyphoto.model.Action;
import org.ospreyphoto.model.Competition;
import org.ospreyphoto.model.CompetitionSettings;
import org.ospreyphoto.nativeui.Controller;
import org.ospreyphoto.nativeui.UIMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import javafx.application.Platform;
import javafx.concurrent.Task;

@Path("/action")
public class ActionController {
    final Logger logger = LoggerFactory.getLogger(ImageResource.class);

    @Inject
    Config cfg;

    @Inject
    EventBus bus;

    @Inject
    AppState appstate;

    @Inject
    UIMain nativeui;

    @POST
    @Path("/persistconfig")
    @Consumes(MediaType.APPLICATION_JSON)
    public String persistConfig(CompetitionSettings settinigs) {
        try {

            appstate.updateSettings(settinigs);

            ObjectMapper mapper = new ObjectMapper();
            var actionJson = mapper.writeValueAsString(settinigs);
            logger.info(actionJson);
            return "done";
        } catch (JsonProcessingException e) {
            logger.error("JSON processing exception ", e);
            throw new WebApplicationException("JSON Processing exception ", e);
        }
    }

    @GET
    @Path("/persistconfig")
    @Produces(MediaType.APPLICATION_JSON)
    public CompetitionSettings getConfig() {
        return appstate.getSettings();
    }

    @GET
    @Path("/imagesrc")
    public Uni<String> initCatalog() {
        logger.info("Requesting img src");

        final Task<String> dirChoice = new Task<String>() {
            public String call() throws Exception {
                String dir = nativeui.fileDialog();
                return dir;
            }
        };
        Platform.runLater(dirChoice);

        return Uni.createFrom().future(dirChoice);
    }

    @GET
    @Path("/currentcomp")
    public String currentComp(){
        return appstate.getCurrentCompetition();
    }

    
    @POST
    @Path("/currentcomp")
    public void setCurrentComp(String name){
        appstate.setCurrentCompetition(name);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void doAction(Action action) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            var actionJson = mapper.writeValueAsString(action);
            logger.info("[action] {}", actionJson);

            bus.<String>publish("displayControl", actionJson);
        } catch (JsonProcessingException e) {
            logger.error("JSON processing exception ", e);
            throw new WebApplicationException("JSON Processing exception ", e);
        }
    }

}
