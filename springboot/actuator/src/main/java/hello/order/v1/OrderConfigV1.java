package hello.order.v1;

import hello.order.OrderService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// http://localhost:8080/actuator/metrics/my.order
@Configuration
public class OrderConfigV1 {
    @Bean
    OrderService orderService(MeterRegistry registry) {
        return new OrderServiceV1(registry);
    }
}
