package com.example.gatewaydemo.controller;

import com.example.gatewaydemo.config.CustomConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 配置测试控制器，用于验证properties配置文件的加载和映射
 */
@RestController
public class ConfigTestController {

    @Autowired
    private CustomConfig customConfig;

    @GetMapping("/api/config")
    public CustomConfig getCustomConfig() {
        return customConfig;
    }

    @GetMapping("/api/config/info")
    public String getConfigInfo() {
        StringBuilder info = new StringBuilder();
        info.append("应用名称: ").append(customConfig.getApp().getName()).append("\n");
        info.append("应用版本: ").append(customConfig.getApp().getVersion()).append("\n");
        info.append("服务器地址: ").append(customConfig.getServer().getIp()).append(":").append(customConfig.getServer().getPort()).append("\n");
        info.append("安全功能: ").append(customConfig.getSecurity().getEnabled() ? "已启用" : "已禁用").append("\n");
        info.append("API密钥: ").append(customConfig.getSecurity().getApiKey()).append("\n");
        return info.toString();
    }
}