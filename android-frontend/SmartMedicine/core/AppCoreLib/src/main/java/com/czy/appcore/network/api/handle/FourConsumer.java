package com.czy.appcore.network.api.handle;

@FunctionalInterface
public interface FourConsumer<T, U, V, X> {
    void accept(T t, U u, V v, X x);
}
