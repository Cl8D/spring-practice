package hello.proxy.common.advice;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

@Slf4j
// 스프링 AOP 모듈 안의 methodInterceptor 사용하기
public class TimeAdvice implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("TimeProxy 실행");
        long startTime = System.currentTimeMillis();

        // target 클래스를 호출하고, 그 결과를 받는다.
        // target 클래스의 정보는 MethodInvocation 안에 있다!
        // 왜냐면, 프록시 팩토리로 프록시를 생성할 때 target의 정보를 파라미터로 받기 때문!
        Object result = invocation.proceed();
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;

        log.info("TimeProxy 종료 resultTime={}ms", resultTime);
        return result;
    }
}
