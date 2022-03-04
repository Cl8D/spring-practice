package jpabook.jpashop2.controller;

import jpabook.jpashop2.domain.Member;
import jpabook.jpashop2.domain.Order;
import jpabook.jpashop2.domain.item.Item;
import jpabook.jpashop2.repository.OrderRepository;
import jpabook.jpashop2.repository.OrderSearch;
import jpabook.jpashop2.service.ItemService;
import jpabook.jpashop2.service.MemberService;
import jpabook.jpashop2.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    // 상품 주문 폼 이동
    /*
    * 메인 화면에서 상품을 주문하면 /order를 GET 방식으로 호출 -> createForm() 실행
    * 주문 화면에 주문할 고객정보와 상품 정보가 필요하기 때문에
    * model 객체에 담아서 view에게 보내준다.
    * */
    @GetMapping("/order")
    public String createForm(Model model) {
        // 상품/고객 정보 전부를 넘겨주는 것
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    // 주문 실행
    /* 주문할 회원과 상품, 그리고 수량을 선택하여 submit 버튼을 누르면
    /order를 POST 방식으로 호출한다. -> order() 실행
    이때 고객 / 상품 식별자, 수량 정보를 받아서 주문 서비스에 주문을 요청하고,
    주문이 끝나면 상품 주문 내역이 존재하는 /orders로 리다이렉트 시켜준다.
    * */
    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count) {

        // 직접적인 처리를 서비스에서 하는 이유는...
        // 아무래도 트랜잭션 내부에서 동작하기 때문에 영속성 컨텍스트 내에서 관리되기도 하고,
        // 서비스가 조금 더 엔티티를 의존하도록 하기 위해서(?)
        // 아무튼, 가급적이면 컨트롤러에서는 식별자만 넘기고,
        // 핵심 비즈니스 로직은 서비스에서 하는 게 더 낫다... 고 말씀하셨다.
        orderService.order(memberId, itemId, count);
        return "redirect:/orders";
    }

    // 주문 목록 검색
    @GetMapping("/orders")
    // 상품 검색시 사용한 검색 조건들이 orderSearch에 담긴다
    public String orderList(@ModelAttribute("orderSearch")OrderSearch orderSearch,
                            Model model) {
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);
        return "order/orderList";

    }

    // 주문 취소
    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
