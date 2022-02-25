package jpabook.jpashop.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
// db에 보통 order by라는 게 있으니까 이름을 orders로 해주기
@Table(name="ORDERS")
public class Order {
    @Id
    @GeneratedValue
    @Column(name="ORDER_ID")
    private Long id;

    // @Column(name="MEMBER_ID")
    // private Long memberId;

    // order 입장에서 member는 1개.
    // 왜냐면 회원은 여러 개의 주문을 할 수 있지만,
    // 주문은 하나의 회원과 매칭이 되니까
    @ManyToOne
    // 외래키로 매핑
    @JoinColumn(name= "MEMBER_ID")
    private Member member;

    // 양방향 설계. Order <-> OrderItem
    // OrderItem의 ManyToOne의 order_ID의 역방향을 설계하자.
    // 마찬가지로 주인은 order_id 외래키를 가진 order가 되는 것
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();


    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        // 양방향 연관관계
        orderItem.setOrder(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }


}
