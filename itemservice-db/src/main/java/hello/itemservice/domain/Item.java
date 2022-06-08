package hello.itemservice.domain;

import lombok.Data;

@Data
public class Item {

    private Long id;

    // 이름, 가격, 수량
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
