package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

// 컨트롤러에서 검증 로직을 분리해보자.
// 스프링에서는 검증을 위한 Validator 인터페이스를 제공한다.
@Component
public class ItemValidator implements Validator {

    @Override
    // 해당 검증기를 지원하는지 여부 확인
    public boolean supports(Class<?> clazz) {
        // 파라미터로 넘어온 클래스가 Item 클래스 / 혹은 Item의 자식 클래스인지 확인
        return Item.class.isAssignableFrom(clazz);
        // item == clazz or item == subItem인지 판단
    }

    @Override
    // 검증 대상 객체(target), BindingResult(errors)
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "itemName", "required");

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000)
            errors.rejectValue("price", "range", new Object[]{1000, 1000000}, null);


        if (item.getQuantity() == null || item.getQuantity() > 10000)
            errors.rejectValue("quantity", "max", new Object[]{9999}, null);


        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

    }
}
