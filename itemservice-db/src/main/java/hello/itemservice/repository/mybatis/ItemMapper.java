package hello.itemservice.repository.mybatis;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

// MyBatis 매핑 XML을 호출해주는 매퍼 인터페이스
@Mapper
public interface ItemMapper {
    // xml의 해당하는 SQL을 실행하고 결과를 돌려주게 된다.
    void save(Item item);
    // 파라미터가 2개 이상이면 @Param으로 이름을 지정해줘야 한다.
    void update(@Param("id") Long id, @Param("updateParam")ItemUpdateDto updateParam);
    Optional<Item> findById(Long id);
    List<Item> findAll(ItemSearchCond itemSearchCond);
}
