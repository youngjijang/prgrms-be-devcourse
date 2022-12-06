package org.prgrms.kdt;

import com.fasterxml.jackson.core.util.BufferRecycler;
import org.prgrms.kdt.order.OrderItem;
import org.prgrms.kdt.order.OrderProperties;
import org.prgrms.kdt.order.OrderService;
import org.prgrms.kdt.voucher.FixedAmountVoucher;
import org.prgrms.kdt.voucher.VoucherRepository;
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
    public static void main(String[] args) throws Exception{

    //    var applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);

        var applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(AppConfiguration.class);
        var environment = applicationContext.getEnvironment();
        environment.setActiveProfiles("local");
        applicationContext.refresh(); //profile이 재대로 적용될 수 있게 refresh

//        var environment = applicationContext.getEnvironment();
//        var version = environment.getProperty("kdt.version");
//        environment.getProperty("kdt.minimum-order-amount", Integer.class);
//        environment.getProperty("kdt.supportVendors", List.class);

        var orderProperties = applicationContext.getBean(OrderProperties.class);
//        System.out.println("test -- "+orderProperties.getVersion());

        var resource = applicationContext.getResource("application.yaml"); //working directory 기준, classpath 가 기본값
        var resource2 = applicationContext.getResource("file:test/sample.txt"); //
        var resource3 = applicationContext.getResource("https://stackoverflow.com/");

        System.out.println(resource.getClass().getCanonicalName());

        var file = resource2.getFile();
        var strings = Files.readAllLines(file.toPath());
        System.out.println(strings.stream().reduce("",(a,b)->a+"\n"+b));

        var readableByChannel = Channels.newChannel(resource3.getURL().openStream());
        var bufferedReader = new BufferedReader(Channels.newReader(readableByChannel, StandardCharsets.UTF_8));
        var contents = bufferedReader.lines().collect(Collectors.joining("\n"));
        System.out.println(contents);

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