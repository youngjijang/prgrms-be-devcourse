package com.programmers.java.iter;

import com.programmers.java.collection.MyCollection;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> list = Arrays.asList("A","CC","BF","DFD");
        Iterator<String> iter = list.iterator();

        while(iter.hasNext()){
            System.out.println(iter.next());
        }

        MyInterator<String> iter2 =
                new MyCollection<>(Arrays.asList("A","CC","BF","DFD"))
                        .iterator();
        while ((iter2.hasNext())){
            String s = iter2.next();
            int len = s.length();
            if(len % 2 == 0)
                System.out.println(s);
        }

    }
}
