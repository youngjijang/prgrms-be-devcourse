package com.programmers.java.poly;

public class UserService implements Login{
    private Login login; // 캡슐화

    // 의존성을 외부에 맡겨 의존도를 낮춘다. -> 여러 기능을 수행할 잠재성을 가짐
    // 구상체와 결합하면 결합성 강해진다. => 추상체와 결합하여 결합도를 낮춘다.
    public UserService(Login login){ //의존성 주입, Dependency Injection
        this.login = login;
    }

    @Override
    public void login() {
        login.login();
    }
}
