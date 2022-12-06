package org.prgrms.kdt.customer.contollor;

import org.prgrms.kdt.customer.service.CustomerService;
import org.prgrms.kdt.customer.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@CrossOrigin(origins ="*")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);


    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping( "/api/v1/customers") // api를 만들때 대체로 version을 입력해준다.
    @ResponseBody
    public List<Customer> findCustomers(Model model){
        return customerService.getAllCustomers();
    }

    @GetMapping( "/api/v1/customers/{customerId}")
    @ResponseBody
    public ResponseEntity<Customer> findCustomer(@PathVariable("customerId") UUID customerId){
        var customer = customerService.getCustomer(customerId);
        return customer.map(v -> ResponseEntity.ok(v)).orElse(ResponseEntity.notFound().build()); // 202
    }

    @PostMapping( "/api/v1/customers/{customerId}")
    @ResponseBody
    public CustomerDTO saveCustomer(@PathVariable("customerId") UUID customerId, @RequestBody CustomerDTO customer){
        logger.info("Got customer save request {}",customer);
        return customer;
    }


    @GetMapping( "/customers")
    public String viwCustomersPage(Model model){
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
