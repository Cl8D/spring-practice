package hello.core;

import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackages = "hello.core",
        excludeFilters = @ComponentScan.Filter(
                type= FilterType.ANNOTATION,
                classes = Configuration.class)
)
// ComponentScan은 @Component가 붙은 클래스를 자동으로 spring bean으로 등록한다.
// 그중에서 filter 기능을 통해서 등록하지 않을 애들을 걸어준다.
// 여기서 @Configuration이 붙은 애를 제외한 건 기존의 AppConfig 클래스 (우리가 만든 클래스)는
// 직접 등록해주는 코드니까 충돌나게 하지 않기 위해서!
public class AutoAppConfig {

    // 기존의 memoryMemberRepository와 동일한 이름의 빈 등록하기
    /*
    @Bean(name = "memoryMemberRepository")
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }
    */
}
