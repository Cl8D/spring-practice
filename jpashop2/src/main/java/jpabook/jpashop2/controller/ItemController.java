package jpabook.jpashop2.controller;

import jpabook.jpashop2.domain.item.Book;
import jpabook.jpashop2.domain.item.Item;
import jpabook.jpashop2.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    // 상품 등록
    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form) {
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }

    // 상품 목록
    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";

    }

    // 상품 수정
    // 상품을 수정하는 건 변할 수 있기 때문에 -> itemId는 변할 수 있으니까
    // 이를 매핑시켜주기 위해 pathVariable 사용

    /*
    * 로직)
    * <수정> 버튼 클릭 시 /items/{itemId|/edit URL을 GET으로 요청
    * -> 그 결과값으로 updateItemForm() 실행
    * -> itemService.findOne(itemId) 호출을 통해 수정할 상품 조회
    * -> 조회 결과를 모델 객체에 담아서(엔티티 X, 폼을 보냄) 뷰 (items/updateItemForm)에 전달.
    * */
    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId);

        // 여기서 특징은 book 엔티티를 보내는 게 아니라 bookForm을 보낸다는 점
        BookForm form = new BookForm();

        form.setId(item.getId());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    /*
    * 로직)
    * 상품 수정 폼에서 정보를 수정하고 submit 버튼 클릭
    * -> /items/{itemId}/edit URL을 post로 요청하고 updateItem() 메서드를 실행
    * -> 이때, item 엔티티 인스턴스는 준영속 상태이기 때문에 영속성 컨텍스트의 지원 x
    * -> 그래서 데이터를 수정해도 변경 감지 (dirty checking)가 동작 X!
    * */
    @PostMapping("/items/{itemId}/edit")
    // html 파일에서 object="${form}"에 의해서 이름이 그대로 넘어오기 때문에 이런 식으로 옵션 지정 필요
    public String updateItem (@ModelAttribute("form") BookForm form) {
        // 가급적이면 이 방법보다
        /*
        Book book = new Book();

        book.setId(form.getId());
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        */

        // 이 방법을 사용하자.
        // 어설프게 엔티티를 생성하지 않은 것.
        itemService.updateItem(form.getId(), form.getName(), form.getPrice(), form.getStockQuantity());

        return "redirect:/items";


    }

}
