package com.programmers.java.defualt;

interface MyInterface {
    void method1(); // 추상메소드
    default void sayHello(){ // default 키워드를 통해 구현체를 가짐
        System.out.println("Hello World");
    }
}
public class Main implements MyInterface{
    public static void main(String[] args) {
        new Main().sayHello();
    }

//    @Override // 오버라이드 가능
//    public void sayHello(){
//        System.out.println("override hello");
//    }
//
    @Override
    public void method1() {
        throw new RuntimeException();
    }
}
