package org.example;

public class Setting {

    // private 생성자를 만들면 밖에서는 호출이 안되고 새로운 인스턴스를 만들 수 없다.
    private Setting(){
        System.out.println("셍성자 호춯 어 ");
    }

    //inner class
    private static class SettingsHolder{
        private static final Setting INSTANCE = new Setting();
    }

    // 내부에서 인스턴스를 만들어 전역에서 접근이 가능하게 해야한다. -> static
    public static Setting getInstance(){
        return SettingsHolder.INSTANCE;
    }
}
