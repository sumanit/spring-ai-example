package com.jdh.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.sql.SQLOutput;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class SseController {

    // 模拟异步处理附件的方法
    private Mono<Map<String,Object>> processAttachmentAsync(int number) {
        return Mono.fromFuture(CompletableFuture.supplyAsync(() -> {
            try {
                // 模拟耗时处理

                Map<String, Object> response = new HashMap<>();
                System.out.println("start: " + number);
                response.put("start", number);
                // 随机1-10秒处理时间
                Thread.sleep(20000);
                response.put("end", number);
                System.out.println("end: " + number);
                response.put("number", number);
                return response;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
    }
    @GetMapping(value = "/sse/numbers", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Object>> streamNumbers(
            @RequestParam(defaultValue = "1") int start,
            @RequestParam(defaultValue = "10") int end,
            @RequestParam(defaultValue = "5") int specialNumber) {

        Random random = new Random();

        Flux<Integer> range = Flux.range(start, end - start + 1);
        Flux<Map<String,Object>> result = Flux.empty();
        Flux<Map<String,Object>> one =  range.flatMapSequential(number -> {
            Map<String, Object> response = new HashMap<>();
            System.out.println("befor: " + number);
            if (number == specialNumber) {
                // 特殊数字：立即发射空事件+异步处理
                // 附件异步处理，先返回处理中状态
                return Flux.merge(
                        Flux.empty(),
                        processAttachmentAsync(number) // 避免立即完成
                                .flatMapMany(Flux::just)
                );
            } else {
                // 普通数字 - 并发处理但保持顺序
                return Mono.fromCallable(() -> {
                    System.out.println("start: " + number);
                    response.put("start", number);
                    // 随机1-10秒处理时间
                    Thread.sleep(1000 * (random.nextInt(10) + 1));
                    response.put("end", number);
                    System.out.println("end: " + number);
                    return response;
                }).subscribeOn(Schedulers.boundedElastic());
            }
        }); // 最大并发数设为最大值
        result = result.concatWith(one);
        return result;
    }

}