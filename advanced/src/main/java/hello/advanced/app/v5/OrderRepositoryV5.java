package hello.advanced.app.v5;

import hello.advanced.trace.callback.TraceTemplate;
import hello.advanced.trace.logtrace.LogTrace;
import hello.advanced.trace.template.AbstractTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryV5 {
    private final TraceTemplate template;

    public OrderRepositoryV5(LogTrace trace) {
        this.template = new TraceTemplate(trace);
    }

    public void save(String itemId) {
        // 람다를 사용해서 기존보다 코드가 훨씬 더 간결해짐
        template.execute("OrderRepository.save()", () -> {
            // 저장 로직
            // 아이템 id가 ex면 예외 발생
            if (itemId.equals("ex")){
                throw new IllegalStateException("예외 발생");
            }
            // 상품을 저장하는데 걸리는 시간이 1초 정도라고 가정
            sleep(1000);
            return null;

        });
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 사실, 아무리 최적화를 해도 원본 코드를 수정해야 한다는 점이 있다.
     * 원본 코드 없이 로그 추적기를 적용해보자! -> 프록시 시용하기.
     *
     * +) xxxTemplate의 경우 대부분 템플릿 콜백 패턴이 적용된 예시라고 생각하자!
     * restTemplate나 뭐 그런 거 ㅎㅎ
     */
}
