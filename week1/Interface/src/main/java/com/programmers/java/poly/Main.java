package com.programmers.java.poly;

public class Main {
    public static void main(String[] args) {
       UserService s = new UserService(new KakaoLogin());
       s.login();
    }
}
