package hello.itemservice.repository.v2;

import hello.itemservice.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

// 리포지토리 분리
// 스프링 데이터 JPA의 기능을 제공하는 레포지토리
public interface ItemRepositoryV2 extends JpaRepository<Item, Long> {

}
