package hello.advanced.trace.threadlocal.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadLocalService {
    // 값 저장: set(), 값 조회: get(), 값 제거: remove()
    private ThreadLocal<String> nameStore = new ThreadLocal<>();

    // 스레드로컬 -> 각 스레드마다 고유의 전용 보관소가 존재함. 거기에 내부 필드 저장 가능!
    public String logic(String name) {
        log.info("저장 name={} -> nameStore={}", name, nameStore.get());
        nameStore.set(name);
        sleep(1000);

        // 필드에 저장한 다음 1초 뒤에 조회하기
        log.info("조회 nameStore={}",nameStore.get());
        return nameStore.get();
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
