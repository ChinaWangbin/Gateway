package com.example.gateway.test;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ReactorDemo {
    public static void main(String[] args) {
        test2();
    }

    public static  void  test2(){
        Mono<String> mono1 = Mono.justOrEmpty("Hello");
        Mono<String> mono2 = Mono.justOrEmpty(null); // 返回 Mono.empty()

        mono1.subscribe(   value -> System.out.println(value),
                error -> System.err.println(error)); // 输出: Hello
        mono2.subscribe(
                value -> System.out.println(value),  // 不会执行
                error -> System.err.println(error),  // 不会执行
                () -> System.out.println("Completed") // 输出: Completed
        );
    }
    public static void test1(){
        // 模拟输入：尝试把 token 改成 "user" 看看效果
        String token = "admin";

        Mono.just(token)
                // 1. 打印日志
                .doOnNext(t -> System.out.println("收到Token: " + t))

                // 2. 校验逻辑 (过滤)
                .filter(t -> "admin".equals(t))

                // 3. 如果过滤后为空（即校验失败），走这里
                .switchIfEmpty(Mono.error(new RuntimeException("401 无权访问")))

                // 4. 模拟异步查库 (flatMap)
                .flatMap(t -> {
                    System.out.println("正在查询数据库...");
                    // 模拟耗时 1秒
                    return Mono.just("张三").delayElement(Duration.ofSeconds(1));
                })

                // 5. 转换结果
                .map(name -> "欢迎您，" + name)

                // 6. 订阅（触发流程）- 实际网关中框架会帮你做这一步
                .subscribe(
                        success -> System.out.println("最终结果: " + success),
                        error -> System.err.println("发生错误: " + error.getMessage())
                );

        // 只有在 main 方法测试时需要阻塞主线程等待异步结果，否则程序直接退出了
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
    }
}