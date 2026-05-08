package com.yellowmoonsoftware.gmcatalog.gmdb.api.util;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ReactiveUtilsTest {

    @Test
    void canBeInstantiated() {
        assertThat(new ReactiveUtils()).isNotNull();
    }

    @Test
    void asyncDefersCallableUntilSubscriptionAndEmitsValue() {
        final AtomicInteger callCount = new AtomicInteger();
        final var async = ReactiveUtils.async(() -> {
            callCount.incrementAndGet();
            return "value";
        });

        assertThat(callCount).hasValue(0);

        StepVerifier.create(async)
                .expectNext("value")
                .verifyComplete();

        assertThat(callCount).hasValue(1);
    }

    @Test
    void asyncPropagatesCallableError() {
        final IllegalStateException error = new IllegalStateException("failed");

        StepVerifier.create(ReactiveUtils.async(() -> {
                    throw error;
                }))
                .expectErrorSatisfies(e -> assertThat(e).isSameAs(error))
                .verify();
    }
}
