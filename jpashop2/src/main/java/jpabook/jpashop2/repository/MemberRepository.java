package jpabook.jpashop2.repository;

import jpabook.jpashop2.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

// Repository -> Entity에 의해 생성된 db에 접근하는 메서드를 사용하기 위한 인터페이스
// CRUD를 어떻게 할 것인지 정의해주는 계층이라고 생각해도 된다.
// @Repository가 붙으면 스프링 빈으로 등록되며(내부에 @Component 덕분에 컴포넌트 스캔 대상 됨), JPA 예외를 스프링 기반 예외로 처리가 가능하다.
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    // springBoot가 이 어노테이션을 보고 entityManager를 만들어서 주입한다. (표준)
    // @PersistenceContext
    // SpringDataJPA를 이용하면 그냥 @autowired로 주입이 가능하기 때문에
    // @requiredArgs와 함께 사용하자.
    private final EntityManager em;

    // 저장
    public void save(Member member){
        em.persist(member);
    }

    // 조회 (한 명)
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    // 전부 조회
    public List<Member> findAll() {
        String query = "select m from Member m";

        // 참고로, sql은 테이블 대상 쿼리지만
        // jpql은 엔티티 대상 쿼리이다.
        return em.createQuery(query, Member.class)
                .getResultList();
    }

    // 이름으로 회원 조회
    public List<Member> findByName(String name) {
        String query = "select m from Member m where m.name =:name";
        return em.createQuery(query, Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}

