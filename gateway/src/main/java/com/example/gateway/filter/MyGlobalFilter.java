package com.example.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author : wangbin
 * @date: 2025/12/31 - 12 - 31 - 17:01
 * @Description: com.example.gateway.filter
 */
@Component
@Slf4j
public class MyGlobalFilter implements GlobalFilter, Ordered {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取客户端 IP
        String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        String blacklistKey = "blacklist:" + ip;
        String countKey = "visit_count:" + ip;
        log.info("当前IP: " + ip);
        // 2. 开始响应式链式操作
        // 第一步：先执行计数（固定逻辑，不需要判断结果）
        return Mono.just(ip)
                .flatMap(ips -> {
                     log.info("计数开始...");
                     log.info("ip: " + ips);
                     return chain.filter(exchange);
                });

    }

    @Override
    public int getOrder() {
        // 返回的值越小，优先级越高
        return -1;
    }
}
