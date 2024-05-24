package hello.order.gauge.v1;

import hello.order.OrderService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * http://localhost:8080/actuator/metrics/my.stock
 */
@Configuration
public class StockConfigV1 {
    @Bean
    public MyStockMetric myStockMetric(
            OrderService orderService,
            MeterRegistry registry
    ) {
        return new MyStockMetric(orderService, registry);
    }

    @Slf4j
    @RequiredArgsConstructor
    static class MyStockMetric {
        private final OrderService orderService;
        private final MeterRegistry registry;

        /**
         * 함수의 반환값이 일종의 게이지 값이다.
         * 메트릭 확인 요청이 들어올 때마다 해당 함수가 호출된다고 볼 수 있다.
         * 프로메테우스가 1초에 한 번씩 메트릭을 자동으로 호출하기 때문에, 애플리케이션 로드 시 계속 호출되는 것을 알 수 있다.
         */
        @PostConstruct
        public void init() {
            Gauge.builder("my.stock", orderService, service -> {
                log.info("stock gauge call");
                return service.getStock().get();
            }).register(registry);
        }
    }
}
