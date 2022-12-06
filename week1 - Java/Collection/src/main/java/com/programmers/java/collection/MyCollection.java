package com.programmers.java.collection;

import com.programmers.java.iter.MyInterator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class MyCollection<T> {
    private List<T> list;

    public MyCollection(List<T> list){
        this.list = list;
    }

    public MyCollection<T> filter(Predicate<T> predicate){
        List<T> newList = new ArrayList<>();
        foreach(d -> {
            if (predicate.test(d)) { //test 값 -> 필터가 참이면
                newList.add(d);
            }
        });
        return new MyCollection<>(newList);
    }

    // 이 메서드에서 <U>를 제네릭으로 활용해라
    public <U> MyCollection<U> map(Function<T,U> function){
        List<U> newList = new ArrayList<>();
        foreach(d -> newList.add(function.apply(d)));
        return new MyCollection<>(newList);
    }

    public void foreach(Consumer<T> consumer){
        for (int i = 0; i < list.size(); i++){
            T data = list.get(i);
            consumer.accept(data);
        }
    }

    int size(){
        return list.size();
    }
    // 인터페이스를 메개변수로 넣어도되는것이군,,,,,,

    public MyInterator<T> iterator(){
        return new MyInterator<T>() {
            private int idx = 0;
            @Override
            public boolean hasNext() {
                return idx < list.size();
            }
            @Override
            public T next() {
                return list.get(idx++);
            }
        };
    }
}
