package study.querydsl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;

import java.util.List;

/**
 * 사용자 저으이 리포지토리 사용하는 방법
 * 1. 사용자 정의 인터페이스 작성하기
 * 2. 사용자 정의 인터페이스 구현하기
 * 3. 스프링 데이터 리포지토리에 사용자 정의 인터페이스 상속하기.
 */
// 우리는 querydsl 전용 기능인 회원 search를 작성하기 위해 사용자 정의 리포지토리를 만들려고 한다.
// 1. 사용자 정의 인터페이스 작성하기
public interface MemberRepositoryCustom {
    // 동적 쿼리 search
    List<MemberTeamDto> search(MemberSearchCondition condition);

    // 페이징
    // 전체 카운트를 한 번에 조회하는 방법
    Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable);

    // 데이터 내용과 전체 카운트를 별도로 조회하는 바업ㅂ
    Page<MemberTeamDto> searchPageComplex (MemberSearchCondition condition, Pageable pageable);

}
