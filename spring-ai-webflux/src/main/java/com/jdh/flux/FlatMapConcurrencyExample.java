package com.jdh.flux;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class FlatMapConcurrencyExample {
    public static void main(String[] args) throws InterruptedException {
        // 示例：模拟异步调用远程API（每个请求耗时不同）
        Flux.range(1, 10) // 1. 创建1-10的整数流
                .flatMap(id ->
                                // 2. 对每个元素应用异步转换（模拟API调用）
                                Flux.just("API Response for ID: " + id)
                                        // 3. 设置每个元素的延迟时间（随机0-1秒）
                                        .delayElements(Duration.ofMillis(ThreadLocalRandom.current().nextInt(1000))),
                        3 // 4. 关键参数：设置并发度为3（最多同时3个请求）
                )
                .subscribe(
                        response -> System.out.println("Received: " + response),
                        error -> System.err.println("Error: " + error),
                        () -> System.out.println("All requests completed!")
                );

        // 5. 防止主线程退出（实际项目不需要）
        Thread.sleep(5000);
    }

    public static void main2(String[] args) throws InterruptedException {
        // 示例：模拟异步调用远程API（每个请求耗时不同）
        Flux.range(1, 10) // 1. 创建1-10的整数流
                .flatMap(id -> Mono.fromCallable(()->{
                            Thread.sleep(ThreadLocalRandom.current().nextInt(1000));
                            return "API Response for ID: " + id;
                        }),
                        3 // 4. 关键参数：设置并发度为3（最多同时3个请求）
                )
                .subscribe(
                        response -> System.out.println("Received: " + response),
                        error -> System.err.println("Error: " + error),
                        () -> System.out.println("All requests completed!")
                );

        // 5. 防止主线程退出（实际项目不需要）
        Thread.sleep(5000);
    }

}
