package hello.core;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Java -> Spring Style 변경
// 설정 정보에 @configuration, 메서드에 @bean
// @Bean이 붙이면 모두 스프링 컨테이너에 등록된다.
@Configuration
public class AppConfig {
    // application에 대한 환경 구성을 전부 여기서 해줄 것.
    // 기존에는 memberServiceImpl에서 new를 통해 MemoryMemberRepository를 생성하였었음.
    // 이런 일을 그냥 appconfig에서 한 번에 하자는 것.
    @Bean
    public MemberService memberService() {
        // memberService 호출 시 멤버 서비스의 구현체(impl)이 생성되는데,
        // 이때 memoryMemberRepository가 만들어지는 형태임
        // 이렇게 되면 memberServiceImpl에서는 메모리멤버리포에 대한 코드가 없어지게 된다. (여기서 다 하니까)
        // 이런 걸 보통 생성자 주입이라고 한다!
        return new MemberServiceImpl(memberRepository());
    }

    // 역할을 조금 더 분명하게 해주는 코드.
    // memberRepository라는 역할이 생기는 것.
    @Bean
    public MemoryMemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    // 마찬가지로 이렇게 코드를 수정해주게 되면, 할인 정책 변경 시 여기서만 수정해주면 된다.
    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(
                memberRepository(),
                discountPolicy());
    }

    // 마찬가지로 역할이 조금 더 분명하게 드러나도록 한다.
    @Bean
    public DiscountPolicy discountPolicy() {
        //return new FixDiscountPolicy();

        //할인 정책을 변경해준다.
        return new RateDiscountPolicy();
    }
}
