package hello.proxy.app.v2;

/**
 * V2 : 인터페이스 - 구체 클래스 분리 x
 */
public class OrderRepositoryV2 {
    public void save(String itemId) {
        // 저장 로직
        if(itemId.equals("ex"))
            throw new IllegalArgumentException("예외 발생!");
        sleep(1000);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();;
        }
    }
}
