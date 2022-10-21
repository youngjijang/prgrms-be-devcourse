package com.programmers.java.collection;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
//        MyCollection<String> c1 = new MyCollection<>(Arrays.asList("11","2222","3","444","5"));
//        c1.foreach(str -> System.out.println(str));
//
//        MyCollection<Integer> c2 = c1.map(str ->str.length());
//        c2.foreach(str -> System.out.println(str));

        /////////////////////////////////////////
        // 메소드 체이닝 방식
        int s = new MyCollection<>(Arrays.asList("11","2222","3","444","5"))
                .map(str ->str.length())
                .filter(i ->i%2 == 1)
                .size();
        System.out.println(s);
                //.foreach(str -> System.out.println(str));
    }
}
