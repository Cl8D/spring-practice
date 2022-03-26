package jpabook.jpashop3.repository;

import jpabook.jpashop3.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {
    // 회원 이름 / 주문 상태
    private String memberName;
    private OrderStatus orderStatus;
}
