package jpabook.jpashop2.domain;

import jpabook.jpashop2.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter @Setter
// 생성자를 protected로 두는 코드의 축약 어노테이션
@NoArgsConstructor(access = PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name="order_item_id")
    private Long id;

    // 상품 하나에 여러 개의 주문이 들어갈 수 있기 때문에,
    // 상품과 주문 상품의 관계는 1:N
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    // 하나의 주문은 여러 개의 주문 아이템을 가질 수 있다.
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    private int orderPrice; // 주문 가격

    private int count; // 주문 수량

    /*
    // new 객체를 만들어서 set을 통한 주문 생성을 막기 위해 생성자를 protected 형으로 막아버리기
    protected OrderItem() {

    }
    */

    // 생성 메서드
    public static OrderItem createOrderItem (Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        // 상품 주문하면 재고가 까여야 하니까
        item.removeStock(count);
        return orderItem;
    }


    // 비즈니스 로직
    // 1. 주문 상품 취소
    public void cancel() {
        // 재고를 다시 주문했던 수량만큼 늘려주기
        getItem().addStock(count);
    }

    // 주문 상품의 총 가격
    public int getTotalPrice() {
        // 주문 상품의 개수에 가격 곱해주기
        return getOrderPrice() * count;
    }
}
