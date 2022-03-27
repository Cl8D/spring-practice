package jpabook.jpashop3.repository.order.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class OrderItemQueryDto {
    // jsonIgnore를 해주면 orderId는 json으로 만들어지지 않는다!
    // dto니까 이런 식으로 화면 출력을 제어할 수 있음.
    @JsonIgnore
    private Long orderId; //주문번호

    private String itemName;//상품 명
    private int orderPrice; //주문 가격
    private int count; //주문 수량

    public OrderItemQueryDto(Long orderId, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }

}
