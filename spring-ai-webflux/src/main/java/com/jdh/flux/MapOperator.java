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
public class MapOperator {

    public static void main(String[] args) {
        mapOperator();
    }

    public static void mapOperator() {
        Function<Integer,Map<String, Object>> mapper = item->{
            Map<String, Object> result = new HashMap();
            result.put("data", item);
            return result;
        };

        Function<Integer, Publisher<Map<String,Object>>> fluxMapper = item-> Mono.fromCompletionStage(CompletableFuture.supplyAsync(() -> {
                int delay = ThreadLocalRandom.current().nextInt(1000);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Map<String, Object> result = new HashMap();
                result.put("data", item);
                return result;
            }));
        Flux<Map<String, Object>> flux = null;
        flux = Flux.range(1, 100).map(mapper);
        wrapFlux(flux,"map");
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
