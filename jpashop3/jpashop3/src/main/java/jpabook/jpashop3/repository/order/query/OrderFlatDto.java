package jpabook.jpashop3.repository.order.query;

import jpabook.jpashop3.domain.Address;
import jpabook.jpashop3.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

// SQL 조인의 결과를 그대로 가져올 수 있도록 전부 다 DTO에 넣어주는 형태
@Data
public class OrderFlatDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private Address address;
    private OrderStatus orderStatus;

    private String itemName;//상품 명
    private int orderPrice; //주문 가격
    private int count; //주문 수량

    public OrderFlatDto(Long orderId, String name, LocalDateTime orderDate,
                        OrderStatus orderStatus, Address address,
                        String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }

}
