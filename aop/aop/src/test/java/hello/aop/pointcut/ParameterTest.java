package hello.aop.pointcut;

import hello.aop.member.MemberService;
import hello.aop.member.annotation.ClassAop;
import hello.aop.member.annotation.MethodAop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Slf4j
@Import(ParameterTest.ParameterAspect.class)
@SpringBootTest
public class ParameterTest {
    @Autowired
    MemberService memberService;
    @Test
    void success() {
        // 프록시 적용 완료!
        // memberService Proxy=class hello.aop.member.MemberServiceImpl$
        log.info("memberService Proxy={}", memberService.getClass());
        // 파라미터로 'helloA'를 넘겨준 상태.
        memberService.hello("helloA");
    }

    @Slf4j
    @Aspect
    static class ParameterAspect {
        @Pointcut("execution(* hello.aop.member..*.*(..))")
        private void allMember() {}

        @Around("allMember()")
        public Object logArgs1(ProceedingJoinPoint joinPoint) throws Throwable
        {
            // 이런 식으로 매개변수를 빼낼 수 있다.
            // arg에 helloA가 찍히게 된다!
            Object arg1 = joinPoint.getArgs()[0];
            // String hello.aop.member.MemberServiceImpl.hello(String), arg=helloA
            log.info("[logArgs1]{}, arg={}", joinPoint.getSignature(), arg1);
            return joinPoint.proceed();
        }


        // 이런 식으로 직접 꺼내지 않고, 여기서 꺼내서 함수 내의 파라미터로 받을 수 있다.
        @Around("allMember() && args(arg,..)")
        // 여기서 인자로 arg가 넘어온다.
        public Object logArgs2(ProceedingJoinPoint joinPoint, Object arg)
                throws Throwable {
            log.info("[logArgs2]{}, arg={}", joinPoint.getSignature(), arg);
            return joinPoint.proceed();
        }

        // 타입 지정 가능, @Before를 써서 조금 더 깔끔하게 코드를 유지할 수 있다.
        @Before("allMember() && args(arg,..)")
        public void logArgs3(String arg) {
            log.info("[logArgs3] arg={}", arg);
        }

        // this-target의 차이.
        // 둘 다 *같은 패턴 사용 불가능, 부모 타입을 허용한다. 타입 하나를 정확하게 지정해야 한다.
        // this는 스프링 컨테이너에 올라간 프록시 객체, target은 프록시가 호출한 실제 대상이 된다.
        @Before("allMember() && this(obj)")
        public void thisArgs(JoinPoint joinPoint, MemberService obj) {
            // [this]String hello.aop.member.MemberServiceImpl.hello(String), obj=class
            // hello.aop.member.MemberServiceImpl$$EnhancerBySpringCGLIB$$8
            log.info("[this]{}, obj={}", joinPoint.getSignature(),
                    obj.getClass());
        }

        @Before("allMember() && target(obj)")
        public void targetArgs(JoinPoint joinPoint, MemberService obj) {
            // [target]String hello.aop.member.MemberServiceImpl.hello(String), obj=class
            //hello.aop.member.MemberServiceImpl
            log.info("[target]{}, obj={}", joinPoint.getSignature(),
                    obj.getClass());
        }

        // annotation 전달받기.
        @Before("allMember() && @target(annotation)")
        public void atTarget(JoinPoint joinPoint, ClassAop annotation) {
            log.info("[@target]{}, obj={}", joinPoint.getSignature(),
                    annotation);
        }
        @Before("allMember() && @within(annotation)")
        public void atWithin(JoinPoint joinPoint, ClassAop annotation) {
            log.info("[@within]{}, obj={}", joinPoint.getSignature(),
                    annotation);
        }

        // 애노테이션 내부에 있는 값을 꺼낼 수 있다.
        @Before("allMember() && @annotation(annotation)")
        public void atAnnotation(JoinPoint joinPoint, MethodAop annotation) {
            // [@annotation]String hello.aop.member.MemberServiceImpl.hello(String),
            //annotationValue=test value
            log.info("[@annotation]{}, annotationValue={}",
                    joinPoint.getSignature(), annotation.value());
        }
    }
}