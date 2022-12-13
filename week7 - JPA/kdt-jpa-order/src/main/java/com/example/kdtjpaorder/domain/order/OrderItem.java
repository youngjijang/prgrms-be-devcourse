package com.example.kdtjpaorder.domain.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "order_item")
@Getter
@Setter
public class OrderItem extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private int price;
    private int quantity;

    @Column(name = "order_id", insertable = false, updatable = false) // fk
    private String orderId;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id") // 명시하지 않을경우 해당필드명_PK필드명
    private Order order;

    @Column(name = "item_id") // fk
    private Long itemId;

    public void setOrder(Order order){
        if(Objects.nonNull(this.order)){
            this.order.getOrderItems().remove(this);
        }
        this.order = order;
        order.getOrderItems().add(this);
    }
}