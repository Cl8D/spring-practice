package jpabook.jpashop3.repository;

import jpabook.jpashop3.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    // 저장
    public void save(Member member) {
        em.persist(member);
    }

    // id로 찾기 (한 명)
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    // 모든 회원 찾기
    public List<Member> findAll() {
        String query = "select m from Member m";
        return em.createQuery(query, Member.class)
                .getResultList();
    }

    // 회원 이름으로 찾기
    public List<Member> findByName(String name) {
        String query = "select m from Member m where m.name = :name";
        return em.createQuery(query, Member.class)
                .setParameter("name", name)
                .getResultList();
    }



}
