package hello.itemservice.repository.memory;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

// 인메모리에서 레포지토리 구현
@Repository
public class MemoryItemRepository implements ItemRepository {

    private static final Map<Long, Item> store = new HashMap<>(); //static
    private static long sequence = 0L; //static

    @Override
    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        // id로 찾고 dto에 저장된 정보를 바탕으로 저장해주기
        Item findItem = findById(itemId).orElseThrow();
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    @Override
    // optional로 해서 null도 들어가도록!
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        // 검색 조건을 받아서 데이터 조회
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        // 자바 스트림으로 객체 조회
        return store.values().stream()
                .filter(item -> {
                    // itemName이 비어있으면 무시
                    if (ObjectUtils.isEmpty(itemName)) {
                        return true;
                    }
                    // 비어있지 않으면 contains으로 체크하기
                    // 만약 itemA, itemB가 있고 들어온 게 item이라면 둘 다 가져오게 된다.
                    // 특정 문자열 포함 여부 확인해주는 것
                    return item.getItemName().contains(itemName);
                }).filter(item -> {
                    // 가격제한이 비어있으면 무시
                    if (maxPrice == null) {
                        return true;
                    }
                    // 아니라면 해당 가격보다 작은 애들 가져오기
                    return item.getPrice() <= maxPrice;
                })
                .collect(Collectors.toList());
    }

    // 메모리에 저장된 item 모두 초기화 (테스트 용도에서 사용)
    public void clearStore() {
        store.clear();
    }

}
