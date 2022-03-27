package jpabook.jpashop3.repository;

import jpabook.jpashop3.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    // 주문 저장
    public void save (Order order) {
        em.persist(order);
    }

    // 주문 한 개 조회
    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    // 주문 조회 - 검색 이용
    public List<Order> findAllByString(OrderSearch orderSearch) {
        // 주문자와 상태가 동일한 기존 주문 검색하기

        // jpql을 이용한 동적 쿼리
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    // fetch join 활용한 코드
    public List<Order> findAllWithMemberDelivery() {
        // order를 가져올 때 member, delivery까지 한 번에 가져오기
        // 1번의 select절로 한 번에 가져오는 것.
        // 이때 proxy 객체를 가져오는 것이 아니라,
        // 그냥 join을 통해서 실제 db에 있는 애들을 한 번에 가져온다구 생각하자
        String query = "select o from Order o"
                + " join fetch o.member m"
                + " join fetch o.delivery d";

        return em.createQuery(query, Order.class).getResultList();
    }

    // fetch join 활용한 코드 - Item 조회
    public List<Order> findAllWithItem() {
        // distinct를 사용한 이유.
        // order가 2개, orderItem이 4개가 db에 존재하는데,
        // 이를 join하게 되면 결과적으로 컬럼이 4개가 되어버린다.
        // 즉, 같은 orderId에 대해서 orderItem에 맞추다 보니까 데이터가 뻥튀기 될 수 있다는 것!!

        // JPA 입장에서는 order를 조회해보면 동일한 PK값일 때 주소값까지 동일하게 나오는데,
        // distinct를 사용하게 되면 같은 엔티티 조회 시 중복을 걸러주게 된다. (같은 id면 제거해주는 형식인 것 같음,)
        // 이때 db 쿼리에도 distinct로 붙여서 날려주지만, 사실 db에서는 distinct를 사용해서 중복을 제거하려면
        // 둘이 완전히 동일해야 해서... db 자체에서는 join 하면 4개가 그대로 나오기는 함.

        // 그러나, 이 경우에는 페이징이 불가능하다는 단점이 있다. (setFirstREsult, setMaxResults)
        // 이러면 하이버네이트는 db에서 모든 데이터를 읽어서 메모리 단에서 페이징을 해버림...
        // 메모리 내에서 수많은 데이터를 올려서 페이징을 해버리면... 큰일난다.
        String query = "select distinct o from Order o"
                        + " join fetch o.member m"
                        + " join fetch o.delivery d"
                        + " join fetch o.orderItems oi"
                        + " join fetch oi.item i";

        return em.createQuery(query, Order.class).getResultList();
    }

    // cf) fetch join 관련 너무 좋은 QnA! 나중에 다시 보면서 정리하기 - https://www.inflearn.com/questions/15876


    // xToOne 관계를 fetch join 시켜주기 + 페이징 처리
    public List<Order> findAllWithMemberDelivery (int offset, int limit) {

        // order(n) - member(1)
        // order(n) - delivery(1)
        String query = "select o from Order o"
                        + " join fetch o.member m"
                        + " join fetch o.delivery d";

        return em.createQuery(query, Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
