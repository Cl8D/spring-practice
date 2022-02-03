package hello.hellospring;

import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;
import hello.hellospring.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    // 자바 코드로 직접 스프링 빈 등록하기
    // 이렇게 하면 memberService와 memberRepository를 모두 스프링 빈에 등록을 하고,
    // 그리고, 스프링 빈에 등록되어 있는 memberRepository를 멤버 서비스 안에 넣어준다.
    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }
}
