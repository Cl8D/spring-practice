package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;


/**
 * 예외 누수 문제 해결
 * SQLException 제거
 *
 * MemberRepository 인터페이스 의존하도록
 */
// 트랜잭션 추가, 파라미터 연동, 커넥션 풀을 고려한 종료
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV4 {

    private final MemberRepository memberRepository;

    // throws SQLException이 제거된 것을 확인할 수 있다!
    @Transactional // 메서드 시작 시 트랜잭션을 걸게 되는 것!
    public void accountTransfer(String fromId, String toId, int money)  {
        bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money)  {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        // A->B에게 돈을 계좌이체 하는 예제
         memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }


    private void validation(Member toMember) {
        // toMember의 id가 ex라면 예외 발생
        if(toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생!");
        }
    }
}
