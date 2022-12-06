package com.programmers.java.baseball.engin;
// engin 밖의 코드는 호스코드 (app)

import com.programmers.java.baseball.engin.io.Input;
import com.programmers.java.baseball.engin.io.NumberGenerator;
import com.programmers.java.baseball.engin.io.Output;
import com.programmers.java.baseball.engin.model.BallCount;
import com.programmers.java.baseball.engin.model.Numbers;
import lombok.AllArgsConstructor;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class BaseBall implements Runnable {
    private final int COUNT_OF_NUMBER = 3;

    private NumberGenerator generator;
    private Input input; // 인터페이스를 변수로 선언한면 생성은 안해줘도 되는거?
    private Output output;

    @Override
    public void run() {
        Numbers answer = generator.generate(COUNT_OF_NUMBER);

        while (true){
            String inputString = input.input("숫자를 맞춰보세요 : ");
            Optional<Numbers> inputNumbers = parse(inputString);
            if(inputNumbers.isEmpty()){
                output.inputError();
                continue;
            }

            BallCount bc = ballCount(answer, inputNumbers.get());
            output.ballCount(bc);
            if (bc.getStrike() == COUNT_OF_NUMBER){
                output.correct();
                break;
            }
        }
    }

    private BallCount ballCount(Numbers answer, Numbers inputNumbers) {
        AtomicInteger strike = new AtomicInteger(); // 스코프안에서 값을 변경하기 위해 동기화 기능을 추가해줘야한다.
        AtomicInteger ball = new AtomicInteger();

        answer.indexedForEach((a,i)->{
            inputNumbers.indexedForEach((n,j)->{
                if(!a.equals(n)) return;
                if(Objects.equals(i, j)) strike.addAndGet(1);
                else ball.addAndGet(1);
            });
        });
        return new BallCount(strike.get(),ball.get());
    }

    private Optional<Numbers> parse(String inputString) {
        if(inputString.length() != COUNT_OF_NUMBER) return Optional.empty();

        long count = inputString.chars()
                .filter(Character::isDigit)
                .map(Character:: getNumericValue)
                .filter(i -> i>0)
                .distinct()
                .count();
        if(count != COUNT_OF_NUMBER) return Optional.empty();
        return Optional.of(
                new Numbers(
                        inputString.chars()
                                .map(Character::getNumericValue)
                                .boxed()// int -> integer
                                .toArray(Integer[]::new)
                )
        );
    }
}
