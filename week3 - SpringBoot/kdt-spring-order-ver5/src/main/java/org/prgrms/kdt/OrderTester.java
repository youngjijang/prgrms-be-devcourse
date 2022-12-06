package org.prgrms.kdt;

import com.fasterxml.jackson.core.util.BufferRecycler;
import org.prgrms.kdt.order.OrderItem;
import org.prgrms.kdt.order.OrderProperties;
import org.prgrms.kdt.order.OrderService;
import org.prgrms.kdt.voucher.FixedAmountVoucher;
import org.prgrms.kdt.voucher.VoucherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderTester {

    // static으로 생성해야 단 하나의 logger만 만들어지게 한다.
    // org.prgrms.kdt.OrderTester
    private static final Logger logger = LoggerFactory.getLogger(OrderTester.class); //메소드가 아니라 class 단위에서 생성
    public static void main(String[] args) throws Exception{
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS); // conversion

    //    var applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);

        var applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(AppConfiguration.class);
        var environment = applicationContext.getEnvironment();
        environment.setActiveProfiles("local");
        applicationContext.refresh(); //profile이 재대로 적용될 수 있게 refresh


        var orderProperties = applicationContext.getBean(OrderProperties.class);
        logger.info("logger name => {}",logger.getName());
        logger.info("version : {}",orderProperties.getVersion());
        logger.info("minimumOrderAmount : {}",orderProperties.getMinimumOrderAmount());
        logger.info("SupportVendors : {}",orderProperties.getSupportVendors());
        logger.info("Description : {}",orderProperties.getDescription());


        var customId = UUID.randomUUID();
        var voucherRepository = applicationContext.getBean(VoucherRepository.class);
        var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(),10L));
        var orderService = applicationContext.getBean(OrderService.class);
        var order = orderService.createOrder(customId,new ArrayList<OrderItem>() {{
            add(new OrderItem(UUID.randomUUID(), 100L, 1));
        }},voucher.getVoucherId());

        Assert.isTrue(order.totalAmount() == 90L, MessageFormat.format("totalAmount {0} is not 90L",order.totalAmount()));

        applicationContext.close(); // 컨테이너에 등록된 모둔 Bean 소멸
    }
}