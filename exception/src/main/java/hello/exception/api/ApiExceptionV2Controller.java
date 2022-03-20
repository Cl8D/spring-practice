package hello.exception.api;

import hello.exception.exception.UserException;
import hello.exception.exhandler.ErrorResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class ApiExceptionV2Controller {

//
//    // 얘를 안 넣어주면 그냥 정상 흐름이라고 판단하여 200으로 넘어감
//    // 이때 다른 상태코드로 넘겨주고 싶다면 @ResponseStatus를 넣어준다.
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    // @ExceptionHandler 사용하여 해당 컨트롤러에서 처리하고 싶은 예외 지정해주기
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ErrorResult illegalExHandle(IllegalArgumentException e) {
//        log.error("[exceptionHandle] ex", e);
//        return new ErrorResult("BAD", e.getMessage());
//
//        /**
//         * {
//         *     "code": "BAD",
//         *     "message": "잘못된 입력 값"
//         * }
//         * 그러나 상태 코드는 400으로 찍힘!
//         */
//
//
//    }
//
//    // ExceptionHandler에 인자를 주지 않고 함수의 매개변수로 담아줘도 된다. (UserException e)
//    @ExceptionHandler
//    public ResponseEntity<ErrorResult> userHandle (UserException e) {
//        log.error("[exceptionHandle] ex", e);
//        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
//        // 여기서는 객체를 넘길 때 상태코드를 넘겨줘서 @ResponseStatus를 안 써도 된다.
//        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
//
//        /**
//         * {
//         *     "code": "USER-EX",
//         *     "message": "사용자 오류"
//         * }
//         * 상태 코드는 400.
//         */
//    }
//
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler
//    // Exception은 최상위 클래스이기 때문에
//    // 위에서 처리하지 못한 예외들은 다 여기서 처리가 된다.
//    public ErrorResult exHandle(Exception e) {
//        log.error("[exceptionHandle] ex", e);
//        return new ErrorResult("EX", "내부 오류");
//
//        /**
//         * {
//         *     "code": "EX",
//         *     "message": "내부 오류"
//         * }
//         * 상태 코드는 500.
//         */
//    }


    @GetMapping("/api2/members/{id}")
    public MemberDto getMember (@PathVariable("id") String id) {

        if (id.equals("ex"))
            throw new RuntimeException("잘못된 사용자");

        if (id.equals("bad"))
            throw new IllegalArgumentException("잘못된 입력 값");

        if (id.equals("user-ex"))
            throw new UserException("사용자 오류");

        return new MemberDto(id, "hello " + id);
    }

    // cf) 항상 자세한 것이 우선권을 가지기 때문에,
    // 만약 부모클래스 - 자식클래스 예외가 선언된 메서드가 2개 있다고 하자.
    // 자식 예외가 발생하면 둘 다 호출 대상이지만,
    // 더 자세한 자식 클래스 예외가 호출이 된다.

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }
}
