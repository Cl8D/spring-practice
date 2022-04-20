package hello.advanced.app.v1;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderControllerV1 {

    private final OrderServiceV1 orderServiceV1;
    private final HelloTraceV1 trace;

    TraceStatus status = null;

    @GetMapping("/v1/request")
    public String request(String itemId) {

        // 단순하게만 쓰면 exception이 발생했을 때 아예 예외가 던저져버려서
        // end 부분이 호출되지 않게 된다.
        // 이를 방지하기 위해 try-catch를 사용해준다.
        try {
            // 컨트롤러 이름 + 메서드 이름
            status = trace.begin("OrderController.request()");
            orderServiceV1.orderItem(itemId);
            trace.end(status);
            return "ok";
        } catch(Exception e) {
            trace.exception(status, e);
            // 예외를 꼭 다시 던져줘야 함.
            // 안 던져주면 정상 흐름처럼 동작이 되어서, 애초에 컴파일 에러가 발생함
            throw e;
        }


    }

}
