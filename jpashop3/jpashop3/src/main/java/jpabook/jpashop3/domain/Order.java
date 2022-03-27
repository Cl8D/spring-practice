package jpabook.jpashop3.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Getter
@Setter
public class Order {
    @Id @GeneratedValue
    @Column(name="order_id")
    private Long id;

    // order(n) - member(1)
    // ~one으로 끝나면 fetchType 웬만하면 lazy로!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    //@JsonIgnore
    private Member member;


    // 컬렉션에 적용할 때는 이런 식으로 적용 가능
    // @BatchSize(size=1000)
    // order(1) - orderItem(n)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // order(1) - deliver(1)
    //@JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // 연관관계 메서드
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    // 주문 생성 메서드 (복잡하니까 따로 메서드로 빼두기)
    // 주문 회원, 배송 정보, 주문 상품의 정보를 받아서 실제 주문 엔티티를 생성한다.
    public static Order createOrder (Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    // 비즈니스 로직
    // 1. 주문 취소
    public void cancel () {
        // 배송 상태가 이미 완료되었다면
        if(delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능합니다.");
        }
        // 그게 아니라면 주문 상태 취소로 변경
        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem : orderItems) {
            // 주문 상품 모두 취소를 해주기
            orderItem.cancel();
        }
    }

    // 조회 로직
    // 1. 전체 주문 가격이 얼마인지 조회
    public int getTotalPrice() {
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
