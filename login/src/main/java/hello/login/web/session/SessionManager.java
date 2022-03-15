package hello.login.web.session;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// 세션 관리
@Component
public class SessionManager {
    public static final String SESSON_COOKIE_NAME = "mySessionId";

    // 동시성을 고려하여 concurrentHashMap을 사용하기
    // 세션 저장소
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    // cf) request -> 클라이언트에게 온 요청! response -> 클라이언트에게 응답!이라고 생각하장
    // 세션 생성
    public void createSession(Object value, HttpServletResponse response) {
        // 세션 id 생성 및 값 세션 저장
        // UUID.randomUUID는 추정 불가능할 정도의 랜덤한 값을 생성해준다.
        String sessionId = UUID.randomUUID().toString();
        // 키값으로 UUID를 (세션id), value로 세션에 보관할 값을 저장
        sessionStore.put(sessionId, value);

        // 응답 쿠키 생성 -> 세션 id 이용
        Cookie mySessionCookie = new Cookie(SESSON_COOKIE_NAME, sessionId);
        // 클라이언트에게 전달해주기
        response.addCookie(mySessionCookie);
    }

    // 세션 조회
    public Object getSession(HttpServletRequest request) {
        // 쿠키 찾기
        Cookie sessionCookie = findCookie(request, SESSON_COOKIE_NAME);
        if (sessionCookie == null)
            return null;
        // 세션 저장소에서 쿠키의 값을 이용해서 꺼낸다 -> 쿠키에는 (name-value) 순으로 들어있었고,
        // value에 sessionId가 저장되어 있으니깐,
        // 세션 저장소에서 sessionId를 통해 쿠키 꺼내기.
        return sessionStore.get(sessionCookie.getValue());
    }

    // 세션 만료
    public void expire(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSON_COOKIE_NAME);
        if (sessionCookie != null)
            sessionStore.remove(sessionCookie.getValue());
    }

    private Cookie findCookie(HttpServletRequest request, String cookieName) {
        // 만약 쿠키가 없다면 null 리턴
        if(request.getCookies() == null)
            return null;
        // 배열을 stream으로 바꾼 다음 filter로 돌리기.
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElse(null);
    }


}
