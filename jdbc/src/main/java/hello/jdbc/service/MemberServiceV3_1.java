package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * 트랜잭션 - 트랜잭션 매니저
 */
// 트랜잭션 추가, 파라미터 연동, 커넥션 풀을 고려한 종료
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    // 트랜잭션 매니저 주입받기.
    // 우리는 JDBC를 사용하기 때문에 DataSourceTransactionManager를 주입 받을 예정!
    // JPA를 사용한다면 JpaTransactionManager를 주입받는다.
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepositoryV3;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        // 트랜잭션 시작
        // 트랜잭션과 관련된 옵션을 지정할 수 있다.
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            // 비즈니스 로직 수행
            bizLogic(fromId, toId, money);

            // 로직이 정상적으로 수행되었다면 commit 진행
            transactionManager.commit(status);

        } catch (Exception e) {
            // 예외 발생 시 롤백
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }
        // release는 알아서 트랜잭션 매니저가 해주기 때문에 따로 지정해줄 필요 x!

    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepositoryV3.findById(fromId);
        Member toMember = memberRepositoryV3.findById(toId);

        // A->B에게 돈을 계좌이체 하는 예제
         memberRepositoryV3.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepositoryV3.update(toId, toMember.getMoney() + money);
    }


    private void validation(Member toMember) {
        // toMember의 id가 ex라면 예외 발생
        if(toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생!");
        }
    }
}
