package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

@Slf4j
@Aspect
public class AspectV6Advice {

    // 메서드 호출 이전에 실행, 사실 모든 것을 다 할 수 있다고 생각하면 된다.
    // 단, @Around는 인자로 ProceedingJoinPoint를 사용해야 한다. (JoinPoint의 상속형)
    @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // 핵심 로직 실행 전에 트랜잭션 시작 = @Before
            log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
            // 핵심 로직 실행
            Object result = joinPoint.proceed();
            // 로직에 문제가 없으면 커밋 = @AfterReturning
            log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
            return result;
        } catch (Exception e) {
            // 문제가 있으면 롤백 = @AfterThrowing
            log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        } finally {
            // 종료 후 리소스 릴리즈. = @After
            log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }

    // Join point 실행 이전에 실행 - 작업 흐름 자체는 변경 불가능.
    // 인자로 JoinPoint 사용
    // 메서드가 종료하면 자동으로 다음 타켓이 호출된다.
    @Before("hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doBefore(JoinPoint joinPoint) {
        log.info("[before] {}", joinPoint.getSignature());
    }

    // 조인 포인트 정상 완료 다음 실행
    // 단, 이 경우 return 값에 대한 것을 바꿀 수가 없다. 단순히 부가 로직이 실행될 때 사용하면 좋을 듯
    // returning에 있는 이름은 어드바이스 메서드의 매개변수 이름과 동일해야 한다.
    @AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()",
            returning = "result")
    public void doReturn(JoinPoint joinPoint, Object result) {
        log.info("[return] {} return={}", joinPoint.getSignature(), result);
    }

    // 메서드가 예외를 던지는 경우 실행
    // throwing에 있는 이름은 어드바이스 메서드의 매개변수 이름과 동일해야 한다.
    // 여기서 지정된 타입과 맞은 예외를 대사응로 실행되며, 부모 타입이라면 모든 자식 타입을 인정한다.
    @AfterThrowing(value = "hello.aop.order.aop.Pointcuts.orderAndService()",
            throwing = "ex")
    public void doThrowing(JoinPoint joinPoint, Exception ex) {
        log.info("[ex] {} message={}", joinPoint.getSignature(),
                ex.getMessage());
    }

    // 조인 포인트가 정상이든 예외이든 상관없이 실행
    @After(value = "hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doAfter(JoinPoint joinPoint) {
        log.info("[after] {}", joinPoint.getSignature());
    }

    /**
     * 호출 순서)
     * @Around -> @Before -> @After -> @AfterReturning -> @AfterThrowing 순서.
     * 호출 순서와 리턴 순서는 반대다!
     *
     * @Around는 모든 일을 할 수 있지만, 꼭 joinPoint.proceed()를 호출해야 하기 때문에 오류가 발생할 수 있다.
     * 그래도 조인 포인트 실행 여부 선택, 전달값 및 반환값 변환, 예외 변환이 가능하다.
     */
}
