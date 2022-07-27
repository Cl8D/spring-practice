package hello.aop;

import hello.aop.order.OrderRepository;
import hello.aop.order.OrderService;
import hello.aop.order.aop.*;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Slf4j
//@Import(AspectV1.class) // 스프링 빈 등록도 가능하다. (당연히 @Configuration, @Bean을 사용해도 된다)
//@Import(AspectV2.class)
//@Import(AspectV3.class)
//@Import(AspectV4Pointcut.class)
//@Import({AspectV5Order.LogAspect.class, AspectV5Order.TxAspect.class})
@Import(AspectV6Advice.class)
@SpringBootTest
public class AopTest {
    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void aopInfo() {
        // @Import 전 -> 당연히 둘 다 false가 나온다. (아직 프록시 관련 설정 아무것도 없음)
        // @Import로 프록시 적용 후 -> 둘 다 true가 나온다. 즉, 둘 다 프록시가 적용된 것.
        // hello.aop.order 밑에 있는 두 가지 모두 proxy가 적용된 것을 볼 수 있다.
        log.info("isAopProxy, orderService={}",
                AopUtils.isAopProxy(orderService));

        log.info("isAopProxy, orderRepository={}",
                AopUtils.isAopProxy(orderRepository));
    }

    @Test
    void success() {
        orderService.orderItem("itemA");
    }

    @Test
    void exception() {
        Assertions.assertThatThrownBy(() -> orderService.orderItem("ex"))
                .isInstanceOf(IllegalStateException.class);
    }
}
