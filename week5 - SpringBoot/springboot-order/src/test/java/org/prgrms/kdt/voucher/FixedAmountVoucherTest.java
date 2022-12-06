package org.prgrms.kdt.voucher;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FixedAmountVoucherTest { // FixedAmountVoucher -> SUT
    private static final Logger logger = LoggerFactory.getLogger(FixedAmountVoucherTest.class);
    @BeforeAll
    static void setup(){
        logger.info("@BeforeAll - 단 한번 실행");
    }

    @BeforeEach
    void init(){
        logger.info("@BefeorEach - 매 테스트 마다 실행");
    }

    @Test
    @DisplayName("기본적인 assertEqual 테스트 ")
    @Disabled // 이 테스트 건너뛰기
    void testAssertEqual(){ // 어떠한 값도 return 하면 안된다.
        assertEquals("1","1");
    }

    @Test
    @DisplayName("주어진 금액만큼 할인을 해야한다.")
    void testDiscount() {
        var sut = new FixedAmountVoucher(UUID.randomUUID(),100);
        assertEquals(900, sut.discount(1000));
    }

    @Test
    @DisplayName("할인된 큼액은 마이너스가 될 수 없다.")
    void testMinusDiscountAmount() {
        var sut = new FixedAmountVoucher(UUID.randomUUID(),1000);
        assertEquals(0, sut.discount(900));
    }

    @Test
    @DisplayName("할인 금액은 음수가 되서는 안된다")
    void testWithMinus(){
        assertThrows(IllegalArgumentException.class,() ->new FixedAmountVoucher(UUID.randomUUID(),-100));
        // 발생될 예외 class를 인자로 받아 예외가 잘 발생하나 확인 : 예외가 발생하지 않으면 실패
    }

    @Test
    @DisplayName("유효한 할인 금액으로만 생성할 수 있다.")
    void testVoucherCreation(){
        // 여러 케이스의 테스트를 한번에 테스트
        assertAll("FixedAmountVoucher creation",
                ()-> assertThrows(IllegalArgumentException.class,()-> new FixedAmountVoucher(UUID.randomUUID(),0)),
                ()-> assertThrows(IllegalArgumentException.class,()-> new FixedAmountVoucher(UUID.randomUUID(),-100)),
                ()-> assertThrows(IllegalArgumentException.class,()-> new FixedAmountVoucher(UUID.randomUUID(),10000000)));
    }

}