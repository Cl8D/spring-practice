package hello.proxy.config;

import hello.proxy.app.v1.*;
import hello.proxy.config.v2_dynamicproxy.handler.LogTraceFilterHandler;
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

@Slf4j
@Configuration
public class ProxyFactoryConfigV1 {
    @Bean
    public OrderControllerV1 orderControllerV1(LogTrace logTrace) {
        OrderControllerV1 orderController = new OrderControllerV1Impl(orderServiceV1(logTrace));
        ProxyFactory factory = new ProxyFactory(orderController);
        factory.addAdvisor(getAdvisor(logTrace));

        OrderControllerV1 proxy = (OrderControllerV1) factory.getProxy();
        log.info("ProxyFactory proxy={}, target={}", proxy.getClass(),
                orderController.getClass());
        return proxy;
    }

    @Bean
    public OrderServiceV1 orderServiceV1(LogTrace logTrace) {
        OrderServiceV1 orderService = new OrderServiceV1Impl(orderRepositoryV1(logTrace));
        ProxyFactory factory = new ProxyFactory(orderService);
        factory.addAdvisor(getAdvisor(logTrace));

        OrderServiceV1 proxy = (OrderServiceV1) factory.getProxy();
        log.info("ProxyFactory proxy={}, target={}", proxy.getClass(),
                orderService.getClass());
        return proxy;
    }

    @Bean
    public OrderRepositoryV1 orderRepositoryV1(LogTrace logTrace) {
        OrderRepositoryV1 orderRepository = new OrderRepositoryV1Impl();
        ProxyFactory factory = new ProxyFactory(orderRepository);
        factory.addAdvisor(getAdvisor(logTrace));

        OrderRepositoryV1 proxy = (OrderRepositoryV1) factory.getProxy();
        log.info("ProxyFactory proxy={}, target={}", proxy.getClass(),
                orderRepository.getClass());
        return proxy;
    }

    /**
     * 실행되는 로그
     * ProxyFactory proxy=class com.sun.proxy.$Proxy50, target=class hello.proxy.app.v1.OrderRepositoryV1Impl
     * ProxyFactory proxy=class com.sun.proxy.$Proxy52, target=class hello.proxy.app.v1.OrderServiceV1Impl
     * ProxyFactory proxy=class com.sun.proxy.$Proxy53, target=class hello.proxy.app.v1.OrderControllerV1Impl
     *
     * 인터페이스가 있기 때문에 기본적으로 JDK 동적 프록시를 활용하여 ($proxy) 만들어진 걸 볼 수 있다.
     */

    private Advisor getAdvisor(LogTrace logTrace) {
        // PointCut 생성하기 - string 여러 개 매칭
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*", "order*", "save*");

        // advice 생성하기
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);

        // advisor
        return new DefaultPointcutAdvisor(pointcut, advice);
    }
}
