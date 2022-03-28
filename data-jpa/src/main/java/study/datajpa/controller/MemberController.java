package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;


@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    /*
    // 도메인 클래스 컨버터 사용 전 -> HTTP 파라미터로 넘어온 엔티티의 아이디로 엔티티 객체를 찾아서 바인딩한다.
    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }
    */
    // 도메인 클래스 컨버터 사용 후
    // 도메인 클래스 컨버터가 중간에 동작하여 회원 엔티티 객체를 반환해준다.
    // 이때, 리파지토리를 사용하여 엔티티를 찾는 것.

    // 이때, 엔티티를 파라미터로 받으면 단순히 조회용으로 사용해야 함!
    // (트랜잭션 밖에서 엔티티를 조회했기 때문에 변경해도 db에 반영이 안 됨)
    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    /**************/

    @GetMapping("/members")
    //public Page<Member> list(Pageable pageable) {
    public Page<MemberDto> list(Pageable pageable) {
        // 파라미터로 pageable 받기.
        // 이때 pageable은 인터페이스여서 실제로는 pageRequest 객체를 생성하여 사용해야 한다.
        Page<Member> page = memberRepository.findAll(pageable);
        // DTO로 바꿔주기
        Page<MemberDto> pageDto = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        //return page;
        return pageDto;
    }

    /*
        spring data가 제공하는 페이징 및 정렬 기능.
        사용 예시)
        localhost:8080/members?page=0&size=3&sort=id, desc&sort=username,desc
        이런 식으로 파라미터에 페이징 정보를 담으면 정렬할 수 있다.

        page -> 현재 페이지로, 0부터 시작
        size -> 한 페이지에 노출할 데이터 개수
        sort -> 정렬 조건. (asc or desc)
     */

    // 샘플 데이터
    //@PostConstruct
    public void init() {
        for(int i=0; i<100; i++)
            memberRepository.save(new Member("user" + i, i));
    }

    // 혹은 이런 식으로도 쓸 수 있다.
    /*
    @RequestMapping(value = "/members_page", method = RequestMethod.GET)
    public String list(@PageableDefault(size = 12, sort = "username",
            direction = Sort.Direction.DESC) Pageable pageable) {
        ...
    }
     */

}
