package org.prgrms.kdt;

import org.prgrms.kdt.order.OrderProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KdtApplication {

    public static void main(String[] args) {

        var springApplication = new SpringApplication(KdtApplication.class);
        springApplication.setAdditionalProfiles("dev");
        var applicationContext = springApplication.run(args);

        //var applicationContext = SpringApplication.run(KdtApplication.class, args);
//
//        var orderProperties = applicationContext.getBean(OrderProperties.class);
//        System.out.println("test -- "+orderProperties.getVersion());
        applicationContext.close(); // 컨테이너에 등록된 모둔 Bean 소멸
    }

}
