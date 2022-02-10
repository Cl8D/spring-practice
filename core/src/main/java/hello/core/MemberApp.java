package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MemberApp {

    public static void main(String[] args) {
        // 이전 코드
        //MemberService memberService = new MemberServiceImpl();

        // 수정된 코드 (관심사 분리)
        //AppConfig appConfig = new AppConfig();
        // 이 안에는 memberServiceImpl이 들어가 있음!
        //MemberService memberService = appConfig.memberService();

        // Spring 이용
        // Spring Container를 ApplicationContext라고 생각하기.
        // 얘가 모든 객체들을 관리해주게 된다.
        // annotation 기반으로 config를 하기 때문에 annotationConfig~를 사용함
        // AppConfig의 설정 정보를 가져오기 위해 파라미터로 넘겨준다.
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        // 기본적으로 bean 등록 시 메서드 이름으로 등록된다.
        // 두 번째 인자로는 반환 타입을 가진다.
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);


        Member member = new Member(1L, "memberA", Grade.VIP);
        memberService.join(member);

        Member findMember = memberService.findMember(1L);
        System.out.println("new member = " + member.getName());
        System.out.println("findMember = " + findMember.getName());

    }
}