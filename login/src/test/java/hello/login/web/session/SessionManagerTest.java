package hello.login.web.session;

import hello.login.domain.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    SessionManager sessionManager = new SessionManager();

    @Test
    void sessionTest() {

        // 세션 생성
        // spring이 가짜 httpservletResponse를 만들어준다. (진짜 거는 사용 못하니까)
        MockHttpServletResponse response = new MockHttpServletResponse();
        Member member = new Member();
        // 웹 브라우저에 응답이 나갔다고 가정하자!
        sessionManager.createSession(member, response);

        // 여기는 웹 브라우저의 요청이라고 가정하자.
        // 요청에 응답 쿠키 저장하기
        MockHttpServletRequest request = new MockHttpServletRequest();
        // 쿠키를 만들어서 서버에 전달해주는데, 이때 응답에서 나온 쿠키 값을 통해서 요청 쿠키를 만드는 것.
        // ex) mySessionId=2340932-23049-er-wer
        request.setCookies(response.getCookies());

        // 세션 조회
        Object result = sessionManager.getSession(request);
        assertThat(result).isEqualTo(member);

        // 세션 만료
        sessionManager.expire(request);
        Object expired = sessionManager.getSession(request);
        assertThat(expired).isNull();
    }


}