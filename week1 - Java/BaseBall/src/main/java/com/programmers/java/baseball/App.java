package com.programmers.java.baseball;

import com.programmers.java.baseball.engin.BaseBall;
import com.programmers.java.baseball.engin.io.Input;
import com.programmers.java.baseball.engin.io.NumberGenerator;
import com.programmers.java.baseball.engin.io.Output;

public class App {
    public static void main(String[] args) {

        NumberGenerator generator = new HackFakerNumberGenerator();
        Console console = new Console();

        new BaseBall(generator,console,console).run();
    }
}
