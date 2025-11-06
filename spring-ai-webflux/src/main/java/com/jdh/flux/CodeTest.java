package com.jdh.flux;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Project: spring-ai-example
 * Author: suman6
 * Created: 2025/10/24
 * Description:
 */

public class CodeTest {
    public static void main(String[] args) throws InterruptedException {
        Flux<Integer> range = Flux.range(1, 100);
        range = range.flatMap(item-> {
            System.out.println(Thread.currentThread().getName()+":"+item);
            Flux<Integer> just = Flux.just(item + 1);
            return just;
        },10);
        range.doOnSubscribe(item -> {
            System.out.println(Thread.currentThread().getName()+" "+item + " [start]");
        }).doOnComplete(() -> {
            System.out.println(" [end]");
        }).doOnNext(item -> {
        }).subscribeOn(Schedulers.parallel()).subscribe();
        Thread.sleep(1000L);
    }
}
