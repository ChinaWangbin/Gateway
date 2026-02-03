package com.example.gateway.decoder.impl;

import com.example.gateway.decoder.ServerResponseHttpDecorator;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerCodecConfigurer;
import reactor.core.publisher.Flux;
import org.springframework.cloud.gateway.support.BodyInserterContext;

/**
 * @author : wangbin
 * @date: 2026/1/16 - 01 - 16 - 10:03
 * @Description: com.example.gateway.decoder.impl
 */
@Component
@Slf4j
public class ServerResponseHttpDecoratorImpl  {
    @Autowired
    private ServerCodecConfigurer codecConfigurer;
    
    public Mono<Void> writeWith(ServerWebExchange exchange,ServerHttpResponseDecorator decorator,Publisher<? extends DataBuffer> body){
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange,decorator.getDelegate().getHeaders());
        return BodyInserters.fromPublisher(ClientResponse.create(
            exchange.getResponse().getStatusCode(),codecConfigurer.getReaders()).body(
                Flux.from(body)).build().bodyToMono(String.class).flatMap(
                    originBody -> Mono.just(getResponse(exchange,originBody, (HttpStatus) exchange.getResponse().getStatusCode()))),String.class).insert(outputMessage,new BodyInserterContext())
            .then(Mono.defer(() -> {
                    Flux<DataBuffer> bodyFlux = outputMessage.getBody();
                    HttpHeaders headers = decorator.getDelegate().getHeaders();
                    if(! headers.containsKey(HttpHeaders.TRANSFER_ENCODING)){
                        bodyFlux=bodyFlux.doOnNext(dataBuffer -> headers.setContentLength(dataBuffer.readableByteCount()));

                    }
                    return decorator.getDelegate().writeWith(bodyFlux);
                }));
    }

    public String getResponse(ServerWebExchange exchange,String originalBody, HttpStatus httpStatus){
        //响应报文转换
        return originalBody;
    }
}
