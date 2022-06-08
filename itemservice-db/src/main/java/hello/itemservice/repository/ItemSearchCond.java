package hello.itemservice.repository;

import lombok.Data;

// 일종의 DTO.
// 보통 최종 호출되는 소유자와 같은 패키지에 둔다고 한다.
// 여기서는 repository에 의해 호출되니깐 repository와 같은 패키지에 둠!
// 어차피 서비스에서도 리포지토리를 호출해서 사용하는 형태니까.
@Data
public class ItemSearchCond {

    // 검색 조건 - 상품명, 가격 제한
    // 상품명의 일부만 포함되어도 검색이 되도록 (like)
    private String itemName;
    private Integer maxPrice;

    public ItemSearchCond() {
    }

    public ItemSearchCond(String itemName, Integer maxPrice) {
        this.itemName = itemName;
        this.maxPrice = maxPrice;
    }
}
