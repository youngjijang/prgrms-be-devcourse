package org.prgrms.kdt;

import java.util.UUID;

public class OrderItem {
    private final UUID productId;
    private final long productPrice;
    public final long quantity;

    public OrderItem(UUID productId, long productPrice, int quantity) {
        this.productId = productId;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public long getQuantity() {
        return quantity;
    }
}

//public record OrderItem(UUID productId,
//                        long productPrice,
//                        long quantity) {
//}