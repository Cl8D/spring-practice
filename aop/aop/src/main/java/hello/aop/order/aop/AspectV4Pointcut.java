package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class AspectV4Pointcut {

    // 외부에 있는 포인트컷을 사용할 때는 패키지명을 포함한 클래스 이름과 포인트컷 시그니처 사용하기!
    @Around("hello.aop.order.aop.Pointcuts.allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature());
        return joinPoint.proceed();
    }

    @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // 핵심 로직 실행 전에 트랜잭션 시작
            log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
            // 핵심 로직 실행
            Object result = joinPoint.proceed();
            // 로직에 문제가 없으면 커밋
            log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
            return result;
        } catch (Exception e) {
            // 문제가 있으면 롤백
            log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        } finally {
            // 종료 후 리소스 릴리즈.
            log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }
}
