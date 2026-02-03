package com.example.gateway.decoder;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import com.example.gateway.util.SpringContextUtil;
import com.example.gateway.decoder.impl.ServerResponseHttpDecoratorImpl;

/**
 * @author : wangbin
 * @date: 2026/1/16 - 01 - 16 - 10:00
 * @Description: com.example.gateway.decoder
 */
public class ServerResponseHttpDecorator extends ServerHttpResponseDecorator {
    private ServerWebExchange exchange;
    public ServerResponseHttpDecorator(ServerHttpResponse delegate, ServerWebExchange exchange) {
        super(delegate);
        this.exchange = exchange;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
       // return super.writeWith(new ServerResponseBodyDecorator(body, exchange));
       return SpringContextUtil.getBean(ServerResponseHttpDecoratorImpl.class).writeWith(exchange, this, body);
    }
}
