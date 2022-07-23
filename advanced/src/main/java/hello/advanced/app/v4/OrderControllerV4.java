package hello.advanced.app.v4;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logtrace.LogTrace;
import hello.advanced.trace.template.AbstractTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class OrderControllerV4 {

    private final OrderServiceV4 orderServiceV4;
    private final LogTrace trace;

    @GetMapping("/v4/request")
    public String request(String itemId) {
        AbstractTemplate<String> template = new AbstractTemplate<String>(trace) {
            @Override
            protected String call() {
                orderServiceV4.orderItem(itemId);
                return "ok";
            }
        };
        // 템플릿을 실행하면서 message로 남길 로그 설정
        return template.execute("OrderController.request()");
    }
}

/*
= 정상 흐름 =
2022-06-13 18:47:33.418  INFO 15800 --- [nio-8080-exec-3] h.a.trace.logtrace.ThreadLocalLogTrace   : [45878969] OrderController.request()
2022-06-13 18:47:33.419  INFO 15800 --- [nio-8080-exec-3] h.a.trace.logtrace.ThreadLocalLogTrace   : [45878969] |-->OrderService.orderItem()
2022-06-13 18:47:33.419  INFO 15800 --- [nio-8080-exec-3] h.a.trace.logtrace.ThreadLocalLogTrace   : [45878969] | |-->OrderRepository.save()
2022-06-13 18:47:34.419  INFO 15800 --- [nio-8080-exec-3] h.a.trace.logtrace.ThreadLocalLogTrace   : [45878969] | |<--OrderRepository.save() time=1000ms
2022-06-13 18:47:34.420  INFO 15800 --- [nio-8080-exec-3] h.a.trace.logtrace.ThreadLocalLogTrace   : [45878969] |<--OrderService.orderItem() time=1001ms
2022-06-13 18:47:34.420  INFO 15800 --- [nio-8080-exec-3] h.a.trace.logtrace.ThreadLocalLogTrace   : [45878969] OrderController.request() time=1002ms


= 예외 발생 =
2022-06-13 18:47:53.645  INFO 15800 --- [nio-8080-exec-1] h.a.trace.logtrace.ThreadLocalLogTrace   : [adaef8a6] OrderController.request()
2022-06-13 18:47:53.648  INFO 15800 --- [nio-8080-exec-1] h.a.trace.logtrace.ThreadLocalLogTrace   : [adaef8a6] |-->OrderService.orderItem()
2022-06-13 18:47:53.650  INFO 15800 --- [nio-8080-exec-1] h.a.trace.logtrace.ThreadLocalLogTrace   : [adaef8a6] | |-->OrderRepository.save()
2022-06-13 18:47:53.651  INFO 15800 --- [nio-8080-exec-1] h.a.trace.logtrace.ThreadLocalLogTrace   : [adaef8a6] | |<X-OrderRepository.save() time=0ms ex=java.lang.IllegalStateException: 예외 발생
2022-06-13 18:47:53.651  INFO 15800 --- [nio-8080-exec-1] h.a.trace.logtrace.ThreadLocalLogTrace   : [adaef8a6] |<X-OrderService.orderItem() time=4ms ex=java.lang.IllegalStateException: 예외 발생
2022-06-13 18:47:53.651  INFO 15800 --- [nio-8080-exec-1] h.a.trace.logtrace.ThreadLocalLogTrace   : [adaef8a6] OrderController.request() time=7ms ex=java.lang.IllegalStateException: 예외 발생

 */