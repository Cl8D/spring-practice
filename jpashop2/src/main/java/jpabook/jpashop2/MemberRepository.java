package jpabook.jpashop2;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

// Repository -> Entity에 의해 생성된 db에 접근하는 메서드를 사용하기 위한 인터페이스
// CRUD를 어떻게 할 것인지 정의해주는 계층이라고 생각해도 된다.
@Repository
public class MemberRepository {

    // springBoot가 이 어노테이션을 보고 entityManager를 주입해준다.
    @PersistenceContext
    private EntityManager em;

    public Long save(Member member){
        // 저장
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

}
