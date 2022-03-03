package jpabook.jpashop2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 이 어노테이션이 있으면 얘가 있는 패키지 + 하위 패키지까지 다 컴포넌트 스캔의 대상이 된다.
@SpringBootApplication
public class Jpashop2Application {

	public static void main(String[] args) {

		SpringApplication.run(Jpashop2Application.class, args);
	}


}
