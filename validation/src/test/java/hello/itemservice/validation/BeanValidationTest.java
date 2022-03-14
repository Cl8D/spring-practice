package hello.itemservice.validation;

import hello.itemservice.domain.item.Item;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class BeanValidationTest {

    @Test
    void beanValidation() {
        // 검증기 생성 코드 (사실 나중에는 스프링이 해줌)
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Item item = new Item();
        item.setItemName(" ");
        item.setPrice(0);
        item.setQuantity(10000);

        // 검증 대상인 item을 검증기에 넣고 결과를 받는다.
        // 즉, set에는 검증 오류값이 담기며 결과가 비어있다면 검증 오류 x
        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        for (ConstraintViolation<Item> violation : violations) {
            // hibername에서 기본으로 제공하는 메시지가 출력된다.
            // violations=ConstraintViolationImpl{interpolatedMessage='9999 이하여야 합니다', propertyPath=quantity, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{javax.validation.constraints.Max.message}'}
            // violation.getMessage() = 9999 이하여야 합니다
            // violations=ConstraintViolationImpl{interpolatedMessage='1000에서 1000000 사이여야 합니다', propertyPath=price, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{org.hibernate.validator.constraints.Range.message}'}
            // violation.getMessage() = 1000에서 1000000 사이여야 합니다
            // violations=ConstraintViolationImpl{interpolatedMessage='공백일 수 없습니다', propertyPath=itemName, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{javax.validation.constraints.NotBlank.message}'}
            // violation.getMessage() = 공백일 수 없습니다
            System.out.println("violations=" + violation);
            System.out.println("violation.getMessage() = " + violation.getMessage());
        }

    }
}
