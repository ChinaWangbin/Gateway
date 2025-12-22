package com.example.model1.controller;

import com.example.model1.entity.ResponseResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import java.time.Duration;

@RestController
@RequestMapping("/ask")
public class AskController {

    @RequestMapping("/noflux")
    public ResponseResult ask(@RequestBody String input) {
        return ResponseResult.success("你输入的内容是:" + input);
    }

    @RequestMapping(value = "/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> askFlux(@RequestBody String input) {
        // 将输入字符串拆分为字符数组，模拟流式输出
        String[] parts = ("你输入的内容是: " + input).split("");
        // 使用Flux.interval创建一个定时发射序列，每隔200毫秒发射一个字符
        return Flux.interval(Duration.ofMillis(200))
                .zipWith(Flux.fromArray(parts))
                .map(tuple -> tuple.getT2())
                .take(parts.length);
    }
}