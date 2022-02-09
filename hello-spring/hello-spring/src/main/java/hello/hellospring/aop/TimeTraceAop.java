package hello.hellospring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

//aop를 사용하기 위해 @Aspect, 스프링 빈에 등록하기 위해 @Component
@Component
@Aspect
public class TimeTraceAop {
    // 공통 관심 사항을 어디에 적용시킬지 targeting 하는 코드
    @Around("execution(* hello.hellospring..*(..))")

    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        // 시간 측정
        long start = System.currentTimeMillis();

        // 안에 어떤 메서드가 있는지
        System.out.println("START: " + joinPoint.toString());

        try {
            // 다음 메서드로 진행됨
            return joinPoint.proceed();
        } finally {
            long finish = System.currentTimeMillis();
            // 시간 측정 코드
            long timeMs = finish - start;

            System.out.println("END: " + joinPoint.toString()+ " " + timeMs + "ms");
        }
    }
}
