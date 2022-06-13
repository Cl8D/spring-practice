package hello.advanced.trace.threadlocal.code;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class FieldServiceTest {
    private FieldService fieldService = new FieldService();

    @Test
    void field() {
        log.info("main start");

        // 똑같은 건데 하나는 이렇게, 하나는 람다식으로 쓴 거
        Runnable userA = new Runnable() {
            @Override
            public void run() {
                fieldService.logic("userA");
            }
        };

        Runnable userB = () -> {
            fieldService.logic("userB");
        };

        // 스레드 생성
        Thread threadA = new Thread(userA); // userA 로직
        threadA.setName("thread-A");

        Thread threadB = new Thread(userB); // userB 로직
        threadB.setName("thread-B");

        threadA.start(); //A 실행
//        sleep(2000); //동시성 문제 발생X
         sleep(100); //동시성 문제 발생O

        threadB.start(); //B실행
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

}

/*
    - 2초 -
    15:36:44.625 [main] INFO hello.advanced.trace.threadlocal.code.FieldServiceTest - main start
    15:36:44.638 [thread-A] INFO hello.advanced.trace.threadlocal.code.FieldService - 저장 name=userA -> nameStore=null
    15:36:45.658 [thread-A] INFO hello.advanced.trace.threadlocal.code.FieldService - 조회 nameStore=userA
    15:36:46.645 [thread-B] INFO hello.advanced.trace.threadlocal.code.FieldService - 저장 name=userB -> nameStore=userA
    15:36:47.658 [thread-B] INFO hello.advanced.trace.threadlocal.code.FieldService - 조회 nameStore=userB
    15:36:49.654 [main] INFO hello.advanced.trace.threadlocal.code.FieldServiceTest - main exit

    - 1초 이내 -
    15:47:02.946 [main] INFO hello.advanced.trace.threadlocal.code.FieldServiceTest - main start
    15:47:02.953 [thread-A] INFO hello.advanced.trace.threadlocal.code.FieldService - 저장 name=userA -> nameStore=null
    15:47:03.054 [thread-B] INFO hello.advanced.trace.threadlocal.code.FieldService - 저장 name=userB -> nameStore=userA
    15:47:03.964 [thread-A] INFO hello.advanced.trace.threadlocal.code.FieldService - 조회 nameStore=userB
    15:47:04.060 [thread-B] INFO hello.advanced.trace.threadlocal.code.FieldService - 조회 nameStore=userB
    15:47:06.069 [main] INFO hello.advanced.trace.threadlocal.code.FieldServiceTest - main exit
    --> 저장에 걸리는 시간이 1초인데 (logic() 메서드 내부 구현) 1초가 지나기 전에 또 호출을 해버리면
    userA가 저장이 되기 전에 userB가 새로 저장이 되기 때문에 조회 결과가 둘 다 userB가 되는 것이다.
    : 이런 식으로, 여러 스레드가 동시에 같은 인스턴스의 필드 값을 변경하면서 발생하는 문제가 '동시성 문제'이다.

    cf) 지역변수는 스레드마다 다른 메모리 영역이 할당되지만,
    같은 인스턴스의 필드(싱글톤에서), 혹은 static 같은 공용 필드에서 동시성 문제가 자주 발생한다.
    -> 단순히 값을 읽을 때는 발생하지 않으며, 값을 쓸 때 발생한다.

    ==> 싱글톤 객체 필드를 사용하면서 동시성 문제 해결하기 = "스레드 로컬" 사용!
 */