package jpabook.jpashop2.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

@Entity
@Table(name="orders")
@Getter @Setter
// 객체 생성을 통한 set 메서드를 이용하여 주문 생성 방지
@NoArgsConstructor(access= PROTECTED)
public class Order {

    @Id @GeneratedValue
    @Column(name="order_id")
    private Long id;

    // 하나의 회원은 여러 개의 주문을 할 수 있다.
    // order <-> member는 N:1
    // fk인 member_id 작성해주기
    // N쪽을 연관관계의 주인으로 설정해준다.
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    // 모든 엔티티는 값을 저장하고 싶으면 각각 persist 해줘야 하지만,
    // cascade 옵션의 경우 order만 persist해줘도 하위인 orderItem도 함께 persist된다.
    @OneToMany (mappedBy = "order", cascade = ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 마찬가지로 order 저장 시 devliery도 함께 persist하도록 cascade 옵션 지정
    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name="delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // 연관관계 편의 메서드. -> 양방향일 때 헤주면 좋음
    // 양방향으로 세팅되어 있을 때 서로 주입을 해줘야 하는데,
    // 이런 일을 메서드로 묶어서 처리할 수 있도록 해주는 것.
    // 즉, 메인에서 order.setMember(member)만 하더라도
    // 해당 member의 order 정보도 함께 설정이 되도록 하는 것.

    // Order(n) <-> Member(1)
    public void setMember(Member member){
        // 주문을 한 회원을 집어넣기
        this.member = member;
        // 회원이 주문한 목록에 해당 주문을 추가해주기
        member.getOrders().add(this);
    }

    // Order(1) <-> OrderItems(n)
    public void addOrderItem(OrderItem orderItem) {
        // 주문이 들어오면 주문 아이템에 추가해주기
        orderItems.add(orderItem);
        // 주문 아이템이 어떤 주문이었는지 주문 정보 넣어주기
        orderItem.setOrder(this);
    }

    // order(1) <-> delivery(1)
    public void setDelivery(Delivery delivery) {
        // 주문에 대한 배송 정보 넣어주기
        this.delivery = delivery;
        // 배송에 주문에 대한 정보 넣어주기
        delivery.setOrder(this);
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
    // 1, 주문 취소
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
