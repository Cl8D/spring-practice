package hello.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 운영 중 로그 레벨 변경하기
 * POST /actuator/loggers/{name}
 * {
 *     "configuredLevel": "DEBUG"
 * }
 */
@Slf4j
@RestController
public class LogController {
    @GetMapping("/log")
    public String log() {
        log.trace("trace log");
        log.debug("debug log");
        log.info("info log");
        log.warn("warn log");
        log.error("error log");
        return "ok";
    }
}
