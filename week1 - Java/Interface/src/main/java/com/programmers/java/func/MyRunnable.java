package com.programmers.java.func;

@FunctionalInterface
public interface MyRunnable {
    void run(); // 추상메소드가 하나밖에 없는 메소드 == 함수형 인터페이스
}

@FunctionalInterface
interface MyMap{
    void map();
    default void sayHello(){
        System.out.println("Hello World");
    }
    static void sayBye(){
        System.out.println("Bye World");
    }
}
