package hello.login;

import hello.login.web.filter.LogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;


@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean logFilter() {
        // 필터 등록하기.
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        // 등록할 필터 지정
        filterRegistrationBean.setFilter(new LogFilter());
        // 필터는 체인으로 동작하기 때문에 순서가 중요하다. 낮을수록 우선됨.
        filterRegistrationBean.setOrder(1);
        // 필터를 적용할 URL 패턴. 한 번에 여러 패턴 가능.
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
