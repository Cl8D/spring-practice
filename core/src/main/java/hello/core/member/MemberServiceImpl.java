package hello.core.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// componentScan
@Component
public class MemberServiceImpl implements MemberService{

    // 이전 코드
    // private final MemberRepository memberRepository = new MemoryMemberRepository();

    // 수정된 코드 (관심사 분리)
    private final MemberRepository memberRepository;

    // 생성자를 통해서 memberRepository의 구현체에 뭐가 들어갈지 선택할 것임!
    // autowired를 통해 자동으로 의존 관계 주입을 해줄 수 있다
    @Autowired
    // 약간 ac.getBean(MemberRepository.class)와 동일한 역할
    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }

    // 테스트 용도
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }

}
