package com.programmers.java.lambda;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
        MySupplier<String> s = () -> "Hello";

        MyMapper<String,Integer> m = (str) -> str.length();

        MyConsummer c = System.out::println; // 인자를 변경없이 바로 사용할 경우 method reference를 지정해주는 표현방식

        MyRunnable r = () -> c.consumer(m.map(s.supply()));

        r.run();

        Print print = new Print();
        Consumer<Integer> printName1 = Print::printStaticNums;

        Supplier<User> userSupplier = User::new;
        User user1 = userSupplier.get();

        Function<String,User> userFunction = User::new;
        User user2 = userFunction.apply("장영지");

    }
}
