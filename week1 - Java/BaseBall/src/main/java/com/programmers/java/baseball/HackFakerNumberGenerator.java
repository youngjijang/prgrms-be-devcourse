package com.programmers.java.baseball;

import com.github.javafaker.Faker;
import com.programmers.java.baseball.engin.io.NumberGenerator;
import com.programmers.java.baseball.engin.model.Numbers;

import java.util.stream.Stream;

public class HackFakerNumberGenerator implements NumberGenerator {

    private final Faker faker = new Faker(); // 하나로 계속 사용할거니까 전역
    @Override
    public Numbers generate(int count) {
        Numbers num =  new Numbers(
                Stream.generate(()-> faker.number().randomDigitNotZero())
                        .distinct()
                        .limit(count)
                        .toArray(Integer[]::new)
        );
        System.out.println(num);
        return num;
    }
}
