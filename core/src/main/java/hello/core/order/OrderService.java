package hello.core.order;

public interface OrderService {
    // 주문 결과를 반환하는 부분
    Order createOrder (Long memberId, String itemName, int itemPrice);

}
