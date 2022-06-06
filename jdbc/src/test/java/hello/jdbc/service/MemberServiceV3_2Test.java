package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
class MemberServiceV3_2Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV3 memberRepositoryV3;
    private MemberServiceV3_2 memberServiceV3_2;

    // 각 테스트 시작 전에 호출
    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepositoryV3 = new MemberRepositoryV3(dataSource);

        // JDBC 사용 -> DataSourceTransactionManager 구현체 주입
        // 이때 파라미터로 dataSource를 넘겨줘야 한다...
        // dataSource를 넘겨줘야 트랜잭션 매니저가 얘를 가지고 커넥션을 만드니까!
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        memberServiceV3_2 = new MemberServiceV3_2(transactionManager, memberRepositoryV3);
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
        memberServiceV3_2.accountTransfer(memberA.getMemberId(), memberB.getMemberId(),2000);

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
                memberServiceV3_2.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
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