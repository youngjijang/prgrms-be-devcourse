package com.example.kdtspringjpa.order.service;

import com.example.kdtspringjpa.domain.Order;
import com.example.kdtspringjpa.domain.repository.OrderRepository;
import com.example.kdtspringjpa.order.converter.OrderConverter;
import com.example.kdtspringjpa.order.dto.OrderDto;
import com.example.kdtspringjpa.order.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;


@Service
public class OrderService {


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderConverter orderConverter;

    @Transactional
    public String save(OrderDto orderDto) {
        // 1. dto -> entity 변환 (준영속)
        Order order = orderConverter.convertOrder(orderDto);
        // 2. orderRepository.save(entity) -> 영속화
        Order entity = orderRepository.save(order);
        // 3. 결과 반환
        return entity.getUuid(); // entity 객체가 아닌 id만 반
    }

    @Transactional
    public OrderDto findOne(String uuid) throws NotFoundException {
        // 1. 조회를 위한 키값 인자로 받기
        // 2. orderRepository.findByIdd(uuid) -> 조회(영속화된 엔티티)
        OrderDto orderDto = orderRepository.findById(uuid)
                .map(orderConverter::convertOrderDto) // 3. entity -> dto
                .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));

        return orderDto;
    }

    @Transactional
    public Page<OrderDto> findAll(Pageable pageable){
        return orderRepository.findAll(pageable) //pageable 객체를 이용하면 손쉽 page 쿼리를 보낼 수 있다.
                .map(orderConverter::convertOrderDto);
    }
}
