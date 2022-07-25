package hello.proxy.config.v5_autoproxy;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({AppV1Config.class, AppV2Config.class})
public class AutoProxyConfig {
//    @Bean
    public Advisor advisor1 (LogTrace logTrace) {
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*", "order*", "save*");
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }

    // 단, 이때는 package를 기준으로 포인트컷 매칭을 해서 no-log 접속 시에도 로그 출력됨.
//    @Bean
    public Advisor advisor2(LogTrace logTrace) {
        // AspectJ 포인트컷 표현식의 적용
        // * : 모든 반환 타입, hello.proxy.app.. : 해당 패키지, 하위 패키지
        // *(..) : *는 모든 메서드 이름, (..)는 파라미터는 상관없음.
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* hello.proxy.app..*(..))");
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }

    @Bean
    public Advisor advisor3 (LogTrace logTrace) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        // noLog() 메서드는 제외하도록.
        pointcut.setExpression("execution(* hello.proxy.app..*(..)) && !execution(* hello.proxy.app..noLog(..))");
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }

    /**
     * 프록시 자동 생성기는 프록시를 **하나만** 생성한다.
     * - 프록시 팩토리가 생성하는 프록시는 내부에 여러 advisor들을 포함할 수 있기 때문!
     *
     * advisor1의 포인트컷만 만족한다면 = 프록시 1개, 프록시애 advior1만.
     * advisor1, 2 만족 = 프록시 1개에 advisor1, 2 모두.
     * 둘 다 만족 x = 프록시 생성 X
     */
}
