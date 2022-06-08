package hello.itemservice;

import hello.itemservice.config.*;
import hello.itemservice.repository.ItemRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;


// 설정 파일!
//@Import(MemoryConfig.class)
//@Import(JdbcTemplateV1Config.class)
//@Import(JdbcTemplateV2Config.class)
@Import(JdbcTemplateV3Config.class)
// 컨트롤러만 컴포넌트 스캔을 사용해주기. (나머지는 수동 등록했음!)
// 컴포넌트 스캔의 경로를 hello.itemservice.web의 하위로 지정하였음
@SpringBootApplication(scanBasePackages = "hello.itemservice.web")
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

	@Bean
	// "local"이라는 profile이 사용되는 경우 testDataInit을 스프링 빈에 등록해준다.
	// 초기 데이터를 만들어서 저장해주는 bean임

	// 여기서 profile 정보의 경우 appication.properties에 있다.
	// 얘는 /src/main의 (주로) main()이 실행될 때 동작하는 스프링 설정으로, 본 프로젝트에서는 spring.profiles.active=local 일케 설정해놨기 때문에
	// local이라는 이름의 프로필로 동작한다. 그래서 이 함수가 실행될 때마다 같이 실행되는 것
	// 만약 설정 안 하면 보통 default 프로필이 실행된다
	// No active profile set, falling back to 1 default profile: "default" (이거 되게 많이 봤었는데 이 의미였다...!)

	// 참고로, 테스트 실행 시에는 properties가 test로 되어 있어서 얘가 실행되지 않는다! (test-resource 밑에도 프로퍼티 파일 있음!)
	// 테스트 때 실행되면 카운트 등 할 때 잘못될수도 있어서...!
	@Profile("local")
	public TestDataInit testDataInit(ItemRepository itemRepository) {
		return new TestDataInit(itemRepository);
	}

}
