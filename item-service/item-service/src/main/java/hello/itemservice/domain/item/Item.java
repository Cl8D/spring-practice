package hello.itemservice.domain.item;

import lombok.Data;


@Data
public class Item {
    private Long id;
    private String itemName;
    // null값이 허용될 수 있도록 integer형으로
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
