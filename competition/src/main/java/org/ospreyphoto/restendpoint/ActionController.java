package org.ospreyphoto.restendpoint;

import org.ospreyphoto.config.AppState;
import org.ospreyphoto.config.Config;
import org.ospreyphoto.model.Action;
import org.ospreyphoto.model.CompetitionSettings;
import org.ospreyphoto.nativeui.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;

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
    Controller nativeui;

    @POST
    @Path("/persistconfig")
    @Consumes(MediaType.APPLICATION_JSON)
    public String persistConfig(CompetitionSettings settinigs) {
        try {

            appstate.updateSettings(settinigs);

            ObjectMapper mapper = new ObjectMapper();
            var actionJson = mapper.writeValueAsString(settinigs);
            logger.info(actionJson);
            logger.info(appstate.getSettings().imageSrc);
            return "done";
        } catch (JsonProcessingException e) {
            logger.error("JSON processing exception ", e);
            throw new WebApplicationException("JSON Processing exception ", e);
        }
    }

    @GET
    @Path("/imagesrc")
    public Uni<String> initCatalog() {
        logger.info("Requesting img src");
        Uni<String> r =  bus.<String>request("nativeui", "selectdir")
                .onItem().transform(Message::body);
        logger.info("Got result "+r);
        return r;
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
