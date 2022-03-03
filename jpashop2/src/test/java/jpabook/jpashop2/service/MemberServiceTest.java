package jpabook.jpashop2.service;

import jpabook.jpashop2.domain.Member;
import jpabook.jpashop2.repository.MemberRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

// 스프링과 테스트를 통합해주기
@RunWith(SpringRunner.class)
// 스프링 부트를 띄우고 테스트하기 (아니면 autowired에서 다 fail)
@SpringBootTest
// 테스트 실행될 때마다 트랜젝션이 시작하고, 테스트가 끝나면 강제로 롤백된다. (테스트 케이스에서 사용된다면)
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    // 테스트 할 목록
    // 1. 회원가입에 성공하는가?
    // 2. 회원가입 시 같은 이름이 있으면 예외 발생

    @Test
    // 롤백을 방지하기 위해 (실제로 db에 flush 된 걸 확인하기 위해) false로 옵션 두기
    // @Rollback(false)
    public void 회원가입() throws Exception {
        // given (주어졌을 때)
        Member member = new Member();
        member.setName("kim");

        // when (이렇게 한다면)
        Long savedId = memberService.join(member);

        // then (이렇게 동작한다)
        // 위에서 저장한 멤버와 db에서 조회한 애랑 동일한지

        // 롤백 옵션이 불편하다면 flush를 통해 영속성 컨텍스트에 있는 값을 강제로 db로 flush 해주기
        // em.flush();

        assertEquals(member, memberRepository.findOne(savedId));

    }

    // try-catch문을 간결화하기 위해 expected 사용
    // 발생한 예외가 IllegalStateException이면 됨.
    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        // when
        memberService.join(member1);
        memberService.join(member2);
        // 같은 이름인 멤버 2명을 넣었기 때문에 예외가 발생해야 한다!
        /*
        try {
            memberService.join(member2);
        } catch (IllegalStateException e) {
            // 정상적으로 예외 발생하면 return으로 인해 테스트 통과
            return;
        }
        */

        // then
        // 이 코드가 돌아가지 않고 예외가 발생해야 함.
        fail("예외가 발생해야 합니다.");
    }

}