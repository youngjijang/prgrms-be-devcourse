package com.programmers.java.default2;

public class Main {
    public static void main(String[] args) {
        new Service().method1();
    }

}

// Interface 메소드를 전부 사용하지 않을 경우 Adapter를 만들어(빈 메소드로 구현됨) 상속받아서 원하는 메소드만 오버라이딩 하는 방식을 사용한다.
// Adapter를 사용할 경우 상속은 하나만 받을 수 있기때문에 이미 다른 곳을 상속 받았을 경우 사용하지 못한다.
// => 그럴 경우 interface method를 전부 defualt로 만든 후 원하는 메소드만 오버라이딩 하면 강제성이 없어 코드를 원하는 메소드만 깔끔하게 유지 가능
class Service extends MyInterfaceAdapter{
    @Override
    public void method1(){
        System.out.println("Hello");
    }
}
