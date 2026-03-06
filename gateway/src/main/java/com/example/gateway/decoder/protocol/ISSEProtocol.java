package com.example.gateway.decoder.protocol;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;


/**
 * @author : wangbin
 * @date: 2026/2/12 - 02 - 12 - 14:47
 * @Description: com.example.gateway.decoder.protocol
 */
public interface ISSEProtocol {
    Flux<Flux<DataBuffer>> handle(ServerWebExchange exchange, ServerHttpResponseDecorator decorator , Publisher<? extends Publisher<? extends DataBuffer>> body);
}
