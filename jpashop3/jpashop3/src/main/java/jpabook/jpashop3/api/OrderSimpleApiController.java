package jpabook.jpashop3.api;

import jpabook.jpashop3.domain.Address;
import jpabook.jpashop3.domain.Order;
import jpabook.jpashop3.domain.OrderStatus;
import jpabook.jpashop3.repository.OrderRepository;
import jpabook.jpashop3.repository.OrderSearch;
import jpabook.jpashop3.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop3.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/***********지연 로딩, 조회 성능 최적화***********/

/**
 * Order
 * Order(n) -> Member(1)
 * Order(1) -> Delivery(1)
 * Order(1) -> OrderItems(n)
 *
 * 우리는 이 중에서, XtoOne (ManyToOne, OneToOne)에서의
 * 성능 최적화에 대해서 살펴보자.
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    /*******************************/
    // v1) 엔티티를 직접 노출시키는 방법.
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        // order 반환
        // 그러나, 이런 식으로 하면 양방향 연관관계가 서로 참조하다 보니까 무한루프가 발생한다.
        // 그래서 양방향이 걸린 애들한테 @JsonIgnore를 해줘야 함.

        // 또한, Order -> Member, Order->Address는 지연 로딩으로 설정되어 있기 때문에,
        // 실제 엔티티 대신에 프록시 객체를 생성하게 된다.
        // 그러나, 이러한 프록시 객체를 Json으로 어떻게 생성해야 하는지 알 수 없기 때문에
        // 예외가 발생하여 Hibernate5Module을 스프링 빈으로 등록해야 한다.
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        // 아니면, hibernate 옵션 대신에 이렇게 해주는 방법도 있다.
        for (Order order : all) {
            // 여기서 order.getMember()까지는 지연 로딩으로 인해서 프록시 객체이다. (db 쿼리 x)
            // 여기서 getName()까지 해야 실제 쿼리를 날려서 가져오게 되는 것.
            // 즉, Lazy를 강제로 초기화해주는 것.
            order.getMember().getName();
            order.getDelivery().getAddress();

            // 그러나, 이렇게 하면
        }
        return all;
    }

    /*********************************************/

    // v2 - 엔티티를 DTO로 변환하기
    @GetMapping("/api/v2/simple-orders")
    public Result<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        //return result;
        return new Result<SimpleOrderDto>(result.size(), result);
    }

    // {} 형태로 나오게 하도록 만들기
    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private List<T> simpleOrderDTO;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){
            orderId = order.getId();
            // 여기서 lazy가 초기화 된다.
            // memberId를 가지고 영속성 컨텍스트에서 찾아서 db 쿼리를 날림.
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            // 마찬가지로 lazy가 초기화되는 부분.
            address = order.getDelivery().getAddress();
        }
    }

    /*
    그러나, v2의 경우 쿼리가 너무 많이 나간다는 단점이 있다.
    현재 조회해야 하는 테이블이 order, delivery, member인데

    현재 조회된 주문이 2개인데,
    먼저 order를 조회하기 위해 쿼리가 한 번 나가고, 이때 결과로 주문 수 2개가 리턴된다.
    그래서 result를 받기 위한 루프 역시 2번이 돌게 되는데,

    첫 번째 루프에서는 simpleOrderDto의 생성자 부분에서
    order.getMember()으로 인해 member에 대한 select 쿼리가 나간다.
    마찬가지로 order.getDelivery()로 인해 delivery 쿼리도 나간다.
    -> 첫 번째 주문에 의한 쿼리 끝.

    두 번째 루프에서는 또 다시 member, delivery 쿼리가 나간다.

    즉, 쿼리가 총 5번이 (1+2+2) 나가게 되는 것.
    이게 바로 n+1 문제!

    order의 수가 n개라고 한다면,
    처음에 order를 위한 쿼리 1번, 그리고 조회된 order에 대해
    n번의 쿼리가 추가적으로 또 나가게 된다.
    (여기서는 member-delivery로 인해 n+n번 조회되는 것.
    물론 order에 대해 같은 member가 주문했던 거였다면 order 쿼리(1) - <첫 루프> member 쿼리(1) - delivery 쿼리(1)
    - <두 번째 루프> member는 이미 쿼리를 통해 영속성 컨텍스트에 있으니깐 쿼리 x - delivery 쿼리(1)
    이런 식으로 4번이 나가게 될 것이다!)
     */

    /*********************************************/

    // v3 - 엔티티 DTO 변환 + 페치 조인 활용하기
    @GetMapping("/api/v3/simple-orders")
    public Result<SimpleOrderDto> ordersV3() {
        // fetch join 활용
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return new Result<SimpleOrderDto>(result.size(), result);
    }

    /*
    이렇게 하면 select 쿼리가 딱 한 번 나간다.
    order- member- delivery를 전부 inner join을 통해서 딱 1번의 쿼리로 결과가 나온다.
     */

    /*********************************************/
    // v4 - JPA에서 DTO로 바로 조회하기
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;
    @GetMapping("/api/v4/simple-orders")
    public OsqdResult<OrderSimpleQueryDto> ordersV4() {
        // 내가 원하는 데이터만 가져올 수 있기 때문에 select 절이 조금 더 짧아졌다.
        // 그러나, 이거는 리포지토리 재사용성이 떨어진다는 단점이 있다.
        List<OrderSimpleQueryDto> orderDtos = orderSimpleQueryRepository.findOrderDtos();
        return new OsqdResult<>(orderDtos.size(), orderDtos);
    }

    @Data
    @AllArgsConstructor
    static class OsqdResult<T> {
        private int count;
        private List<T> orderSimpleQueryDto;
    }

    /**
     * <쿼리 방식 선택 권장 순서>
     * 1) 엔티티를 DTO로 변환하기 (v2)
     * 2) 필요 시 페치 조인으로 성능 최적화 (v3)
     * 3) 그래도 안 된다면 DTO로 직접 조회하는 방법 사용하기 (v4)
     * 4) 최후로는 jPA가 제공하는 native SQL이나 spring JDBC Template를 사용하여 SQL을 직접 사용하기
     */


}
