package com.example.kdtjpaorder.domain.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity{
    @Id
    @Column(name = "id")
    private String uuid;

    @Column(name = "order_datetime", columnDefinition = "TIMESTAMP")
    private LocalDateTime orderDatetime;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Lob
    private String memo;

    @Column(name = "member_id", insertable = false, updatable = false) // fk
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY) // 회원 한명에게 여러 주문
    @JoinColumn(name = "member_id", referencedColumnName = "id") // 명시하지 않을경우 해당필드명_PK필드명
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true ) // 연관관계 주인 설정 -> 연관관계 주인 객체의 필드값
    private List<OrderItem> orderItems = new ArrayList<>();

    public void setMember(Member member){
        if(Objects.nonNull(this.member)){
            this.member.getOrders().remove(this);
        }
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem item) {
        item.setOrder(this);
    }
}
