package hello.advanced.trace.code;

import lombok.extern.slf4j.Slf4j;

// 템플릿 메서드 패턴
@Slf4j
public abstract class AbstractTemplate {
    // 변하지 않는 부분에 대한 로직을 다 몰아둔 형태 = 이게 템플릿이 되는 것
    public void execute() {
        long startTime = System.currentTimeMillis();
        //비즈니스 로직 실행
        call(); // 변하는 부분에 대해서 적용 -> 자식 클래스에서 상속 및 오버라이딩을 통해 처리해주기
        //비즈니스 로직 종료
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime={}", resultTime);
    }

    protected abstract void call();
}
