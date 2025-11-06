package com.jdh.flux;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Project: spring-ai-example
 * Author: suman6
 * Created: 2025/7/28
 * Description:
 */
public class FlowFlux {
    public static void main(String[] args) throws InterruptedException {
        Flux.range(1, 10)
                .concatMap(item -> {
                    if (item == 5) {
                        // 将异步操作放入单独的调度器
                        return Mono.fromCallable(() -> {
                                    // 模拟异步操作
                                    try {
                                        Thread.sleep(2000); // 模拟耗时操作
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                    }
                                    return "你好5(异步)";
                                });
                    } else {
                        // 顺序执行其他元素
                        return  Mono.fromCallable(() -> {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            return "你好"+item;
                        });
                    }
                })
                .toStream().forEach(System.out::println);
        Thread.sleep(200000L);
    }
}
