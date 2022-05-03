package com.example;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FluxTest {
    @Test
    public void FirstTest() {
        Flux.just("A", "B")
                .log()
                .subscribe(System.out::println);

    }
    @Test
    public void FirstRange() {
        Flux.range(10, 100)
                .log()
                .subscribe(System.out::println);

    }
}
