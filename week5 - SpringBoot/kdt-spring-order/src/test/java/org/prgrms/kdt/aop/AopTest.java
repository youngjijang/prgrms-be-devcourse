package org.prgrms.kdt.aop;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgrms.kdt.voucher.FixedAmountVoucher;
import org.prgrms.kdt.voucher.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.UUID;


@SpringJUnitConfig
@ActiveProfiles("local")
public class AopTest {

    @Configuration
    @ComponentScan(basePackages = {"org.prgrms.kdt.voucher","org.prgrms.kdt.aop"})
    @EnableAspectJAutoProxy
    static  class Config{

    }

    @Autowired
    ApplicationContext applicationContext;


    @Autowired
    VoucherRepository voucherRepository;

    @Test
    @DisplayName("Aop test")
    public void testOrderService(){

        var fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(),100);
        voucherRepository.insert(fixedAmountVoucher);
    }
}
