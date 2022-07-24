package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Method;

@Slf4j
public class AdvisorTest {
    @Test
    void advisorTest1() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        // Advisor의 구현체. 하나의 포인트컷과 하나의 어드바이스 넣어주기.
        // 항상 true를 반환하는 포인트컷을 넣어주었다.
        // 그리고 부가로직인 timeAdvice 추가해주기.
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());

        // 프록시 팩토리에 어드바이저를 적용해주기.
        // 사실 이전 코드에서는 그냥 프록시 팩토리에 어드바이스를 넣어주었지만,
        // 내부 로직으로는 위의 코드처럼 Pointcut.TRUE가 알아서 들어간다. (이게 디폴트임)
        proxyFactory.addAdvisor(advisor);
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();

        /*
            둘 다 어드바이스가 적용되어서 시간 측정이 잘 되는 걸 볼 수 있다!
            18:08:33.950 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 실행
            18:08:33.967 [Test worker] INFO hello.proxy.common.service.ServiceImpl - save 호출
            18:08:33.968 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 종료 resultTime=1ms
            18:08:33.979 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 실행
            18:08:33.981 [Test worker] INFO hello.proxy.common.service.ServiceImpl - find 호출
            18:08:33.982 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 종료 resultTime=1ms
        */
    }

    // 포인트컷을 직접 구현하기.
    // 포인트컷 = classFilter + methodMatcher.
    // 하나는 클래스가 맞는지, 하나는 메서드가 맞는지 확인한다.
    @Test
    @DisplayName("직접 만든 포인트컷")
    void advisorTest2() {
        ServiceImpl target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(new MyPointCut(), new TimeAdvice());

        proxyFactory.addAdvisor(advisor);

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        proxy.save();
        proxy.find();

        /*
        18:17:12.118 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 실행
        18:17:12.118 [Test worker] INFO hello.proxy.common.service.ServiceImpl - save 호출
        18:17:12.119 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 종료 resultTime=0ms
        18:17:12.119 [Test worker] INFO hello.proxy.advisor.AdvisorTest - 포인트컷 호출 method=find targetClass=class hello.proxy.common.service.ServiceImpl
        18:17:12.131 [Test worker] INFO hello.proxy.advisor.AdvisorTest - 포인트컷 결과 result=false
        18:17:12.132 [Test worker] INFO hello.proxy.common.service.ServiceImpl - find 호출

        보면 find에는 시간 측정이 적용이 안 되고, save에만 적용된 것을 볼 수 있다!
         */
    }


    // 우리는 save에는 시간 측정 적용, find에는 적용을 안 해보자!
    static class MyPointCut implements Pointcut {

        @Override
        public ClassFilter getClassFilter() {
            return ClassFilter.TRUE;
        }

        @Override
        public MethodMatcher getMethodMatcher() {
            return new MyMethodMatcher();
        }
    }

    static class MyMethodMatcher implements MethodMatcher {
        private String matchName = "save";

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            // 메서드 이름이 save인 경우 true를 반환하도록
            boolean result = method.getName().equals(matchName);
            log.info("포인트컷 호출 method={} targetClass={}", method.getName(),
                    targetClass);
            log.info("포인트컷 결과 result={}", result);

            // 18:17:12.011 [Test worker] INFO hello.proxy.advisor.AdvisorTest - 포인트컷 호출 method=save targetClass=class hello.proxy.common.service.ServiceImpl
            // 18:17:12.060 [Test worker] INFO hello.proxy.advisor.AdvisorTest - 포인트컷 결과 result=true
            return result;
        }

        // isRuntime이 참이면 두 번째 matches 함수가 대신 호출되고, (args가 넘어오는 걸 볼 수 있음)
        // 동적으로 넘어오는 매개변수를 판단 로직으로 사용 가능하다. (캐싱 x)
        // false라면 첫 번째 matches를 사용하고, 클래스의 정적 정보만 사용해서 캐싱을 진행한다. => 성능 향상
        @Override
        public boolean isRuntime() {
            return false;
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            throw new UnsupportedOperationException();
        }


        @Test
        @DisplayName("스프링이 제공하는 포인트컷")
        void advisorTest3() {
            ServiceImpl target = new ServiceImpl();
            ProxyFactory proxyFactory = new ProxyFactory(target);

            // 이런 식으로 메서드 이름을 지정하면 스프링에서 제공하는 포인트컷을 사용 가능하다!
            // 그외에도 다양하게 제공하지만, 실무에서는 AspectJExpressionPointcut을 많이 사용한다고 한다.
            NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
            pointcut.setMappedName("save");

            DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, new TimeAdvice());
            proxyFactory.addAdvisor(advisor);
            ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

            proxy.save();
            proxy.find();
        }

    }
}
