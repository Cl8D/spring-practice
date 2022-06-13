package hello.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;

    @Transactional
    public void save(Member member) {
        log.info("member 저장");
        em.persist(member);
    }

    public Optional<Member> find(String username) {
        String query = "select m from Member m where m.username =:username";
        return em.createQuery(query, Member.class)
                .setParameter("username", username)
                .getResultList().stream().findAny();
        // cf) singleResult 사용 시 없으면 예외 발생해서 이런 식으로 처리
        // findAny는 처음으로 조회된 거 반환
    }
}
