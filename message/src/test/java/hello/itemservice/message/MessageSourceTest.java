package hello.itemservice.message;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class MessageSourceTest {
    // springboot는 자동으로 messageSource를 등록해준다.
    // 기본이랑 우리가 만들었던 messages 설정 파일도 함께 등록한다.
    @Autowired
    MessageSource ms;

    @Test
    void helloMessage() {
        // 메시지 코드로 hello를 입력하였다.
        // locale 정보를 null로 설정했기 때문에 basename에서 설정한 기본 이름 메시지 파일을 조회한다.
        // 우리는 basename이 messages 이기 때문에 messages.properties에서 데이터를 조회한다.
        String result = ms.getMessage("hello", null, null);
        assertThat(result).isEqualTo("안녕");
    }

    // 만약 메시지가 없는 경우에는 noSuchMessageException 예외가 발생한다.
    // (우리는 no_code를 안 만들었으니까)
    @Test
    void notFoundMessageCode() {
        assertThatThrownBy(() -> ms.getMessage("no_code", null, null))
                .isInstanceOf(NoSuchMessageException.class);
    }

    // 그러나, 메시지가 없어도 기본 메시지를 사용하게 되면 기본 메시지가 반환된다. (옵션: defaultMessage)
    // 즉, 메시지 찾고 > 없으면 기본 메시지에서 찾는 형태
    @Test
    void notFoundMessageCodeDefaultMessage() {
        String result = ms.getMessage("no_code", null, "기본 메시지", null);
        assertThat(result).isEqualTo("기본 메시지");
    }

    @Test
    void argumentMessage() {
        // hello.name=안녕 {0}이 있을 때,
        // {0} 부분은 매개변수를 전달하여 치환할 수 있다.
        // 여기서 Spring을 args로 전달하였기 때문에, 최종적으로 안녕 Spring이 나오게 된다.
        String result = ms.getMessage("hello.name", new Object[]{"Spring"}, null);
        assertThat(result).isEqualTo("안녕 Spring");
    }

    @Test
    void defaultLang() {
        // 여기서 locale 정보가 없기 때문에 messages를 사용하고
        assertThat(ms.getMessage("hello", null, null))
                .isEqualTo("안녕");
        // 여기서는 locale 정보는 있지만, message_ko가 없기 때문에 그냥 messages를 사용한다.
        assertThat(ms.getMessage("hello", null, Locale.KOREA))
                .isEqualTo("안녕");
    }

    @Test
    void enLang() {
        // locale 정보가 English이기 때문에 messages_en을 찾아서 사용한다.
        assertThat(ms.getMessage("hello", null, Locale.ENGLISH))
                .isEqualTo("hello");
    }
}
