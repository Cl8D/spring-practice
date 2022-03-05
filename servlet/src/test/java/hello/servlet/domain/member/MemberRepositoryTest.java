package hello.servlet.domain.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberRepositoryTest {

    // 싱글톤이기 때문에 new로 생성 x
    // 어차피 스프링은 싱글톤을 보장해준다.
    MemberRepository memberRepository = MemberRepository.getInstance();

    // 테스트 이후 저장소 초기화 (테스트가 서로 영향을 주지 않도록 하기 위해)
    @AfterEach
    void afterEach() {
        memberRepository.clearStore();
    }


    // 저장 테스트
    @Test
    public void save() throws Exception {
        // given
        Member member = new Member("hello", 20);

        // when
        Member savedMember = memberRepository.save(member);

        // then
        Member findMember = memberRepository.findById(savedMember.getId());
        assertThat(findMember).isEqualTo(savedMember);
    }

    // 조회 테스트
    @Test
    public void findAll() throws Exception {
        // given
        Member member1 = new Member("member1", 20);
        Member member2 = new Member("member2", 30);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> result = memberRepository.findAll();

        // then
        assertThat(result.size()).isEqualTo(2);
        // result 객체 안에 member1, member2가 있는지
        assertThat(result).contains(member1, member2);

    }



}