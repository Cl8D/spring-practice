package jpabook.jpashop2.repository;

import jpabook.jpashop2.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    // 주문 저장
    public void save(Order order) {
        em.persist(order);
    }

    // 주문 한 개 조회
    public Order findOne(Long id){
        return em.find(Order.class, id);
    }


    // 주문 조회 - 검색 이용
    /*
    public List<Order> findALl(OrderSearch orderSearch) {

    }
    */
}
