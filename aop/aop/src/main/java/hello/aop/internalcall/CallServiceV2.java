package hello.aop.internalcall;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 생성자 주입을 쓰면서, 스프링 빈을 지연 조회하는 방법.
 * => ObjectProvider (Provider), ApplicationContext 사용하기
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CallServiceV2 {
//    private final ApplicationContext applicationContext;
    private final ObjectProvider<CallServiceV2> callServiceProvider;

    public void external() {
        log.info("call external");
        // 애플리케이션 컨텍스트를 사용하는 방법도 있지만, 이는 너무 많은 기능을 제공하기 때문에
        // 그냥 ObjectProvider를 사용하도록 하자.

        // 이런 식으로 지연해서 꺼내는 것.
//        CallServiceV2 callServiceV2 = applicationContext.getBean(CallServiceV2.class);

        // objectProvider의 경우 .getObject()를 호출하는 시점에! 스프링 컨테이너에서 빈을 조회한다.
        // 자기 자신을 주입받지 않기 때문에 순환 사이클이 발생하지 않는다.
        CallServiceV2 callServiceV2 = callServiceProvider.getObject();
        callServiceV2.internal();
    }

    public void internal() {
        log.info("call internal");
    }

}
