package hello.aop.exam.aop;

import hello.aop.exam.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class RetryAspect {

    @Around("@annotation(retry)")
    public Object deRetry (ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {
        log.info("[retry] {} retry={}", joinPoint.getSignature(), retry);

        int maxRetry = retry.value(); // retry 어노테이션의 value 값이 들어온다. (사용자 지정)
        Exception exceptionHolder = null;

        // 결과가 정상 반환되지 않으면 maxRetry 만큼 재시도.
        for (int retryCount = 1; retryCount <= maxRetry; retryCount++) {
            try {
                log.info("[retry] try count={}/{}", retryCount, maxRetry);
                return joinPoint.proceed(); // 반복문에 의해 여러 번 호출
            } catch (Exception e) { // 예외 발생 시 exception을 넣어두기.
                exceptionHolder = e;
            }
        }
        // 재시도 횟수가 끝나면 담아뒀던 예외 던지기
        throw exceptionHolder;
    }
}
