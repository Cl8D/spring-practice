package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


// 트랜잭션 - 커넥션 파라미터 전달 방식 동기화
class MemberServiceV2Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV2 memberRepositoryV2;
    private MemberServiceV2 memberServiceV2;

    // 각 테스트 시작 전에 호출
    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepositoryV2 = new MemberRepositoryV2(dataSource);
        memberServiceV2 = new MemberServiceV2(dataSource, memberRepositoryV2);
    }

    // 각 테스트가 끝날 때 호출
    @AfterEach
    void after() throws SQLException {
        memberRepositoryV2.delete(MEMBER_A);
        memberRepositoryV2.delete(MEMBER_B);
        memberRepositoryV2.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    public void accountTransfer() throws Exception {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepositoryV2.save(memberA);
        memberRepositoryV2.save(memberB);

        // when
        // A->B에게 2000원 계좌이체
        // 같은 커넥션이 처음에 생성되면(dataSource.getConnection()) 파라미터로 계속 넘겨가며 사용되기 때문에 그대로 유지!!
        memberServiceV2.accountTransfer(memberA.getMemberId(), memberB.getMemberId(),2000);

        // then
        // 그리고 여기서 findByID를 할 때는 다른 커넥션이 사용된다.
        Member findMemberA = memberRepositoryV2.findById(memberA.getMemberId());
        // 마찬가지로 여기서도 다른 커넥션 사용.
        Member findMemberB = memberRepositoryV2.findById(memberB.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체중 예외 발생")
    public void accountTransferEx() throws Exception {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        memberRepositoryV2.save(memberA);
        memberRepositoryV2.save(memberEx);

        // when
        assertThatThrownBy(() ->
                memberServiceV2.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);

        // then
        Member findMemberA = memberRepositoryV2.findById(memberA.getMemberId());
        Member findMemberEx = memberRepositoryV2.findById(memberEx.getMemberId());

        // accountTransfer에서 예외가 발생하면 롤백을 진행해주기 때문에 10000원이 그대로 유지된다.
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        // 또한, 예외가 발생하여 그뒤 로직이 수행되지 않았기 때문에 10000원이 그대로 남아있게 된다.
        assertThat(findMemberEx.getMoney()).isEqualTo(10000);
    }

}