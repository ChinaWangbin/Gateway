package com.example.gateway.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : wangbin
 * @date: 2026/1/5 - 01 - 05 - 17:07
 * @Description: com.example.gateway.properties
 */
@ConfigurationProperties(prefix = "llm")
@Component
@Slf4j
@Data
public class LlmProperties {
    private ConcurrentHashMap<String, Boolean> ignore;

    private ConcurrentHashMap<String, IgnoreBeanParentModel> ignores;
}

