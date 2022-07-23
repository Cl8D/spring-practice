package hello.proxy.config.v1_proxy.concrete_proxy;

import hello.proxy.app.v1.OrderServiceV1;
import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.app.v2.OrderServiceV2;
import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;

public class OrderServiceConcreteProxy extends OrderServiceV2 {
    private final OrderServiceV2 target;
    private final LogTrace logTrace;

    public OrderServiceConcreteProxy(OrderServiceV2 target, LogTrace logTrace) {
        // 클래스를 기반으로 사용하면, 자식 클래스를 생성할 때 항상 super()로 부모 클래스의 생성자를 호출해야 한다.
        // super를 안 쓰면 부모의 기본 생성자가 호출되는데,
        // 부모 클래스에는 기본 생성자가 없으니깐 null이라는 파라미터를 넣어서 super로 호출해주는 것.
        // 프록시에서는 부모 객체의 기능을 사용 안 하니까 null로 주입
        super(null);
        this.target = target;
        this.logTrace = logTrace;
    }

    @Override
    public void orderItem(String itemId) {
        TraceStatus status = null;
        try {
            status = logTrace.begin("OrderService.orderItem()");
            // target 호출
            target.orderItem(itemId);
            logTrace.end(status);
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
