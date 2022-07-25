package hello.proxy.postprocessor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BeanPostProcessorTest {
    @Test
    void postProcessor() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanPostProcessorConfig.class);
        // beanA라는 이름으로 B 객체가 빈으로 등록된다.
        B b = applicationContext.getBean("beanA", B.class);
        b.helloB();

        assertThrows(NoSuchBeanDefinitionException.class,
                () -> applicationContext.getBean(A.class));

    }

    @Slf4j
    @Configuration
    static class BeanPostProcessorConfig {
        @Bean(name = "beanA")
        public A a() {
            return new A();
        }

        @Bean
        public AToBPostProcessor helloPostProcessor() {
            return new AToBPostProcessor();
        }
    }

    @Slf4j
    static class A {
        public void helloA() {
            log.info("hello A");
        }
    }

    @Slf4j
    static class B {
        public void helloB() {
            log.info("hello B");
        }
    }

    // 스프링 빈으로 등록해주면, 스프링 컨테이너가 빈 후처리기로 인식한다.
    @Slf4j
    static class AToBPostProcessor implements BeanPostProcessor {
        // 객체 생성 이후, @PostConstruct 같은 초기화가 발생한 다음 호출되는 프로세서
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            // beanName=beanA bean=hello.proxy.postprocessor.BeanPostProcessorTest$A@7249dadf
            // 보면 A 대신 B가 등록된 것을 확인할 수 있다.
            log.info("beanName={} bean={}", beanName, bean);
            // bean이 A의 인스턴스라면 B로 바꿔치기한다.
            if(bean instanceof A)
                return new B();
            return bean;
        }
    }
}
