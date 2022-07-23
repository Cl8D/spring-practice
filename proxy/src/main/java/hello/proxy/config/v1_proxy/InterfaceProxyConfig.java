package hello.proxy.config.v1_proxy;

import hello.proxy.app.v1.*;
import hello.proxy.config.v1_proxy.interface_proxy.OrderControllerInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderRepositoryInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderServiceInterfaceProxy;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterfaceProxyConfig {

    // proxy => target
    // orderServiceInterfaceProxy => orderServiceV1Impl

    /**
     * 실제 객체는 이제 스프링 컨테이너와 상관 X! 컨테이너는 프록시 객체를 관리한다.
     * 프록시는 스프링 컨테이너가 관리하고, 자바의 힙 메모리에도 올라간다.
     * 실제 객체는 그냥 자바의 힙 메모리에만 올라가는 형태.
     */
    @Bean
    public OrderControllerV1 orderController(LogTrace logTrace) {
        OrderControllerV1Impl orderControllerV1 = new OrderControllerV1Impl(orderService(logTrace));
        // 중요! 실제 객체를 빈으로 등록하는 게 아니라, 프록시를 등록하는 것.
        // 어차피 프록시 내부에서 실제 객체를 참조하기 때문에, 의존 관계를 주입하는 형식으로 진행.
        return new OrderControllerInterfaceProxy(orderControllerV1, logTrace);
    }

    @Bean
    public OrderServiceV1 orderService(LogTrace logTrace) {
        OrderServiceV1Impl orderServiceV1 = new OrderServiceV1Impl(orderRepository(logTrace));
        return new OrderServiceInterfaceProxy(orderServiceV1, logTrace);
    }

    @Bean
    public OrderRepositoryV1 orderRepository(LogTrace logTrace) {
        OrderRepositoryV1Impl orderRepositoryV1 = new OrderRepositoryV1Impl();
        return new OrderRepositoryInterfaceProxy(orderRepositoryV1, logTrace);
    }
}
