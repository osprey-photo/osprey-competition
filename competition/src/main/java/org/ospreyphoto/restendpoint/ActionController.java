package org.ospreyphoto.restendpoint;

import org.ospreyphoto.config.Config;
import org.ospreyphoto.model.Action;
import org.ospreyphoto.model.DisplayMessage;
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

    @GET
    @Path("/imagesrc")
    public Uni<String> initCatalog(){
        return bus.<String>request("nativeui","selectdir")            
        .onItem().transform(Message::body);  
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
