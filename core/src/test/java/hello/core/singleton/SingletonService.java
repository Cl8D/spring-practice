package hello.core.singleton;

public class SingletonService {
    // 자기 자신을 static으로 선언하면 class level로 딱 1개만 생성하게 된다.
    private static final SingletonService instance = new SingletonService();

    // 조회하는 코드, 얘로만 꺼낼 수가 있게 된다.
    public static SingletonService getInstance() {
        return instance;
    }

    // 외부에서 new를 통해 임의로 만드는 것을 막기 위해 private로 생성자를 선언한다
    private SingletonService() {

    }

    public void logic() {
        System.out.println("싱글톤 객체 로직 호출");
    }
}
