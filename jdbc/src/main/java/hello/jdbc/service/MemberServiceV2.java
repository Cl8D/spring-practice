package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


// 트랜잭션 추가, 파라미터 연동, 커넥션 풀을 고려한 종료
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {
    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepositoryV2;

    /*
        트랜잭션은 비즈니스 로직이 있는 서비스 계층에서 시작하는 것이 좋지만,
        트랜잭션을 사용하기 위해서 DataSource / Connection / SQLException 같은 JDBC 기술에 의존을 해야 한다.
        -> 이러면 나중에 JDBC에서 JPA로 변경하면은 서비스 코드까지 변경해야 한다...

     */
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Connection conn = dataSource.getConnection();
        try {
            // 트랜잭션 시작하기 - 수동 커밋
            conn.setAutoCommit(false);

            // 비즈니스 로직 수행
            bizLogic(conn, fromId, toId, money);

            // 로직이 정상적으로 수행되었다면 commit 진행
            conn.commit();

        } catch (Exception e) {
            // 예외 발생 시 롤백
            conn.rollback();
            throw new IllegalStateException(e);
        } finally {
            release(conn);
        }

    }

    private void bizLogic(Connection conn, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepositoryV2.findById(conn, fromId);
        Member toMember = memberRepositoryV2.findById(conn, toId);

        // A->B에게 돈을 계좌이체 하는 예제
         memberRepositoryV2.update(conn, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepositoryV2.update(conn, toId, toMember.getMoney() + money);
    }

    private void release(Connection conn) {
        if(conn != null) {
         try {
             // 커넥션 종료
             // 커밋 모드를 다시 원래대로 바꿔주기 (커넥션 풀 고려)
             conn.setAutoCommit(true);
             // 참고로, 완전 종료가 아니라 커넥션 풀에 반납되는 것!
             conn.close();

         } catch (Exception e) {
             log.info("error", e);
         }

        }
    }

    private void validation(Member toMember) {
        // toMember의 id가 ex라면 예외 발생
        if(toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생!");
        }
    }
}
/*
    - 결과적으로 서비스 계층은 최대한 순수해야 하기 때문에 데이터 접근 계층에 JDBC를 몰아두자.
    또한, 이를 인터페이스로 제공하는 것이 가장 좋다!

    - 트랜잭션용 기능과 트랜잭션을 유지하지 않아도 되는 기능으로 분리해야 한다. (커넥션을 파라미터로 넘기면서 트랜잭션을 유지하는 점을 수정)
    - 트랜잭션 적용 코드 최적화
    - 데이터 접근 계층의 JDBC 예외가 (SQLException)이 서비스 계층까지 전파되고 있다.
    --> 이는 JDBC 관련 기술이라 나중에 JPA 같은 걸로 바꾸면 뜯어 고쳐야 하게 됨.

    ==> 스프링을 통해서 이러한 문제들을 해결해보자!
 */