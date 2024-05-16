### 자동 구성 라이브러리 만들기
- 라이브러리화 하고 싶은 프로젝트를 jar 파일로 만들어주기
  - 이때, 자동 구성을 활용하기 위해서 @AutoConfiguration + imports 파일을 통해 자동 구성 대상이 되도록 만들었음
- 해당 jar 파일을 적용하고 싶은 프로젝트의 libs 폴더 하위에 삽입해주기
- build.gradle 파일에서 다음과 같이 load 해주기
```
implementation files('libs/memory-v2.jar')
```
- gradle refresh를 하게 되면 라이브러리를 로드해올 수 있게 된다.
-> 라이브러리에 대한 세부 정보를 알 필요가 없이, 로드만 해주면 간단하게 사용할 수 있게 되었다. (기존 project-v1 프로젝트와 비교하기)

---

### 실제로 스프링 부트에서 사용하는 imports
- org.springframework.boot.spring-boot-autoconfigure 라이브러리 하위를 보면 META-INF/spring 가 존재하며,
- 그 하위에 org.springframework.boot.autoconfigure.AutoConfiguration.imports 파일이 존재한다.
- 즉, 스프링 부트에서 제공하는 기본 자동 구성 라이브러리들은 이러한 방식으로 자동 구성이 이루어지는 것이다.

### 어떻게 동작하는가
- @SpringBootApplication -> @EnableAutoConfiguration -> @AutoConfigurationPackage -> @Import(AutoConfigurationImportSelector.class)
- 기본적으로 @Import에 설정 정보를 추가하는 방법은 총 2가지가 존재한다.
  - @Import(TargetClass.class) -> 로드하고 싶은 클래스를 명시적으로 지정하기
  - @Import(ImportSelector) -> 동적으로 대상을 지정해주기 (특정 조건에 따라서 선택한다는 등... 로직을 넣을 수 있음)
- AutoConfigurationImportSelector 가 하는 일이 결국 META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports 파일을 읽게 된다.
- 해당 파이르이 설정 정보가 스프링 컨테이너에 등록되어서 사용되는 것!
```java
public class AutoConfigurationImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        if (!isEnabled(annotationMetadata)) {
            return NO_IMPORTS;
        }
        AutoConfigurationEntry autoConfigurationEntry = getAutoConfigurationEntry(annotationMetadata);
        return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
    }
    
    protected AutoConfigurationEntry getAutoConfigurationEntry(AnnotationMetadata annotationMetadata) {
        if (!isEnabled(annotationMetadata)) {
            return EMPTY_ENTRY;
        }
        AnnotationAttributes attributes = getAttributes(annotationMetadata);
        List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
        configurations = removeDuplicates(configurations);
        Set<String> exclusions = getExclusions(annotationMetadata, attributes);
        checkExcludedClasses(configurations, exclusions);
        configurations.removeAll(exclusions);
        configurations = getConfigurationClassFilter().filter(configurations);
        fireAutoConfigurationImportEvents(configurations, exclusions);
        return new AutoConfigurationEntry(configurations, exclusions);
    }

    protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
        List<String> configurations = ImportCandidates.load(AutoConfiguration.class, getBeanClassLoader())
                .getCandidates();
        Assert.notEmpty(configurations,
                "No auto configuration classes found in "
                        + "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports. If you "
                        + "are using a custom packaging, make sure that file is correct.");
        return configurations;
    }
}
```
- 여기에서 List<String> configurations 의 값을 보면, imports 파일에 있는 클래스들이 들어가 있는 것을 확인할 수 있다.

---

- @AutoConfiguration의 순서도 지정해줄 수 있다. (before, after 활용)
- 내부에 @Configuration이 있기 때문에 설정 파일로서 마킹되어 있지만, 일반 스프링 설정과 라이프 사이클이 다르기 때문에 컴포넌트 스캔의 대상이 되면 안 된다.
- imports 파일을 활용하여 지정을 해줘야 한다.
```java
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {}
```
- 그래서 @SpringBootApplication을 보면 컴포넌트 스캔 어노테이션에서 제외할 클래스에 대해 AutoConfigurationExcludeFilter 요 친구가 존재한다. 
  - 이는 자동 구성 정보를 제외하기 위해서 사용하는 것이다.
