package hello.proxy.app.v1;

/**
 * V1 : 인터페이스와 구체 클래스 분리
 */
public interface OrderRepositoryV1 {
    void save(String itemId);
}
