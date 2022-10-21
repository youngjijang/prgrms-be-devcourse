package com.programmers.java.lambda;

public class Main2 {
    public static void main(String[] args) {
        new Main2().loop(10, i ->{
            System.out.println(i);
            return i*2;
        });

    }

    void loop(int n, MyMapper<Integer,Integer> mapper){ // 기능을 인지로 받아 호스트에서 기능을 결정하도록 한다.
        for (int i=0; i<n;i++){
            mapper.map(i);
        }
    }
}
