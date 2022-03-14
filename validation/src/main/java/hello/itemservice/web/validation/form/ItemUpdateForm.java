package hello.itemservice.web.validation.form;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

// item 수정용 폼
@Data
public class ItemUpdateForm {
    // html에서 받는 거 4개 맞춤
    @NotNull
    private Long id;

    @NotBlank
    private String itemName;

    @NotNull
    @Range(min=1000, max=1000000)
    private Integer price;

    private Integer quantity;
}
