package com.example.gateway.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

@Service
public class DynamicRouteService implements ApplicationEventPublisherAware {

    private static final Logger log = LoggerFactory.getLogger(DynamicRouteService.class);

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    @Autowired
    private NacosConfigManager nacosConfigManager;

    private ApplicationEventPublisher publisher;

    // 存储当前路由ID，用于清理
    private List<String> currentRouteIds = new ArrayList<>();

    @PostConstruct
    public void init() throws NacosException {
        log.info("开始初始化Nacos动态路由配置");
        
        // 添加Nacos配置监听器
        ConfigService configService = nacosConfigManager.getConfigService();
        String dataId = "gateway-dynamic-route.yaml";
        String group = "DEFAULT_GROUP";
        
        // 首次加载配置
        String configInfo = configService.getConfig(dataId, group, 5000);
        if (configInfo != null && !configInfo.isEmpty()) {
            log.info("首次加载Nacos路由配置: {}", configInfo);
            updateRoutes(configInfo);
        }
        
        // 添加监听器
        configService.addListener(dataId, group, new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                log.info("接收到Nacos路由配置更新: {}", configInfo);
                updateRoutes(configInfo);
            }
        });
    }

    private void updateRoutes(String configInfo) {
        try {
            // 清除旧路由
            clearCurrentRoutes();
            
            // 解析新的路由配置
            List<RouteDefinition> routeDefinitions = parseRoutes(configInfo);
            
            // 更新当前路由ID列表
            currentRouteIds.clear();
            
            // 添加新路由
            for (RouteDefinition routeDefinition : routeDefinitions) {
                routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
                currentRouteIds.add(routeDefinition.getId());
                log.info("添加路由: {}", routeDefinition.getId());
            }
            
            // 刷新路由
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
            log.info("路由刷新完成，共更新 {} 条路由", routeDefinitions.size());
        } catch (Exception e) {
            log.error("更新路由失败", e);
        }
    }

    private List<RouteDefinition> parseRoutes(String configInfo) {
        List<RouteDefinition> routeDefinitions = new ArrayList<>();
        
        try {
            // 解析YAML格式的配置
            JSONObject configJson = com.example.gateway.config.YAMLParser.yamlToJsonObject(configInfo);
            JSONArray routesArray = configJson.getJSONArray("spring.cloud.gateway.routes");
            
            if (routesArray != null) {
                for (int i = 0; i < routesArray.size(); i++) {
                    JSONObject routeObj = routesArray.getJSONObject(i);
                    RouteDefinition routeDefinition = new RouteDefinition();
                    
                    routeDefinition.setId(routeObj.getString("id"));
                    routeDefinition.setUri(URI.create(routeObj.getString("uri")));
                    
                    // 解析predicates
                    JSONArray predicatesArray = routeObj.getJSONArray("predicates");
                    if (predicatesArray != null) {
                        List<PredicateDefinition> predicateDefinitions = new ArrayList<>();
                        for (int j = 0; j < predicatesArray.size(); j++) {
                            PredicateDefinition predicateDefinition = new PredicateDefinition(predicatesArray.getString(j));
                            predicateDefinitions.add(predicateDefinition);
                        }
                        routeDefinition.setPredicates(predicateDefinitions);
                    }
                    
                    // 解析filters
                    JSONArray filtersArray = routeObj.getJSONArray("filters");
                    if (filtersArray != null) {
                        List<FilterDefinition> filterDefinitions = new ArrayList<>();
                        for (int j = 0; j < filtersArray.size(); j++) {
                            FilterDefinition filterDefinition = new FilterDefinition(filtersArray.getString(j));
                            filterDefinitions.add(filterDefinition);
                        }
                        routeDefinition.setFilters(filterDefinitions);
                    }
                    
                    routeDefinitions.add(routeDefinition);
                }
            }
        } catch (Exception e) {
            log.error("解析路由配置失败", e);
        }
        
        return routeDefinitions;
    }

    private void clearCurrentRoutes() {
        for (String routeId : currentRouteIds) {
            try {
                routeDefinitionWriter.delete(Mono.just(routeId)).subscribe();
                log.info("删除路由: {}", routeId);
            } catch (Exception e) {
                log.warn("删除路由失败: {}", routeId, e);
            }
        }
        log.info("清除现有路由完成");
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}