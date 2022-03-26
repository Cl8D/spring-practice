package jpabook.jpashop3.domain;

import jpabook.jpashop3.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@Table(name="order_item")
public class OrderItem {
    @Id @GeneratedValue
    @Column(name="order_item_id")
    private Long id;

    // orderItem(n) - Item(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    // order(1) - orderItem(n)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    private int orderPrice;
    private int count;


    // 주문 생성 메서드
    public static OrderItem createOrderItem (Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    // 주문 상품 취소
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
