package hello.aop.exam.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Slf4j
@Aspect
public class TraceAspect {
    // @Trace가 붙은 메서드에 어드바이스 적용
    @Before("@annotation(hello.aop.exam.annotation.Trace)")
    public void doTrace(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        log.info("[trace] {} args={}", joinPoint.getSignature(), args);
        /*
            Test에서 돌려보면 다음과 같이 출력된다.
            [trace] void hello.aop.exam.ExamService.request(String) args=[data0]
            [trace] String hello.aop.exam.ExamRepository.save(String) args=[data0]
            [trace] void hello.aop.exam.ExamService.request(String) args=[data1]
            [trace] String hello.aop.exam.ExamRepository.save(String) args=[data1]
            ...
            일케 data4번까지 된다!
         */
    }
}
