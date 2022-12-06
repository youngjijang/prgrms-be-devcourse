package com.programmers.java.baseball.engin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.function.BiConsumer;

@AllArgsConstructor
@ToString
public class Numbers {
    private Integer[] nums;
    public void indexedForEach(BiConsumer<Integer,Integer> consumer){ // 정답값, index값
        for (int i =0;i<nums.length; i++){
            consumer.accept(nums[i],i);
        }
    }
}
