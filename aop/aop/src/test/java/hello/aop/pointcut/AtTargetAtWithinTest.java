package hello.aop.pointcut;

import hello.aop.member.annotation.ClassAop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/*
    @target: 인스턴스의 모든 메서드를 조인 포인트로 적용
        -> 부모 클래스의 메서드까지 어드바이스 적용
    @within : 해당 타입 내에 존재하는 메서드만 조인 포인트로 적용
        -> 자기 자신의 클래스에 정의된 메서드만!
 */
@Slf4j
@Import({AtTargetAtWithinTest.Config.class})
@SpringBootTest
public class AtTargetAtWithinTest {
    @Autowired
    Child child;

    @Test
    void success() {
        // child Proxy=class hello.aop.pointcut.AtTargetAtWithinTest$Child$$EnhancerBySpringCGLIB$$39d8909f
        log.info("child Proxy={}", child.getClass());
        child.childMethod(); //부모, 자식모두 있는 메서드 => 여기서 @target, @within 둘 다 적용

        // 여기서는 오직 @target에 대해서만 적용.
        child.parentMethod(); //부모 클래스만 있는 메서드
    }

    static class Config {
        @Bean
        public Parent parent() {
            return new Parent();
        }
        @Bean
        public Child child() {
            return new Child();
        }
        @Bean
        public AtTargetAtWithinAspect atTargetAtWithinAspect() {
            return new AtTargetAtWithinAspect();
        }
    }

    static class Parent {
        public void parentMethod(){} //부모에만 있는 메서드
    }

    @ClassAop
    static class Child extends Parent {
        public void childMethod(){}
    }

    @Slf4j
    @Aspect
    static class AtTargetAtWithinAspect {
        // 단, @args, @target 같은 경우 단독 사용이 안 된다. 보면 execution을 먼저 적용한 걸 볼 수 있는데,
        // 왜냐면 해당 애노테이션들은 '실제 객체 인스턴스가 생성되고' '실행이 될 때' 적용 여부를 확인할 수 있기 때문에,
        // 프록시가 필요하다. 프록시가 먼저 있어야 하니까 @execution을 통해 미리 만들어두는 것.

        // @target: 인스턴스 기준으로 모든 메서드의 조인 포인트를 선정, 부모 타입의 메서드도 적용
        // classAop라는 어노테이션이 있는 애 적용 (target)
        // target이니까 child, 그리고 child의 부모인 parent까지.
        @Around("execution(* hello.aop..*(..)) && @target(hello.aop.member.annotation.ClassAop)")
        public Object atTarget(ProceedingJoinPoint joinPoint) throws Throwable {
            // [@target] void hello.aop.pointcut.AtTargetAtWithinTest$Child.childMethod()
            // [@target] void hello.aop.pointcut.AtTargetAtWithinTest$Parent.parentMethod()
            log.info("[@target] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        // @within: 선택된 클래스 내부에 있는 메서드만 조인 포인트로 선정, 부모 타입의 메서드는 적용되지 않음
        // within이니까 오직 classAop가 붙어 있는 child만.
        @Around("execution(* hello.aop..*(..)) && @within(hello.aop.member.annotation.ClassAop)")
        public Object atWithin(ProceedingJoinPoint joinPoint) throws Throwable {
            // [@within] void hello.aop.pointcut.AtTargetAtWithinTest$Child.childMethod()
            log.info("[@within] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }
}
