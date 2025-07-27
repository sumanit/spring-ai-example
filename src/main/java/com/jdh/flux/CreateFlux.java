package com.jdh.flux;

import com.google.common.collect.Lists;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.stream.Stream;

/**
 * Project: spring-ai-example
 * Author: suman6
 * Created: 2025/7/22
 * Description:
 */
public class CreateFlux {
    Flux<Integer> just = Flux.just(1, 2, 3);
    Flux<Integer> fromArray = Flux.fromArray(new Integer[]{1,2,3});
    Flux<Integer> fromStream = Flux.fromStream(Stream.of(1,2,3));
    Flux<Integer> fromSupplier = Flux.fromStream(()->Stream.of(1,2,3));
    Flux<Integer> fromIterable = Flux.fromIterable(Lists.newArrayList(1, 2, 3));
    Flux<Integer> from = Flux.from(new MyPublisher());
    Flux<Integer> range = Flux.range(1, 10);// 数字范围
    Flux<Long> delayInterval = Flux.interval(Duration.ofSeconds(1), Duration.ofSeconds(2));// 定时间隔，开始时间为1秒，间隔2秒
    Flux<Long> timerInterval = Flux.interval(Duration.ofSeconds(1),Schedulers.parallel());
    Flux<Long> delayTimerInterval = Flux.interval(Duration.ofSeconds(1), Duration.ofSeconds(2), Schedulers.parallel());

    public static void main(String[] args) {
        Schedulers.onScheduleHook("pre-process", item->{
            return ()->{
                System.out.println("Pre-processing");
                item.run();
                System.out.println("ddd");
            };
        });
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
        interval.takeWhile(item->item<100).map(item->{
            System.out.println(item);
            return item;
        }).blockLast();

    }
    public static class MyPublisher implements Publisher<Integer> {
        @Override
        public void subscribe(Subscriber<? super Integer> s) {
            s.onNext(1);
            s.onNext(2);
            s.onNext(3);
            s.onComplete();
        }
    }
}
