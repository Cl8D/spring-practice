package hello.hellospring.repository;

import hello.hellospring.domain.Member;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class JpaMemberRepository implements MemberRepository{

    private final EntityManager em;

    // jpa는 entitymanager로 동작을 한다.
    public JpaMemberRepository(EntityManager em) {
        this.em = em;
    }


    @Override
    public Member save(Member member) {
        // 이렇게만 해주면 jap가 알아서 insert query 만들고 그럼!
        em.persist(member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> findByName(String name) {
        List<Member> result = em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        // jpql. 테이블이 아닌 객체를 대상으로 쿼리를 날리는 것
        // 객체인 member 자체를 select 하는 걸 볼 수 있다.
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
