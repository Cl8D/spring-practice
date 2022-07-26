package hello.proxy.config.v6_aop.aspect;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect // 포인트컷 + 어드바이스로 구성된 어드바이저 생성 기능. 프록시를 통한 AOP를 가능하도록 한다.
// 사실 자동 프록시 생성기는 @Aspect를 찾아서 Advisor로 만들어준다!
@RequiredArgsConstructor
public class LogTraceAspect {
    private final LogTrace logTrace;

    // AspectJ 포인트컷 표현식을 넣을 수 있다.
    // Around의 메서드는 Advice가 된다.
    @Around("execution(* hello.proxy.app..*(..)) && !execution(* hello.proxy.app..noLog(..))")
    // joinPoint = MehtodInvocation과 비슷함.
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        TraceStatus status = null;

        try {
            // getTarget() = 실제 호출 대상
            // getArgs() = 전달 인자
            // getSignature() = join point 시그니처
            String message = joinPoint.getSignature().toShortString();
            status = logTrace.begin(message);

            // 호출
            Object result = joinPoint.proceed();

            logTrace.end(status);
            return result;
        } catch(Exception e) {
            logTrace.exception(status, e);
            throw e;
        }

    }
}
