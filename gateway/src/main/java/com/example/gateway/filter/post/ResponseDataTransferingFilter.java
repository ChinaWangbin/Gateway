package com.example.gateway.filter.post;

import com.example.gateway.decoder.ServerResponseHttpDecorator;
import com.example.gateway.dto.SensitiveDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @author : wangbin
 * @date: 2026/1/16 - 01 - 16 - 09:55
 * @Description: com.example.gateway.filter.post
 */
@Component
@Slf4j
public class ResponseDataTransferingFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange.mutate().response(new ServerResponseHttpDecorator(exchange.getResponse(),exchange)).build());
    }

    @Override
    public int getOrder() {
        return 0;
    }


}
