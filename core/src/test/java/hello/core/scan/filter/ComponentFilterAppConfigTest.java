package hello.core.scan.filter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.context.annotation.ComponentScan.*;

public class ComponentFilterAppConfigTest {

    @Test
    void filterScan() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(ComponentFilterAppConfig.class);
        BeanA beanA = ac.getBean("beanA", BeanA.class);

        // 우리는 beanA를 include, beanB를 exclude한 상태
        // 당연히 오류 안 남
        assertThat(beanA).isNotNull();

        // beanB는 exclude 했기 때문에 에러남
        // NoSuchBeanDefinitionException: No bean named 'beanB' available
        //ac.getBean("beanB", BeanB.class);
        org.junit.jupiter.api.Assertions.assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> ac.getBean("beanB", BeanB.class));
    }

    @Configuration
    @ComponentScan(
            includeFilters = @Filter(type = FilterType.ANNOTATION,
                    classes = MyIncludeComponent.class),
            excludeFilters = @Filter(type = FilterType.ANNOTATION,
                    classes = MyExcludeComponent.class)
    )
    // Annotation과 관련된 필터 만들기, 나만의 컴포넌트 스캔 기능을 만든 것이라고 볼 수 있음!
    static class ComponentFilterAppConfig {

    }
}
