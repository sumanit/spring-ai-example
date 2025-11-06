package com.jdh.flux;

import com.google.common.collect.Lists;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Project: spring-ai-example
 * Author: suman6
 * Created: 2025/7/22
 * Description:
 */
public class CreateFlux {

    public static void main(String[] args) {
        /** 从静态数据创建 **/
        Flux<Long> empty = Flux.empty(); // 创建一个空的Flux
        Flux<Integer> just = Flux.just(1, 2, 3); // 直接创建包含多个数据的Flux
        Flux<Integer> fromArray = Flux.fromArray(new Integer[]{1, 2, 3}); // 从数组创建
        Flux<Integer> fromIterable = Flux.fromIterable(Lists.newArrayList(1, 2, 3)); // 从Iterable集合创建

        /** 从流式数据创建 **/
        Flux<Integer> fromStream = Flux.fromStream(Stream.of(1, 2, 3)); // 从Stream创建
        Flux<Integer> fromSupplier = Flux.fromStream(() -> Stream.of(1, 2, 3)); // 从streamSupplier创建

        /** 从发布者创建 **/
        Flux<Integer> from = Flux.from(new MyPublisher()); // 从Publisher创建

        /** 数值范围创建**/
        Flux<Integer> range = Flux.range(1, 10); // 创建包含1到10整数的Flux

        /** 定时序列创建 **/
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1)); // 每秒发射一个递增长整数的Flux
        Flux<Long> delayInterval = Flux.interval(Duration.ofSeconds(1), Duration.ofSeconds(2)); // 延迟1秒开始，每2秒发射一次
        Flux<Long> timerInterval = Flux.interval(Duration.ofSeconds(1), Schedulers.parallel()); //  指定调度器的定时Flux
        Flux<Long> delayTimerInterval = Flux.interval(Duration.ofSeconds(1), Duration.ofSeconds(2), Schedulers.parallel()); //带延迟和调度器的定时Flux

        /** 通过generate创建 **/
        Flux.generate(sink -> {
            sink.next(1);
            sink.complete();
        });

        Flux.generate(AtomicInteger::new, (state, sink) -> {
            long i = state.getAndIncrement();
            sink.next(i);
            if (i == 10) {
                sink.complete();
            }
            return state;
        });

        Flux.generate(AtomicInteger::new, (state, sink) -> {
                    long i = state.getAndIncrement();
                    sink.next(i);
                    if (i == 10) {
                        sink.complete();
                    }
                    return state;
                }, state -> state.set(0) // 最终清理
        );

        /** 特殊类型创建 **/
        Flux<Integer> error = Flux.error(new RuntimeException("错误")); // 创建一个只包含错误信号的Flux
        Flux<Integer> never = Flux.never(); //创建一个永远不会发射任何数据的Flux
        Flux<Integer> defer = Flux.defer(() -> Flux.range(0, 10)); // 延迟创建Flux，直到有订阅者订阅时才执行
        just.subscribe();
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
