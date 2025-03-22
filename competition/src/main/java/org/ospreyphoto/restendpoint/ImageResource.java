package org.ospreyphoto.restendpoint;

import java.util.List;

import org.ospreyphoto.imagehandler.ImageCatalog;
import org.ospreyphoto.model.CompetitionImage;
import org.ospreyphoto.model.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/images")
public class ImageResource {

    final Logger logger = LoggerFactory.getLogger(ImageResource.class);

    @Inject
    ImageCatalog catalog;

    @Inject
    EventBus bus;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CompetitionImage> getImages() {
        return catalog.getSummary();
    }

    @POST
    @Path("/state/{imgid}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateState(@PathParam("imgid") String id, State state) {
        catalog.updateState(id, state);
        bus.<String>publish("displayControl", "refresh");
    }
}
