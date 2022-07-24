package hello.proxy.advisor;

import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

// 하나의 target에 여러 어드바이스를 적용해보자.
public class MultiAdvisorTest {
    @Test
    @DisplayName("여러 프록시")
    void multiAdvisorTest1() {
        //client -> proxy2(advisor2) -> proxy1(advisor1) -> target
        ServiceInterface target = new ServiceImpl();

        ProxyFactory proxyFactory1 = new ProxyFactory(target);
        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
        proxyFactory1.addAdvisor(advisor1);
        ServiceInterface proxy1 = (ServiceInterface) proxyFactory1.getProxy();

        ProxyFactory proxyFactory2 = new ProxyFactory(proxy1);
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());
        proxyFactory2.addAdvisor(advisor2);
        ServiceInterface proxy2 = (ServiceInterface) proxyFactory2.getProxy();

        proxy2.save();

        /*
        18:30:37.005 [Test worker] INFO hello.proxy.advisor.MultiAdvisorTest$Advice2 - advice2 호출
        18:30:37.016 [Test worker] INFO hello.proxy.advisor.MultiAdvisorTest$Advice1 - advice1 호출
        18:30:37.018 [Test worker] INFO hello.proxy.common.service.ServiceImpl - save 호출

         단, 이 방법은 어드바이저가 여러 개면 프록시도 여러 개를 만들어야 한다.
         하나의 프록시에 여러 어드바이저를 적용할 수 없을까?
         */
    }

    @Slf4j
    static class Advice1 implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("advice1 호출");
            return invocation.proceed();
        }
    }

    @Slf4j
    static class Advice2 implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("advice2 호출");
            return invocation.proceed();
        }
    }

    // 하나의 프록시에 여러 어드바이저 적용하기!
    @Test
    @DisplayName("하나의 프록시, 여러 어드바이저")
    void multiAdvisorTest2() {
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());
        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());

        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        // 프록시 팩토리에 원하는 만큼 addAdvisor를 통해서 어드바이저 등록해주기.
        // 등록하는 순서대로! advisor가 호출된다.
        proxyFactory.addAdvisor(advisor2);
        proxyFactory.addAdvisor(advisor1);
    }

    /**
     * 스프링은 AOP를 적용할 때, 최적화를 진행하여 프록시는 1개만 만들고,
     * 하나의 프록시에 여러 어드바이저를 적용한다.
     * -> AOP 적용 수만큼 프록시가 생성되는 것이 아니다.
     *
     * -- target에 여러 AOP가 동시에 적용이 되더라도,
     * 스프링의 AOP는 target마다 하나의 프록시만 생성한다!
     */

}
