package hello.proxy.cglib.code;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@Slf4j
// CGLIB -> MethodInterceptor 제공
@RequiredArgsConstructor
public class TimeMethodInterceptor implements MethodInterceptor {
    private final Object target;

    @Override
    // o : CGLIB가 적용된 객체
    // method : 호출된 메서드
    // objects: 메서드를 호출하면서 전달된 인수
    // methodProxy : 메서드 호출에 사용
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        log.info("TimeProxy 실행");
        long startTime = System.currentTimeMillis();

        // 프록시는 target을 실제로 호출한다.
        Object result = methodProxy.invoke(target, objects);
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("TimeProxy 종료 resultTime={}", resultTime);
        return result;
    }
}
