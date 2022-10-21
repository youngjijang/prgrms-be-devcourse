package com.programmers.java.default3;

interface Ability{
    static void sayHello(){
        System.out.println("Hello");
    }
}

interface Flyable {
    default void fly(){
        System.out.println("fly");
    }
}

interface Swimmable {
    default void swim(){
        System.out.println("swim");
    }
}

interface Wolkable{
    default void wolk(){
        System.out.println(("wolk"));
    }
}
