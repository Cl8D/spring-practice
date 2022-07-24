package hello.proxy.config.v2_dynamicproxy.handler;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

// JDK 동적 프록시는 인터페이스가 필수이기 때문에 V1 애플리케이션에만 적용 가능!
@RequiredArgsConstructor
public class LogTraceBasicHandler implements InvocationHandler {
    private final Object target;
    private final LogTrace logTrace;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TraceStatus status = null;
        try {
            // 호출되는 클래스와 메서드 이름 정보 남기기
            String message = method.getDeclaringClass().getSimpleName() + "."
                    + method.getName() + "()";

            status = logTrace.begin(message);
            //로직 호출
            Object result = method.invoke(target, args);
            logTrace.end(status);
            return result;
        } catch(Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
