package hello.proxy.jdkdynamic.code;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

// 동적 프록시 적용하려면 InvocationHandler 인터페이스를 구현하면 된다!
@RequiredArgsConstructor
@Slf4j
public class TimeInvocationHandler implements InvocationHandler {
    // 동적 프록시가 호출할 대상
    private final Object target;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("TimeProxy 실행");
        long startTime = System.currentTimeMillis();

        // 리플렉션을 통해서 target 인스턴스의 메서드 실행.
        Object result = method.invoke(target, args);

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;

        log.info("TimeProxy 종료 resultTime={}", resultTime);
        return result;

    }
}
