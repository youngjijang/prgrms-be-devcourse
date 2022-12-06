package com.example.gccoffee;

import org.junit.jupiter.api.*;

public class TestClassTest {

    @BeforeEach
    void beforeEach() {
        System.out.println(String.format("[%s] : %s", "beforeEach", this));
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println(String.format("[%s] : %s", "beforeAll", "beforeAll"));
    }

    @AfterAll
    static void afterAll() {
        System.out.println(String.format("[%s] : %s", "afterAll", "afterAll"));
    }

    @Test
    void test1() {
        System.out.println(String.format("[%s] : %s", "test1", this));
    }

    @Test
    void test2() {
        System.out.println(String.format("[%s] : %s", "test2", this));
    }

    @Test
    void test3() {
        System.out.println(String.format("[%s] : %s", "test3", this));
    }

    @AfterEach
    void afterEach() {
        System.out.println(String.format("[%s] : %s", "afterEach", this));
    }

}