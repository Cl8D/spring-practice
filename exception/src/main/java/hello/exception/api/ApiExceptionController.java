package hello.exception.api;

import hello.exception.exception.BadRequestException;
import hello.exception.exception.UserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ApiExceptionController {
    @GetMapping("/api/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {
        if (id.equals("ex"))
            throw new RuntimeException("잘못된 사용자");


        /**
         * 현재, 이 상태에서 postman으로 실험하면
         * 인자로 ex를 넘겼을 때 우리가 만든 오류 페이지 HTML이 반환된다.
         * 클라이언트는 오류 페이지일 때도 JSON 형식으로 받기를 원한다면,
         * 이를 위해 수정해야 한다. -> ErrorPageController
         */

        // 기능 추가) IllegalArgumentException 처리 실패하면 http 상태코드를 400으로 설정해주자.
        if (id.equals("bad"))
            throw new IllegalArgumentException("잘못된 입력 값");

        /**
         * 근데 이 상태에서 실행해봤자 (bad를 파라미터로) 상태코드가 500이 나온다.
         */

        // 우리가 만든 사용자 정의 예외 발생
        if(id.equals("user-ex"))
            throw new UserException("사용자 오류");


        return new MemberDto(id, "hello " + id);
    }


    @GetMapping("/api/response-status-ex1")
    public String responseStatusEx1() {
        throw new BadRequestException();

        /**
         * {
         *     "timestamp": "2022-03-19T10:56:44.000+00:00",
         *     "status": 400,
         *     "error": "Bad Request",
         *     "exception": "hello.exception.exception.BadRequestException",
         *     "message": "잘못된 요청 오류",
         *     "path": "/api/response-status-ex1"
         * }
         */
    }


    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }
}

