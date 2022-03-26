package jpabook.jpashop3.service;

import jpabook.jpashop3.domain.item.Item;
import jpabook.jpashop3.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    // 상품 저장
    // readOnly 옵션 끄기
    @Transactional
    public void saveItem (Item item) {
        itemRepository.save(item);
    }

    // 상품 수정
    @Transactional
    public Item updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);
        return findItem;
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
