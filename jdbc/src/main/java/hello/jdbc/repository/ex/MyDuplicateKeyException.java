package hello.jdbc.repository.ex;

// DB 관련 예외라는 계층을 만들기 위해 기존에 만들었던 예외를 상속하도록.
// 데이터 중복인 경우에 이 예외를 던져준다.
// 원래 SQLException 내부에 있는 errorCode를 확인하면 키 중복 오류를 확인할 수 있지만,
// 다시 또 SQLException을 사용하게 되면 리포지토리 -> 서비스로 예외 던지고, 또 다시 JDBC 기술에 의존하게 되니깐 순수성이 무너짐.
// 그러나 이렇게 하면 직접 만든 예외여서 특정 기술에 종속적이지 않게 된다!
public class MyDuplicateKeyException extends MyDbException{
    public MyDuplicateKeyException() {
    }

    public MyDuplicateKeyException(String message) {
        super(message);
    }

    public MyDuplicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDuplicateKeyException(Throwable cause) {
        super(cause);
    }

    public MyDuplicateKeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
