package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
@Slf4j
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    /************************************************/

    // 검증 처리 개발
    //@PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        // 검증 로직
        // Stringutils.hasText -> 파라미터가 문자열인지 확인해서 t/f 반환
        if (!StringUtils.hasText(item.getItemName()))
            // fieldError 객체 -> 필드 자체의 에러일 때 사용
            // objectName = @ModelAttribute 이름
            // field =  오류가 발생한 필드 이름
            // defaultMessage = 오류 메시지
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000)
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));


        if (item.getQuantity() == null || item.getQuantity() > 10000)
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999 까지 허용합니다."));


        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            // ObjectError
            // 특정 필드에 종속되지 않으니까 objectError를 사용하였음)
            // objectName = @ModelAttribute 이름
            // defaultMessage = 오류 메시지
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        // 만약 검증에서 오류 메시지가 존재하면 오류 메시지 출력을 위해 model에 errors를 담아주기
        // 이후 입력 폼이 있는 뷰 템플릿으로 보내준다.
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        // 상품 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /******************************************************/

    //@PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
       if (!StringUtils.hasText(item.getItemName()))
           // FieldError의 또 다른 버전이다.
           // rejectedValue (3번째 param) -> 사용자가 입력한 값 (거절된 값)
           // bindingFailure -> 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값
           // codes -> 메시지 코드
           // arguments -> 메시지에서 사용하는 인자
           bindingResult.addError(new FieldError("item", "itemName", item.getItemName(),
                    false, null, null, "상품 이름은 필수입니다."));

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000)
            bindingResult.addError(new FieldError("item", "price", item.getPrice(),
                    false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));


        if (item.getQuantity() == null || item.getQuantity() > 10000)
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(),
                    false, null, null, "수량은 최대 9,999 까지 허용합니다."));


        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", null, null,
                        "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /******************************************************/

    //@PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (!StringUtils.hasText(item.getItemName()))

            // required.item.itemName=상품 이름은 필수입니다.
            // 여기서 new String[]으로 여러 인자를 줄 수 있는데, 첫 번째 것을 properties 파일에서 못 찾으면
            // 두 번째 것을 찾고... 또 못 찾으면 아예 defaultMessage를 출력하는 식으로 동작한다.
            // default까지 없으면 오류가 난다는 점!
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(),
                    false, new String[]{"required.item.itemName"}, null, null));


        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000)
            // range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
            // argument에 {0}과 {1}에 들어갈 정보를 준다.
            bindingResult.addError(new FieldError("item", "price", item.getPrice(),
                    false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null));


        if (item.getQuantity() == null || item.getQuantity() > 10000)
            // max.item.quantity=수량은 최대 {0} 까지 허용합니다.
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(),
                    false, new String[]{"max.item.quantity"}, new Object[]{9999}, null));


        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                // totalPriceMin=가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}
                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"},
                        new Object[]{10000, resultPrice},null));
            }
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /******************************************************/

    //@PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        // bindingResult는 본인이 검증해야 하는 객체를 알고 있다.
        // objectName=item => @ModelAttribute name 의미함
        log.info("objectName={}", bindingResult.getObjectName());
        // target=Item(id=null, itemName=상품, price=100, quantity=1234) -> 뒤에는 사용자가 입력한 값
        log.info("target={}", bindingResult.getTarget());


        if (!StringUtils.hasText(item.getItemName()))
            // 조금 더 편리하게 코드를 쓰기 위해 rejectValue를 사용해보자.
            // field : 오류 필드명
            // errorCode : 오류 코드 (messageResolver를 위한 코드)
            // 저런 식으로 축약해서 사용해도 required가 들어간 애가 자동으로 되는...? 느낌임. (원래 required.item.itemName이지만...! requried만 있어도 동작한다는 것)
            // errorArgs : 오류 메시지에서 {0}을 치환하기 위한 값
            // defaultMessage : 오류 메시지를 찾을 수 없을 때 사용하는 기본 메시지
            bindingResult.rejectValue("itemName", "required");


        // 위 코드를 축약해서 다음과 같이 쓸 수도 있다. (if문 같은 거 내장되어 있음)
        //ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "itemName", "required");

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000)
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);


        if (item.getQuantity() == null || item.getQuantity() > 10000)
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);


        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /******************************************************/

    // 얘는 어차피 requiredArgConstructor에 의해서 생성자 주입이 일어난다.
    // 스프링 빈으로 주입
    private final ItemValidator itemValidator;

    //@PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        itemValidator.validate(item, bindingResult);

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }


    /******************************************************/

    // 이 친구는 컨트롤러가 호출될 때마다 호출된다.
    // webDataBinder는 스프링의 파라미터 바인딩 역할 및 검증 기능을 제공한다.
    // InitBinder는 해당 컨트롤러에만 영향을 준다.
    @InitBinder
    public void init(WebDataBinder dataBinder) {
        log.info("init binder {}", dataBinder);
        // webDataBinder에 검증기를 추가하면 해당 컨트롤러에서는 검증기를 자동으로 적용할 수 있다.
        dataBinder.addValidators(itemValidator);
    }

    @PostMapping("/add")
    // validator 호출 대신에 검증 대상에 @Validated 어노테이션을 추가해준다.
    // 이러면 webDataBinder에 등록한 검증기를 찾아서 실행하는데,
    // 이때 supports(Item.class)가 호출되어 결과가 참이기 때문에
    // ItemValidator.validate()가 호출된다.
    public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }


    /******************************************************/


    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

