package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class AspectV1 {

    // @Around 내부의 값 = 포인트컷, 메서드는 Advice 자체.
    // hello.app.order 패키지와 하위 패키지에 적용.
    @Around("execution(* hello.aop.order..*(..))")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature());
        // [log] void hello.aop.order.OrderService.orderItem(String)
        // 이런 식으로 어떤 메서드가 적용되었는지 확인할 수 있다.
        return joinPoint.proceed();
    }
}
