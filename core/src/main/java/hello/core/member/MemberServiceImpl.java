package hello.core.member;

public class MemberServiceImpl implements MemberService{

    // 이전 코드
    // private final MemberRepository memberRepository = new MemoryMemberRepository();

    // 수정된 코드 (관심사 분리)
    private final MemberRepository memberRepository;

    // 생성자를 통해서 memberRepository의 구현체에 뭐가 들어갈지 선택할 것임!
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
}
