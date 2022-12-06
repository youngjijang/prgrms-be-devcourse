package com.programmers.java.default3;
class Duck implements Flyable,Wolkable,Swimmable{}
class Swan implements Flyable,Swimmable{}

public class Main {
    public static void main(String[] args) {
        new Duck().fly();
        new Swan().swim();

        Ability.sayHello();
    }
}
