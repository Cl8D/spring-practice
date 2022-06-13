package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceTest {
    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LogRepository logRepository;

    /**
     * 서비스 계층에 트랜잭션 x
     * MemberService @Transactional:OFF
     * MemberRepository @Transactional:ON
     * LogRepository @Transactional:ON
     */
    // 참고로, 트랜잭션 전파의 기본값은 REQUIRED이다.
    // 기존 트랜잭션이 없으면 새로 만들고, 있으면 참여하는 방식.
    @Test
    public void outerTxOff_success() {
        // given
        String username = "outerTxOff_success";

        // when
        memberService.joinV1(username);
        /*
            로직)
            service -> memberRepository 호출 (@Transactional 존재)
            -> 트랜잭션 AOP 작동 -> 트랜잭션 매니저를 통해 트랜잭션 시작 (B)
            : 여기서 트랜잭션은 커넥션 conn1을 사용한다고 가정.
            -> 회원 저장 시 JPA는 conn1을 사용하여 저장
            -> 정상 응답을 반환했기 때문에 트랜잭션 AOP는 트랜잭션 매니저에 커밋 요청
            -> conn1을 통해 물리 트랜잭션 커밋.
            => 트랜잭션 B 종료.

            이후, LogRepository를 통해 같은 과정으로 트랜잭션C 시작, 정상 커밋, 종료.
            이는 member, log 모두 각각 @Transactional을 가지고 있기 때문에
            둘 다 다른 논리 트랜잭션을 가지고 있게 되는 것.
         */

        // then
        // optional이니까 존재하는지 확인 (isPresent)
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * 서비스 계층에 트랜잭션 x, 로그에서 예외 발생
     * MemberService @Transactional:OFF
     * MemberRepository @Transactional:ON
     * LogRepository @Transactional:ON Exception
     */
    @Test
    void outerTxOff_fail() {
        //given - 로그 예외라는 이름이 contain 되어 있는 경우
        String username = "로그 예외_outerTxOff_fail";

        //when - 예외 발생
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);
        /*
            로직)
            logrepository가 사용하는 트랜잭션 C의 경우 런타임 예외가 발생하였다.
            이때, 예외를 밖으로 던지기 때문에 (throw new) 트랜잭션 AOP가 예외를 받게 되는데,
            런타임 예외가 발생했기 때문에 트랜잭션 AOP는 트랜잭션 매니저에 롤백을 호출한다.
            -> 트랜잭션C는 신규 트랜잭션이기 때문에 물리 롤백을 호출한다.

            == 결과적으로, 트랜잭션 AOP도 내부에서는 트랜잭션 매니저를 사용하게 된다.
         */

        //then: 완전히 롤백되지 않고, member 데이터가 남아서 저장된다. = 데이터 정합성 문제 발생
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }


    // 회원-로그 트랜잭션을 하나로 묶기 = 서비스에만 적용하자!
    /**
     * MemberService @Transactional:ON
     * MemberRepository @Transactional:OFF
     * LogRepository @Transactional:OFF
     */
    @Test
    void singleTx() {
        //given
        String username = "singleTx";

        //when
        memberService.joinV1(username);
        /*
            @Transactional이 MemberService에만 붙어있기 때문에, 여기에만 트랜잭션 AOP 적용됨.
            -> memberService와 관련된 로직은 이 트랜잭션이 만든 커넥션을 사용한다.
         */

        //then: 모든 데이터가 정상 저장된다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }


    // 전파 커밋
    // 모든 논리 트랜잭션이 정상 커밋되는 경우
    /**
     * MemberService @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository @Transactional:ON
     */
    @Test
    void outerTxOn_success() {
        //given
        String username = "outerTxOn_success";

        //when
        memberService.joinV1(username);
        /*
            memberService가 호출되면서 트랜잭션 AOP가 호출 -> 신규 트랜잭션 생성(A), 물리 트랜잭션 시작
            - memberRepository 호출 -> 트랜잭션 AOP 호출 -> 이미 A가 있으니까 기존 트랜잭션에 참여
            --> 로직 종료 후 트랜잭션 매니저에 커밋 요청 -> 신규가 아니니까 실제 커밋 x
            - logRepository 역시 똑같이 동작
            - memberService 로직 종료, 신규니까 물리 커밋 호출
         */

        //then: 모든 데이터가 정상 저장된다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }


    // 전파 롤백
    // 로그에서 예외가 발생해서 전체가 롤백 = 정합성 문제 발생 X!
    /**
     * MemberService @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository @Transactional:ON Exception
     */
    @Test
    void outerTxOn_fail() {
        //given
        String username = "로그 예외_outerTxOn_fail";

        //when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        /*
            앞선 과정은 위랑 동일, 단 로그에서 예외 발생.
            예외를 던졌기 때문에 트랜잭션 AOP가 해당 예외를 받는다.
            -> 런타임 예외 발생 -> 트랜잭션 매니저에 롤백 요청
            -> 그러나, 로그는 신규 트랜잭션이 아니기 때문에 rollbackOnly=true로 설정
            -> 트랜잭션 AOP도 예외를 던지고, memberService가 받는다. 여기서도 밖으로 던짐.
            -> 런타임 예외 발생 -> 트랜잭션 매니저에 롤백 요청
            -> 신규 트랜잭션이니까 물리 롤백 수행 (rollbackOnly 설정은 영향을 주지 않는다)
            --> 최종적으로 outerTxOn_fail()은 런타임 예외를 받는다.
         */

        //then: 모든 데이터가 롤백된다.
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }


    // 만약 로그에서 예외가 발생하더라도 회원가입은 유지해야 한다면?
    // 즉, 로그에서 예외가 발생하였다면 예외를 잡아서 정상 흐름으로 바꿔야 하는 경우
    // memberService의 트랜잭션 AOP에서 예외를 잡은 다음, 커밋을 해보자.

    /**
     * MemberService @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository @Transactional:ON Exception
     */
    @Test
    void recoverException_fail() {
        //given
        String username = "로그 예외_recoverException_fail";

        //when - joinV2() 함수를 통해 예외를 잡아서 처리하였음!
        // 그러나, rollbackOnly를 설정하기 때문에 결과적으로 물리 트랜잭션을 롤백된다. (+unexpectedRollbackException 발생)
        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        /*
            정확하게는, logRepository에서 예외가 발생하면 트랜잭션 AOP가 해당 예외를 받는다.
            신규가 아니기 때문에 롤백하지 않고, rollbackonly=true로 설정한다.
            이후, AOP는 전달받은 예외를 밖으로 던지고, memberService에 던져진다.
            memberService는 해당 예외를 복구한 다음 정상적으로 리턴한다.

            그럼 memberService의 트랜잭션 AOP는 커밋을 호출하지만,
            신규 트랜잭션이라 물리 트랜잭션을 커밋하려고 할 때 rollbackOnly 옵션을 체크한다.
            이때 true이기 때문에 커밋 대신에 롤백을 하고,
            트랜잭션 매니저는 UnexpectedRollbackException 예외를 던지게 된다.
            그리고 aop는 이를 받아서 클라이언트에게 던진다.
         */

        //then: 모든 데이터가 롤백된다.
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }


    // 회원가입이 유지되도록, 물리 트랜잭션을 분리해야 한다. : required_new 사용하기
    /**
     * MemberService @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository @Transactional(REQUIRES_NEW) Exception
     */
    @Test
    void recoverException_success() {
        //given
        String username = "로그 예외_recoverException_success";

        //when
        memberService.joinV2(username);
        /*
            logRepository의 트랜잭션은 requires_new 옵션으로 인해서 물리 트랜잭션이 분리된다.
            - 그럼, 로그에서 예외가 발생했을 때 AOP가 예외를 받는데, 이때 신규 트랜잭션이니까 물리 트랜잭션을 롤백해준다. 그리고 종료!
            이후 AOP는 전달받은 예외를 밖으로 던지는데, memberService는 이를 받아서 정상 흐름으로 만들어준다.
            그럼 memberService의 AOP는 정상 흐름이니까 커밋을 호출하고, 신규이기 때문에 그냥 커밋한다. (rollbackOnly 체크했을 때 true가 아닐 테니까)
            -> 그래서 멤버는 저장, log는 롤백이 정상적으로 진행된다!
         */

        //then: member 저장, log 롤백
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }

}