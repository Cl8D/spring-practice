package hello.aop.proxyvs;

import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class ProxyCastingTest {
    @Test
    void jdkProxy() {
        // JDK 동적 프로시는 인터페이스 기반으로 프록시를 생성하기 때문에,
        // 구체 클래스로 타입 캐스팅이 불가능하다.
        MemberServiceImpl target = new MemberServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        // JDK 동적 프록시 사용하기.
        proxyFactory.setProxyTargetClass(false);

        // 인터페이스로 타입 캐스팅이 가능하다.
        MemberService memberServiceProxy =
                (MemberService) proxyFactory.getProxy();

        // proxy class=class com.sun.proxy.$Proxy11
        log.info("proxy class={}", memberServiceProxy.getClass());

        // 그러나, 구체 클래스로는 타입 캐스팅이 안 된다.
        // 왜? 인터페이스를 기반으로 프록시를 생성하니까.
        assertThrows(ClassCastException.class, () -> {
            MemberServiceImpl castingMemberService
                    = (MemberServiceImpl) memberServiceProxy;
        });
    }

    @Test
    void cglibProxy() {
        MemberServiceImpl target = new MemberServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        // CGLIB 프록시 적용하기.
        proxyFactory.setProxyTargetClass(true);

        // 인터페이스로 해도, 구체 클래스라 해도 성공한다.
        // CGLIB는 구체 클래스로 프록시를 생성하기 때문에 당연히 구체 클래스로 가능하고,
        // 자식 클래스를 부모 클래스로 타입 캐스팅이 가능하기 때문이다!
        MemberService memberServiceProxy
                = (MemberService) proxyFactory.getProxy();

        log.info("proxy class={}", memberServiceProxy.getClass());

        MemberServiceImpl castingMemberService
                = (MemberServiceImpl) memberServiceProxy;

    }
}
