package jpabook.jpashop2.domain.item;

import jpabook.jpashop2.domain.Category;
import jpabook.jpashop2.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
// 싱글 테이블 전략 설정
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// dtype을 기준으로 구분
@DiscriminatorColumn(name="dtype")
public abstract class Item {

    @Id @GeneratedValue
    @Column(name="item_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // 비즈니스 로직 설계
    // 여기서 중요한 점은, item.setStockQuantity()를 사용하지 않고
    // 이러한 비즈니스 메서드를 통해서 수량을 제어해야 한다는 점이다.

    // 재고 수량 증가
    public void addStock (int quantity) {
        this.stockQuantity += quantity;
    }
    // 재고 수량 감소
    public void removeStock (int quantity) {
        int restStock = this.stockQuantity - quantity;
        // 재고 수량이 0보다 작아지는 경우 체크
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
