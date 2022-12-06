package org.prgrms.kdt;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.prgrms.kdt.order.OrderItem;
import org.prgrms.kdt.order.OrderService;
import org.prgrms.kdt.order.OrderStatus;
import org.prgrms.kdt.voucher.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

//@ExtendWith(SpringExtension.class) //test context와 상호작용하게 해줌
//@ContextConfiguration
@SpringJUnitConfig
@ActiveProfiles("local")
public class KdtSpringContextTest {

    @Configuration
    @ComponentScan
    static  class Config{

    }
    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    OrderService orderService;

    @Autowired
    VoucherRepository voucherRepository;

    @Test
    @DisplayName("application context가 생성된다.")
    public void testApplicationContext(){
        assertThat(applicationContext, notNullValue());
    }

    @Test
    @DisplayName("VoucherRepository가 빈으로 등록되어있어야한다.")
    public void testVoucherRepository(){
        var been = applicationContext.getBean(VoucherRepository.class);
        assertThat(been, notNullValue());

    }

    @Test
    @DisplayName("OrderService를 사용해서 주문을 생성할수있다.")
    public void testOrderService(){

        // Given
        var fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(),100);
        voucherRepository.insert(fixedAmountVoucher);

        // When
        var order = orderService.createOrder(UUID.randomUUID(), List.of(new OrderItem(UUID.randomUUID(), 200, 1)),
                fixedAmountVoucher.getVoucherId());

        //Then
        assertThat(order.totalAmount(), is(100L));
        assertThat(order.getVoucher().isEmpty(), is(false));
        assertThat(order.getVoucher().get().getVoucherId(), is(fixedAmountVoucher.getVoucherId()));
        assertThat(order.getOrderStatus(), is(OrderStatus.ACCEPTED));

    }
}
