package hello.core.singleton;

import hello.core.AppConfig;
import hello.core.member.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SingletonTest {
    @Test
    @DisplayName("스프링 없는 순수한 DI Container")
    void pureContainer() {
        AppConfig appConfig = new AppConfig();

        // 1. 조회: 호출할 때마다 객체를 생성하는지 확인해보자.
        MemberService memberService1 = appConfig.memberService();
        MemberService memberService2 = appConfig.memberService();

        // 2. 참조값이 다른 것을 확인해 보자.
        /*
        memberService1 = hello.core.member.MemberServiceImpl@609cd4d8
        memberService2 = hello.core.member.MemberServiceImpl@17f7cd29
        Appconfg에게 memberService를 요청할 때마다 @뒤의 주소가 다른 것 = 즉, 다른 객체를 생성하는 걸 볼 수 있다.
        */
        System.out.println("memberService1 = " + memberService1);
        System.out.println("memberService2 = " + memberService2);

        // memberService1과 memberService2는 달라야 한다.
        assertThat(memberService1).isNotSameAs(memberService2);
    }


    @Test
    @DisplayName("싱글톤 패턴을 적용한 객체 사용")
    public void singletonServiceTest() {
        // java: SingletonService() has private access in hello.core.singleton.SingletonService
        // private로 막아뒀기 때문에 외부에서 객체를 생성할 수 없다.
        // new SingletonService();

        SingletonService singletonService1 = SingletonService.getInstance();
        SingletonService singletonService2 = SingletonService.getInstance();

        System.out.println("singletonService1 = " + singletonService1);
        System.out.println("singletonService2 = " + singletonService2);

        /*
        singletonService1 = hello.core.singleton.SingletonService@6913c1fb
        singletonService2 = hello.core.singleton.SingletonService@6913c1fb
        @뒤에 값이 같다. 즉, 같은 객체를 가져다 쓰는 걸 확인할 수 있다!
         */

        /*
        cf) isSameAs와 isEqualTo의 차이
        same은 ==을 의미하고, (인스턴스 참조 비교)
        equal은 자바의 equals를 의미함.

        음... isSameAs는 주소값을 비교하는 메서드이고,
        isEqualTo는 대상의 내용을 비교하는 것이다!
        */
        assertThat(singletonService1).isSameAs(singletonService2);
    }
}
