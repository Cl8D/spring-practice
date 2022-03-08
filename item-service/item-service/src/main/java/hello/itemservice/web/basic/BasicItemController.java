package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import java.util.List;

// itemRepository에서 모든 상품을 조회한 다음에 모델에 담고,
// 뷰 템플릿을 호출한다.
@Controller
@RequestMapping("/basic/items")
// final이 붙은 멤버변수에 대해서 생성자를 만들어준다.
// -> 생성자가 1개이면 스프링이 자동으로 @Autowired를 통해 의존관계를 주입함
@RequiredArgsConstructor
public class BasicItemController {
    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        // 뷰에게 데이터를 전달해줄 때 model.addAttribute 사용
        model.addAttribute("items", items);
        return "basic/items";
    }

    // 테스트용 데이터 추가
    // 해당 빈의 의존관계가 모두 주입되고 나면 초기화 용도로 호출
    @PostConstruct
    public void init() {
        itemRepository.save(new Item("testA", 10000, 10));
        itemRepository.save(new Item("testB", 20000, 20));
    }

    // 상품 상세
    // pathVariable로 넘어온 상품Id로 상품을 조회하고, 모델에 담은 뒤 뷰 템플릿 호출
    @GetMapping("/{itemId}")
    public String item(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);

        // <이거는 뒷부분 내용>
        // 여기로 제어가 간다!! ========================= 2번
        // 그래서 우리가 원하는 <저장되었습니다> 메시지를 처리하기 위해서 item.html에 처리를 해줘야 하는 것
        return "basic/item";
    }

    // 상품 등록 폼
    // GET이면 상품 등록 폼 = /basic/items/add
    // POST면 /basic/items/add

    // <form action="item.html" th:action method="post"> 에서
    // th:action을 비워두었다.
    // 상품 등록 버튼을 클릭해도 url이 변경되지 않고 그대로 /add.

    // 취소 버튼 누르면 상품 목록으로 이동한다.
    // th:onclick="|location.href='@{/basic/items}'|"
    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }


    // 상품 등록 처리
    // content-type: application/x-www-form-urlencoded
    // 메시지 바디에 쿼리 파라미터 형식으로 전달할 예정
    // ex) itemName=itemA&price=10000&quantity=10
    //@PostMapping("/add")
    public String addItemV1(@RequestParam String itemName,
                            @RequestParam int price,
                            @RequestParam Integer quantity,
                            Model model) {
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);

        model.addAttribute("item", item);
        // 상품 상세의 item.html 뷰 템플릿 그대로 사용하기
        // 이러면 등록 후 해당 상품의 상세사항을 보여준다.
        return "basic/item";
    }

    // modelAttribute를 통해서 한 번에 처리하기
    // 이러면 알아서 item 객체를 생성한 뒤 set 함수를 호출하여 입력해준다.
    // 추가적으로, model.addAttribute()로 해당 객체를 넣어준다.
    // 이때 속성으로 @ModelAttribute의 key-value 값을 사용한다는 점.

    // ex) @ModelAttribute("hello") Item item => model.addAttribute("hello", item);
    //@PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item,
                            Model model) {
        itemRepository.save(item);
        return "basic/item";
    }

    // modelAttribute의 이름을 생략해주자.
    // 이러면 클래스명의 첫 글자를 소문자로 바꿔서 자동으로 등록해준다.
    // ex) Item => item으로 바꿔서 name으로 등록!
    //@PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item) {
        itemRepository.save(item);
        return "basic/item";
    }

    // modelAttribute 자체를 생략해주기.
    // 이러면 객체가 알아서 자동 등록된다.
    // 참고로 string은 requestParam이 적용됨
    //@PostMapping("/add")
    public String addItemV4(Item item) {
        itemRepository.save(item);
        // 이거는 뷰 템플릿으로 이동하는 것
        return "basic/item";
    }

    // 새로고침 문제를 해결하기
    //@PostMapping("/add")
    public String addItemV5(Item item) {
        itemRepository.save(item);
        // 뷰 템플릿이 아니라 상품 상세 화면으로 리다이렉트.
        // 근데 이런 식으로 쓰면 URL 인코딩이 안 되어서 별로 좋지 않음
        return "redirect:/basic/items/" + item.getId();
    }


    // 저장 이후 <저장되었습니다>라는 메시지를 보여지도록 해보자.
    // redirectAttribute를 사용하면 url 인코딩 및 pathVariable, 쿼리 파라미터를 처리해준다.
    @PostMapping("/add")
    public String addItemV6(Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        // 뷰 템플릿에서 이 값이 있으면 '저장되었습니다' 메시지 출력되도록 할 것
        redirectAttributes.addAttribute("status", true);
        // pathVariable 바인딩을 통해 {itemId}를 처리하며,
        // status 정보가 쿼리 파라미터로 넘어간다 => ?status=true

        // 그럼 여기서 제어는 어디로 가냐면 ------------------- 1번
        return "redirect:/basic/items/{itemId}";
    }

    // 상품 수정 폼
    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    // 상품 수정
    // 수정이 완료되면 상품 상세 화면으로 리다이렉트
    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId,
                       @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}";
    }

    /**
     * > HTML Form 전송은 PUT, PATCH를 지원하지 않는다. GET, POST만 사용할 수 있다.
     * > PUT, PATCH는 HTTP API 전송시에 사용
     * > 스프링에서 HTTP POST로 Form 요청할 때 히든 필드를 통해서 PUT, PATCH 매핑을 사용하는 방법이
     * 있지만, HTTP 요청상 POST 요청이다.
     * */
}
