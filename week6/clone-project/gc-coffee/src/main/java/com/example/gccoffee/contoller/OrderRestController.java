package com.example.gccoffee.contoller;

import com.example.gccoffee.model.Email;
import com.example.gccoffee.model.Order;
import com.example.gccoffee.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderRestController {

    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/api/v1/orders")
    public Order createOrder(@RequestBody CreateOrderRequest createOrderRequest){
        // CreateOrderRequest -> json을 표현하기 위한 타입을 정의할 수 있다. (DTO를 message포맷으로 변경시키기 위한 정의할 수 있다.)
        // service에서 인지하는 것보다. service는 service 자체의 메세지 시그니쳐를 가져가고
        // controller가 service가 필요한 value Object나 parameter의 변수로 변환하도록 동일한 값을 넘겨 줄지라고 계층을 구분하는 것이 좋다.
        return orderService.createOrder(
               new Email(createOrderRequest.email()),
                createOrderRequest.address(),
                createOrderRequest.postcode(),
                createOrderRequest.orderItems()
        );
    }

}
