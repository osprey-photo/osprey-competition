package org.ospreyphoto.display;

import static org.ospreyphoto.model.Constants.FULL_IMAGE;
import static org.ospreyphoto.model.Constants.LIGHT_BOX_IMAGES;
import static org.ospreyphoto.model.Constants.RESULTS_SUMMARY;

import java.util.List;

import org.ospreyphoto.imagehandler.ImageCatalog;
import org.ospreyphoto.model.Action;
import org.ospreyphoto.model.DisplayMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.vertx.ConsumeEvent;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/display")
public class DisplayControl {
    final Logger logger = LoggerFactory.getLogger(DisplayControl.class);

    Session session;

    @Inject
    ImageCatalog catalog;

    @OnOpen
    public void onOpen(Session session) {
        logger.info("ws open {}", session);
        this.session = session;
    }

    @OnClose
    public void onClose(Session session) {
        logger.info("ws close {}", session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("ws error {}", throwable);
    }

    @OnMessage
    public void onMessage(String message) {
        logger.info("ws msg {}", message);
        // broadcast(">> " + username + ": " + message);
    }

    private String previousActionJSON = "";

    @ConsumeEvent("displayControl")
    public void consume(String actionJSON) {
        try {
            if (actionJSON.equals("refresh")) {
                actionJSON = previousActionJSON;
            } else {
                previousActionJSON = actionJSON;
            }

            ObjectMapper mapper = new ObjectMapper();
            Action action = mapper.readValue(actionJSON, Action.class);

            logger.info("ws action {}", actionJSON);
            String dataToSend = null;

            DisplayMessage dm = new DisplayMessage();
            dm.displayType = action.action;
            switch (action.action) {
                case FULL_IMAGE:
                    dm.images = List.of(catalog.getFullImage(action.payload));

                    break;
                case LIGHT_BOX_IMAGES:
                    dm.images = catalog.getSetOfImages(action.payload);
                    break;
                case RESULTS_SUMMARY:
                    dm.images = catalog.getResults();
                default:
                    break;
            }

            dataToSend = mapper.writeValueAsString(dm);

            session.getAsyncRemote().sendObject(dataToSend, result -> {
                if (result.getException() != null) {
                    logger.error("Unable to send message: " + result.getException());
                }
            });

        } catch (JsonProcessingException e) {
            logger.error("JSON Processing", e);
            throw new RuntimeException(e);
        }

    }
}
