package com.example.gateway.entity;

import java.util.Map;

/**
 * @author : wangbin
 * @date: 2026/2/13 - 02 - 13 - 17:21
 * @Description: com.example.gateway.entity
 */
public class DataConvertResponseDTO {
    private String content;//敏感词检查字段

    private String response;//响应报文

    private boolean sendEnabled;

    private Map<String, Object> extMap;

    public static DataConvertResponseDTO build() {
        return  new DataConvertResponseDTO();
    }
    public DataConvertResponseDTO content(String content) {
        this.content = content;
        return this;
    }
    public DataConvertResponseDTO response(String response) {
        this.response = response;
        return this;
    }
    public DataConvertResponseDTO sendEnabled(boolean sendEnabled) {
        this.sendEnabled = sendEnabled;
        return this;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }


    public void setSendEnabled(boolean sendEnabled) {
        this.sendEnabled = sendEnabled;
    }

    public Map<String, Object> getExtMap() {
        return extMap;
    }

    public void setExtMap(Map<String, Object> extMap) {
        this.extMap = extMap;
    }

    public boolean isSendEnabled(){
        return sendEnabled;
    }
    public DataConvertResponseDTO setsendEnabled(boolean sendEnabled) {
        this.sendEnabled = sendEnabled;
        return this;
    }
}
