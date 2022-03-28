package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberTest {
    @PersistenceContext
    EntityManager em;

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
}