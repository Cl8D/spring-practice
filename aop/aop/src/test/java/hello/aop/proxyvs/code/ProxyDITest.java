package hello.aop.proxyvs.code;

import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Slf4j
// 테스트에 설정 임의 적용.
//@SpringBootTest(properties = {"spring.aop.proxy-target-class=false"}) // JDK 동적 프록시
@SpringBootTest(properties = {"spring.aop.proxy-target-class=true"}) // CGLIB
@Import(ProxyDIAspect.class)
class ProxyDITest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberServiceImpl memberServiceImpl;

    @Test
    void go() {
        // JDK 동적 프록시의 경우 - 에러 발생.
        // 왜냐면, @Autowired MemberServiceImpl 때문이다.
        // MemberServiceImpl에 주입되는 타입이 hello.aop.member.MemberServiceImpl이기를 원하지만,
        // 실제로는 com.sun.proxy.$Proxy54가 넘어오기 때문이다. 구체 클래스에 의존 관계 주입이 안 되는 것.

        // 그러나, CGLIB의 경우 둘 다 정상적으로 동작한다.
        // 구체클래스를 기반으로 만들기 때문에, MemberService든 MemberServiceImpl까지 다 주입이 가능한 것.
        log.info("memberService class={}", memberService.getClass());
        log.info("memberServiceImpl class={}", memberServiceImpl.getClass());
        memberServiceImpl.hello("hello");
    }

    // 사실 당연히, DI의 경우 인터페이스를 기반으로 의존관계를 주입받아야 한다.
    // 구체클래스로 주입받으면 구체클래스를 바꿀 때 클라이언트 코드도 변경해야 하니까...
    // 그래서 설계를 잘해야 한다는 것.

}