package com.programmers.java.collection;

public class User {
    public static User EMPTY = new User(0,"");
    private int age;
    private String name;

    public User(int age, String name){
        this.age =age;
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public boolean isOver19(int i) {
        if (i > 19) return true;
        return false;
    }

    @Override
    public String toString() {
        return "User{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}
