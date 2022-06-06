package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;


/**
 * 트랜잭션 - @Transactional AOP
 */
// 트랜잭션 추가, 파라미터 연동, 커넥션 풀을 고려한 종료
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_3 {

    private final MemberRepositoryV3 memberRepositoryV3;

    // 순수한 비즈니스 코드만 남겨둘 수 있다.
    // 트랜잭션 AOP 적용!
    @Transactional // 메서드 시작 시 트랜잭션을 걸게 되는 것!
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        bizLogic(fromId, toId, money);
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
