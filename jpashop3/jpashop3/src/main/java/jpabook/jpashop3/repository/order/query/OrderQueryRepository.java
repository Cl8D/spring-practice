package jpabook.jpashop3.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * cf)
 * orderRepository -> Order 엔티티를 조회할 때 사용하기 (핵심 비즈니스 로직)
 * orderQueryRepository -> 화면이나 api에 의존관계가 있을 때, 엔티티와는 관련이 없는 애들 관리
 */

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();

        // order=member=delivery로 찾은 목록을 루프 돌기
        result.forEach(o -> {
            // 여기서 이제 orderItem을 찾야 하는데,
            // 여기서 o가 OrderQueryDto를 의미한다.
            // 지금 필드 목록 중에서 List<OrderItemQueryDto> orderItems에 해당하는 (컬렉션)
            // 값을 못 넣었으니까, set을 이용해서 넣어주는 과정.
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    // 1:N 관계인 컬렉션을 제외하고 나머지를 한 번에 조회하도록!
    // order-member-delivery를 join으로 조회하기
    private List<OrderQueryDto> findOrders() {
        String query = "select new "
                + " jpabook.jpashop3.repository.order.query.OrderQueryDto"
                + "(o.id, m.name, o.orderDate, o.status, d.address)"
                + " from Order o"
                + " join o.member m"
                + " join o.delivery d";

        return em.createQuery(query, OrderQueryDto.class)
                .getResultList();
    }

    // 컬렉션인 orderItems 조회하기
    // 이때 orderId를 기준으로 조회해준다.
    private List<OrderItemQueryDto> findOrderItems (Long orderId) {
        String query = "select new "
                + " jpabook.jpashop3.repository.order.query.OrderItemQueryDto"
                + "(oi.order.id, i.name, oi.orderPrice, oi.count)"
                + " from OrderItem oi"
                + " join oi.item i"
                // orderItem의 order의 id가 파라미터로 받은 orderId인 애를 찾기
                + " where oi.order.id = :orderId";

        return em.createQuery(query, OrderItemQueryDto.class)
                // setParamter를 통해 파라미터 바인딩이 가능하다.
                // query의 where절에 있는 orderId를 바인딩하는 것.
                .setParameter("orderId", orderId)
                .getResultList();
    }


    /*******************************/
    // 컬렉션 조회 최적화하기
    public List<OrderQueryDto> findAllByDto_optimization() {
        // XToOne 코드 한 번에 조회하기 (order-member-delivery)
        List<OrderQueryDto> result = findOrders();

        // orderItem 컬렉션을 한 번에 조회
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));

        // 루프를 돌면서 컬렉션 추가해주기 (추가 쿼리 실행 x)
        // map의 key값이 orderId니까 이를 기준으로 value값인 List<OrderItemQueryDto>를 꺼내서
        // result (OrderQueryDto)의 orderItem 컬렉션을 set으로 지정해준다.
        result.forEach(o ->
                o.setOrderItems(orderItemMap.get(o.getOrderId())));

        // 즉, 이렇게 해주면 쿼리를 1번 날리고 map으로 가져온 다음에
        // 메모리에서 값을 세팅해주는 과정.
        return result;
    }

    // orderId 리스트 뽑기
    // order에 있는 id를 다 뽑아와서 리스트로 바꿔주는 과정이다.
    private List<Long> toOrderIds (List<OrderQueryDto> result) {
        return result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        String query = "select new "
                + " jpabook.jpashop3.repository.order.query.OrderItemQueryDto"
                + "(oi.order.id, i.name, oi.orderPrice, oi.count)"
                + " from OrderItem oi"
                + " join oi.item i"
                // id를 하나씩 가져오는 게 아니라, in절을 활용하여 한 번에 가져오도록
                // 이는 즉, orderItem에 해당하는 order의 id를 한 번에 가져온다.
                + " where oi.order.id in :orderIds";

        List<OrderItemQueryDto> orderItems = em.createQuery(query, OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        // 리스트를 map으로 바꿔주는 과정.
        // groupingBy 내부를 풀어쓰면
        // orderItemQueryDto -> orderItemQueryDto.getOrderId()를 의미한다.
        // 즉, orderId를 키값으로 해서 map으로 만들어주는 과정!!
        return orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
    }

    /****************************/

    public List<OrderFlatDto> findAllByDto_flat() {
        String query = "select new"
                + " jpabook.jpashop3.repository.order.query.OrderFlatDto"
                + "(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)"
                // order <=> member <=> delivery 조인
                + " from Order o"
                + " join o.member m"
                + " join o.delivery d"
                // 추가적으로 orderItems까지 조인 (단, db 뻥튀기는 어쩔 수 없음)
                + " join o.orderItems oi"
                // item도 함께 가져오도록
                + " join oi.item i";

        return em.createQuery(query, OrderFlatDto.class)
                .getResultList();
    }


}
