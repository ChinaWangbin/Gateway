package com.example.gateway.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author : wangbin
 * @date: 2026/3/7 - 03 - 07 - 00:04
 * @Description: com.example.gateway.util
 */

@Slf4j
public class ExchangeUtils {


    /**
     * 获取请求报文
     * @param exchange
     * @return
     */
    public static Map<String,Object> getRequestBody(ServerWebExchange exchange){
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.putAll((Map<String, Object>)exchange.getAttributes().getOrDefault(LlmGatewayConstant.LLM_GATEWAY_REQUEST_BODY,new HashMap<String,Object>()));
        return requestBody;
    }
    /**
     * 获取请求报文
     * @param exchange
     * @return
     */
    public static void setRequestBody(ServerWebExchange exchange,Map<String,Object> request){
        exchange.getAttributes().put(LlmGatewayConstant.LLM_GATEWAY_REQUEST_BODY,request);
    }
    /**
     * @description:获取请求体大小
     * @param:
     * @return:
     */
    public static Integer getRequestBodySize(ServerWebExchange exchange) {

        try {
            String requestBodySize = getHeaderToString(exchange, CONTENT_LENGTH);
            return Integer.parseInt(requestBodySize);
        } catch (NumberFormatException e) {
            log.error("转换失败",e.getMessage());
            return 0;
        }

    }
    /**
     *
     * @param exchange
     * @return
     */
    public static Route getRoute(ServerWebExchange exchange){
        return exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
    }

    /**
     * 安全检查
     * @param exchange
     * @return
     */
    public static boolean securityEnabled(ServerWebExchange exchange){
        return (boolean)exchange.getAttributes().getOrDefault(LlmGatewayConstant.LLM_GATEWAY_SECURITY_ENABLED,false);
    }

    /**
     *
     * @param exchange
     * @param key
     * @return
     */
    public static String getHeaderToString(ServerWebExchange exchange, String key){
        Map<String, String> stringMap = exchange.getRequest().getHeaders().toSingleValueMap();
        if(stringMap!=null){
            return stringMap.get(key);
        }
        return null;
    }

    /**
     * 在请求头里面获取全局业务跟踪号
     * @param exchange
     * @return
     */
    public static String getGlobalBusiTrackNoFromHeader(ServerWebExchange exchange){
        return getHeaderToString(exchange,LlmGatewayConstant.LLM_GATEWAY_GLOBAL_BUSI_TRACK_NO_KEY);
    }
    public static String getGlobalBusiTrackNo(ServerWebExchange exchange){
        String globalBusiTrackNo = getHeaderToString(exchange, LlmGatewayConstant.LLM_GATEWAY_GLOBAL_BUSI_TRACK_NO_KEY);
        return StringUtils.hasLength(globalBusiTrackNo) ? globalBusiTrackNo : "";
    }

    /**
     * 敏感词校验放入上下文
     */
    public static void setSkipSensitiveFlag(ServerWebExchange exchange){
        exchange.getAttributes().put(LlmGatewayConstant.SKIP_SENSITIVE_FLAG,Boolean.TRUE);
    }
    /**
     * 获取敏感词校验开关
     */
    public static Boolean getSkipSensitiveFlag(ServerWebExchange exchange){
        return (boolean)exchange.getAttributes().getOrDefault(LlmGatewayConstant.SKIP_SENSITIVE_FLAG,false);
    }

    /**
     * 获取发送系统号
     * @param exchange
     * @return
     */
    public static String getSystemCodeByHeader(ServerWebExchange exchange){
        String sendSystemCode = getHeaderToString(exchange, LlmGatewayConstant.LLM_START_SYS_OR_CMPTNO);
        if(StringUtils.hasLength(sendSystemCode)){
            return sendSystemCode;
        }
        return getHeaderToString(exchange, LlmGatewayConstant.LLM_SEND_SYS_OR_CMPTNO);
    }
    /**
     * 获取发起系统号
     * @param exchange
     * @return
     */
    public static String getStartSystemCodeByHeader(ServerWebExchange exchange){
        return getHeaderToString(exchange, LlmGatewayConstant.LLM_START_SYS_OR_CMPTNO);
    }
    /**
     * 获取场景标识
     * @param exchange
     * @return
     */
    public static String getSceneIdByHeader(ServerWebExchange exchange){
        return getHeaderToString(exchange, LlmGatewayConstant.LLM_SCENE_ID);
    }
    /**
     * 获取appcode
     * @param exchange
     * @return
     */
    public static String getAppCodeByHeader(ServerWebExchange exchange){
        String appCode = getHeaderToString(exchange,LlmGatewayConstant.LLM_AUTH_AUTHORIZATION);
        if(StringUtils.hasLength(appCode)){
            appCode = appCode.substring(7).trim();
        }else{
            appCode = getHeaderToString(exchange,LlmGatewayConstant.LLM_AUTH_APP_CODE);
        }
        return appCode;
    }

    /**
     * 获取model
     * @param exchange
     * @return
     */
    public static String getModel(ServerWebExchange exchange){
        return (String)getRequestBody(exchange).get(LlmGatewayConstant.MODEL);
    }

    /**
     * 获取访问网关模型名称
     * @param exchange
     * @return
     */
    public static String getOriginalModel(ServerWebExchange exchange) {
        String modelId = (String) exchange.getAttributes().get(LlmGatewayConstant.ORIGINAL_GATEWAY_MODEL);
        return StringUtils.hasLength(modelId) ? modelId : "";
    }
    /**
     * 获取请求IP
     */
    public static String getIP(ServerWebExchange exchange) {
        Map<String, String> headerMap = exchange.getRequest().getHeaders().toSingleValueMap();
        String hostName = Optional.ofNullable(headerMap.get("X-Real-IP"))
                .filter(ip -> !ip.isEmpty())
                .orElseGet(() -> Optional.ofNullable(headerMap.get("X-Forwarder-For"))
                        .filter(ip -> !ip.isEmpty())
                        .orElse(""));
        if (!StringUtils.hasText(hostName)) {
            hostName = exchange.getRequest().getRemoteAddress().getHostName();
        }
        return hostName;
    }

    public static ServerWebExchange addHeader(ServerWebExchange exchange, String headerKey, String headerValue){
        ServerHttpRequest modifiedRequest = exchange.getRequest()
                .mutate()
                .header(headerKey, headerValue)
                .build();
        return exchange.mutate().request(modifiedRequest).build();
    }

    /**
     * 获取命中敏感词信息
     * @param exchange
     * @param sensitiveDTO
     */
    public static void setSensitiveInfo(ServerWebExchange exchange, SensitiveDTO sensitiveDTO) {
        exchange.getAttributes().put(LlmGatewayConstant.LLM_GATEWAY_SENSITIVE, sensitiveDTO);
    }

    /**
     * 输入敏感词信息
     * @param exchange
     * @return
     */
    public static SensitiveDTO getSensitiveInfo(ServerWebExchange exchange) {
        Object sensitiveInfo = exchange.getAttributes().get(LlmGatewayConstant.LLM_GATEWAY_SENSITIVE);
        if (sensitiveInfo instanceof SensitiveDTO) {
            return (SensitiveDTO) sensitiveInfo;
        }
        return new SensitiveDTO();
    }

    public static void setStringInfo(ServerWebExchange exchange,String key, String value){
        if(StringUtils.hasText(key)){
            exchange.getAttributes().put(key,value);
        }
    }

    public static void setObjectInfo(ServerWebExchange exchange, String key, Object object) {
        if (StringUtils.hasLength(key) && Objects.nonNull(object)) {
            exchange.getAttributes().put(key, object);
        }
    }
}
