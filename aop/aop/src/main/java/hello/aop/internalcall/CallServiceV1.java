package hello.aop.internalcall;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 프록시 내부 호출 해결 방법
 * - 자기 자신을 의존 관계 주입 받기. (단, 수정자 주입 이용)
 */
@Slf4j
@Component
public class CallServiceV1 {
    private CallServiceV1 callServiceV1;

    // 단, 생성자 주입으로는 순환 사이클을 만들기 때문에 실패한다.
    // setter 주입을 이용하자. - properties 추가 필요.
    // 여기서 주입받은 대상은 AOP가 적용되었다면 실제 객체가 아닌 프록시 객체가 된다.
    // 그렇기 때문에 프록시 callServiceV1.internal()을 호출하게 되어 내부 함수 호출에도 적용이 가능하다.
    @Autowired
    public void setCallServiceV1(CallServiceV1 callServiceV1) {
        // callServiceV1 setter=class hello.aop.internalcall.CallServiceV1$$EnhancerBySpringCGLIB$$32f238b0
        log.info("callServiceV1 setter={}", callServiceV1.getClass());
        this.callServiceV1 = callServiceV1;
    }

    public void external() {
        log.info("call external");
        // 여기서 바로 internal()이 아닌 (this.internal())
        // callService1.internal()을 통해, 프록시 객체의 internal()을 호출하기 때문에
        // 내부에서 호출해도 잘 적용이 되는 것이다.
        callServiceV1.internal();
    }

    public void internal() {
        log.info("call internal");
    }

}
