package hello.proxy.config;

import hello.proxy.app.v1.*;
import hello.proxy.app.v2.OrderControllerV2;
import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.app.v2.OrderServiceV2;
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ProxyFactoryConfigV2 {
    @Bean
    public OrderControllerV2 orderControllerV2(LogTrace logTrace) {
        OrderControllerV2 orderController = new OrderControllerV2(orderServiceV2(logTrace));
        ProxyFactory factory = new ProxyFactory(orderController);
        factory.addAdvisor(getAdvisor(logTrace));

        OrderControllerV2 proxy = (OrderControllerV2) factory.getProxy();
        log.info("ProxyFactory proxy={}, target={}", proxy.getClass(),
                orderController.getClass());
        return proxy;
    }

    @Bean
    public OrderServiceV2 orderServiceV2(LogTrace logTrace) {
        OrderServiceV2 orderService = new OrderServiceV2(orderRepositoryV2(logTrace));
        ProxyFactory factory = new ProxyFactory(orderService);
        factory.addAdvisor(getAdvisor(logTrace));

        OrderServiceV2 proxy = (OrderServiceV2) factory.getProxy();
        log.info("ProxyFactory proxy={}, target={}", proxy.getClass(),
                orderService.getClass());
        return proxy;
    }

    @Bean
    public OrderRepositoryV2 orderRepositoryV2(LogTrace logTrace) {
        OrderRepositoryV2 orderRepository = new OrderRepositoryV2();
        ProxyFactory factory = new ProxyFactory(orderRepository);
        factory.addAdvisor(getAdvisor(logTrace));

        OrderRepositoryV2 proxy = (OrderRepositoryV2) factory.getProxy();
        log.info("ProxyFactory proxy={}, target={}", proxy.getClass(),
                orderRepository.getClass());
        return proxy;
    }

    /**
     * 실행되는 로그
     * ProxyFactory proxy=class hello.proxy.app.v2.OrderRepositoryV2$$EnhancerBySpringCGLIB$$5d106bda, target=class hello.proxy.app.v2.OrderRepositoryV2
     * ProxyFactory proxy=class hello.proxy.app.v2.OrderServiceV2$$EnhancerBySpringCGLIB$$b16099fd, target=class hello.proxy.app.v2.OrderServiceV2
     * ProxyFactory proxy=class hello.proxy.app.v2.OrderControllerV2$$EnhancerBySpringCGLIB$$5f179290, target=class hello.proxy.app.v2.OrderControllerV2
     *
     * 구체 클래스이기 때문에 CGLIB 프록시가 적용된 것을 확인할 수 있다!
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
