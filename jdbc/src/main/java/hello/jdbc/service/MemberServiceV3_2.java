package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;


/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
// 트랜잭션 추가, 파라미터 연동, 커넥션 풀을 고려한 종료
@Slf4j
public class MemberServiceV3_2 {

    // 트랜잭션 템플릿
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepositoryV3;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepositoryV3) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepositoryV3 = memberRepositoryV3;
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // 템플릿 안에서 트랜잭션이 시작 -> 로직 내에서 커밋, 롤백이 진행됨
        // 정확하게는 비즈니스 로직이 정상 수행되면 커밋, 언체크 예외가 발생하면 롤백하고 그외의 경우 커밋한다.
        txTemplate.executeWithoutResult((status) -> {
            try {
                // 비즈니스 로직 수행
                // try-catch는 비즈니스 로직에서 발생하는 SQLException 처리를 위해서.
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
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
