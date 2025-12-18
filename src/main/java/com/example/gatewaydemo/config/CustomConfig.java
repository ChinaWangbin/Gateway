package com.example.gatewaydemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义配置实体类，用于映射custom-config.properties文件中的配置项
 */
@Configuration
@ConfigurationProperties(prefix = "custom")
public class CustomConfig {

    private App app;
    private Server server;
    private Security security;

    // getter和setter方法
    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    // 内部类
    public static class App {
        private String name;
        private String version;

        // getter和setter方法
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    public static class Server {
        private String ip;
        private Integer port;

        // getter和setter方法
        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }
    }

    public static class Security {
        private Boolean enabled;
        private String apiKey;

        // getter和setter方法
        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }

    @Override
    public String toString() {
        return "CustomConfig{" +
                "app=" + app +
                ", server=" + server +
                ", security=" + security +
                '}';
    }
}