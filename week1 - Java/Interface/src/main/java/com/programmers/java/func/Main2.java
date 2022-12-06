package com.programmers.java.func;

public class Main2 {
    public static void main(String[] args) {
        // 1. functional interface를 익명 객체로 만들 경우 무조건 하나의 method를 override를 해줘야한다.
        MyRunnable r = new MyRunnable() {
            @Override
            public void run() {
                System.out.println("run");
            }
        };
        r.run();

        // 어차피 하나의 메소드만 override 해줘야하게 때문에 더 간단하게 표현할 수 있다. => 익명메소드
        // 구현부가 한줄일 경우 {} 생략가능
        MyRunnable r2 = () -> {
            System.out.println("run~~~");
        };
    }
}

