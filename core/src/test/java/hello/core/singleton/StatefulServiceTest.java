package hello.core.singleton;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StatefulServiceTest {

    @Test
    void statefulServiceSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);
        StatefulService statefulService1 = ac.getBean(StatefulService.class);
        StatefulService statefulService2 = ac.getBean(StatefulService.class);

        // ThreadA : A 사용자가 10000원을 주문함
        statefulService1.order("userA", 10000);
        // ThreadB : B 사용자가 20000원을 주문함
        statefulService2.order("userB", 20000);

        // ThreadA : 사용자 A가 주문 금액 조회함
        // 우리가 기대하는 건 10000원이었지만, 결과값으로는 20000원이 나오게 된다.
        // 정말 매우 심각한 상황임
        int price = statefulService1.getPrice();

        assertThat(statefulService1.getPrice()).isEqualTo(20000);

    }

    static class TestConfig {
        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }
    }

}