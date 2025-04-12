package net.notjustanna.gateway.stream;

import reactor.core.publisher.Flux;


public interface GatewayStream {
    Flux<?> stream();
}
