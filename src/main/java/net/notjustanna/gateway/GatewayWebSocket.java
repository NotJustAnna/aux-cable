package net.notjustanna.gateway;

import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.ServerWebSocket;
import net.notjustanna.exceptions.ApplicationException;
import net.notjustanna.exceptions.ExceptionType;
import net.notjustanna.exceptions.Exceptions;
import net.notjustanna.gateway.stream.GatewayStream;
import net.notjustanna.models.ws.SubscriptionEvent;
import net.notjustanna.models.ws.SubscriptionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerWebSocket("/api/gateway")
public class GatewayWebSocket {
    private final Logger log = LoggerFactory.getLogger(GatewayWebSocket.class);
    private final Map<String, GatewayStream> streams;
    private final Map<String, Map<String, Disposable>> subscriptions;

    public GatewayWebSocket(Map<String, GatewayStream> streams) {
        this.streams = streams;
        this.subscriptions = new ConcurrentHashMap<>();
    }

    @OnMessage
    public void onMessage(SubscriptionRequest event, WebSocketSession session) {
        String id = session.getId();
        Map<String, Disposable> userSubscriptions = subscriptions.computeIfAbsent(id, k -> new ConcurrentHashMap<>());

        String type = event.type();
        if (event.enabled()) {
            GatewayStream stream = streams.get(type);
            if (stream == null) {
                Exceptions.noSuchStream();
                return;
            }
            Disposable disposable = userSubscriptions.computeIfAbsent(type, k -> stream.stream().subscribe(
                message -> session.sendSync(new SubscriptionEvent(type, message)),
                error -> {
                    if (!(error instanceof ApplicationException)) {
                        log.error(error.getMessage(), error);
                    }
                    ExceptionType err = error instanceof ApplicationException e ? e.getType() : ExceptionType.UNKNOWN;
                    session.sendSync(new SubscriptionEvent("error", err.name()));
                }
            ));
            if (disposable.isDisposed()) {
                userSubscriptions.remove(type);
            }
        } else {
            Disposable disposable = userSubscriptions.remove(type);
            if (disposable != null) {
                disposable.dispose();
            }
        }

    }

    @OnClose
    public void onClose(WebSocketSession session) {
        String id = session.getId();
        Map<String, Disposable> userSubscriptions = subscriptions.get(id);
        if (userSubscriptions != null) {
            for (Disposable disposable : userSubscriptions.values()) {
                disposable.dispose();
            }
            subscriptions.remove(id);
        }
    }
}
