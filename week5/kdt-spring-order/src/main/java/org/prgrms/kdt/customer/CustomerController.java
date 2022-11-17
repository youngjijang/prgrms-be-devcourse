package org.prgrms.kdt.customer;

import org.prgrms.kdt.servlet.KdtWebApplicationInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public ModelAndView findCustomers() {
        var allCustomers = customerService.getAllCustomers();
        return new ModelAndView("customers", Map.of("serverTime", LocalDateTime.now(),
                "customers", allCustomers));
        // 이름에 해당하는 veiw를 찾고 해당 view가 랜더링 된다. -> 해당 view customers.jsp가 WEB-INF안에 존재해야한다.
    }
}
