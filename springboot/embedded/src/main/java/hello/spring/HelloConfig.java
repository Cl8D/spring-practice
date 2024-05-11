package hello.spring;

import org.springframework.context.annotation.Bean;

// @ComponentScan에 의해서 HelloController가 자동으로 빈으로 등록된다.
//@Configuration
public class HelloConfig {

    @Bean
    public HelloController helloController() {
        return new HelloController();
    }
}
