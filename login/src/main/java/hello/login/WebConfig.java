package hello.login;

import hello.login.web.argumentresolver.LoginMemberArgumentResolver;
import hello.login.web.filter.LogFilter;
import hello.login.web.filter.LoginCheckFilter;
import hello.login.web.interceptor.LogInterceptor;
import hello.login.web.interceptor.LoginCheckInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.List;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    /*
    @Bean
    public FilterRegistrationBean logFilter() {
        // 필터 등록하기. -> 로그 출력 필터
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        // 등록할 필터 지정
        filterRegistrationBean.setFilter(new LogFilter());
        // 필터는 체인으로 동작하기 때문에 순서가 중요하다. 낮을수록 우선됨.
        filterRegistrationBean.setOrder(1);
        // 필터를 적용할 URL 패턴. 한 번에 여러 패턴 가능.
        // 이렇게 하면 모든 URL에 적용된다.
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    // 로그인 체크 필터
    @Bean
    public FilterRegistrationBean loginCheckFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LoginCheckFilter());
        // 순서) 로그 => 로그인 필터 순서.
        filterRegistrationBean.setOrder(2);
        // 모든 요청에 로그인 필터 적용하기
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
    */

    // WebMvcConfigurer가 제공하는 addInterceptors()를 사용해서 인터셉터 등록하기
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 인터셉터 등록하기
        registry.addInterceptor(new LogInterceptor())
                // 호출순서 지정하기
                .order(1)
                // 인터셉터를 적용할 URL 패턴 지정
                // **는 경로 끝까지 0개 이상의 '경로'가 일치할 때 쓴다.
                // *는 경로 안에서 0개 이상의 '문자'가 일치할 때!
                .addPathPatterns("/**")
                // 인터셉터를 제외할 패턴 지정
                .excludePathPatterns("/css/**", "/*.ico", "/error");

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                // 여기서 제외할 패턴을 선택하기 때문에 loginCheckInterceptor의 코드가 간결해진다.
                .excludePathPatterns("/", "/members/add", "/login", "/logout",
                        "/css/**", "/*.ico", "/error");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 우리가 개발한 argumentResolver 추가
        resolvers.add(new LoginMemberArgumentResolver());
    }
}
