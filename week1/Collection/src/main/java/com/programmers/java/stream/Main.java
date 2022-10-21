package com.programmers.java.stream;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Arrays.asList("SD","AA","GFE","WQW","q")
                .stream()
                .map(s -> s.length())
                .filter(i -> i % 2 == 1)
                .forEach(i -> System.out.println(i));
    }
}
