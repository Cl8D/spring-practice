package hello.advanced.app.v2;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OrderServiceV2 {

    private final OrderRepositoryV2 orderRepositoryV2;
    private final HelloTraceV2 trace;

    // 상품 주문
    public void orderItem(TraceId traceId, String itemId) {
        TraceStatus status = null;

        try {
            // 컨트롤러부터 전달받은 traceId를 사용한다.
            // beginSync를 통해 동일한 트랜잭션id를 가지면서 level이 한칸 증가하도록 한다.
            status = trace.beginSync(traceId,"OrderService.orderItem()");
            // beginSync로 인해 반환된 새로운 TraceStatus를 레파지토리에 넘겨준다.
            orderRepositoryV2.save(status.getTraceId(), itemId);
            trace.end(status);
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }

    }


}
