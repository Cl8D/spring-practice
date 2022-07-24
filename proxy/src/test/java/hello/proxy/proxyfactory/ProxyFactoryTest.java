package hello.proxy.proxyfactory;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ConcreteService;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class ProxyFactoryTest {
    @Test
    @DisplayName("인터페이스가 있으면 JDK 동적 프록시 사용하기")
    void interfaceProxy() {
        ServiceInterface target = new ServiceImpl();
        // 프록시 팩토리를 생성할 때 프록시가 호출할 인스턴스를 함께 넘겨주기 때문에,
        // 해당 인스턴스에 인터페이스가 있다면 JDK를 기본으로,
        // 구체 클래스만 있다면 CGLIB를 통해서 동적 프록시를 생성한다.

        // ** 여기서는 인터페이스가 있으니까 JDK 동적 프록시 생성!
        ProxyFactory proxyFactory = new ProxyFactory(target);

        // 부가 기능 로직을 설정해준다. (Advice)
        proxyFactory.addAdvice(new TimeAdvice());

        // 프록시 객체 생성하기!
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        log.info("targetClass={}", target.getClass());
        // 17:27:34.206 [Test worker] INFO hello.proxy.proxyfactory.ProxyFactoryTest - proxyClass=class com.sun.proxy.$Proxy13
        // $Proxy인 걸 보면 JDK 동적 프록시인 것을 확인할 수 있다 :D
        log.info("proxyClass={}", proxy.getClass());

        // 프록시에서 save 호출
        proxy.save();

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isTrue();
        assertThat(AopUtils.isCglibProxy(proxy)).isFalse();
    }

    @Test
    @DisplayName("구체 클래스만 있으면 CGLIB 사용")
    void concreteProxy() {
        ConcreteService target = new ConcreteService();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(new TimeAdvice());
        ConcreteService proxy = (ConcreteService) proxyFactory.getProxy();

        log.info("targetClass={}", target.getClass());
        // 17:42:54.190 [Test worker] INFO hello.proxy.proxyfactory.ProxyFactoryTest - proxyClass=class hello.proxy.common.service.ConcreteService$$EnhancerBySpringCGLIB$$ace9fd57
        // EnhancerBySpringCGLIB
        log.info("proxyClass={}", proxy.getClass());

        proxy.call();

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
        assertThat(AopUtils.isCglibProxy(proxy)).isTrue();
    }


    @Test
    @DisplayName("ProxyTargetClass 옵션을 사용하면 인터페이스가 있어도 CGLIB를 사용하고, 클래스 기반 프록시 사용")
    void proxyTargetClass() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        // CGLIB를 사용하도록 강제하기!
        // 기본적으로 스프링부트는 AOP를 적용할 때 이 옵션을 설정해서 사용한다고 한다!
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addAdvice(new TimeAdvice());

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        log.info("targetClass={}", target.getClass());
        // 17:52:04.891 [Test worker] INFO hello.proxy.proxyfactory.ProxyFactoryTest - proxyClass=class hello.proxy.common.service.ServiceImpl$$EnhancerBySpringCGLIB$$d642b54
        // 보면 CGLIB로 만들어진 것을 확인할 수 있다!
        log.info("proxyClass={}", proxy.getClass());

        proxy.save();

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
        assertThat(AopUtils.isCglibProxy(proxy)).isTrue();
    }


}
