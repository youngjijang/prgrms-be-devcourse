package com.example.gccoffee.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    @DisplayName("")
    public void testInvalidEmail(){
        assertThrows(IllegalArgumentException.class, ()->{
            var email = new Email("assss");
        });
    }

    @Test
    @DisplayName("")
    public void testValidEmail(){
        var email = new Email("youngji804@naver.com");
        assertEquals(email.getAddress(),"youngji804@naver.com");
    }

    @Test
    @DisplayName("")
    public void testEqEmail(){
        var email1 = new Email("youngji804@naver.com");
        var email2 = new Email("youngji804@naver.com");
        assertTrue(email1.equals(email2));
    }

}