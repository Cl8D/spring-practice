package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class TeamJpaRepository {
    @PersistenceContext
    private EntityManager em;

    // 저장
    public Team save(Team team) {
        em.persist(team);
        return team;
    }

    // 변경 -> 변경 감지 이용하기

    // 삭제
    public void delete(Team team) {
        em.remove(team);
    }

    // 전체 조회
    public List<Team> findAll() {
        String query = "select t from Team t";
        return em.createQuery(query, Team.class)
                .getResultList();
    }

    // 단건 조회
    public Optional<Team> findById(Long id){
        Team team = em.find(Team.class, id);
        return Optional.ofNullable(team);
    }

    // 카운트
    public long count() {
        String query = "select count(t) from Team t";
        return em.createQuery(query, Long.class)
                .getSingleResult();
    }

}
