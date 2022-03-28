package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import java.util.Arrays;
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

    @Autowired
    EntityManager em;

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

    /*******************************/

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

    /*******************************/

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

    /*******************************/

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

    /*******************************/

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

    /*******************************/

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

    /*******************************/

    @Test
    public void findByNames() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        // then
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    /*******************************/

    @Test
    public void paging() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        // when
        /* pageRequest -> 현재 페이지 / 조회할 데이터 수 / 정렬 정보 이런 식으로 들어간다.
            페이지는 0부터 시작!
            Page형을 반환하게 되면 count 쿼리가 날라간다. -> 이후에 현재 페이지 번호나 전체 페이지 번호 계산 가능하도록!
        */
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = memberRepository.findByAge(10, pageRequest);
        /* 참고로, Slice<Member> page 이런 식으로 슬라이스 형으로도 받을 수 있는데,
         이때는 limit+1 = 4개를 요청하게 된다.
         totalCount를 애초에 계산하지 않음! (count 쿼리가 안 나감)
         모바일 앱에서 막... 페이지를 보여주는 게 아니라 더보기로 계속해서 내려가는 그런 기능 때 사용함!!
        */

        /*
           혹은 List<Member>형으로도 받을 수 있는데, 이러면 그냥 딱 limit=3만 받아온다.
         */

        // cf) page->Dto로 변환시키기
        // 이런 식으로 dto로 변환하여 api에 반환하는 식으로 해야 한다.
        page.map(m -> new MemberDto(m.getId(), m.getUsername(),null));


        // then
        // 조회된 데이터 리스트
        List<Member> content = page.getContent();

        // 조회된 데이터 수
        assertThat(content.size()).isEqualTo(3);
        // 전체 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(5);
        // 페이지 번호
        assertThat(page.getNumber()).isEqualTo(0);
        // 전체 페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2);
        // 첫 번째 항목인지
        assertThat(page.isFirst()).isTrue();
        // 다음 페이지가 있는지
        assertThat(page.hasNext()).isTrue();

    }

    /*******************************/

    @Test
    public void bulkUpdate() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        // 20살 이상이면 1살씩 증가시켜주기
        int resultCount = memberRepository.bulkAgePlus(20);

        // 벌크 연산은 영속성 컨텍스트를 신경쓰지 않는다.
        // 이 상태에서 member를 조회하면, 벌크 연산 이전값이 1차 캐시에 남아있어서
        // 변경되기 전의 값을 가져오게 된다.
        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        // member5 = Member(id=5, username=member5, age=40)
        System.out.println("member5 = " + member5);

        // 이후 아이템을 조회한다면 영속성 컨텍스트 초기화해주기! -> 그래야 db에서 가져옴!
        em.flush();
        em.clear();
        List<Member> result2 = memberRepository.findByUsername("member5");
        Member dbMember5 = result2.get(0);
        // dbMember5 = Member(id=5, username=member5, age=41)
        System.out.println("dbMember5 = " + dbMember5);

        // 꼭 이런 식으로 수동으로 안 해도 되고,
        // @Query의 옵션으로 줘도 됨!!

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    /*******************************/

    // 지연 로딩 확인 테스트
    @Test
    public void findMemberLazy() throws Exception {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));

        em.flush();
        em.clear();

        // when
        // select Member 쿼리 1번.
        List<Member> members = memberRepository.findAll();

        // then
        for (Member member : members) {
            // 여기서는 이전에 찾았던 member 쿼리를 바탕으로 username을 뽑아줌.
            // member.getTeam.getClass()를 했을 때 프록시 객체가 찍힌다.
            System.out.println("member.getUsername() = " + member.getUsername());
            // 이 시점에서 team에 대한 select 쿼리가 나간다. (getName을 통해 실제로 데이터를 뽑으려고 할 때)
            // 여기서 지연로딩이 발생하는 것. -> 이때 db에서 받아오는 것
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());

            // 첫 번째 루프 때 member 쿼리 -> getTeam.getName() -> teamA에 대한 쿼리
            // 두 번째 루프 때는 member 쿼리 x -> getTeam.getName() -> teamB에 대한 쿼리
        }
        // 지연 로딩 단점 -> N+1 문제 발생
    }

    /*******************************/
    // 쿼리 힌트 사용 확인
    @Test
    public void queryHint() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        // db에 쿼리 나감 + 영속성 컨텍스트 클리어
        em.flush();
        em.clear();

        // when
        Member member = memberRepository.findReadOnlyByUsername("member1");
        member.setUsername("member2");

        // then
        // 원래 기존의 findById()를 사용한다면 여기서 영속성 컨텍스트 -> db로 날려주니까
        // 변경 감지로 인해서 update 쿼리가 나가게 되는데,
        // 우리는 readOnly=true로 해버렸으니까 update query가 나가지 않는다.
       em.flush();

    }

}