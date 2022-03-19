package hello.exception.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// BadRequestException 예외가 컨트롤러 밖으로 넘어가게 되면,
// ResponseStatusExceptionResolver 예외가
// @ResponseStatus를 확인하고 오류 코드를 400으로 변경해준다. (bad request)
//@ResponseStatus(code= HttpStatus.BAD_REQUEST, reason = "잘못된 요청 오류")

// 혹은, reason을 messageSource에서 찾는 기능도 제공한다.
@ResponseStatus(code= HttpStatus.BAD_REQUEST, reason = "error.bad")
public class BadRequestException extends RuntimeException{
}
