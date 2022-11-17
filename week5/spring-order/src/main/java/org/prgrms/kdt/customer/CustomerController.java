package org.prgrms.kdt.customer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;

@Controller
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


//    @GetMapping( "/customers")
    @RequestMapping(value = "/customers", method = RequestMethod.GET)
    public String findCustomers(Model model){
        var allCustomers = customerService.getAllCustomers();
        // 이름에 해당하는 veiw를 찾고 해당 view가 랜더링 된다. -> 해당 view customers.jsp가 WEB-INF안에 존재해야한다.\
        model.addAttribute("serverTime", LocalDateTime.now());
        model.addAttribute("customers",allCustomers);
        return "views/customers";

    }
}
