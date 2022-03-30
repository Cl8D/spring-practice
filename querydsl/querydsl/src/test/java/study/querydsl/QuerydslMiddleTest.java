package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.*;

@SpringBootTest
@Transactional
public class QuerydslMiddleTest {
    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

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
    // 중급 문법
    // 프로섹션과 결과 반환
    // 프로젝션이란 = select절에 뭘 가져올지 대상을 지정하는 것. (특정 컬럼 지정)

    @Test
    public void projection() throws Exception {
        // 프로젝트 대상이 하나일 때
        // 이때는 타입을 명확하게 지정할 수 있다.

        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
            /*
            s = member1
            s = member2
            s = member3
            s = member4
             */
        }
    }

    @Test
    public void projection2() throws Exception {
        // 프로젝션 대상이 둘 이상일 때는 튜플이나 DTO로 조회하자.
        // 튜플 조회 방법 (Querydsl이 만들어놓은 문법이라고 생각)
        // 튜플을 애플리케이션이나 서비스 계층까지 보내는 건 안 좋음.
        // 컨트롤러까지 가지 않고, 그냥 리파지토리에서만 쓰도록...? 하는 게 좋다고 함.
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            System.out.println("username = " + username);
            System.out.println("age = " + age);
            /*
            username = member1
            age = 10
            username = member2
            age = 20
            username = member3
            age = 30
            username = member4
            age = 40
             */
        }
    }

    @Test
    public void DtoJPAProjection() throws Exception {
        // dto로 반환하기 - 순수 JPA 활용하기
        String query = "select new study.querydsl.dto.MemberDto(m.username, m.age)"
                + " from Member m";

        List<MemberDto> result = em.createQuery(query, MemberDto.class)
                .getResultList();

        // 엔티티가 아닌 Dto이기 때문에 new를 이용해서 써줘야 하며,
        // 패키지 이름까지 다 써야 한다는 단점이 있다.

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
            /*
                memberDto = MemberDto(username=member1, age=10)
                memberDto = MemberDto(username=member2, age=20)
                memberDto = MemberDto(username=member3, age=30)
                memberDto = MemberDto(username=member4, age=40)
             */
        }

    }
    
    @Test
    public void DtoProjectionSetter() throws Exception {
        // queryDSL 활용하기
        // 1) 프로퍼티 접근법 - setter 이용
        // setter를 통한 주입을 통해 만들어졌다고 생각하면 될 듯!
        // setUsername(member.username), setAge(member.age) 같은 개념
        List<MemberDto> result = queryFactory
                // 앞에 클래스 타입을, 뒤부터는 꺼내올 값을 지정해주기
                .select(Projections.bean(MemberDto.class,
                        member.username, member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
        /*
        memberDto = MemberDto(username=member1, age=10)
        memberDto = MemberDto(username=member2, age=20)
        memberDto = MemberDto(username=member3, age=30)
        memberDto = MemberDto(username=member4, age=40)
         */
    }

    @Test
    public void DtoProjectionField() throws Exception {
        // 2) 필드 직접 접근법
        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username, member.age))
                .from(member)
                .fetch();

        // 필드에 바로 값을 꽂아버리는 방법.
        // username = member.username, age=member.age
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
        /*
        memberDto = MemberDto(username=member1, age=10)
        memberDto = MemberDto(username=member2, age=20)
        memberDto = MemberDto(username=member3, age=30)
        memberDto = MemberDto(username=member4, age=40)
         */
    }

    @Test
    public void DtoProjectionAliasing() throws Exception {
        // 1), 2) 방식에서 별칭이 다를 경우 사용
        QMember memberSub = new QMember("memberSub");

        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                        // as를 통해 필드명 지정해줄 수 있음.
                        member.username.as("name"),
                        // ExpressionUtils.as(source,alias) : 서브쿼리에 별칭 지정 시 유용
                        ExpressionUtils.as(
                                JPAExpressions
                                        // 가장 나이가 많은 회원의 나이로 age 세팅해주는 형식
                                        .select(memberSub.age.max())
                                        // 이렇게 두 번쨰 파라미터로 별칭 지정이 가능하다.
                                        .from(memberSub), "age")

                ))
                .from(member)
                .fetch();

        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
            /*
            userDto = UserDto(name=member1, age=40)
            userDto = UserDto(name=member2, age=40)
            userDto = UserDto(name=member3, age=40)
            userDto = UserDto(name=member4, age=40)
             */
        }
    }

    @Test
    public void DtoProjectionConstructor() throws Exception {
        // 3) 생성자 접근법
        List<MemberDto> result = queryFactory
                // 생성자를 통해 주입해서 만들어주는 방법!
                // 생성자 주입은 타입이 중요하니까
                // 필드의 타입이 동일한 MemberDto-userDto를 바꿔서 써도 잘 돌아간다!
                // 여기서 MemberDto -> UserDto로 바꿔도 잘 됨!
                .select(Projections.constructor(MemberDto.class,
                        member.username, member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
            /*
            memberDto = MemberDto(username=member1, age=10)
            memberDto = MemberDto(username=member2, age=20)
            memberDto = MemberDto(username=member3, age=30)
            memberDto = MemberDto(username=member4, age=40)
             */
        }
        
    }

    @Test
    public void DtoProjectionAnnotation() throws Exception {
        // @QueryProjection 활용하기
        List<MemberDto> result = queryFactory
                // 컴파일 타임 때 타입 체크가 가능하지만, (생성자의 경우 실행 후, 런타임 오류 때 알 수 있음)
                // queryDsl 어노테이션을 dto에 넣어야 한다는 점이랑 (Dto는 여러 곳에서 사용하는데, 좀 의존적이어서)
                // q파일을 생성해야 한다는 단점이 있다...!
                .select(new QMemberDto(member.username, member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
            /*
            memberDto = MemberDto(username=member1, age=10)
            memberDto = MemberDto(username=member2, age=20)
            memberDto = MemberDto(username=member3, age=30)
            memberDto = MemberDto(username=member4, age=40)
             */
        }

        // 얘는 그냥 추가. distinct() 사용법! jpql이랑 똑같다.
        List<String> result2 = queryFactory
                .select(member.username)
                .distinct()
                .from(member)
                .fetch();

    }

    /************************/
    // 동적 쿼리 - BooleanBuilder, where 다중 파라미터 사용하기
    
    // 1) BooleanBuilder 사용하기
    @Test
    public void dynamicQuery_BooleanBuilder() throws Exception {
        // given
        String usernameParam = "member1";
        Integer ageParam = 10;
        
        // when
        // 이름이 member1이고 나이가 10인 회원을 찾고 싶음
        // 현재는 파라미터의 값이 null인지에 따라 쿼리가 동적으로 바뀌는 형태
        List<Member> result = searchMember1(usernameParam, ageParam);

        // then
        assertThat(result.size()).isEqualTo(1);
        
    }

    private List<Member> searchMember1(String usernameCond, Integer ageCond) {
        BooleanBuilder builder = new BooleanBuilder();

        // 만약 나이가 비어있다면 이름에 대한 조건만 들어갈 것이다.
        if(usernameCond != null)
            builder.and(member.username.eq(usernameCond));
        // 만약 이름이 비어있다면 age에 대한 조건만 들어갈 것
        if(ageCond != null)
            builder.and(member.age.eq(ageCond));

        // 둘 다 null이라면 그냥 member 전체를 가져오게 될 것이다.
        // 반대로 둘 다 있으면 그냥 두 개 다 조건에 들어가게 됨!

        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();
    }


    // 2) where 다중 파라미터 사용하기
    // 이게 좀 더 코드상으로 깔끔한 것 같음!!
    @Test
    public void dynamicQuery_whereParam() throws Exception {
        // given
        String usernameParam = "member1";
        Integer ageParam = 10;

        // when
        List<Member> result = searchMember2(usernameParam, ageParam);

        // then
        assertThat(result.size()).isEqualTo(1);

    }

    private List<Member> searchMember2(String usernameCond, Integer ageCond) {
        return queryFactory
                .selectFrom(member)
                // where절의 값으로 null이 들어오면 무시된다는 점을 이용
                // 메서드 재사용 가능, 쿼리 가독성 높음
                .where(usernameEq(usernameCond), ageEq(ageCond))
                .fetch();
    }

    private BooleanExpression usernameEq(String usernameCond) {
        // 만약 usernameCond가 null이 아니라면
        // member.username.eq(usernameCond)를 반환하고
        // 아니면 null 반환
        return usernameCond != null ? member.username.eq(usernameCond) : null;
    }

    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond != null ? member.age.eq(ageCond) : null;
    }

    // 메서드 재사용 예시
    private BooleanExpression allEq(String usernameCond, Integer ageCond) {
        // 만약 여기서 usernameEq(usernameCond)가 null이라면
        // return null.and(~~)가 되어버린다.
        // 이럴 때는 아예 booleanBuilder를 추가적으로 활용하는 방법이 있다!
        return usernameEq(usernameCond).and(ageEq(ageCond));
    }


    // where + booleanBuilder 활용하기
    @Test
    public void dynamicQuery_whereBuilder() throws Exception {
        String usernameParam = null;
        Integer ageParam = null;

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(nameAndAgeEq(usernameParam, ageParam))
                .fetch();

        // result = [Member(id=3, username=member1, age=10), Member(id=4, username=member2, age=20),
        // Member(id=5, username=member3, age=30), Member(id=6, username=member4, age=40)]
        // 조건이 둘 다 null이니까 member를 다 가져오게 된다.
        System.out.println("result = " + result);

    }

    private BooleanBuilder nameAndAgeEq(String usernameCond, Integer ageCond) {
        return usernameEq2(usernameCond).and(ageEq2(ageCond));
    }

    private BooleanBuilder usernameEq2(String usernameCond) {
        /*
        if (usernameCond == null)
            return new BooleanBuilder();
        return new BooleanBuilder(member.username.eq(usernameCond));
        */

        // 코드 줄이기
        return nullSafeBuilder(()->member.username.eq(usernameCond));
    }


    private BooleanBuilder ageEq2(Integer ageCond) {
        /*
        if (ageCond == null)
            return new BooleanBuilder();
        return new BooleanBuilder(member.age.eq(ageCond));
        */
        return nullSafeBuilder(() -> member.age.eq(ageCond));
    }

    private static BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch(IllegalArgumentException e) {
            return new BooleanBuilder();
        }
    }

    /************************/
    // 수정, 삭제 벌크 연산
    @Test
    public void bulkOperation() throws Exception {
        // 쿼리 한 번으로 대량의 데이터 수정이 가능하다.
        // 나이가 28살 이하인 사람의 이름을 비회원이라고 바꿔보자.

        long count = queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();

        // 2명이 바뀜 (member1-10, member2-20이었으니깐!)
        System.out.println("count = " + count);

        /*
            주의!
            JPQL의 배치 연산처럼, 여기서의 벌크 연산도 영속성 컨텍스트를 거치지 않고
            바로 DB로 쿼리를 날려버려서, 영속성 컨텍스트와 디비 상태아 달라질 수도 있음.
            - 즉, 현재 상태에서는 member1, member2의 이름이 바뀌지 않았을 것임.
            (db에만 바로 '비회원'이라고 적용된 상태)
            그래서 이러한 배치 쿼리를 날린 이후에는 영속성 컨텍스트를 초기화 해야 한다!
         */

        // 그래셔, 현재 여기서 이런 식으로 가져오는 값은
        // 영속성 컨텍스트에서 가져오는 값이어서 출력을 해보면 변경 전 값이 들어오게 된다.
        List<Member> result = queryFactory
                .selectFrom(member)
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
            /*
            member1 = Member(id=3, username=member1, age=10)
            member1 = Member(id=4, username=member2, age=20)
            member1 = Member(id=5, username=member3, age=30)
            member1 = Member(id=6, username=member4, age=40)
             */
        }

        // 그래서, 이런 식으로 비워줘야 정상적으로 나온다!
        em.flush();
        em.clear();

        List<Member> result2 = queryFactory
                .selectFrom(member)
                .fetch();

        for (Member member1 : result2) {
            System.out.println("member1 = " + member1);
            /*
            member1 = Member(id=3, username=비회원, age=10)
            member1 = Member(id=4, username=비회원, age=20)
            member1 = Member(id=5, username=member3, age=30)
            member1 = Member(id=6, username=member4, age=40)
             */
        }
    }

    @Test
    public void bulkOperation2() throws Exception {
        // 숫자 더하기, 곱하기
        // 모든 회원의 나이를 +1 한 뒤 *2 해주기
        long count = queryFactory
                .update(member)
                .set(member.age, member.age.add(1).multiply(2))
                .execute();

        em.flush();
        em.clear();

        List<Member> result = queryFactory
                .selectFrom(member)
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
            /*
            member1 = Member(id=3, username=member1, age=22)
            member1 = Member(id=4, username=member2, age=42)
            member1 = Member(id=5, username=member3, age=62)
            member1 = Member(id=6, username=member4, age=82)
             */
        }

    }

    @Test
    public void bulkOperation3() throws Exception {
        // 대량으로 데이터 삭제하기
        // 18살 이상인 회원 삭제하기
        long count = queryFactory
                .delete(member)
                .where(member.age.gt(18))
                .execute();

        em.flush();
        em.clear();

        List<Member> result = queryFactory.selectFrom(member).fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
            // member1 = Member(id=3, username=member1, age=10)
        }
    }

    /************************/
    // SQL function 호출하기 - JPA처럼 Dialect에 등록된 내용만 호출 가능함
    
    @Test
    public void sqlFunction() throws Exception {
        // member -> M으로 변경하는 replace 함수 사용하기
        String result = queryFactory
                .select(Expressions.stringTemplate(
                        // member.username에서 "member"를 "M"으로 바꿀 것.
                        "function('replace', {0}, {1}, {2})",
                        member.username, "member", "M"
                ))
                .from(member)
                // 처음 거 결과 가져오기
                .fetchFirst();

        // result = M1
        System.out.println("result = " + result);

        // 소문자로 변경하기
        List<String> result2 = queryFactory
                .select(member.username)
                .from(member)
                .where(member.username.eq(
                        Expressions.stringTemplate(
                                "function('lower', {0})",
                                member.username
                        )
                ))
                // 아니면 그냥 .eq(member.username.lower())라고 써도 됨!
                .fetch();

    }


}
