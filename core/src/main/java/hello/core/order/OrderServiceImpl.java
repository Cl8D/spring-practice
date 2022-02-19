package hello.core.order;

import hello.core.annotation.MainDiscountPolicy;
import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
// lombok에서 제공하는 생성자 자동 생성 기능
//@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    //private final MemberRepository memberRepository = new MemoryMemberRepository();
    //private final DiscountPolicy discountPolicy = new FixDiscountPolicy();

    // 인터페이스에만 의존하는 코드
    //private DiscountPolicy discountPolicy;

    // 수정된 코드 (관심사 분리)
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;


    // 2가지 모두 받을 수 있도록 생성자 만들기
    // 생성자가 1개니까 Autowired 생략
    // +) 추가적으로, 만약 rate와 fix 모두 @Component를 설정해놨다면 여기서 의존관계 주입시 DiscountPolicy에 2개가 조회되어 에러남
    // 이를 방지하기 위해 두 번째 파라미터로 rateDiscountPolicy라는 빈 이름을 추가해준다.
    /*
    @Autowired
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy rateDiscountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = rateDiscountPolicy;
    }
    */
    /*
    // @Qualified 예제
    @Autowired
    public OrderServiceImpl(MemberRepository memberRepository, @Qualifier("mainDiscountPolicy") DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
    */

    @Autowired
    public OrderServiceImpl(MemberRepository memberRepository, @MainDiscountPolicy DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    // 할인에 대한 내용이랑 상관없이 오직 주문에 관한 내용을 참조하기 때문에
    // 나중에 할인에 대한 수정이 이 부분에서 일어나지 않아도 된다.
    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }

    // 테스트 용도
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
