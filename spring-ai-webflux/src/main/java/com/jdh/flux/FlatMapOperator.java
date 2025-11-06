package com.jdh.flux;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

/**
 * Project: spring-ai-example
 * Author: suman6
 * Created: 2025/9/8
 * Description: 展示 Reactor 中各种 flatMap 相关操作符的用法和特点
 */
public class FlatMapOperator {

    public static void main(String[] args) {
        flatMapOperator();
    }

    public static void flatMapOperator() {
        // 基本映射函数，将整数转换为包含该整数的 Map
        Function<Integer, Map<String, Object>> mapper = item -> {
            Map<String, Object> result = new HashMap<>();
            result.put("data", item);
            return result;
        };

        // 异步映射函数，将整数转换为包含该整数的 Map，并添加随机延迟
        Function<Integer, Publisher<Map<String, Object>>> fluxMapper = item -> Mono.fromCompletionStage(CompletableFuture.supplyAsync(() -> {
            System.out.println("start to process " + item);
            int delay = ThreadLocalRandom.current().nextInt(1000);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("data", item);
            System.out.println("end to process " + item);
            return result;
        }));
        
        Flux<Map<String, Object>> flux = null;

        // 1. 基本的 flatMap 用法
        // flatMap 将每个元素转换为一个 Publisher，然后合并这些 Publisher 的结果
        // 不保证顺序，适合处理异步操作
        flux = Flux.range(1, 10).flatMap(fluxMapper);
        wrapFlux(flux, "flatMap");

        // 2. 带并发参数的 flatMap
        // 限制同时处理的元素数量为 5
        flux = Flux.range(1, 10).flatMap(fluxMapper, 5);
        wrapFlux(flux, "flatMapConcurrency");

        // 3. 带预取值的 flatMap
        // 限制同时处理的元素数量为 5，预取 10 个元素
        flux = Flux.range(1, 10).flatMap(fluxMapper, 5, 10);
        wrapFlux(flux, "flatMapConcurrencyPrefetch");

        // 4. 带错误处理的 flatMap
        // 提供错误处理函数和完成处理函数
        flux = Flux.range(1, 10).flatMap(
            fluxMapper,
            throwable -> {
                System.out.println("Error occurred: " + throwable.getMessage());
                return Flux.error(throwable);
            },
            () -> {
                System.out.println("completed");
                return Mono.empty();
            }
        );
        wrapFlux(flux, "flatMapThreeParam");

        // 5. flatMapMany - 将 Mono 转换为 Flux
        // 将单个元素转换为多个元素的流
        Flux<Integer> flatMapManyFlux = Mono.just(5)
            .flatMapMany(item -> Flux.range(1, item));
        wrapFlux(flatMapManyFlux, "flatMapMany");

        // 6. flatMapIterable - 将元素转换为 Iterable，然后展开
        // 适合将元素映射到集合，然后展开集合中的元素
        Flux<Integer> flatMapIterableFlux = Flux.range(1, 3)
            .flatMapIterable(item -> {
                List<Integer> list = new ArrayList<>();
                for (int i = 0; i < item; i++) {
                    list.add(item);
                }
                return list;
            });
        wrapFlux(flatMapIterableFlux, "flatMapIterable");

        // 7. flatMapSequential - 保持原始序列顺序的 flatMap
        // 内部使用有序合并，确保输出顺序与输入顺序一致
        flux = Flux.range(1, 10).flatMapSequential(fluxMapper);
        wrapFlux(flux, "flatMapSequential");

        // 8. flatMapSequential 带并发参数
        // 限制同时处理的元素数量为 5
        flux = Flux.range(1, 10).flatMapSequential(fluxMapper, 5);
        wrapFlux(flux, "flatMapSequentialConcurrency");

        // 9. flatMapSequential 带预取值
        // 限制同时处理的元素数量为 5，预取 10 个元素
        flux = Flux.range(1, 10).flatMapSequential(fluxMapper, 5, 10);
        wrapFlux(flux, "flatMapSequentialConcurrencyPrefetch");

        // 10. concatMap - 顺序处理的 flatMap
        // 等待前一个 Publisher 完成后再处理下一个
        flux = Flux.range(1, 10).concatMap(fluxMapper);
        wrapFlux(flux, "concatMap");

        // 12. concatMapDelayError - 延迟错误处理的 concatMap
        // 继续处理所有元素，然后在最后传播错误
        Flux<Object> errorFlux = Flux.just(1, 2, 0, 4)
            .concatMapDelayError(item -> {
                if (item == 0) {
                    return Mono.error(new ArithmeticException("Division by zero"));
                }
                Map<String, Object> result = new HashMap<>();
                result.put("data", 10 / item);
                return Mono.just(result);
            });
        wrapFlux(errorFlux, "concatMapDelayError");

        // 13. flatMapDelayError - 延迟错误处理的 flatMap
        // 继续处理所有元素，然后在最后传播错误
        errorFlux = Flux.just(1, 2, 0, 4)
            .flatMapDelayError(item -> {
                if (item == 0) {
                    return Mono.error(new ArithmeticException("Division by zero"));
                }
                Map<String, Object> result = new HashMap<>();
                result.put("data", 10 / item);
                return Mono.just(result);
            }, 10, 32);
        wrapFlux(errorFlux, "flatMapDelayError");

        // 14. switchMap - 只处理最新的 Publisher
        // 当新元素到达时，取消前一个 Publisher 的订阅
        Flux<String> switchMapFlux = Flux.just("A", "B", "C")
            .switchMap(item -> Flux.interval(Duration.ofMillis(100))
                .take(3)
                .map(i -> item + i));
        wrapFlux(switchMapFlux, "switchMap");

        // 15. expandDeep - 深度优先递归展开
        // 递归地将每个元素转换为 Publisher，然后深度优先展开
        Flux<Integer> expandDeepFlux = Flux.just(1)
            .expandDeep(item -> {
                if (item >= 3) {
                    return Flux.empty();
                }
                return Flux.just(item + 1);
            });
        wrapFlux(expandDeepFlux, "expandDeep");

        // 16. expand - 广度优先递归展开
        // 递归地将每个元素转换为 Publisher，然后广度优先展开
        Flux<Integer> expandFlux = Flux.just(1)
            .expand(item -> {
                if (item >= 3) {
                    return Flux.empty();
                }
                return Flux.just(item + 1);
            });
        wrapFlux(expandFlux, "expand");
    }

    /**
     * 包装 Flux 执行，添加订阅、完成和元素处理的日志
     * @param flux 要执行的 Flux
     * @param key 操作符名称，用于日志输出
     * @param <T> Flux 元素类型
     */
    public static <T> void wrapFlux(Flux<T> flux, String key) {
        flux.doOnSubscribe(item -> {
            System.out.println(key + " [start]");
        }).doOnComplete(() -> {
            System.out.println(key + " [end]");
        }).doOnNext(item -> {
            System.out.println(item.getClass().getSimpleName() + ":" + item);
        }).blockLast();
        System.out.println();
    }
}
