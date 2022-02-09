package hello.hellospring;

import hello.hellospring.aop.TimeTraceAop;
import hello.hellospring.domain.Member;
import hello.hellospring.repository.*;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.Time;

@Configuration
public class SpringConfig {
    /*
    // spring boot가 자체적으로 datasource를 생성함
    // 데이터랑 연결할 수 있는 bean을 spring boot가 자체적으로 만들어준다고 함
    private DataSource dataSource;

    @Autowired
    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    */

    /*
    private final DataSource dataSource;
    private final EntityManager em;

    public SpringConfig(DataSource dataSource, EntityManager em) {
        this.dataSource = dataSource;
        this.em = em;
    }
    */

    // 이렇게 만들어 두면 spring data jpa가 만들어둔 게 등록이 된다
    private final MemberRepository memberRepository;


    // spring container에서 memberRepository를 찾게 되는데,
    // 이때 우리가 만들어둔 springdataJPA레포에서 만든 인터페이스를 보고 알아서 구현체를 만들어서
    // spring bean에 등록을 해준다
    @Autowired
    public SpringConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    // 자바 코드로 직접 스프링 빈 등록하기
    // 이렇게 하면 memberService와 memberRepository를 모두 스프링 빈에 등록을 하고,
    // 그리고, 스프링 빈에 등록되어 있는 memberRepository를 멤버 서비스 안에 넣어준다.
    @Bean
    public MemberService memberService() {
        //return new MemberService(memberRepository());
        return new MemberService(memberRepository);
    }

    /*
    @Bean
    public MemberRepository memberRepository() {
        //return new MemoryMemberRepository();
        // return new JdbcMemberRepository(dataSource);
        //return new JdbcTemplateMemberRepository(dataSource);
        return new JpaMemberRepository(em);
    }
    */

    /*
    // spring bin에 등록. 이거 대신에 @Component 사용해도 된다
    @Bean
    public TimeTraceAop timeTraceAop() {
        return new TimeTraceAop();
    }
    */

}
