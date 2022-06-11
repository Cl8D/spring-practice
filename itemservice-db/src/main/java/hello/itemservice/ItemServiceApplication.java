package hello.itemservice;

import hello.itemservice.config.*;
import hello.itemservice.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;


// 설정 파일!
//@Import(MemoryConfig.class)
//@Import(JdbcTemplateV1Config.class)
//@Import(JdbcTemplateV2Config.class)
//@Import(JdbcTemplateV3Config.class)
@Import(MyBatisConfig.class)
// 컨트롤러만 컴포넌트 스캔을 사용해주기. (나머지는 수동 등록했음!)
// 컴포넌트 스캔의 경로를 hello.itemservice.web의 하위로 지정하였음
@SpringBootApplication(scanBasePackages = "hello.itemservice.web")
@Slf4j
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


	// h2의 임베디드 모드 - h2는 자바로 개발되어 있기 때문에 JVM 내에서 메모리 모드로 동작이 가능하다.
	// test일 때는 dataSource를 직접 등록해서 사용하겠다는 것
//	@Bean
//	@Profile("test")
//	public DataSource dataSource() {
//		log.info("메모리 데이터베이스 초기화");
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//		dataSource.setDriverClassName("org.h2.Driver");
//		// 여기서 mem:db로 해줘야 임베디드 모드로 동작하는 h2를 사용할 수 있다.
//		// close_delay는 db 커넥션 연결이 모두 끊어지면 db도 종료되는데, 이를 방지하는 설정
//		dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
//		dataSource.setUsername("sa");
//		dataSource.setPassword("");
//		return dataSource;
//		// 그러나, 메모리 db는 애플리케이션 종료 시 함께 사라지기 때문에
//		// 애플리케이션 실행 시점에 데이터베이스 테이블도 새로 만들어줘야 한다!
//		// 이를 위해 SQL 스크립트를 작성해보자. (스프링부트가 이 스크립트를 실행해서 db를 초기화해줌!)
//	}

	// 근데 사실, 스프링 부트는 db에 대한 설정이 없으면 알아서 임베디드 데이터베이스를 사용한다!

}
