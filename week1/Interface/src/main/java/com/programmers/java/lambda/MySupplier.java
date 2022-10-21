package com.programmers.java.lambda;

@FunctionalInterface
public interface MySupplier<T> {
    T supply();
}
