package hello.itemservice.web.form;

import hello.itemservice.domain.item.DeliveryCode;
import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.ItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/form/items")
@RequiredArgsConstructor
@Slf4j
public class FormItemController {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "form/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "form/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        // 빈 아이템 객체를 넘겨준다 (타임리프 사용 위해)
        // th:object="${item}"으로 받을 수 있다.
        model.addAttribute("item", new Item());
        return "form/addForm";
    }

    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        // 로그 출력
        log.info("item.open={}", item.getOpen());
        log.info("item.region={}", item.getRegions());
        log.info("item.itemType={}", item.getItemType());

        return "redirect:/form/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "form/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/form/items/{itemId}";
    }

    // 등록 지역 - 서울, 부산, 제주 추가
    // modelAttribute를 사용하면 상품 등록 / 상세 / 수정 폼에서 보여야 하는 서울/부산/제주 체크박스를 위해
    // 보통 model.addAttribute()를 통해 계속 넣어줘야 한다.
    // 그러나, 이런 식으로 사용하면 컨트롤러에 있는 별도의 메서드에 적용이 가능해진다.
    // 이러면 컨트롤러 요청 시 regions에서 반환한 값이 자동으로 model에 담겨서 좀 더 편리해진다.
    @ModelAttribute("regions")
    public Map<String, String> regions() {
        Map<String, String> regions = new LinkedHashMap<>();
        regions.put("SEOUL", "서울");
        regions.put("BUSAN", "부산");
        regions.put("JEJU", "제주");
        return regions;

        // 그냥 이게 자동으로 들어가는 거라고 생각하자.
        // model.addAttribute("regions", regions);
    }

    // 모델에 enum전달
    @ModelAttribute("itemTypes")
    public ItemType[] itemTypes() {
        // enum의 모든 정보를 배열로 반환한다.
        // ex) [BOOK, FOOD, ETC]
        return ItemType.values();
    }

    // <div th:each="type : ${T(hello.itemservice.domain.item.ItemType).values()}">
    // 이런 식으로 springEL을 이용해 enum을 직접 사용할 수도 있다고 한다.
    // 여기서 values()를 통해 마찬가지로 enum의 모든 정보를 배열로 반환한다.
    // 근데 이러면 패키지 경로를 알아야 해서 변경 시 수정이 귀찮아져서 비추천 (컴파일 에러로 잡을 수도 x)



    @ModelAttribute("deliveryCodes")
    public List<DeliveryCode> deliveryCodes() {
        List<DeliveryCode> deliveryCodes = new ArrayList<>();
        // 컨트롤러가 호출될 때마다 사용되기 때문에 DeliveryCode 객체가 계속 생성된다는 점은 주의하자.
        // 미리 만들어두고 재사용하는 것이 더 효율적이긴 함
        deliveryCodes.add(new DeliveryCode("FAST", "빠른 배송"));
        deliveryCodes.add(new DeliveryCode("NORMAL", "일반 배송"));
        deliveryCodes.add(new DeliveryCode("SLOW", "느린 배송"));
        return deliveryCodes;
    }
}

