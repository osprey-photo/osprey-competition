package org.ospreyphoto.display;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.vertx.ConsumeEvent;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/status")
public class StatusControl {
    final Logger logger = LoggerFactory.getLogger(StatusControl.class);

    Session session;

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

    @ConsumeEvent("statusUpdate")
    public void consume(String update) {
        try {

            if (session != null && session.isOpen()) {

                session.getAsyncRemote().sendObject(update, result -> {
                    if (result.getException() != null) {
                        logger.error("Unable to send message: " + result.getException());
                    }
                });
            }
        } catch (Exception e) {
            logger.error("JSON Processing", e);
            throw new RuntimeException(e);
        }

    }
}
