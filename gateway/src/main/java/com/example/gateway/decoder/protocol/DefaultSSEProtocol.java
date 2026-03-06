package com.example.gateway.decoder.protocol;

import com.example.gateway.entity.DataConvertResponseDTO;
import com.example.gateway.util.ByteUtils;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author : wangbin
 * @date: 2026/2/12 - 02 - 12 - 14:46
 * @Description: com.example.gateway.decoder.protocol
 */
@Component
@Slf4j
public class DefaultSSEProtocol implements ISSEProtocol {
    @Override
    public Flux<Flux<DataBuffer>> handle(ServerWebExchange exchange, ServerHttpResponseDecorator decorator, Publisher<? extends Publisher<? extends DataBuffer>> body) {
        /*Map<String, Object> map = new HashMap<>();
        Map<String, String> singleValueMap = exchange.getRequest().getHeaders().toSingleValueMap();
        map.putAll(singleValueMap);*/
        //异步处理
        return asyncHandel(exchange, decorator, body);

    }

    private Flux<Flux<DataBuffer>> asyncHandel(ServerWebExchange exchange, ServerHttpResponseDecorator decorator, Publisher<? extends Publisher<? extends DataBuffer>> body) {
        StringBuffer buffer = new StringBuffer("");
        StringBuffer contentBuffer = new StringBuffer("");
        StringBuffer beforeBuffer = new StringBuffer("");
        AtomicInteger i = new AtomicInteger(0);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        AtomicReference<Boolean> errorFlag = new AtomicReference(true);
        Map<String, String> singleValueMap = exchange.getRequest().getHeaders().toSingleValueMap();
        return ((Flux<Flux<DataBuffer>>) body)
                .map(innerFlux -> innerFlux.map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    try {
                        outputStream.write(bytes);
                    } catch (IOException e) {
                        String errorResponse = "error";
                        buffer.setLength(0);
                        errorFlag.set(false);
                        return decorator.bufferFactory().wrap(errorResponse.getBytes());
                    }
                    String response = new String(outputStream.toByteArray(), Charset.forName("UTF-8"));
                    DataBufferUtils.release(dataBuffer);
                    boolean endFlag = false;
                    if (ByteUtils.isReceiveEnd(bytes) && errorFlag.get() && response.length() > 2) {
                        try {
                            outputStream.reset();
                            buffer.setLength(0);
                            String[] responses = response.split("\n\n");
                            for (String responseBody : responses) {
                                //判断结束标识
                                if (responseBody.substring(responseBody.lastIndexOf("data:") + "data:".length()).equals("[DONE]")) {
                                    endFlag = true;
                                    outputStream.close();
                                }
                                //判断包含结束标识
                                if (responseBody.substring(responseBody.lastIndexOf("data:") + "data:".length()).startsWith("[DONE]")) {
                                    endFlag = true;
                                    outputStream.close();
                                }
                                //报文转换
                                dataConvert(exchange,responseBody,decorator.getStatusCode());
                                //响应报文拼接
                                if (!responseBody.isEmpty()) {
                                    contentBuffer.append(responseBody);
                                    i.getAndIncrement();
                                }
                                buffer.append(responseBody).append("\n\n");
                            }
                            response = buffer.toString().trim();
                            buffer.setLength(0);
                            //默认50个token校验一次敏感词
                            if (i.get() >= 50 || endFlag) {
                                i.set(0);
                                String context = contentBuffer.toString();
                                String checkContext = beforeBuffer.append(context).toString();
                                beforeBuffer.setLength(0);
                                //敏感词检查
                                //todo
                                beforeBuffer.setLength(0);
                                beforeBuffer.append(context);
                            }
                            if (!StringUtils.hasLength(response)) {
                                return decorator.bufferFactory().wrap(ByteUtils.emptyByte());
                            }

                        } catch (Exception e) {
                        }
                        String responseBody = buffer.append(response).append("\n\n").toString();
                        buffer.setLength(0);
                        outputStream.reset();
                        return decorator.bufferFactory().wrap(responseBody.getBytes());
                    }
                    return decorator.bufferFactory().wrap(ByteUtils.emptyByte());
                }));


    }

    private DataConvertResponseDTO dataConvert(ServerWebExchange exchange, String response, HttpStatusCode statusCode) {
        StringBuffer responseBody = new StringBuffer();
        String[] responses = response.split(String.valueOf(10));
        StringBuffer contentBuffer = new StringBuffer();
        for (String body : responses) {

        }
    }


}
