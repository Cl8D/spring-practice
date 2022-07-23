package hello.advanced.app.v5;

import hello.advanced.trace.callback.TraceCallback;
import hello.advanced.trace.callback.TraceTemplate;
import hello.advanced.trace.logtrace.LogTrace;
import hello.advanced.trace.template.AbstractTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class OrderControllerV5 {

    private final OrderServiceV5 orderServiceV5;
    private final TraceTemplate template;

    public OrderControllerV5(OrderServiceV5 orderServiceV5, LogTrace trace) {
        this.orderServiceV5 = orderServiceV5;
        this.template = new TraceTemplate(trace);
    }

    @GetMapping("/v5/request")
    public String request(String itemId) {
        // 두 번째 인자로 콜백함수를 넘겨주기
        return template.execute("OrderController.request()",
                new TraceCallback<String>() {
                    @Override
                    public String call() {
                        orderServiceV5.orderItem(itemId);
                        return "ok";
                    }
                });
    }
}

