package hello.proxy.jdkdynamic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 지금까지는 대상 클래스만큼 동일한 기능을 하는 프록시 클래스를 만들었어야 했는데,
 * 이번에는 프록시 객체를 동적으로 만들어보자.
 *
 * 그전에, 자바의 리플렉션을 이해해보자.
 */
@Slf4j
public class ReflectionTest {
    @Test
    void reflection0() {
        Hello target = new Hello();

        // 공통 로직 1 시작
        log.info("start");
        String result1 = target.callA();
        log.info("result={}", result1);
        //공통 로직1 종료

        //공통 로직2 시작
        log.info("start");
        String result2 = target.callB(); //호출하는 메서드가 다름
        log.info("result={}", result2);
        //공통 로직2 종료

        // 이렇게 호출하는 메서드를 동적으로 처리하여 공통화하는 게 리플렉션.
    }

    @Test
    void reflection1() throws Exception {
        // 패키지 경로를 포함하여 Hello class에 접근
        // 내부 클래스는 $로 접근한다.
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();

        // 해당 클래스의 call 메서드 메타정보를 획득하기
        // Hello 클래스의 callA 메서드의 메타 정보가 들어간다.
        Method callA = classHello.getMethod("callA");
        // 메타정보를 통해 실제 인스턴스의 메서드 호출
        // 즉, target의 callA가 호출되는 것.
        Object result1 = callA.invoke(target);
        log.info("result1={}", result1);

        Method callB = classHello.getMethod("callB");
        Object result2 = callB.invoke(target);
        log.info("result2={}", result2);

        // 여기서 보면, 'Method'라는 제네릭 클래스를 사용하게 된다.
        // 즉, 동적으로 클래스, 메서드 정보를 바꿀 수 있게 되는 것!
    }

    @Test
    void reflection2() throws Exception {
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();
        Method callA = classHello.getMethod("callA");
        dynamicCall(target, callA);

        Method callB = classHello.getMethod("callB");
        dynamicCall(target, callB);

    }

    // 한 번에 처리할 수 있도록 만든 통합된 공통 로직 처리
    // Method를 통해서 메타 정보가 동적으로 제공된다.
    // 단, 리플렉션은 컴파일 시점이 아닌 런타임 시점에 동작하기 대문에 사실 일반적으로 사용하면 안 된다.
    private void dynamicCall(Object target, Method method) throws Exception {
        log.info("start");
        Object result = method.invoke(target);
        log.info("result={}", result);
    }


    @Slf4j
    static class Hello {
        public String callA() {
            log.info("callA");
            return "A";
        }
        public String callB() {
            log.info("callB");
            return "B";
        }

    }
}
