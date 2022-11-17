package org.prgrms.kdt.customer;

import org.prgrms.kdt.servlet.KdtWebApplicationInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);


    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping( "/customers")
    public String findCustomers(Model model){
        var allCustomers = customerService.getAllCustomers();
        // 이름에 해당하는 veiw를 찾고 해당 view가 랜더링 된다. -> 해당 view customers.jsp가 WEB-INF안에 존재해야한다.\
        model.addAttribute("serverTime", LocalDateTime.now());
        model.addAttribute("customers",allCustomers);
        return "views/customers";

    }

    @GetMapping( "/customers/{customerId}")
    public String findCustomer(@PathVariable("customerId") UUID customerId, Model model){
        // PathVariable을 통해 값이 들어오며 자동으로 형변환을해준다. 실패하면 에러
        var maybeCustomer = customerService.getCustomer(customerId);
        if(maybeCustomer.isPresent()){
            model.addAttribute("customer",maybeCustomer.get());
            return "views.customer-details";
        }
        return "views/404";
    }

    @GetMapping("/customers/new")
    public String viewNewCustomerPage(){
        return "views/new-customers";
    }

    @PostMapping("/customers/new")
    public String addNewCustomerPage(CreateCustomerRequest createCustomerRequest){
        // Spring MVC에서 form 데이터를 알아서 바꿔준다. field이름이 같아야함.
        // controller는 외부로 부터 DTO로 받아서 Validation이나, http 핸들을 수행한다. 도메인 로직은 Service와 entity에서
        customerService.createCustomer(createCustomerRequest.email(),createCustomerRequest.name());
        return "redirect:/customers";
    }

}
