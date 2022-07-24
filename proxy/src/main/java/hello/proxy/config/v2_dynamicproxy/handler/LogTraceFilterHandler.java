package hello.proxy.config.v2_dynamicproxy.handler;

// no-log를 했을 때는 로그가 안 남도록!
// 이를 위해 메서드 이름을 기준으로 특정 조건을 만족할 때만 로그를 남기도록 하자.

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;
import org.springframework.util.PatternMatchUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@RequiredArgsConstructor
public class LogTraceFilterHandler implements InvocationHandler {
    private final Object target;
    private final LogTrace logTrace;
    private final String[] patterns;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 메서드 이름으로 필터
        String methodName = method.getName();

        // 스프링에서 제공하는 메서드 이름 매칭
        // *을 통해서 시작, 끝 str 매칭 할 수 있음
        // pattern은 외부에서 입력받도록!
        if(!PatternMatchUtils.simpleMatch(patterns, methodName))
            return method.invoke(target, args);

        // 매칭이 되는 경우는 다음과 같이 로그 추적기도 함께 실행되도록!
        TraceStatus status = null;
        try {
            String message = method.getDeclaringClass().getSimpleName() + "."
                    + method.getName() + "()";
            status = logTrace.begin(message);
            //로직 호출
            Object result = method.invoke(target, args);
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
