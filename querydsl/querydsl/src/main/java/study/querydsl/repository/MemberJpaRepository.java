package study.querydsl.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.ObjectUtils.isEmpty;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.team;

/*
    혼자 갑자기 헷갈려서 찾아본 거...
    repository -> DB에 접근하는 모든 코드가 모여 있음
    service -> DB에 접근하는 코드는 repository에게 위임하고, 비즈니스 로직과 관련된 모든 코드가 모여있음
 */

// 순수 JPA repository
@Repository
public class MemberJpaRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public MemberJpaRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 멤버 저장
    public void save (Member member) {
        em.persist(member);
    }

    // 단건 조회
    public Optional<Member> findById (Long id) {
        Member findMember = em.find(Member.class, id);
        return Optional.ofNullable(findMember);
    }

    // 전체 조회
    public List<Member> findAll() {
        String query = "select m from Member m";
        return em.createQuery(query, Member.class)
                .getResultList();
    }

    // 전체 조회 - querydsl
    public List<Member> findAll_Querydsl() {
        return queryFactory.selectFrom(member).fetch();
    }

    // 이름으로 조회하기
    public List<Member> findByUsername (String username) {
        String query = "select m from Member m where m.username=:username";
        return em.createQuery(query, Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    // 이름 조회 - querydsl
    public List<Member> findByUsername_Querydsl(String username) {
        return queryFactory.selectFrom(member)
                .where(member.username.eq(username))
                .fetch();
    }

    // 동적 쿼리 - Builder 사용
    public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();

        // null이나 비어있음을 감지
        if(StringUtils.hasText(condition.getUsername())) {
            builder.and(member.username.eq(condition.getUsername()));
        }

        if(StringUtils.hasText(condition.getTeamName())) {
            builder.and(team.name.eq(condition.getTeamName()));
        }

        if(condition.getAgeGoe() != null) {
            builder.and(member.age.goe(condition.getAgeGoe()));
        }

        if(condition.getAgeLoe() != null) {
            builder.and(member.age.loe(condition.getAgeLoe()));
        }

        return queryFactory
                .select(new QMemberTeamDto(
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(builder)
                .fetch();
    }

    // 동적 쿼리 = where절 사용하기
    public List<MemberTeamDto> search(MemberSearchCondition condition) {
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .fetch();
                
    }


    // predicate -> 파라미터로 type인자를 받고, 리턴으로 boolean이 되는 함수형 인터페이스
    // return형이 booleanExpression형이지만, 어차피 얘는 boolean을 상속받고 있어서
    // 리턴 타입으로 해도 되는 듯!
    private Predicate usernameEq(String username) {
        return isEmpty(username) ? null : member.username.eq(username);
    }
    private Predicate teamNameEq(String teamName) {
        return isEmpty(teamName) ? null : team.name.eq(teamName);
    }

    private Predicate ageGoe(Integer ageGoe) {
        return ageGoe == null ? null : member.age.goe(ageGoe);
    }

    private Predicate ageLoe(Integer ageLoe) {
        return ageLoe == null ? null : member.age.loe(ageLoe);
    }


}
