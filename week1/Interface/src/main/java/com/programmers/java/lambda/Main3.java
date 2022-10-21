package com.programmers.java.lambda;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Main3 {
    public static void main(String[] args) {
        new Main3().filteredNumbers(30,
                i -> i % 3 == 0 && i > 0,
                System.out::println
        );
    }
    void filteredNumbers(int max, Predicate<Integer> p, Consumer<Integer> c) {
        for (int i = 0; i < max; i++) {
            if (p.test(i)) c.accept(i);
        }
    }
}
