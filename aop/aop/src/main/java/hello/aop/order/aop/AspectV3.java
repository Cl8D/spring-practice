package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
@Aspect
public class AspectV3 {
    @Pointcut("execution(* hello.aop.order..*(..))")
    public void allOrder() {}

    // 클래스의 이름 패턴이 *Service 형태.
    @Pointcut("execution(* *..*Service.*(..))")
    private void allService() {}

    @Around("allOrder()") // 다음과 같이 포인트컷을 직접 등록하지 않고 시그니처를 사용해도 된다.
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature());
        return joinPoint.proceed();
    }

    @Around("allOrder() && allService()") // 리파지토리는 적용 x
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

        /**
         * 이렇게 하면 실행 흐름
         * 클라이언트 -> doLog -> doTransaction -> orderService.orderItem()
         * -> doLog() -> orderRepository.save()
         *
         * orderService에는 doLog, doTx 두 가지의 어드바이스가,
         * orderRepository에는 doLog 하나만 적용.
         */
    }
}
