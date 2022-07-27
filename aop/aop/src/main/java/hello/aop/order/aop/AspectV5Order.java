package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

@Slf4j
public class AspectV5Order {

    // 기본적으로 어드바이스는 순서를 보장하지 않기 때문에, 순서를 지정하려면 @Order 사용하기.
    // 단, @Order는 클래스 단위에서 동작하기 때문에 클래스별로 분리해야 한다.

    // doTx -> doLog 순서대로 실행되도록.
    /*
    [트랜잭션 시작] void hello.aop.order.OrderService.orderItem(String)
    [log] void hello.aop.order.OrderService.orderItem(String)
    [orderService] 실행...
    이런 식으로 트랜잭션이 먼저 찍힌다.
     */

    @Aspect
    @Order(2)
    public static class LogAspect {
        @Around("hello.aop.order.aop.Pointcuts.allOrder()")
        public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[log] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }

    @Aspect
    @Order(1)
    public static class TxAspect {
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

}
