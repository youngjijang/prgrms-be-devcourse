package com.programmers.java.stream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main2 {
    public static void main(String[] args) {
        Arrays.asList(1,2,3).stream();

        // IntStream : primitive 타입을 위한 stream 타입
        IntStream s = Arrays.stream( new int [] {1,2,3});

        Stream<Integer> ss = Arrays.stream( new int [] {1,2,3}).boxed();
//        List<Integer> l = ss.collect(Collectors.toList());
        ss.toArray(Integer[] :: new); // type을 지정해주지 않으면 Object

        ////////////////////////////////////////
        // Stream 만들기
        Stream.generate(()-> 1)
                .limit(10)
                .forEach(System.out::println);

        Stream.iterate(0,(i) -> i+1)
                .limit(10)
                .forEach(System.out::println); //seed 값부터

    }
}
