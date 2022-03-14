package hello.itemservice.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
//@ScriptAssert(lang="javascript", script="_this.price * _this.quantity >= 10000")
public class Item {
    /*
    // 수정 요구사항 추가
    @NotNull(groups = UpdateCheck.class) // 수정 시에만 적용
    private Long id;

    // bean validation annotation 적용하기
    @NotBlank(groups = {SaveCheck.class, UpdateCheck.class})
    private String itemName;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Range(min=1000, max=1000000, groups = {SaveCheck.class, UpdateCheck.class})
    private Integer price;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Max(value=9999, groups = SaveCheck.class) // 등록 시에만 적용
    private Integer quantity;
    */

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }


    // Form 전송 객체 분리하기
    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;

}
