package hello.advanced.trace.threadlocal.code;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ThreadLocalServiceTest {
    private ThreadLocalService threadLocalService = new ThreadLocalService();

    @Test
    void field() {
        log.info("main start");

        // 똑같은 건데 하나는 이렇게, 하나는 람다식으로 쓴 거
        Runnable userA = new Runnable() {
            @Override
            public void run() {
                threadLocalService.logic("userA");
            }
        };

        Runnable userB = () -> {
            threadLocalService.logic("userB");
        };

        // 스레드 생성
        Thread threadA = new Thread(userA); // userA 로직
        threadA.setName("thread-A");

        Thread threadB = new Thread(userB); // userB 로직
        threadB.setName("thread-B");

        threadA.start(); //A 실행
        sleep(100);

        threadB.start(); //B 실행
        sleep(3000); //메인 쓰레드 종료 대기
        log.info("main exit");
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
        이제 1초보다 짧아도 동시성 문제가 발생하지 않는다.  스레드마다 별도의 저장소를 가지니까!
        16:05:41.764 [main] INFO hello.advanced.trace.threadlocal.code.ThreadLocalServiceTest - main start
        16:05:41.781 [thread-A] INFO hello.advanced.trace.threadlocal.code.ThreadLocalService - 저장 name=userA -> nameStore=null
        16:05:41.898 [thread-B] INFO hello.advanced.trace.threadlocal.code.ThreadLocalService - 저장 name=userB -> nameStore=null
        16:05:42.844 [thread-A] INFO hello.advanced.trace.threadlocal.code.ThreadLocalService - 조회 nameStore=userA
        16:05:42.906 [thread-B] INFO hello.advanced.trace.threadlocal.code.ThreadLocalService - 조회 nameStore=userB
        16:05:44.904 [main] INFO hello.advanced.trace.threadlocal.code.ThreadLocalServiceTest - main exit
    */

}
