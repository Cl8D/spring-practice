package jpabook.jpashop2.controller;

import jpabook.jpashop2.domain.Address;
import jpabook.jpashop2.domain.Member;
import jpabook.jpashop2.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // HTTP GET
    // 데이터를 받아오는 역할
    @GetMapping("/members/new")
    public String createForm(Model model) {
        // controller->view로 넘어갈 때 데이터를 실어서 넘겨준다.
        // 즉, model에 데이터를 담을 때 addAttribute() 메서드를 주로 사용하며,
        // value 객체를 name의 이름으로 추가한다.
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }


    // HTTP POST
    // 데이터를 실제 등록하는 역할
    // @Valid는 valid 관련 annotation을 사용함을 인지
    // valid에 의해 걸린 오류는 bindingResult에 담기게 된다..
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {

        // 만약 에러가 있다면 리다이렉트
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        // member 저장
        memberService.join(member);
        // 첫 번째 페이지로 리다이렉트.
        return "redirect:/";
    }


    /*
    * cf) 폼 객체 vs 엔티티 직접 사용
    * 엔티티는 핵심 비즈니스 로직만 가지고 있고, 화면을 위한 로직은 없어야 한다.
    * (특히, API를 만들 때는 절대 엔티티를 반환해서는 안 된다)
    * 아무튼, 화면이나 API에 맞는 폼 객체나 DTO를 사용하는 게 좋다.
    * 엔티니는 최대한 순수하게 유지하는 게 좋음.
   * */
    @GetMapping("/members")
    public String list(Model model) {
        List<Member> member = memberService.findMembers();
        model.addAttribute("members", member);
        return "members/memberList";
    }

}
