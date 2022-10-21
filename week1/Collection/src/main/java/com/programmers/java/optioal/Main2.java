package com.programmers.java.optioal;

import com.programmers.java.collection.User;

import java.util.Optional;

public class Main2 {
    public static void main(String[] args) {
        // 값이 없을때
        Optional<User> optionalUser = Optional.empty();

        // 값이 있을때
        optionalUser = Optional.of(new User(5,"kim"));

        optionalUser.isEmpty(); // 값이 없으면(==null) ture
        optionalUser.isPresent(); // 값이 있으면 ture

        optionalUser.ifPresent(user -> {
        });

        optionalUser.ifPresentOrElse(user -> {}, () ->{});

    }
}
