package hello;

import hello.order.gauge.v1.StockConfigV1;
import hello.order.gauge.v2.StockConfigV2;
import hello.order.v0.OrderConfigV0;
import hello.order.v1.OrderConfigV1;
import hello.order.v2.OrderConfigV2;
import hello.order.v3.OrderConfigV3;
import hello.order.v4.OrderConfigV4;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;


//@Import(OrderConfigV0.class)
//@Import(OrderConfigV1.class)
//@Import(OrderConfigV2.class)
//@Import(OrderConfigV3.class)
//@Import(OrderConfigV4.class)
//@Import({OrderConfigV4.class, StockConfigV1.class})
@Import({OrderConfigV4.class, StockConfigV2.class})
@SpringBootApplication(scanBasePackages = "hello.controller")
public class ActuatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActuatorApplication.class, args);
    }

    /**
     * 최대 100개의 HTTP 요청을 제공한다.
     * setCapacity() 메서드를 사용하여 다른 값으로 설정할 수 있다.
     * 물론 이것보다는 핀포인트나 zipkin 사용하기
     */
    @Bean
    public InMemoryHttpExchangeRepository httpExchangeRepository() {
        return new InMemoryHttpExchangeRepository();
    }
}
