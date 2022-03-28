package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(false)
class MemberRepositoryTest {
    // JPA가 만들어낸 클래스.
    // sout로 출력해보면 memberRepository의 클래스 타입은
    // com.sun.proxy.$ProxyXXX 라고 나온다.
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Test
    public void testMember() throws Exception {
        // given
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }


    @Test
    public void basicCRUD() throws Exception {
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        // 단건 조회
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        // 전체 조회
        List<Member> all = memberRepository.findAll();

        // 카운트
        long count = memberRepository.count();

        // 삭제
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        // then
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        assertThat(all.size()).isEqualTo(2);

        assertThat(count).isEqualTo(2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        // then
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void testQuery() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<Member> result = memberRepository.findUser("AAA", 10);

        // then
        assertThat(result.get(0)).isEqualTo(m1);

    }

    @Test
    public void findUsernameList() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<String> usernameList = memberRepository.findUsernameList();

        // then
        for (String s : usernameList) {
            System.out.println("s = " + s);
            /*
                s = AAA
                s = BBB
            */
        }

    }

    @Test
    public void findMemberDto() throws Exception {
        // given
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        // when
        List<MemberDto> memberDto = memberRepository.findMemberDto();

        // then
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
            // dto = MemberDto(id=2, username=AAA, teamName=teamA)
            // 이런 식으로 dto도 잘 출력되는 걸 확인할 수 있다!
        }

    }
}