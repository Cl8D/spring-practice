package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * 트랜잭션 - DataSource, transactionManager 자동 등록
 */
@Slf4j
@SpringBootTest
class MemberServiceV3_4Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired
    private MemberRepositoryV3 memberRepositoryV3;
    @Autowired
    private MemberServiceV3_3 memberServiceV3_3;

    @TestConfiguration
    static class TestConfig {
        // 스프링부트가 자동으로 .yml 속성 파일을 보고 dataSource와 트랜잭션 매니저를 자동 생성해준다!
        private final DataSource dataSource;
        public TestConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Bean
        MemberRepositoryV3 memberRepositoryV3() {
            return new MemberRepositoryV3(dataSource);
        }

        @Bean
        MemberServiceV3_3 memberServiceV3_3() {
            return new MemberServiceV3_3(memberRepositoryV3());
        }

    }

    // AOP 프록시 적용 확인
    @Test
    void AopCheck() {
        log.info("memberService class={}", memberServiceV3_3.getClass());
        log.info("memberRepository class={}", memberRepositoryV3.getClass());
        Assertions.assertThat(AopUtils.isAopProxy(memberServiceV3_3)).isTrue();
        Assertions.assertThat(AopUtils.isAopProxy(memberRepositoryV3)).isFalse();

        /*
            실행 결과)
            memberService class=class hello.jdbc.service.MemberServiceV3_3$$EnhancerBySpringCGLIB$$462006d3
            memberRepository class=class hello.jdbc.repository.MemberRepositoryV3
            => 여기서 CGLIB라는 것을 통해 프록시가 적용된 것을 확인할 수 있다. (물론 assert에서도 통과하지만!)
            memberRepository는 AOP를 적용하지 않았으니까 프록시 적용 X
         */
    }

    // 각 테스트가 끝날 때 호출
    @AfterEach
    void after() throws SQLException {
        memberRepositoryV3.delete(MEMBER_A);
        memberRepositoryV3.delete(MEMBER_B);
        memberRepositoryV3.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    public void accountTransfer() throws Exception {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepositoryV3.save(memberA);
        memberRepositoryV3.save(memberB);

        // when
        // A->B에게 2000원 계좌이체
        // 같은 커넥션이 처음에 생성되면(dataSource.getConnection()) 파라미터로 계속 넘겨가며 사용되기 때문에 그대로 유지!!
        memberServiceV3_3.accountTransfer(memberA.getMemberId(), memberB.getMemberId(),2000);

        // then
        // 그리고 여기서 findByID를 할 때는 다른 커넥션이 사용된다.
        Member findMemberA = memberRepositoryV3.findById(memberA.getMemberId());
        // 마찬가지로 여기서도 다른 커넥션 사용.
        Member findMemberB = memberRepositoryV3.findById(memberB.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체중 예외 발생")
    public void accountTransferEx() throws Exception {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        memberRepositoryV3.save(memberA);
        memberRepositoryV3.save(memberEx);

        // when
        assertThatThrownBy(() ->
                memberServiceV3_3.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);

        // then
        Member findMemberA = memberRepositoryV3.findById(memberA.getMemberId());
        Member findMemberEx = memberRepositoryV3.findById(memberEx.getMemberId());

        // accountTransfer에서 예외가 발생하면 롤백을 진행해주기 때문에 10000원이 그대로 유지된다.
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        // 또한, 예외가 발생하여 그뒤 로직이 수행되지 않았기 때문에 10000원이 그대로 남아있게 된다.
        assertThat(findMemberEx.getMoney()).isEqualTo(10000);
    }

}