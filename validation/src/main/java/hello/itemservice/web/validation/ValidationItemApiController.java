package hello.itemservice.web.validation;

import hello.itemservice.web.validation.form.ItemSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/validation/api/items")
public class ValidationItemApiController {

    @PostMapping("/add")
    // json 형식으로 한 번 받아보자는 것.
    public Object addItem(@RequestBody @Validated ItemSaveForm form,
                          BindingResult bindingResult) {

        log.info("API 컨트롤러 호출");

        if(bindingResult.hasErrors()) {
            log.info("검증 오류 발생 errors={}", bindingResult);
            // ObjectError와 FieldError 반환함.
            // 스프링은 이 객체를 json으로 반환하여 클라이언트에게 전달하였다.
            return bindingResult.getAllErrors();
        }

        log.info("성공 로직 실행행");
        return form;
    }
}
