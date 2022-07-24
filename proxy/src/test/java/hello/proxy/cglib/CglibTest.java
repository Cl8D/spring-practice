package hello.proxy.cglib;

import hello.proxy.cglib.code.TimeMethodInterceptor;
import hello.proxy.common.service.ConcreteService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.Enhancer;

@Slf4j
public class CglibTest {
    @Test
    void cglib() {
        ConcreteService target = new ConcreteService();
        // CGLIB는 Enhancer를 통해서 프록시를 생성한다!
        Enhancer enhancer = new Enhancer();

        // 구체 클래스를 상속받아서 프록시를 생성 가능!
        // 여기서 구체클래스를 지정해주는 것.
        enhancer.setSuperclass(ConcreteService.class);

        // 프록시에 적용할 실제 로직 할당
        enhancer.setCallback(new TimeMethodInterceptor(target));

        // 프록시 생성! - 상속받아서 만들었으니까 캐스팅 가능.
        ConcreteService proxy = (ConcreteService) enhancer.create();

        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        /*
        16:51:35.553 [Test worker] INFO hello.proxy.cglib.CglibTest - targetClass=class hello.proxy.common.service.ConcreteService
        // 여기 보면 byCGLIBS라고 되어 있는데, CGLIB가 만든 프록시 클래스니까.
        16:51:35.587 [Test worker] INFO hello.proxy.cglib.CglibTest - proxyClass=class hello.proxy.common.service.ConcreteService$$EnhancerByCGLIB$$25d6b0e3
        16:51:35.590 [Test worker] INFO hello.proxy.cglib.code.TimeMethodInterceptor - TimeProxy 실행
        16:51:35.621 [Test worker] INFO hello.proxy.common.service.ConcreteService - ConcreteService 호출
        16:51:35.621 [Test worker] INFO hello.proxy.cglib.code.TimeMethodInterceptor - TimeProxy 종료 resultTime=30
         */

        proxy.call();
    }
}
