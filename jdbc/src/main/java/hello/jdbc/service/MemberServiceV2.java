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
