package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
@Aspect
public class AspectV2 {

    // 포인트컷 표현식 사용. 메서드 이름 + 파라미터 = 포인트컷 시그니처.
    // 메서드의 반환 타입은 void이어야 하며, 코드 내용을 비워둔다.
    // 다른 어드바이스에서 사용할 수 있도록, 그리고 외부 어드바이스에서도 사용 가능하도록 (단, 이때는 접근 제어자가 public 이어야 한다)
    @Pointcut("execution(* hello.aop.order..*(..))")
    private void allOrder() {
    }

    @Around("allOrder()") // 다음과 같이 포인트컷을 직접 등록하지 않고 시그니처를 사용해도 된다.
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature());
        return joinPoint.proceed();
    }
}
