package com.example.gateway.decoder.impl;

import com.example.gateway.decoder.ServerResponseHttpDecorator;
import com.example.gateway.decoder.protocol.DefaultSSEProtocol;
import com.example.gateway.entity.DataConvertResponseDTO;
import com.example.gateway.util.ExchangeUtils;
import com.example.gateway.util.SpringContextUtil;
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

import java.io.IOException;
import java.util.Map;

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


    public Mono<Void> writeWith(ServerWebExchange exchange,ServerHttpResponseDecorator decorator, Publisher<? extends DataBuffer> body) {

        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, decorator.getDelegate().getHeaders());
        return BodyInserters.fromPublisher(
                        ClientResponse.create(exchange.getResponse().getStatusCode(),codecConfigurer.getReaders())
                                .body(Flux.from(body))
                                .build()
                                .bodyToMono(String.class)
                                .flatMap(originalBody -> Mono.just(getResponse(exchange,originalBody, (HttpStatus) exchange.getResponse().getStatusCode())))
                        , String.class)
                .insert(outputMessage, new BodyInserterContext())
                .then(Mono.defer(() -> {
                    try {
                        int statusCode = exchange.getResponse().getStatusCode().value();
                        if (statusCode>=200 && statusCode<300){
                           // modelInvokeMetrics.registMetricsSuccess(sceneId);
                        }else {
                           // modelInvokeMetrics.registMetricsFailed(sceneId);
                        }
                    } catch (Exception e) {
                        log.error("非流式响应添加监控指标异常", e);
                    }
                    Flux<DataBuffer> messageBody = outputMessage.getBody();
                    HttpHeaders headers = decorator.getDelegate().getHeaders();
                    if (!headers.containsKey(HttpHeaders.TRANSFER_ENCODING)) {
                        messageBody = messageBody
                                .doOnNext(data -> headers.setContentLength(data.readableByteCount()));
                    }
                    return decorator.getDelegate().writeWith(messageBody);
                }));
    }

    public String getResponse(ServerWebExchange exchange,String originalBody, HttpStatus originStatus) {
        String sceneId = ExchangeUtils.getSceneIdByHeader(exchange);
        String response = "error!";
        Map<String, String> headerMap = exchange.getRequest().getHeaders().toSingleValueMap();
        try{
            // 响应报文转换
            DataConvertResponseDTO dataConvertResponseDTO = dataConvert(exchange,originalBody, originStatus);
            response = dataConvertResponseDTO.getResponse();
            log.debug("response header:globalBusiTrackNo:{};startSysOrCmptNo:{};sceneId:{};after convert response->{}", headerMap.get(LlmGatewayConstant.LLM_GATEWAY_GLOBAL_BUSI_TRACK_NO_KEY),headerMap.get(LlmGatewayConstant.LLM_START_SYS_OR_CMPTNO),headerMap.get(LlmGatewayConstant.LLM_SCENE_ID),response);
            // 安全检查
            //securityCheckFactory.getSecurity(exchange).checkContent(exchange,dataConvertResponseDTO.getContent(), SensitiveRequestTypeEnum.OUTPUT.name());
        } catch (Throwable e) {
            try {
               // modelInvokeMetrics.registMetricsSecurity(sceneId);
            } catch (Exception ex) {
                log.error("响应记录敏感词指标异常", ex);
            }

            //log.error("response header:globalBusiTrackNo:{};startSysOrCmptNo:{};sceneId:{};response exception->{}", headerMap.get(LlmGatewayConstant.LLM_GATEWAY_GLOBAL_BUSI_TRACK_NO_KEY),headerMap.get(LlmGatewayConstant.LLM_START_SYS_OR_CMPTNO),headerMap.get(LlmGatewayConstant.LLM_SCENE_ID),originalBody,e);
//            if(e instanceof CommonGatewayException){
//                throw new CommonGatewayException(ErrorCodeConstant.XLLM00300001,"error",e);
//            }else{
//                throw new CommonGatewayException(ErrorCodeConstant.XLLM00000006,"error",e);
//
        }
        //log.info("response header:globalBusiTrackNo:{};startSysOrCmptNo:{};sceneId:{};send_response->{}", headerMap.get(LlmGatewayConstant.LLM_GATEWAY_GLOBAL_BUSI_TRACK_NO_KEY),headerMap.get(LlmGatewayConstant.LLM_START_SYS_OR_CMPTNO),headerMap.get(LlmGatewayConstant.LLM_SCENE_ID),response);
        return response;
    }

    private DataConvertResponseDTO dataConvert(ServerWebExchange exchange, String originalBody, HttpStatus originStatus) {
        //暂时为空,todo报文转换和处理
        return null;
    }

    /*public DataConvertResponseDTO dataConvert(ServerWebExchange exchange,String response, HttpStatus originStatus) throws GatewayException, IOException {
        Map<String, String> headerMap = exchange.getRequest().getHeaders().toSingleValueMap();
        //log.debug("response header:globalBusiTrackNo:{};startSysOrCmptNo:{};sceneId:{};before convert response->{}", headerMap.get(LlmGatewayConstant.LLM_GATEWAY_GLOBAL_BUSI_TRACK_NO_KEY),headerMap.get(LlmGatewayConstant.LLM_START_SYS_OR_CMPTNO),headerMap.get(LlmGatewayConstant.LLM_SCENE_ID),response);
        return responseDataConvertFactory.getDataConvert(exchange).responseDataConvert(exchange, response, originStatus);
    }*/
    public Mono<Void> writeAndFlushWith(ServerWebExchange exchange, ServerResponseHttpDecorator decorator, Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return decorator.getDelegate().writeAndFlushWith(SpringContextUtil.getBean(DefaultSSEProtocol.class).handle(exchange,decorator,body));
    }
}
