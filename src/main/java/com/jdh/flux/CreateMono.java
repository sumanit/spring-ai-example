package com.jdh.flux;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Project: spring-ai-example
 * Author: suman6
 * Created: 2025/8/27
 * Description:
 */
public class CreateMono {
    public static void main(String[] args) {
        /** 从静态数据创建 **/
        Mono.just("Hello, Mono!"); // 创建一个元素的Mono
        Mono.empty(); // 创建一个空的Mono
        Mono.justOrEmpty(null); // 创建一个元素或空的Mono
        Mono.justOrEmpty(Optional.of(null)); // 从Optional创建一个元素或空的Mono

        /** 从CompletableFuture创建 **/
        Mono.fromFuture(CompletableFuture.supplyAsync(()->null)); // 从CompletableFuture创建Mono
        Mono.fromFuture(()->CompletableFuture.supplyAsync(()->null)); // 从CompletableFuture Supplier创建Mono
        Mono.fromFuture(CompletableFuture.supplyAsync(()->null),true); // 从CompletableFuture创建Mono，并在取消时取消Future
        Mono.fromFuture(()->CompletableFuture.supplyAsync(()->null),true); // 从CompletableFuture Supplier创建Mono，并在取消时取消Future

        /** 从CompletionStage创建 **/
        Mono.fromCompletionStage(CompletableFuture.supplyAsync(()->null)); // 从CompletionStage创建Mono
        Mono.fromCompletionStage(()->CompletableFuture.supplyAsync(()->null)); // 从CompletionStage Supplier创建Mono

        /** 从发布者创建 **/
        Mono.from(new MyPublisher()); // 从Publisher创建Mono

        /** 从lambda表达式创建 **/
        Mono.fromRunnable(()->{}); // 从Runnable创建Mono，执行完毕后发出完成信号
        Mono.fromCallable(()->null); // 从Callable创建Mono，发出Callable的返回值
        Mono.fromSupplier(()->null); // 从Supplier创建Mono，发出Supplier的返回值


        /** 延迟发射创建 **/
        Mono.delay(Duration.ofSeconds(1)); // 创建一个在指定延迟后发出0的Mono
        Mono.delay(Duration.ofSeconds(1),  Schedulers.parallel()); // 创建一个在指定延迟后发出的Mono，并指定调度器

        /** 其他创建方式 **/
        Mono.never(); // 创建一个永远不会发射任何数据的Mono
        Mono.fromDirect(new MyPublisher()); // 创建的 Mono 对象在订阅时会立即执行，但不会阻塞当前线程
        Mono.defer(()->Mono.just("Deferred Mono")); // 延迟创建Mono，直到有订阅者订阅时才执行
        Mono.error(()->new RuntimeException("Error Mono from Supplier")); // 从Supplier创建一个只包含错误信号的Mono
        Mono.error(new RuntimeException("Error Mono")); // 创建一个只包含错误信号的Mono
        Mono.create(sink -> {
            sink.success("Created Mono");
        }); // 通过MonoSink手动创建Mono
        Mono.deferContextual(ctx -> Mono.just("Contextual Mono")); // 创建一个可以访问上下文信息的Mono

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
