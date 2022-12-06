package com.programmers.java.lambda;

import java.util.function.IntConsumer;

public class Main4 {

    static int num = 200;
    public static void main(String[] args) {
//        int num = 200;
//        num2++;
//        IntConsumer consumer = (i -> System.out.println(i+num2));
//
        new Main4().run();
    }
    void run(){
        int localNum = 100;

        //localNum ++;

        IntConsumer consumer = (i -> {
            System.out.println(i+localNum);
        });
    }

}
