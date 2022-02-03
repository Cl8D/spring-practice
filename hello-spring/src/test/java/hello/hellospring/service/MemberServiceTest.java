package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberServiceTest {

    MemberService memberService;
    MemoryMemberRepository memberRepository;


    @BeforeEach
    public void beforeEach() {
        // test method가 동작하기 이전에 실행됨
        // test가 실행되기 전에 메모리 멤버 리포지토리가 생성이 되고,
        memberRepository = new MemoryMemberRepository();
        // 이를 멤버서비스의 파라미터로 넘겨주어서
        // 결과적으로 같은 메모리 멤버 리포지토리가 사용된당
        memberService = new MemberService(memberRepository);
    }

    @AfterEach
    public void afterEach() {
        // afterEach annotation을 사용하면 각 테스트 메서드 이후에 실행됨
        memberRepository.clearStore();
    }


    @Test
    void 회원가입() {
        // test 코드 작성 시 참고.
        // 무엇이 주어지고, 이를 실행했을 때, 어떠한 결과가 나오는가.

        // given
        Member member = new Member();
        member.setName("hello");

        // when
        Long saveId = memberService.join(member);

        // then
        Member findMember = memberService.findOne(saveId).get();
        assertThat(member.getName()).isEqualTo(findMember.getName());
    }

    @Test
    public void 중복_회원_예외() {
        //given
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");

        //when
        memberService.join(member1);
        // () -> 뒤에 있는 로직을 실행하게 되면 앞에 있는 예외가 발생하는지 탐지
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");


        // try-catch 방식 활용 (예외가 잘 처리되는지)
        /*
        try {
            memberService.join(member2);
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다!!");
        }
        memberService.join(member2);
        */

        //then
    }

    @Test
    void findMembers() {
    }

    @Test
    void findOne() {
    }
}