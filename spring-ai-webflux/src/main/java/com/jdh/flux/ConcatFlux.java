package com.jdh.flux;

import reactor.core.publisher.Flux;

/**
 * Project: spring-ai-example
 * Author: suman6
 * Created: 2025/7/23
 * Description:
 */
public class ConcatFlux {
    public static void main(String[] args) {
        Flux<Integer> flux1 = Flux.just(1, 2, 3);
        Flux<Integer> flux2 = Flux.just(4, 5, 6);
        Flux<Integer> flux = Flux.concat(flux1,flux2);
        flux.map(item-> {
            System.out.println(item);
            return item;
        }).blockLast();
    }
}
