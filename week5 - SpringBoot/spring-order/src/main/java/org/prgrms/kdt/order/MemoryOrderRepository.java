package org.prgrms.kdt.order;

import org.prgrms.kdt.order.Order;
import org.prgrms.kdt.order.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Repository
public class MemoryOrderRepository implements OrderRepository {

    private  final Map<UUID, Order> storage = new ConcurrentHashMap<>();

    @Override
    public Order insert (Order order) {
        storage.put(order.getCustomerId(),order);
        return order;
    }

}
