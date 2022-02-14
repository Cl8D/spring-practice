package hello.core.singleton;

import hello.core.AppConfig;
import hello.core.member.MemberRepository;
import hello.core.member.MemberServiceImpl;
import hello.core.order.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationSingletonTest {

    @Test
    void configurationTest() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        // 원래는 구체 타입으로 꺼내면 안 좋지만 테스트 용도로 만들었던 애들 꺼내기 위한 용도
        MemberServiceImpl memberService = ac.getBean("memberService", MemberServiceImpl.class);
        OrderServiceImpl orderService = ac.getBean("orderService", OrderServiceImpl.class);
        MemberRepository memberRepository = ac.getBean("memberRepository", MemberRepository.class);

        MemberRepository memberRepository1 = memberService.getMemberRepository();
        MemberRepository memberRepository2 = orderService.getMemberRepository();

        
        /*
        * memberService -> memberRepository = hello.core.member.MemoryMemberRepository@61ce23ac
        orderService -> memberRepository = hello.core.member.MemoryMemberRepository@61ce23ac
        memberRepository = hello.core.member.MemoryMemberRepository@61ce23ac
        결과를 보면 모두 다 똑같은 곳에서 꺼내오는 걸 볼 수 있다.
        *
        new가 3번 호출되는데 어떻게 다 똑같은 곳에서 꺼내오는 것일까?
        * */
        System.out.println("memberService -> memberRepository = " + memberRepository1);
        System.out.println("orderService -> memberRepository = " + memberRepository2);
        System.out.println("memberRepository = " + memberRepository);

        assertThat(memberService.getMemberRepository()).isSameAs(memberRepository);
        assertThat(orderService.getMemberRepository()).isSameAs(memberRepository);
    }

    @Test
    void configurationDeep() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        //AppConfig도 스프링 빈으로 등록된다.
        AppConfig bean = ac.getBean(AppConfig.class);

        System.out.println("bean = " + bean.getClass());
        //출력: bean = class hello.core.AppConfig$$EnhancerBySpringCGLIB$$bd479d70
        // 뒤에 뭔가 이상한 게 붙어서 출력되는 걸 볼 수 있다.
    }
}
