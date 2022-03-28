package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    public void save() {
        // 여기서 트랜잭션이 없어도 .save 안에 구현체 자체에 @Transactional이 붙어 있어서
        // 그러면서 commit - flush가 되는 것. (쿼리 나감)
        Item item = new Item();
        itemRepository.save(item);
    }

}