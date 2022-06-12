package hello.springtx.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;

    // JPA는 커밋 시점에 order 데이터를 db에 반영해준다.
    @Transactional
    public void order(Order order) throws NotEnoughMoneyException {
        log.info("order 호출!");
        orderRepository.save(order);

        log.info("결제 프로세스에 진입");
        if(order.getUsername().equals("예외")) {
            log.info("시스템 예외 발생!");
            throw new RuntimeException("시스템 예외!"); // 복구 불가능 - 런타임 예외 발생
        }
        else if(order.getUsername().equals("잔고 부족")) {
            log.info("잔고 부족 - 비즈니스 예외 발생!"); // 시스템 문제가 아님. 고객의 잔고가 문제인 것임.
            order.setPayStatus("대기"); // 결제 상태 변경 - 고객에게 잔고 부족을 알리고, 별도의 계좌로 입금하도록 안내하기. 롤백 X
            // 즉, 변경된 결제 상태에 대해서 커밋을 진행하는 것임 (+ 원래라면 고객의 주문 정보도 함께)
            // 이러면 나중에 고객이 별도의 계좌로 입금하면 커밋되었던 주문 정보를 바탕으로 주문이 성공적으로 진행되는 것
            throw new NotEnoughMoneyException("잔고가 부족합니다.");
        }
        else {
            log.info("정상적으로 승인되었습니다.");
            order.setPayStatus("완료");
        }

        log.info("결제 프로세스 완료!");

    }
}
