package com.jdh.flux;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

/**
 * Project: spring-ai-example
 * Author: suman6
 * Created: 2025/9/8
 * Description:
 */
public class BufferOperator {

    public static void main(String[] args) {
        bufferOperator();
    }

    /**
     * buffer 操作符
     */
    public static void bufferOperator() {
        /**
         * 聚合为一个list
         */
        Flux flux = Flux.range(1, 100)
                .buffer();
        wrapFlux(flux,"buffer");

        /**
         * 按照数量进行聚合 类型为 ArrayList
         */
        flux = Flux.range(1, 100)
                .buffer(20);
        wrapFlux(flux,"buffer maxSize");

        /**
         * 按照数量进行聚合 但是可以指定聚合后的集合类型
         */
        flux = Flux.range(1, 100)
                .buffer(10, LinkedList::new);
        wrapFlux(flux,"buffer maxSize supplier");

        /**
         * 按照数量进行聚合 但是每次聚合都会从头开始算 跳过 n*skip 的元素 n从0开始
         * 如果skip < maxSize 会有重复数据
         * 如果skip = maxSize 等价于无skpi参数的重载方法
         * 如果skip > maxSize 会有数据不处理
         */
        flux =  Flux.range(1,100)
                .buffer(10,12);
        wrapFlux(flux, "buffer maxSize skip");


        /**
         * 等同于 buffer maxSize skip 只是可以指定聚合后的集合类型
         */
        flux =  Flux.range(1,100)
                .buffer(10,12, LinkedList::new);
        wrapFlux(flux, "buffer maxSize skip supplier");


        /**
         * 按时间间隔进行聚合，指定时间范围内的数据会聚合为一个list
         */
        flux =  Flux.interval(Duration.ofMillis(100))
                .take(20)
                .buffer(Duration.ofMillis(1000));
        wrapFlux(flux, "buffer bufferingTimespan");


        /**
         * 按时间间隔 bufferingTimespan 进行聚合，指定时间范围内的数据会聚合为一个list
         * 同时按照时间间隔 openBufferEvery 创建缓冲区
         * 如果 bufferingTimespan > openBufferEvery 会有数据重复
         * 如果 bufferingTimespan = openBufferEvery 等价于无openBufferEvery参数的重载方法
         * 如果 bufferingTimespan < openBufferEvery 会有数据不处理
         */
        flux =  Flux.interval(Duration.ofMillis(100))
                .take(20)
                .buffer(Duration.ofMillis(1000),Duration.ofMillis(1300));
        wrapFlux(flux, "buffer bufferingTimespan openBufferEvery");

        /**
         *
         */
        flux =  Flux.interval(Duration.ofMillis(100))
                .take(20)
                .buffer(Flux.interval(Duration.ofMillis(500)),LinkedList::new);
        wrapFlux(flux, "buffer other");

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



    public static <T> void wrapFlux(Flux<T> flux,String key) {
        flux.doOnSubscribe(item->{
            System.out.println( key+" [start]");
        }).doOnComplete(()->{
            System.out.println( key+" [end]");
        }).doOnNext(item->{
            System.out.println(item.getClass().getSimpleName()+":"+item);
        }).blockLast();
        System.out.println();
    }
}
