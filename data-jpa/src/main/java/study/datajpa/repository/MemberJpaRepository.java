package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

// 순수한 JPA 기반 리포지토리.

/**
 * CRUD (저장 / 변경 / 삭제 / 전체 조회 / 단건 조회 / 카운트)
 * JPA에서 수정은 변경 감지 기능을 사용하기.
 * -> 트랜잭션 안에서 엔티티 조회 후 데이터를 변경하면
 * 트랜잭션 종료 시점에 변경 감지 기능이 동작하여 변경된 엔티티를 감지하고
 * update 쿼리를 날려준다!
 */
@Repository
public class MemberJpaRepository {

    // @PersistenceContext -> 영속성 컨텍스트 등록
    @PersistenceContext
    private EntityManager em;

    // 저장
    public Member save(Member member){
        em.persist(member);
        return member;
    }

    // 수정은 코드 x -> 그냥 변경 감지 기능을 통해서 해주면 된다.
    // 나중에 조회 후 데이터 변경하는 방식으로.

    // 삭제
    public void delete(Member member){
        em.remove(member);
    }

    // 전체 조회
    public List<Member> findAll() {
        String query = "select m from Member m";
        // 두 번째 인자는 반환 타입!
        return em.createQuery(query, Member.class)
                .getResultList();
    }

    // 단건 조회 - null 감지
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        // null일 수도 있고 아닐 수도 있다는 것.
        return Optional.ofNullable(member);
    }

    // 카운트
    public long count() {
        String query = "select count(m) from Member m";
        return em.createQuery(query, Long.class)
                .getSingleResult();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    // 이름과 나이 기준으로 회원 조회하기
    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
        String query = "select m from Member m where m.username =:username and m.age > :age";
        return em.createQuery(query)
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

}
