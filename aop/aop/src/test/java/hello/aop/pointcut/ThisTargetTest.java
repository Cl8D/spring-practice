package hello.aop.pointcut;

import hello.aop.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * application.yml
 * spring.aop.proxy-target-class=true CGLIB
 * : 구체클래스를 상속받아서 프록시 객체 생성
 * spring.aop.proxy-target-class=false JDK 동적 프록시
 * : 인터페이스를 구현한 프록시 객체.
 */

@Slf4j
@Import(ThisTargetTest.ThisTargetAspect.class)
@SpringBootTest(properties = "spring.aop.proxy-target-class=false") //JDK 동적 프록시
//@SpringBootTest(properties = "spring.aop.proxy-target-class=true") //CGLIB
public class ThisTargetTest {
    @Autowired
    MemberService memberService;

    @Test
    void success() {
        log.info("memberService Proxy={}", memberService.getClass());
        memberService.hello("helloA");
    }

    @Slf4j
    @Aspect
    static class ThisTargetAspect {
        //부모 타입 허용 - this일 경우
        @Around("this(hello.aop.member.MemberService)")
        public Object doThisInterface(ProceedingJoinPoint joinPoint) throws Throwable {
            // [JDK]
            // [this-interface] String hello.aop.member.MemberService.hello(String)
            // JDK에서는 인터페이스를 구현하기 때문에 부모 타입으로 잘 적용된다.

            // [CGLIB]
            // [this-interface] String hello.aop.member.MemberServiceImpl.hello(String)
            // CGLIB는 구체 클래스를 사용하지만, 부모타입이 가능하기 때문에 memberService도 가능하다.
            log.info("[this-interface] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        //부모 타입 허용 - target일 경우.
        @Around("target(hello.aop.member.MemberService)")
        public Object doTargetInterface(ProceedingJoinPoint joinPoint) throws Throwable {
            // [JDK]
            // [target-interface] String hello.aop.member.MemberService.hello(String)

            // [CGLIB]
            // [target-interface] String hello.aop.member.MemberServiceImpl.hello(String)

            // target은 실제 객체 사용. DK, CGLIB에 상관없이
            // 부모 타입을 허용하기 때문에 memberService, MemberServiceImpl 모두 대상이다.
            log.info("[target-interface] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        //this: 스프링 AOP 프록시 객체 대상
        //JDK 동적 프록시는 인터페이스를 기반으로 생성되므로 구현 클래스를 알 수 없음
        //CGLIB 프록시는 구현 클래스를 기반으로 생성되므로 구현 클래스를 알 수 있음
        @Around("this(hello.aop.member.MemberServiceImpl)")
        public Object doThis(ProceedingJoinPoint joinPoint) throws Throwable {
            // [JDK]
            // JDK에서는 로그를 안 남기는데, 왜냐면 JDK는 인터페이스를 이용해서 만드니까 구체 클래스를 알 수 없어서 안 뜬다.

            // [CGLIB]
            // [this-impl] String hello.aop.member.MemberServiceImpl.hello(String)
            // CGLIB는 구체 클래스를 사용하기 때문에 가능하다.
            log.info("[this-impl] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        //target: 실제 target 객체 대상
        @Around("target(hello.aop.member.MemberServiceImpl)")
        public Object doTarget(ProceedingJoinPoint joinPoint) throws Throwable {
            // [JDK]
            // [target-impl] String hello.aop.member.MemberService.hello(String)

            // [CGLIB]
            // [target-impl] String hello.aop.member.MemberServiceImpl.hello(String)

            // target은 실제 객체 사용. JDK, CGLIB에 상관없이
            // MemberServiceImpl 자체는 당연히 대상이다.
            log.info("[target-impl] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }
}