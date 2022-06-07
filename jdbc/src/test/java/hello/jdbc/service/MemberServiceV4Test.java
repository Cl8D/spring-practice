package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.*;
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

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * 예외 누수 문제 해결
 * SQLException 제거
 *
 * MemberRepository 의존
 */
@Slf4j
@SpringBootTest
class MemberServiceV4Test {

    /*
        체크 예외 -> 언체크 예외로 바꿔주면서 인터페이스-서비스 계층의 순수성 유지 가능
        나중에 JDBC에서 다른 걸로 변경하더라도 서비스 계층의 코드를 변경할 필요가 없어졌다.

        --> 근데, 지금은 모두 MyDbException으로 넘어오기 때문에 예외를 구분할 수가 없다.
        특정 상황에서 예외를 잡아서 복구하고 싶으면 어떻게 구분하여 처리할 수 있을까?
     */
    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberServiceV4 memberServiceV4;

    @TestConfiguration
    static class TestConfig {
        // 스프링부트가 자동으로 .yml 속성 파일을 보고 dataSource와 트랜잭션 매니저를 자동 생성해준다!
        private final DataSource dataSource;
        public TestConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Bean
        MemberRepository memberRepository() {
            // 빈 등록했을 때는 MemberRepository가 인터페이스니깐 구체 클래스인 MemberRepositoryV4_1로!
//            return new MemberRepositoryV4_1(dataSource);

            // 우리는 인터페이스를 선언했으니까 구체클래스를 여기서 바꿔주면 됨.
            // 스프링이 제공하는 예외로 바꿔주는 V4_2를 적용해두자.
//            return new MemberRepositoryV4_2(dataSource);

            // JdbcTemplate 적용
            return new MemberRepositoryV5(dataSource);
        }

        @Bean
        MemberServiceV4 memberServiceV4() {
            return new MemberServiceV4(memberRepository());
        }

    }

    // AOP 프록시 적용 확인
    @Test
    void AopCheck() {
        log.info("memberService class={}", memberServiceV4.getClass());
        log.info("memberRepository class={}", memberRepository.getClass());
        Assertions.assertThat(AopUtils.isAopProxy(memberServiceV4)).isTrue();
        Assertions.assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();

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
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    public void accountTransfer() throws Exception {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        // A->B에게 2000원 계좌이체
        // 같은 커넥션이 처음에 생성되면(dataSource.getConnection()) 파라미터로 계속 넘겨가며 사용되기 때문에 그대로 유지!!
        memberServiceV4.accountTransfer(memberA.getMemberId(), memberB.getMemberId(),2000);

        // then
        // 그리고 여기서 findByID를 할 때는 다른 커넥션이 사용된다.
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        // 마찬가지로 여기서도 다른 커넥션 사용.
        Member findMemberB = memberRepository.findById(memberB.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체중 예외 발생")
    public void accountTransferEx() throws Exception {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);

        // when
        assertThatThrownBy(() ->
                memberServiceV4.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);

        // then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberEx = memberRepository.findById(memberEx.getMemberId());

        // accountTransfer에서 예외가 발생하면 롤백을 진행해주기 때문에 10000원이 그대로 유지된다.
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        // 또한, 예외가 발생하여 그뒤 로직이 수행되지 않았기 때문에 10000원이 그대로 남아있게 된다.
        assertThat(findMemberEx.getMoney()).isEqualTo(10000);
    }

}