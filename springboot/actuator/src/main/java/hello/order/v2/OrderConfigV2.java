package hello.order.v2;

import hello.order.OrderService;
import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfigV2 {
    @Bean
    OrderService orderService() {
        return new OrderServiceV2();
    }

    /**
     * @Counted 인지를 위해서 사용
     * CountedAspect 을 빈으로 등록해야만 동작한다.
     */
    @Bean
    public CountedAspect countedAspect(MeterRegistry registry) {
        return new CountedAspect(registry);
    }
}
