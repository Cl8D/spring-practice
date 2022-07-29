package hello.aop.internalcall;

import hello.aop.internalcall.aop.CallLogAspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@Import(CallLogAspect.class)
@SpringBootTest
class CallServiceV3Test {
    @Autowired
    CallServiceV3 callServiceV3;

    @Test
    void external() {
        callServiceV3.external();
        /*
        구조 자체가 callService -> internalService를 호출하도록 변경되었다.
        실제 타겟이 외부 호출을 하는 거니까, 프록시를 호출하고, internal에서도 실제 객체를 호출하게 되는 것.
         */
    }
}