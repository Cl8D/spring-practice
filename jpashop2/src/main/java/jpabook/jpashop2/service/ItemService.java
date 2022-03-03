package jpabook.jpashop2.service;

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

    // 상품 조회
    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    // 상품 하나 조회
    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

}
