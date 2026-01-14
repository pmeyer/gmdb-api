package com.yellowmoonsoftware.gmcatalog.gmdb.api.util;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Callable;

public final class ReactiveUtils {
    public static <T> Mono<T> async(final Callable<T> supplier) {
        return Mono.defer(() -> Mono.fromCallable(supplier))
                .subscribeOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.parallel());
    }
}
