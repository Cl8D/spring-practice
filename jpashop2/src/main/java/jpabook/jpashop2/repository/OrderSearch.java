package jpabook.jpashop2.repository;

import jpabook.jpashop2.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
// 주문 검색 기능 개발 - JPA의 동적 쿼리
public class OrderSearch {
    private String memberName; // 회원 이름
    private OrderStatus orderStatus; // 주문 상태
}
