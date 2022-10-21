package com.programmers.java.lambda;

@FunctionalInterface
public interface MyMapper<IN, OUT> {
    OUT map(IN s);
}
