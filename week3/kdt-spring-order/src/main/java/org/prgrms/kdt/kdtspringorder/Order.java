package org.prgrms.kdt.kdtspringorder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class Order {
    private final UUID orderId; // 식별자
    private final UUID customerId;
    private final List<OrderItem>  orderItems;
    private long discountAmount;
    private  OrderStatus orderStatus = OrderStatus.ACCEPTED;


    public Order(UUID orderId, UUID customerId, List<OrderItem> orderItems, long discountAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderItems = orderItems;
        this.discountAmount = discountAmount;
    }

    public long totalAmount(){
        var beforeDiscount = orderItems.stream().map(v -> {
                    return v.getProductPrice() * v.getQuantity();
                })
                .reduce(0L,long::sum);
        return beforeDiscount -discountAmount;
    }
    public void setDiscountAmount(long discountAmount) {
        this.discountAmount = discountAmount;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
