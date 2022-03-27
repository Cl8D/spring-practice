package jpabook.jpashop3.api;

/*********컬렉션 조회 최적화***************/
// 주문 내역에서 추가로 주문한 상품 정보를 추가 조회하기
// order - orderItem / Item 필요

import jpabook.jpashop3.domain.Address;
import jpabook.jpashop3.domain.Order;
import jpabook.jpashop3.domain.OrderItem;
import jpabook.jpashop3.domain.OrderStatus;
import jpabook.jpashop3.repository.OrderRepository;
import jpabook.jpashop3.repository.OrderSearch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 컬렉션 1:n (OneToMany) 조회 및 최적화하기
 * order(1) - orderItem(n)
 */

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;

    /***************************/
    // v1 - 엔티티 직접 노출하기
    // hibernate5Module 설정 필수 -> json으로 생성
    // 무한루프에 걸리지 않도록 @JsonIgnore 추가 필수
    // 하지만 엔티티 직접 호출이기 때문에 좋은 방법 x
    // 그리고 엔티티 정보가 다 나와서 불필요한 정보가 나온다.

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        for (Order order : all) {
            // Lazy 강제 초기화
            // lazy 타입이기 때문에 데이터가 비어 있으니까
            // order에 대한 member/delivery/orderItem 정보를 미리 초기화해주는 것!
            order.getMember().getName();
            // Lazy 강제 초기화
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream()
                    // Lazy 강제 초기화
                    // orderItem의 item이름도 가져와서 초기화해버리기
                    /* 참고로, 여기서 .getName()을 한다는 것은 name 속성을 가져오는 것이 아니라
                       fetch='LAZY'로 인해 프록시 객체가 들어 있는 item 객체 자체를 가져오기 위함이다.
                       => 즉, .getName()을 통해 item 객체 자체를 조회했기 때문에
                       그외에 있는 다른 속성들도 함께 조회가 가능한 것.
                    */
                    .forEach(o -> o.getItem().getName());
        }
        return all;
    }

    /*
        추가적으로, 유용한 것 같아서 적어둠.
        postman을 통해 해당 주소를 호출했을 때 정보를 json 형식으로 받아오는 걸 볼 수 있는데,
        이는 스프링이 내부에서 json을 만들 때 jackson 라이브러리를 만들고,
        얘는 자바의 getXXX() 메서드를 호출하여 get을 떼고 소문자로 변경한 다음에,
        이를 필드값으로 만드는 정책을 만든다.
        -> 이게 즉, 자바 빈 프로퍼티 접근 방식이라고 함.
        그래서 json 형식을 보면 다양한 필드값이 나오게 되는 것
     */


    /***************************/
    // v2 - 엔티티를 dto로 변환하기
    @GetMapping("/api/v2/orders")
    public Result<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<OrderDto> orderDtoList = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return new Result<>(orderDtoList.size(), orderDtoList);
    }

    /*
        당연히 이렇게 하면 dto로 인해 필요한 정보만 출력된다.

        그러나, 이렇게 했을 때 날아간 쿼리의 수를 생각해보자.
        먼저, findAll로 인해서 order에 대한 select 쿼리 1번.
        -> 이때 주문의 수가 2개니까 아래 stream().map() 루프를 2번 돌아야 한다.

        1) 먼저, 첫 번째 루프에 대해 (첫 번째 주문)
        .getMember()로 인한 member select 쿼리 한 번,
        .getDelivery()로 인한 delivery select 쿼리 한 번,
        그리고 .getOrderItems()로 인해 orderItem select 쿼리 한 번이 나간다.
        ==> 여기까지 쿼리 3번이 나감.
        이때 첫 번째 주문에 대해 주문 아이템이 2개 존재하기 때문에,
        .stream.map() 루프가 2번 돌게 된다.

        1-1) 첫 번째 주문 아이템
        -> item에 대한 쿼리 한 번이 나간다.
        1-2) 두 번째 주문 아이템
        -> 또, item에 대한 쿼리 한 번이 나간다.
        ==> 여기서 쿼리 2번이 추가적으로 나간다.


        2) 두 번째 루프 (두 번째 주문)
        마찬가지로 member / delivery / orderItem으로 인한 쿼리 3번이 나간다.
        두 번째 주문에서도 주문 아이템이 2개이기 때문에 루프가 2번 돈다.

        2-1) 첫 번째 주문 아이템
        2-2) 두 번쨰 주문 아이템
        ==> 여기서 item 쿼리 2번.

        결과적으로 보면, 1+3+2+3+2 = 11번의 쿼리가 나간다... 어마어마함
        -> 지연 로딩으로 인한 너무 많은 쿼리 실행
        (전에도 말했지만, 지금은 member가 서로 다르기 때문에
        영속성 컨텍스트에 없는 결과들이어서 계속 새로 쿼리를 날리는 것.
        영속성 컨텍스트에 있다면 거기서 가져올 수 있기 때문에 새로 쿼리 x)
     */

    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();

            // 여기서 dto로 변환할 때 orderItem에 대한 dto로 추가적으로 만들어줘야 한다.
            // 이걸 안 해주면 엔티티가 반환되니까 dto로 바꿔야 한다는 것!!
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(toList());
        }
    }

    @Data
    static class OrderItemDto {
        private String itemName;//상품명
        private int orderPrice; //주문 가격
        private int count; //주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }


    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private List<T> orderDto;
    }

    /***************************/
    // v3 - 엔티티 dto 변환 (fetch join)
    // 이렇게 하면 join을 통해 쿼리가 한 번만 나간다!
    @GetMapping("/api/v3/orders")
    public Result<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> orderDtoList = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return new Result<>(orderDtoList.size(), orderDtoList);
    }

    /***************************/
    // v3.1 -> 엔티티 dto 변환 + 페이징 이용하기
    // XToOne은 페치조인으로, 컬렉션은 batchSize 활용하기

    /*
        컬렉션을 페치 조인 하면 페이징이 불가능하다.
        -> 기본적으로 1:N에서 1을 기준으로 페이징을 목표인데,
        조인 시 N을 기준으로 row가 생성되어 뻥튀기가 된다.
        -> 이때 페이징 시도 시 하이버네이트는 메모리에 다 올리고 페이징을 시도해서 장애가 발생할 수 있다.

        그렇다면, 이런 상황에서 페이징은 아예 할 수 없는 것일까?
        방법이 있다.

        1) XToOne (1:1, N:1) 관계는 모두 fetch join을 수행한다.
        => :1 관계는 row 수를 증가시키기 않아서 페이징에 영향 x
        2) 컬렉션은 지연 로딩으로 조회.
        3) 성능 최적화를 위해 hibernate.default_fetch_size, @BatchSize 적용하기
        -> 첫 번째 거는 글로벌하게, 두 번째 거는 개별 최적화를 의미한다.
        -> 이를 사용하면 컬렉션이나 프록시 객체를 한꺼번에 설정한 size만큼 in 쿼리로 조회 가능하다.
     */

    @GetMapping("/api/v3.1/orders")
    public Result<OrderDto> ordersV3_page (
            @RequestParam(value="offset", defaultValue = "0") int offset,
            @RequestParam(value="limit", defaultValue = "100") int limit) {

        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> orderDtoList = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return new Result<>(orderDtoList.size(), orderDtoList);
    }

    /*
        <단순히 xToOne 관계를 fetch join 했을 경우 쿼리 개수>
        1) order에 대한 member, delivery를 조인했기 때문에 select 쿼리 1번
        2) orderDto 생성을 위한 orderItems를 위해 쿼리 1번 (첫 번째 주문)
        3) orderItems 내부에 아이템이 2개이기 때문에 item 관련 쿼리 2번
        4) 두 번째 order에 대한 orderItems 쿼리 1번
        (이때, 아까 처음에 member/delivery는 한 번에 가져와서 그에 대한 쿼리는 x)
        5) 그 내부에 있는 item 2개에 대한 쿼리 2번.
        -> 즉, 1+1+2+1+2 = 7번의 쿼리가 나간다.


        <hibernate Batch size 옵션 줬을 경우>
        1) 처음에 order-member-delivery select 쿼리 1번
        2) orderItem 쿼리 1번
        -> 이때 쿼리르 보면 무슨 in 쿼리가 들어가있다.
        : 한 번에 db에 있는 userA, userB의 orderItem을 다 가져와버림.
        => orders와 관련된 orderItem을 그냥 다 가져와버리는?
        (우리는 fetch_size를 100으로 줬으니까 100개를 미리 긁어오는 느낌)

        3) item 쿼리 1번
        -> 마찬가지로 item 역시 in 쿼리를 통해서 item에 있는 4개를 한 번에 가져옴.
        (fetch_size만큼 한 번에 가져온닥 생각해주면 된다)
        => 이러면 쿼리가 최종적으로 1+1+1 만큼 날리게 되는 것.

        1+N+N ----> 1+1+1으로!

        cf) fetch_size는 100~1000 사이를 선택하는 게 낫다.
        SQL IN절, 1000개를 한 번에 가져오면 순간 부하가 증가할 수는 있지만,
        애플리케이션이 감당할 수 있을 정도로 설정해주면 됨.
     */



}
