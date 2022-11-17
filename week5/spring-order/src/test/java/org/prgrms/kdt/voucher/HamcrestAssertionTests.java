package org.prgrms.kdt.voucher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class HamcrestAssertionTests {

    @Test
    @DisplayName("여러 hamcrest matcher 테스트")
    void hamcrestTest() {
        assertEquals(2, 1 + 1);
        assertThat(1 + 1, equalTo(2)); //MatcherAssert 메소드
        assertThat(1 + 1, is(2));
        assertThat(1 + 1, anyOf(is(2), is(3)));

        assertNotEquals(1,1+1);
        assertThat(1+1, not(equalTo(1)));
    }


    // list나 collection을 테스트 하기 위한 편리성을 제공한다.
    @Test
    @DisplayName("컬렉션에 대한 matcher 테스트")
    void hamcrestListMatcherTest(){
        var prices = List.of(1,2,3);
        assertThat(prices, hasSize(3));
        assertThat(prices, everyItem(greaterThan(0)));
        assertThat(prices, containsInAnyOrder(1,2,3));
        assertThat(prices, hasItem(greaterThanOrEqualTo(2)));
    }
}
