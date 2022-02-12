package hello.core.beanfind;

import hello.core.AppConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class ApplicationContextInfoTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    @DisplayName("모든 빈 출력하기")
    // 이 경우 스프링 내부에 있는 모든 빈이 출력된다.
    void findAllBean() {
        // Container에 저장된 bean 꺼내기
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = ac.getBean(beanDefinitionName);
            System.out.println("name = " + beanDefinitionName + " object = " + bean);
        }
    }

    @Test
    @DisplayName("애플리케이션 빈 출력하기")
    // 내가 직접 등록한 스프링 빈만 보고 싶을 때
    void findApplicationBean() {
        // Container에 저장된 bean 꺼내기
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            // 빈 하나하나에 대한 메타데이터 출력
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);

            // 내가 개발을 위해 등록한 빈들만 출력. (스프링 내부에서 등록된 빈 제외시킴)
            // cf. 참고로 AppConfig도 등록되어 있음! (Bean만 있는 게 ㄴㄴ)
            // 반대로, 내부에서 사용하는 빈만 출력하고 싶다면 ROLE_INFRASTRUCTURE 옵션 사용하기
            if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
                Object bean = ac.getBean(beanDefinitionName);
                System.out.println("name = " + beanDefinitionName + " object = " + bean);
            }

        }
    }
}
