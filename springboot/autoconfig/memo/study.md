### 스프링 부트의 자동 구성 
- spring-boot-autoconfigure 모듈은 스프링 부트의 자동 구성을 담당한다.
```java
@AutoConfiguration(after = DataSourceAutoConfiguration.class)
@ConditionalOnClass({ DataSource.class, JdbcTemplate.class })
@ConditionalOnSingleCandidate(DataSource.class)
@EnableConfigurationProperties(JdbcProperties.class)
@Import({ DatabaseInitializationDependencyConfigurer.class, JdbcTemplateConfiguration.class,
		NamedParameterJdbcTemplateConfiguration.class })
public class JdbcTemplateAutoConfiguration {

}
```
- @AutoConfiguration
  after를 사용하면 자동 구성이 실행되는 순서를 지정할 수 있으며, DataSourceAutoConfiguration이 실행된 후에 JdbcTemplateAutoConfiguration이 실행된다.
  - DataSourceAutoConfiguration의 경우 DataSource를 자동으로 등록해준다.
- @ConditionalOnClass 
  - DataSource와 JdbcTemplate 클래스가 존재할 때만 자동 구성이 실행된다.
- @Import
  - 스프링에서 자바 설정 클래스를 가져와서 빈으로 등록할 때 사용한다.

- 스프링 부트에서 제공해주는 자동 구성 클래스들
  - JdbcTemplateAutoConfiguration -> JdbcTemplate
  - DataSourceAutoConfiguration -> DataSource
  - DataSourceTransactionManagerAutoConfiguration -> TransactionManager

----

### @Conditional
- @Conditional은 조건에 따라 빈을 등록할지 말지 결정할 수 있다.
- 스프링 부트의 Auto Configuration 에서 자주 사용되는 기능이다.
- Condition 인터페이스
```java
@FunctionalInterface
public interface Condition {

	boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);

}
```
- true를 반환하면 조건에 만족하여 동작하고, false를 반환하면 동작하지 않는다.
- ConditionContext: 스프링 컨테이너나 환경 정보 등을 담고 있다.
- AnnotatedTypeMetadata: 어노테이션에 대한 메타 정보들을 가지고 있다.

---

### 스프링 부트의 @Conditional 시리즈
- @ConditionalOnBean: 특정 빈이 존재할 때만 동작
- @ConditionalOnMissingBean: 특정 빈이 존재하지 않을 때만 동작
- @ConditionalOnClass: 특정 클래스가 존재할 때만 동작
- @ConditionalOnMissingClass: 특정 클래스가 존재하지 않을 때만 동작
- @ConditionalOnProperty: 특정 프로퍼티가 존재할 때만 동작
- @ConditionalOnResource: 특정 리소스가 존재할 때만 동작
- @ConditionalOnWebApplication: 웹 애플리케이션일 때만 동작
- @ConditionalOnNotWebApplication: 웹 애플리케이션이 아닐 때만 동작
- @ConditionalOnExpression: SpEL 표현식을 만족할 때만 동작
- 참고로, 단순한 @Conditional 어노테이션의 경우 스프링에서 제공하는 기능이다. 위의 어노테이션들은 부트가 확장하여 제공하는 기능들.
