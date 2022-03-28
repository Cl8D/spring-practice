package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

// 구현 클래스
// 이때, 리포지토리 인터페이스 이름 + Impl로 만들기.
// 이러면 스프링 데이터 jpa가 알아서 스프링 빈으로 등록해준다.
// 물론, Impl로 안 쓰고 싶다면 XML 설정을 통해서 바꿀 수 있다.
// 추가적으로, MemberRepositoryCustomImpl 이런 식으로 사용자 정의 인터페이스 + Impl도 가능!
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        String query = "select m from Member m";
        return em.createQuery(query)
                .getResultList();
    }
}
