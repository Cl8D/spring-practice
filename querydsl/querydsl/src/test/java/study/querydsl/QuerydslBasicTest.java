package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.AbstractJPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @PersistenceContext
    EntityManager em;

    // 필드 선언으로 JPaQueryFactory 빼주기
    /*
     이때, 동시성 문제는 entitymanager에 달려있기 때문에,
     여러 스레드에서 같은 entityManager에 접근해도 트랜잭션에 따라 영속성 컨텍스트를 제공하기 때문에
     동시성 문제 자체는 걱정할 필요가 없다.
    */
    JPAQueryFactory queryFactory;


    // 테스트 이전에 데이터 미리 세팅해두기
    // teamA - member1(10), member2(20)
    // teamB - member3(30), member4(40)
    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    /************************/
    // Member1 찾는 예제
    // JPQL 형식
    @Test
    public void startJPQL() throws Exception {
        // given
        String qlString = "select m from Member m"
                + " where m.username=:username";
        // when
        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        // then
        assertThat(findMember.getUsername()).isEqualTo("member1");

    }

    @Test
    public void startQuerydsl() throws Exception {
        // given
        //JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        // Gradle -> task -> other -> compileQuerydsl 필수
        QMember m = new QMember("m");

        // when
        Member findMember = queryFactory
                .select(m)
                .from(m)
                // 파라미터 바인딩 자동 처리
                .where(m.username.eq("member1"))
                .fetchOne();

        // then
        assertThat(findMember.getUsername()).isEqualTo("member1");

    }


    @Test
    public void startQuerydsl2() throws Exception {
        // given
        // static import를 통해서 member 사용하기 -> 깔끔!

        // 여기서 뭐 별칭을 준다면 이런 식으로 해주면 된다.
        // 이러면 쿼리 자체가 m1으로 바뀌어서 나감!!
        // QMember m1 = new QMember("m1");

        // when
        Member findMember = queryFactory
                .select(member)
                .from(member)
                // 파라미터 바인딩 자동 처리
                .where(member.username.eq("member1"))
                .fetchOne();

        // then
        assertThat(findMember.getUsername()).isEqualTo("member1");

    }

    /*********************/

    // 검색 조건 쿼리
    @Test
    public void search() throws Exception {
        // given

        // when
        Member findMember = queryFactory
                // select + from = selectFrom
                .selectFrom(member)
                .where(member.username.eq("member1")
                        // 이런식으로 and, or 조건을 줄 수 있다.
                        .and(member.age.eq(10)))
                .fetchOne();

        // then
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    // and를 파라미터로 처리하기
    @Test
    public void searchAndParam() throws Exception {
        // given

        // when
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"),
                        // 이런 식으로 파라미터로 넘기면 자동으로 and라고 인식
                        // 이때 중간에 파라미터로 null을 넣는다면 무시된다. (나중에 매우 유용)
                        member.age.eq(10))
                .fetch();

        // then
        assertThat(result.size()).isEqualTo(1);

    }

    // 그외 여러 검색 조건 관련
    @Test
    public void searchQuery() throws Exception {
        /*
        <검색 조건>
        member.username.eq("member1") // username = 'member1'
        member.username.ne("member1") //username != 'member1'
        member.username.eq("member1").not() // username != 'member1'

        member.username.isNotNull() //이름이 is not null

        member.age.in(10, 20) // age in (10,20)
        member.age.notIn(10, 20) // age not in (10, 20)
        member.age.between(10,30) //between 10, 30

        member.age.goe(30) // age >= 30
        member.age.gt(30) // age > 30
        member.age.loe(30) // age <= 30
        member.age.lt(30) // age < 30

        member.username.like("member%") //like 검색
        member.username.contains("member") // like ‘%member%’ 검색
        member.username.startsWith("member") //like ‘member%’ 검색
        */
    }


    /*********************/
    // 결과 조회
    @Test
    public void resultSearch() throws Exception {
        // 리스트 조회, 데이터 없으면 빈 리스트 반환
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        // 단건 조회. 결과 없으면 nll, 둘 이상이면 NonUniqueResultException
        Member findMember1 = queryFactory
                .selectFrom(QMember.member)
                .fetchOne();

        // 처음 한 건 조회 = limit(1).fetchOne()
        Member findMember2 = queryFactory
                .selectFrom(QMember.member)
                .fetchFirst();

        // 페이징 정보 포함 = total count 쿼리
        // 특이점은, 페이징을 위한 count 쿼리 1번
        // 그리고 content를 위한 쿼리 1번 => 즉, 쿼리가 2번이 나간다.
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();

        // 이제 fetchResult 대신에 fetch()를 사용해야 한다고 한다.
        // 이것도 따로 함수로 뺀다.

        // 이런 정보들도 조회 가능
        results.getTotal();
        results.getLimit();
        results.getOffset();

        // content 이런 거
        List<Member> content = results.getResults();

        // count 수 = count 쿼리 1번
        long count = queryFactory
                .selectFrom(member)
                .fetchCount();
        // queryDsl 버전이 높아지면서 fetchCount 지원 x
        // 아예 따로 함수를 만들어야 한다.
        // 아니면 .fetch().size()를 해줘도 될 것 같다.

    }

    // fetchCount 용도 함수
    @Test
    public void count() {
        Long totalCount = queryFactory
                // select count(*)
                // .select(Wildcard.count)

                // select count(member.id) 의미함
                .select(member.count())
                .from(member)
                .fetchOne();
    }

    // fetchResult() 용도
    public Page<Member> findMemberWithPaging(Pageable pageable) {
        List<Member> content = queryFactory
                .selectFrom(member)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(content, pageable, content.size());
    }


    /*********************/
    // 정렬
    // 회원 나이 내림차순 > 회원 이름 오름차순 > 이름이 없으면 마지막에 출력
    @Test
    public void sort() throws Exception {
        // given
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        // when
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                // 회원 나이 내림차순 (desc)
                .orderBy(member.age.desc(),
                        // 회원 이름 오름차순 (asc)
                        // 단, null이라면 마지막에 가도록
                        member.username.asc().nullsLast())
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);

        // then
        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    /*********************/
    // 페이징
    @Test
    public void paging1() throws Exception {
        // given

        // when
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                // 1부터 시작햐도록 (기본은 0부터 시작)
                .offset(1)
                // 최대 2건 조회
                .limit(2)
                .fetch();

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    // 전체 조회 수 - count 쿼리 나가고 content 쿼리 나가서 성능 주의
    @Test
    public void paging2() throws Exception {
        // given

        // when
        QueryResults<Member> queryResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();

        // then
        assertThat(queryResults.getTotal()).isEqualTo(4);
        assertThat(queryResults.getLimit()).isEqualTo(2);
        assertThat(queryResults.getOffset()).isEqualTo(1);
        assertThat(queryResults.getResults().size()).isEqualTo(2);
    }

    /*********************/

    // 집합

    /**
     * JPQL
     * select
     * COUNT(m), //회원수
     * SUM(m.age), //나이 합
     * AVG(m.age), //평균 나이
     * MAX(m.age), //최대 나이
     * MIN(m.age) //최소 나이
     * from Member m
     */
    @Test
    public void aggregation() throws Exception {
        // given

        // when
        List<Tuple> result = queryFactory
                .select(member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min())
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);

        // then
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);

    }

    // groupBy - 팀의 이름, 각 팀의 평균 연령 구하기
    @Test
    public void group() throws Exception {
        // given

        // when
        List<Tuple> result = queryFactory
                // 팀의 이름, 각 팀 멤버의 평균 연령
                .select(team.name, member.age.avg())
                .from(member)
                // member에 있는 team과 team을 조인해주기
                .join(member.team, team)
                // 팀의 이름으로 그룹화
                // 우리는 팀이 2개니까 teamA, teamB로 그룹화되어 있을 것임
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        // then
        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        // 10+20 / 2 = 15
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        // 30+40 / 2 = 35
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }
    // 물론 having도 가능하다!! grouping에서 조건 주기


    /*********************/
    /*
        헷갈려서 적어두는 join
        inner join -> A, B의 교집합
        outer join -> A, B의 합집합.
            left outer join -> A+B에서 A를 다 가져오도록 (B에 null 허용)
            right outer join -> A+B에서 B를 다 가져오도록 (A에 null 허용)

    */
    // 조인 - 기본 조인
    // 첫 번째 파라미터에 조인 대상, 두 번째 파라미터에 별칭으로 사용할 Q 타입 지정.

    // 팀 A에 소속된 모든 회원 조회하기
    @Test
    public void join() throws Exception {
        // given

        // when
        List<Member> result = queryFactory
                .selectFrom(member)
                // member.team -> 조인 대상, team -> 별칭으로 사용할 Q 타입
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        // then
        assertThat(result)
                // extraction -> 필드명
                // username이 member1, member2인지.
                .extracting("username")
                .containsExactly("member1", "member2");


        // join, innerJoin() -> 내부 조인
        // leftJoin() -> left outer 조인
        // rightJoin() -> right outer 조인
    }

    // 세타 조인 - 연관관계 없는 필드로 조인하기
    // 회원의 이름과 팀 이름이 같은 회원 조회하기
    @Test
    public void theta_join() throws Exception {
        // given
        // 모든 member-team을 조인해서 이름이 같은 애들을 가져오는 것임.
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        // when
        List<Member> result = queryFactory
                .select(member)
                // 여기서 여러 엔티티를 선택하여 세타 조인 가능.
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();

        // then
        assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }

    /*********************/
    // 조인 - on절.
    // on절을 활용한 조인
    // 1) 조인 대상을 필터링하거나 2) 연관관계 없는 엔티티 외부 조인을 하거나

    // 1. 조인 대상 필터랑하기

    /**
     * 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * a left join b, 이때 b=fk값
     * JPQL: SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'teamA'
     * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.TEAM_ID=t.id and
     * t.name='teamA'
     */

    @Test
    public void join_on_filtering() throws Exception {
        // given

        // when
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("teamA"))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
        /*
            left join이어서 member를 기준으로 가져옴, 근데 조건이 teamA만 가져오는 거니깐 teamB는 안 가져옴
            tuple = [Member(id=3, username=member1, age=10), Team(id=1, name=teamA)]
            tuple = [Member(id=4, username=member2, age=20), Team(id=1, name=teamA)]
            tuple = [Member(id=5, username=member3, age=30), null]
            tuple = [Member(id=6, username=member4, age=40), null]
         */

        /*
         cf) 만약 leftJoin이 아니라 그냥 join을 사용했다면 inner join (교집합)이니까
         teamA인 member 2개만 (id=3, id=4) 조회된다.
         애초에 teamB를 가져오지 않아서, 조인할 수 있는 대상이 없어지는 거니까
         그냥 join을 사용하면 on절 대신에 where(team.name.eq("teamA"))를 사용한 것과 동일한 결과가 나온다.
         즉, 이때는 on=where이 되는 것
         */
    }

    // 2. 연관관계 없는 엔티티 외부 조인

    /**
     * 회원의 이름과 팀의 이름이 같은 대상 외부 조인
     * JPQL: SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name
     * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.username = t.name
     */
    @Test
    public void join_on_no_relation() throws Exception {
        // given
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        // when
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team)
                .on(member.username.eq(team.name))
                .fetch();

        // then
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }

        /*
        이름이 같은 경우에 조인을 해서 가져옴
        tuple = [Member(id=3, username=member1, age=10), null]
        tuple = [Member(id=4, username=member2, age=20), null]
        tuple = [Member(id=5, username=member3, age=30), null]
        tuple = [Member(id=6, username=member4, age=40), null]
        tuple = [Member(id=7, username=teamA, age=0), Team(id=1, name=teamA)]
        tuple = [Member(id=8, username=teamB, age=0), Team(id=2, name=teamB)]
         */

        // 당연히, inner join을 활용하면 그냥 id=7, 8의 member만 가져오게 된다.

    }

    /*********************/
    // 조인 - 페치 조인
    @PersistenceUnit
    EntityManagerFactory emf;

    // 페치 조인 적용 안 하고 지연 로딩을 통해 member-team 쿼리 실행
    @Test
    public void fetchJoinNo() throws Exception {
        // given
        em.flush();
        em.clear();

        // when
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        // lazy이기 때문에 여기까지 member만 조회되고, member 내의 team은 프록시객체.

        // findMember의 team이 로딩된 엔티티인지, 초기화되지 않은 엔티티인지 알려준다.
        boolean loaded = emf.getPersistenceUnitUtil()
                .isLoaded(findMember.getTeam());

        // then
        // 페치 조인 적용 전에는 프록시객체니까 당연히 false여야 한다.
        assertThat(loaded).as("페치 조인 미적용").isFalse();

    }

    // 페치 조인 적용했을 때
    @Test
    public void fetchJoinUse() throws Exception {
        // given
        em.flush();
        em.clear();

        // when
        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        // 여기서는 페치 조인으로 인해 연관된 애들을 한 번에 긁어온다.
        boolean loaded = emf.getPersistenceUnitUtil()
                .isLoaded(findMember.getTeam());
        // then
        // 그래서 여기서의 team은 로딩된 엔티티여야 한다.
        assertThat(loaded).as("페치 조인 적용").isTrue();

    }

    /*********************/
    // 서브 쿼리 (쿼리 안에 쿼리 넣기) - JPAExpressions 사용하기
    // 나이가 가장 많은 회원 조회하기
    @Test
    public void subQuery() throws Exception {
        // given
        QMember memberSub = new QMember("memberSub");

        // when
        List<Member> result = queryFactory
                .selectFrom(member)
                // 서브 쿼리여서 바깥의 alias와 겹치면 안 되어서 memberSub를 만들어준 것
                .where(member.age.eq(
                        JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub)

                ))
                .fetch();

        // then
        assertThat(result).extracting("age")
                .containsExactly(40);

    }

    // 나이가 평균 나이 이상인 회원
    @Test
    public void subQueryGoe() throws Exception {
        // given
        QMember memberSub = new QMember("memberSub");

        // when
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();

        // then
        assertThat(result).extracting("age")
                .containsExactly(30, 40);
    }

    // 서브쿼리 여러 건 처리, in 사용
    @Test
    public void subQueryIn() throws Exception {
        // given
        QMember memberSub = new QMember("memberSub");

        // when
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        JPAExpressions
                                .select(memberSub.age)
                                .from(memberSub)
                                // 나이가 10살 이상인 회원 조회
                                .where(memberSub.age.gt(10))
                ))
                .fetch();

        // then
        assertThat(result).extracting("age")
                .containsExactly(20, 30, 40);

    }

    // select 절 내부에 subquery
    @Test
    public void selectSubQuery() throws Exception {
        // given
        QMember memberSub = new QMember("memberSub");

        // when
        List<Tuple> result = queryFactory
                .select(member.username,
                        // 꼭 이렇게 길게 안 쓰고 static import 해도 된다!
                        JPAExpressions
                                // username이랑 회원들의 평균 나이를 같이 출력하도록
                                .select(memberSub.age.avg())
                                .from(memberSub))
                .from(member)
                .fetch();

        // then
        for (Tuple tuple : result) {
            System.out.println("username = " + tuple.get(member.username));
            System.out.println("age = " + tuple.get(JPAExpressions
                    .select(memberSub.age.avg())
                    .from(memberSub)));
        }
        /*
            username = member1
            age = 25.0
            username = member2
            age = 25.0
            username = member3
            age = 25.0
            username = member4
            age = 25.0
         */

    }
    /*
        단, from절의 서브 쿼리는 지원하지 않는다. (JPA 문제)

        1. 서브쿼리를 join으로 변경하기
        2. 애플리케이션에서 쿼리를 2번 분리해서 실행하기
        3. nativeSQL 사용하기

        이런 식으로 해야 된다 ㅠㅠㅠ

     */

    /*********************/
    // case문 - select, where, order by에서 사용 가능
    @Test
    public void basicCase() throws Exception {
        // given

        // when
        List<String> result = queryFactory
                .select(
                        member.age
                                .when(10).then("열살")
                                .when(20).then("스무살")
                                .otherwise("기타"))
                .from(member)
                .fetch();

        // then
        for (String s : result) {
            System.out.println("s = " + s);
        }
        /*
        각각의 멤버의 나이에 따라 이런 식으로 출력 가능
        s = 열살
        s = 스무살
        s = 기타
        s = 기타
         */

    }

    @Test
    public void basicCase2() throws Exception {
        // given

        // when
        List<String> result = queryFactory
                // 복잡한 조건에 대해서는 caseBuilder 사용 가능
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        // then
        for (String s : result) {
            System.out.println("s = " + s);
        }
        /*
        s = 0~20살
        s = 0~20살
        s = 21~30살
        s = 기타
         */

    }

    /*
    임의의 순서로 회원을 출력하고 싶다면?
    1. 0 ~ 30살이 아닌 회원을 가장 먼저 출력
    2. 0 ~ 20살 회원 출력
    3. 21 ~ 30살 회원 출력
     */
    @Test
    public void basicCase3() throws Exception {
        // given

        // when
        // 이런 식으로 복잡한 조건 자체를 변수로 선언해버리기
        NumberExpression<Integer> rankPath = new CaseBuilder()
                .when(member.age.between(0, 20)).then(2)
                .when(member.age.between(21, 30)).then(1)
                .otherwise(3);

        // then
        List<Tuple> result = queryFactory
                .select(member.username, member.age, rankPath)
                .from(member)
                // 그리고 이를 OrderBy의 조건으로 넣어준다.
                .orderBy(rankPath.desc())
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            Integer rank = tuple.get(rankPath);
            System.out.println("username = " + username + " age = " + age + " rank = " + rank);
        }
        /*
        username = member4 age = 40 rank = 3
        username = member1 age = 10 rank = 2
        username = member2 age = 20 rank = 2
        username = member3 age = 30 rank = 1
         */
    }

    /*********************/
    // 상수, 문자 더하기
    @Test
    public void constant() throws Exception {
        // given

        // when
        // 상수 처리는 Expressions 사용하기!
        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
        /*
            이런 식으로 결과에 그냥 상수를 붙여서 반환해준다.
            tuple = [member1, A]
            tuple = [member2, A]
            tuple = [member3, A]
            tuple = [member4, A]
         */

        String result2 = queryFactory
                // username_age 형태로
                .select(member.username.concat("_")
                        // concat은 문자만 되니까 문자로 바꾸어야 함
                        // 참고로, 문자가 아닌 타입은 stringValue()를 통해 변환이 가능하다!
                        .concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        // result2 = member1_10
        System.out.println("result2 = " + result2);

    }
}