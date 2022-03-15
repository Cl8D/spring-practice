package hello.login.web.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Slf4j
@RestController
public class SessionInfoController {

    @GetMapping("/session-info")
    public String sessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null)
            return "세션이 없습니다.";

        // 세션 데이터 출력하기
        // session name=loginMember, value=Member(id=1, loginId=test, name=테스터, password=test!)
        session.getAttributeNames().asIterator()
                .forEachRemaining(name -> log.info("session name={}, value={}",
                        name, session.getAttribute(name)));

        // 세션 id 값으로, jsessionId를 의미함.
        // sessionId=510C6494E7A196BBEA1947A63F74AC0E
        log.info("sessionId={}", session.getId());

        // 세션의 유효 시간
        // maxInactiveInterval=1800 (30분)
        log.info("maxInactiveInterval={}", session.getMaxInactiveInterval());

        // 세션 생성 일시
        // creationTime=Tue Mar 15 19:25:53 KST 2022
        log.info("creationTime={}", new Date(session.getCreationTime()));

        // 세션과 연결된 사용자가 최근에 서버에 접근한 시간
        // 클라이언트에서 서버로 sessionId(JSESSiONID)를 요청한 경우에 갱신된다
        // lastAccessedTime=Tue Mar 15 19:40:44 KST 2022
        log.info("lastAccessedTime={}", new Date(session.getLastAccessedTime()));

        // 새로 생성된 세션인지, 클라이언트에서 서버로 sessionId를 요청해서 조회된 세션인지 여부
        // isNew=false
        log.info("isNew={}", session.isNew());

        return "세션 출력";
    }
}
