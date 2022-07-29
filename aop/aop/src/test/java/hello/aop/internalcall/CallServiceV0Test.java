package hello.aop.internalcall;

import hello.aop.internalcall.aop.CallLogAspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@Import(CallLogAspect.class)
@SpringBootTest
class CallServiceV0Test {
    @Autowired
    CallServiceV0 callServiceV0;

    @Test
    void external() {
        callServiceV0.external();
        /*
        aop=void hello.aop.internalcall.CallServiceV0.external()
        call external
        call internal

        로그를 보면 internal을 호출할 때 aop={}로그가 찍히지 않은 것을 볼 수 있다.
        즉, 어드바이스가 호출되지 않았다는 것!
         */
    }

    @Test
    void internal() {
        callServiceV0.internal();
        /*
        aop=void hello.aop.internalcall.CallServiceV0.internal()
        call internal

        그러나, 외부에서 바로 internal을 호출할 때는 aop가 적용된다.
        즉, 프록시는 외부 함수에서 내부 함수를 호출할 때 적용되지 않는다는 점이다.
         */

        // 참고로, 실제 코드 자체에 AOP를 적용하는 AspectJ를 사용하면 이런 문제는 발생하지 않는다.
        // 그러나, 로드 타임 위빙을 사용하고 JVM 옵션이 들어가야 하기 때문에 부담이다!
        // 그래도 스프링에서는 이에 대한 대안을 내놓았다 ~.~
    }
}