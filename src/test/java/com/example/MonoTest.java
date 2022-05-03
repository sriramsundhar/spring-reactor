package com.example;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MonoTest {
    @Test
    public void FirstTest() {
        Mono.just("A")
            .log()
            .subscribe(System.out::println);

    }
}
