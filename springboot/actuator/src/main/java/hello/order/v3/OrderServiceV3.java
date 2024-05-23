package hello.order.v3;

import hello.order.OrderService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
public class OrderServiceV3 implements OrderService {

    private final MeterRegistry registry;

    private AtomicInteger stock = new AtomicInteger(100);

    /**
     * Timer 활용하기
     * record() 내에 실행할 내용을 넣어두면 된다.
     */
    @Override
    public void order() {
        Timer.builder("my.order")
                .tag("class", this.getClass().getName())
                .tag("method", "order")
                .description("order")
                .register(registry)
                .record(() -> {
                    log.info("주문");
                    stock.decrementAndGet();
                    sleep(500); // 임의로 응답 시간 늘리기
                });
    }

    @Override
    public void cancel() {
        Timer.builder("my.order")
                .tag("class", this.getClass().getName())
                .tag("method", "cancel")
                .description("order")
                .register(registry)
                .record(() -> {
                    log.info("취소");
                    stock.incrementAndGet();
                    sleep(200);
                });
    }

    private static void sleep(int l) {
        try {
            Thread.sleep(l + new Random().nextInt(200));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AtomicInteger getStock() {
        return stock;
    }
}
