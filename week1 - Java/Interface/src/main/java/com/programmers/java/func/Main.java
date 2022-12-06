package com.programmers.java.func;

public class Main {
    public static void main(String[] args) {
        new MyRunnable(){
            @Override
            public void run() {
                System.out.println("run");
            }
        }.run();
    }
}
