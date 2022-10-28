package org.example;

public class Main {
    public static void main(String[] args) {

        Setting setting1 = Setting.getInstance();
        Setting setting2 = Setting.getInstance();

        if (setting1 == setting2){
            System.out.println("Hello world!");
        }

    }
}