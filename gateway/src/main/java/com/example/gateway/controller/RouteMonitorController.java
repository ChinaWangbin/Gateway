package com.example.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RouteMonitorController {

    @Autowired
    private RouteLocator routeLocator;

    @GetMapping("/routes")
    public Mono<List<RouteInfo>> getAllRoutes() {
        return routeLocator.getRoutes()
            .map(route -> {
                RouteInfo info = new RouteInfo();
                info.setId(route.getId());
                info.setUri(route.getUri().toString());
                info.setOrder(route.getOrder());
                return info;
            })
            .collectList()
            .onErrorResume(throwable -> {
                RouteInfo errorInfo = new RouteInfo();
                errorInfo.setId("ERROR");
                errorInfo.setUri("Failed to retrieve routes: " + throwable.getMessage());
                errorInfo.setOrder(-1);
                return Mono.just(List.of(errorInfo));
            });
    }

    public static class RouteInfo {
        private String id;
        private String uri;
        private int order;

        // Getters and setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }
    }
}