package jpabook.jpashop2.service;

import jpabook.jpashop2.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception {
        Book book = em.find(Book.class, 1L);

        // 트랜잭션 내부라고 가정
        book.setName("dofijwoeif");

        // 커밋 -> 이러면 JPA는 자동으로 변경된 지점을 찾아서 update query를 날려준다
        // -> 이게 바로 변경 감지 (dirty checking)


    }

}
