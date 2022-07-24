package hello.proxy.jdkdynamic;

import hello.proxy.jdkdynamic.code.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

@Slf4j
public class JdkDynamicProxyTest {
    @Test
    void dynamicA() {
        AInterface target = new AImpl();

        // 동적 프록시에 적용할 핸들러 로직
        TimeInvocationHandler handler = new TimeInvocationHandler(target);
        // 동적 프록시 생성
        // 클래스 로더 정보, 인터페이스 정보(여러 개 가능, 배열 형태), 핸들러 정보
        // 기존에는 반환 타입이 Object 형이어서 타입 캐스팅해줌
        AInterface proxy = (AInterface) Proxy.newProxyInstance(AInterface.class.getClassLoader(),
                new Class[]{AInterface.class}, handler);
        proxy.call();

        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
        /*
        15:40:44.948 [Test worker] INFO hello.proxy.jdkdynamic.code.TimeInvocationHandler - TimeProxy 실행
        15:40:44.955 [Test worker] INFO hello.proxy.jdkdynamic.code.AImpl - A 호출
        15:40:44.955 [Test worker] INFO hello.proxy.jdkdynamic.code.TimeInvocationHandler - TimeProxy 종료 resultTime=0
        15:40:44.959 [Test worker] INFO hello.proxy.jdkdynamic.JdkDynamicProxyTest - targetClass=class hello.proxy.jdkdynamic.code.AImpl
        15:40:44.960 [Test worker] INFO hello.proxy.jdkdynamic.JdkDynamicProxyTest - proxyClass=class com.sun.proxy.$Proxy12

        보면, target은 우리의 실제 클래스지만 proxy의 경우 프록시가 만들어진 걸 볼 수 있다.
        프록시는 내부의 invoke 함수를 호출해준다. 이때, call() 함수가 넘어왔다고 보면 된다.
         */
    }

    @Test
    void dynamicB() {
        BInterface target = new BImpl();
        TimeInvocationHandler handler = new TimeInvocationHandler(target);
        BInterface proxy = (BInterface) Proxy.newProxyInstance(BInterface.class.getClassLoader(),
                new Class[]{BInterface.class}, handler);
        proxy.call();

        // 참고로, dynamicA, dynamicB를 동시에 함께 실행하면 각각 다른 동적 프록시 클래스가 만들어진다.
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
    }

    /**
     * 클라이언트가 JDK 동적 클래스의 call() 실행.
     * - 그럼, JDK 동적 프록시가 InvocationHandler.invoke()를 호출한다.
     * 이때, 구현체로 Time~이 있으니까 Time.invoke()가 호출된다.
     * - Time~은 내부 로직 수행, method.invoke를 통해 실제 target 객체인 AImpl을 호출한다.
     * AImpl의 call()이 실행되고, 종료 후 TIme~으로 응답이 돌아온 다음 시간 로그를 출력하고 결과 반환.
     *
     * 개발자는 직접 프록시를 만드는 건 아니고, 프록시에 필요한 핸들러만 만들어주면 되는 것!
     */
}
