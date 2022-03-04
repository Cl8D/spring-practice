package jpabook.jpashop2.service;

import jpabook.jpashop2.domain.item.Book;
import jpabook.jpashop2.domain.item.Item;
import jpabook.jpashop2.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    // 값 수정이니깐 readOnly 옵션 해제 (default가 false여서)
    // 상품 저장
    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }


    // 상품 수정 - 변경 감지 기능 사용
    @Transactional
    public Item updateItem(Long itemId, String name, int price, int stockQuantity) {
        // id를 기반으로 실제 db에 존재하는 영속상태의 아이템 찾아옴
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);
        return findItem;
        // 여기까지 제어가 넘어오면 spring의 @Transactional에 의해서 커밋이 되고
        // 이게 변경 감지에 의해서 db에 업데이트가 된다. (update query)
    }

    // 상품 조회
    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    // 상품 하나 조회
    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

}
