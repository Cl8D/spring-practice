package hello.core.autowired;

import hello.core.AutoAppConfig;
import hello.core.discount.DiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AllBeanTest {

    @Test
    void findAllBean() {
        // 이전에 만들었던 autoAppconfig도 등록하고, discountService도 등록을 해두자.
        // autoAppConfig을 등록함으로써 컴포넌트스캔을 하며 rate, fix policy 둘 다 스프링빈에 등록이 된다.
        ApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class, DiscountService.class);

        DiscountService discountService = ac.getBean(DiscountService.class);
        Member member = new Member(1L, "userA", Grade.VIP);

        // 할인이 얼마나 되는지 확인하는 로직
        int discountPrice = discountService.discount(member, 10000, "fixDiscountPolicy");

        assertThat(discountService).isInstanceOf(DiscountService.class);
        // 할인 금액은 1000원이어야 함
        assertThat(discountPrice).isEqualTo(1000);

        int rateDiscountPrice = discountService.discount(member, 20000, "rateDiscountPolicy");
        assertThat(discountPrice).isEqualTo(2000);

    }

    static class DiscountService {
        // 등록된 모든 빈을 조회하자
        // map의 키 값으로 스프링 빈의 이름을, 값으로 discountPolicy 타입으로 조회한 모든 스프링 빈을 담아준다.
        // list에는 discountPolicy 타입으로 조회한 모든 스프링 빈을 담아준다.
        private final Map<String, DiscountPolicy> policyMap;
        private final List<DiscountPolicy> policies;

        @Autowired
        // 1. 먼저 생성자를 통해서 모든 discountPolicy를 주입 받는다.
        // 이때, Map에 fixDiscountPolicy와 rateDiscountPolicy가 주입된다.
        public DiscountService(Map<String, DiscountPolicy> policyMap, List<DiscountPolicy> policies) {
            this.policyMap = policyMap;
            this.policies = policies;
            // 결과를 확인해 보면,
            // policyMap = {fixDiscountPolicy=hello.core.discount.FixDiscountPolicy@765f05af, rateDiscountPolicy=hello.core.discount.RateDiscountPolicy@62f68dff}
            // policies = [hello.core.discount.FixDiscountPolicy@765f05af, hello.core.discount.RateDiscountPolicy@62f68dff]
            // 아무튼 모든 빈이 출력되는 걸 볼 수 있다 (rate, fix)
            System.out.println("policyMap = " + policyMap);
            System.out.println("policies = " + policies);
        }

        public int discount(Member member, int price, String discountCode) {
            // 2. discountCode로 fixDiscountPolicy가 넘어오면
            // Map에서 fixDiscountPolicy 스프링 빈을 찾아서 실행한다.
            // 이때, rateDiscountPolicy가 넘어오면 마찬가지로 rateDiscountPolicy 스프링 빈을 찾아서 실행한다.
            DiscountPolicy discountPolicy = policyMap.get(discountCode);

            // 여기서 discount 함수는 discountPolicy interface를 상속받은 fix/rate에 오버라이딩된 discount 함수이다.
            return discountPolicy.discount(member, price);
        }
    }
}
