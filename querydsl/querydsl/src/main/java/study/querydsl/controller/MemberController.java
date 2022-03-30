package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.repository.MemberJpaRepository;
import study.querydsl.repository.MemberRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberJpaRepository memberJpaRepository;
    private final MemberRepository memberRepository;

    // ex) http://localhost:8080/v1/members?teamName=teamB&ageGoe=31&ageLoe=35
    // 이런 식으로 postman을 통해서 검색 조건을 걸었을 때 나오는 결과를 볼 수 있다.
    // (참고로, @ModelAttribute가 생략된 형태라고 볼 수 있음!)
    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMemberV1 (MemberSearchCondition condition) {
        return memberJpaRepository.search(condition);
    }

    // 한 페이지당 사이즈 5개, 2번째 페이지 (정확하게는 3번째)
    // http://localhost:8080/v2/members?size=5&page=2

    @GetMapping("/v2/members")
    public Page<MemberTeamDto> searchMemberV2(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.searchPageSimple(condition, pageable);
    }

    // http://localhost:8080/v3/members?page=0&size=110
    // 전체 데이터가 100개여서 이렇게 110개를 하면 굳이 count 쿼리를 날리지 않는다!
    // count 쿼리를 날리는 이유는 페이지 수의 계산인데 (전체 데이터 수 -> count로 계산, 이 값을 pageSize로 나누어서 구함)
    // 100개밖에 없는데 size를 110개를 주면 어차피 페이지가 1개니까 굳이 페이지 수를 계산할 필요가 x
    @GetMapping("/v3/members")
    public Page<MemberTeamDto> searchMemberV3(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.searchPageComplex(condition, pageable);
    }
}
