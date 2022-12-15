package com.prgrms;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.runAsync;

public class ThreadLocalApp {
    final static ThreadLocal<Integer> threadLocalValue = new ThreadLocal<>();

    public static void main(String[] args) {
        System.out.println(getCurrentThreadName() + "### main get value = 1" );
        threadLocalValue.set(1);

        a();
        b();

        CompletableFuture<Void> task = runAsync(()->{
            a();
            b();
        });

        task.join();
    }

    public static String getCurrentThreadName(){
        return Thread.currentThread().getName();
    }

    public static void a() {
        Integer value = threadLocalValue.get();
        System.out.println(getCurrentThreadName() + "### a() get value = " + value);
    }

    public static void b() {
        Integer value = threadLocalValue.get();
        System.out.println(getCurrentThreadName() + "### b() get value = " + value);
    }
}
