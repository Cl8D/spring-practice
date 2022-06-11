package hello.itemservice.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor // 기본 생성자 필수
public class Item {

    // identity 방식 사용
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이름, 가격, 수량
    @Column(name = "item_name", length=10) // varchar 10
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
