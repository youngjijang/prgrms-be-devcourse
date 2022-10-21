package com.programmers.java.collection;

import java.util.Arrays;

public class Main2 {
    public static void main(String[] args) {
        new MyCollection<User>(
                Arrays.asList(
                        new User(15, "AAA"),
                        new User(16, "AAA"),
                        new User(18, "AAA"),
                        new User(20, "AAA"),
                        new User(24, "AAA"),
                        new User(26, "AAA")
                )
        )
                .filter(u -> u.isOver19(u.getAge()))
                .foreach(user -> System.out.println(user));
    }
}
