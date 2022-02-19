package hello.core.lifecycle;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class BeanLifeCycleTest {

    @Test
    public void lifeCycleTest() {
        ConfigurableApplicationContext ac = new AnnotationConfigApplicationContext(LifeCycleConfig.class);
        NetworkClient client = ac.getBean(NetworkClient.class);
        // 스프링 컨테이너 종료
        // close() 메서드를 사용하기 위해서는 configurableApplicationContext를 사용해야 한다.
        ac.close();
    }

    @Configuration
    static class LifeCycleConfig {
        // bean 생명주기 콜백 두 번째 방법.
        //@Bean(initMethod = "init", destroyMethod = "close")
        @Bean
        public NetworkClient networkClient() {
            // 여기서 url 정보 없이 호출이 되니까 당연히 결과값으로 null이 들어간다.
            // 객체 생성 후 수정자 주입을 통해서 setUrl()이 호출되어야 url이 세팅됨!
            NetworkClient networkClient = new NetworkClient();
            // 객체 생성 이후에 정보를 넣어주었음.
            networkClient.setUrl("http://hello-spring.dev");
            return networkClient;
        }
    }


}
