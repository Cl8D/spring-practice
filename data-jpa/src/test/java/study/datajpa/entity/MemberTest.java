package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testEntity() throws Exception {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("memberA", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        // 강제로 db에 insert 쿼리 날리기
        em.flush();
        // 영속성 컨텍스트 캐시 초기화
        em.clear();
        
        // when
        String query = "select m from Member m";
        List<Member> members = em.createQuery(query, Member.class)
                .getResultList();

        // then
        for (Member member : members) {
            System.out.println("member = " + member);
            // 지연로딩으로 인해서 .getTeam() 했을 때 쿼리가 나감
            System.out.println("member.getTeam() = " + member.getTeam());
        }
        
    }

    /*******************************/

    // auditing test
    @Test
    public void JpaEventBaseEntity() throws Exception {
        // given
        Member member = new Member("member1");
        // 여기서 persist 호출 전에 @PrePersist 호출
        memberRepository.save(member);

        Thread.sleep(100);
        // 데이터 변경
        member.setUsername("member2");

        // 여기서 @PreUpdate
        em.flush();
        // 영속성 컨텍스트 초기화
        em.clear();

        // when
        Member findMember = memberRepository.findById(member.getId()).get();

        // then
        // findMember.getCreatedDate() = 2022-03-28T16:31:25.455221
        // findMember.getUpdatedDate() = 2022-03-28T16:31:25.647272
        System.out.println("findMember.getCreatedDate() = " + findMember.getCreatedDate());
        //System.out.println("findMember.getUpdatedDate() = " + findMember.getUpdatedDate());
        // findMember.getLastModifiedDate() = 2022-03-28T16:46:38.819058
        System.out.println("findMember.getLastModifiedDate() = " + findMember.getLastModifiedDate()); // spring data jpa 활용
        // findMember.getCreatedBy() = 71f32d0f-5400-4380-86c0-4d5544275169
        // findMember.getLastModifiedBy() = fc75e8f8-2af3-4bc8-9746-0cb902150344
        System.out.println("findMember.getCreatedBy() = " + findMember.getCreatedBy());
        System.out.println("findMember.getLastModifiedBy() = " + findMember.getLastModifiedBy());

    }
}