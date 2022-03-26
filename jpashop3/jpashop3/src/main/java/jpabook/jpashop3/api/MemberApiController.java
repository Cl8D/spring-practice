package jpabook.jpashop3.api;

import jpabook.jpashop3.domain.Member;
import jpabook.jpashop3.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;


    /**************회원 등록 API************************/
    // @RequestBody -> http의 body가 그대로 전달 (json 기반 요청)
    // http 요청의 바디 내용을 자바 객체로 변환하여 (메시지 컨버터)
    // 매핑된 메서드 파라미터로 전달해준다. -> 여기서는 json을 member로 바꿔주는 것!
    // @Valid -> 유효성 검사.

    /**
     * 요청값으로 Member 엔티티를 직접 받기.
     * -> 엔티티에 API 검증을 위한 로직이 들어간다 (@NotEmpty 같은)
     * -> 그러나, API별로 보통 요구사항을 다르기 때문에
     * 엔티티 변경 시 API 스펙이 함께 변할 수 있어 좋지 않다.
     * -> 그래서, API 요청 스펙에 따른 별도의 DTO를 파라미터로 받는 게 좋다.
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1 (@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 요청값으로 member 엔티티 대신에 별도의 DTO를 받아준다!
     * 실무에서는 특히, API에 엔티티가 노출되지 않는 것이 중요함!!!!!!!
     * 절대 절대 절대 받으면 안 된다.
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2 (@RequestBody @Valid
                                              CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberRequest {
        // 여기다가 필요한 스펙을 넣어주면 된다. NotEmpty를 이부분에 붙여주는 느낌!
        private String name;
    }

    // 회원이 등록되면 id가 반환되도록 임시로 만들었음
    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    /**************회원 수정 API****************************/
    @PutMapping("/api/v2/members/{id}")
    // 회원 정보를 부분 업데이트하기 때문에, 사실 patch나 post를 사용하는 게 더 낫다
    // put은 전체 업데이트를 진행할 때 조금 더 올바름!
    public UpdateMemberResponse updateMemberV2 (@PathVariable("id") Long id,
                                                @RequestBody @Valid UpdateMemberRequest request) {
        // command-query를 분리하는 스타일
        // update 메서드에서 바로 member를 반환하지 않고, 따로 그냥 id를 통해 member를 조회해줬음.
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    /**************회원 조회 API************************/
    // 엔티티에 직접 노출하는 v1 버전. 당연히 이렇게 하면 안 된다!
    // 이러면 출력 결과에 member만 있는 게 아니라, 엔티티에 있는 모든 정보들이 다 출력되기 때문에
    // member 정보 외에 orders 등 다른 정보도 함께 출력되게 된다.
    // 사실 @JsonIgnore라는 게 있지만, 이거는 api마다 다른 조건일 수도 있고,
    // 엔티티에 화면에 뿌리는 계층 - 프레젠테이션 계층까지 함께 추가되어서 좋지 않다.
    // 유지 보수에도 별로 좋지 않다는 것.
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    // 응답값 자체를 Result로 감싸주었음.
    public Result membersV2() {
        List<Member> findMembers = memberService.findMembers();

        // 엔티티 -> DTO로 변환해주기
        List<MemberDto> collect = findMembers.stream()
                // m은 findMembers에 있는 각각의 member.
                // member의 이름을 꺼내서 memberDto에 넣어주기
                .map(m -> new MemberDto(m.getName()))
                // 그리고 리스트로 바꿔주기
                .collect(Collectors.toList());

        return new Result(collect);
    }

    // 이런식으로 result로 감싸게 되면,
    // 원래는 리턴 타입이 [] 이런 식으로 배열로 감싸져서 나오는데
    // 껍데기가 object형으로 바뀌게 된다 -> {} 이렇게!
    /* 이런 식으로 들어가서 필드 추가도 유용해짐!
    {
    "data": [
        {
            "name": "하이"
        },
        {
            "name": "안녕"
        }
    ]
    }
    */

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }
}
