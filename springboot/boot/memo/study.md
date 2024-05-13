- 스프링 부트에서 스프링 컨테이너 생성
  - ServletWebServerApplicationContextFactory

```java
@Override
public ConfigurableApplicationContext create(WebApplicationType webApplicationType) {
    return (webApplicationType != WebApplicationType.SERVLET) ? null : createContext();
}

private ConfigurableApplicationContext createContext() {
    if (!AotDetector.useGeneratedArtifacts()) {
        return new AnnotationConfigServletWebServerApplicationContext();
    }
    return new ServletWebServerApplicationContext();
}
```
- 스프링 부트가 applicationContext를 생성하는 코드를 볼 수 있다.

---

- 내장 톰캣이 만들어지는 부분
  - TomcatServletWebServerFactory
```java
@Override
public WebServer getWebServer(ServletContextInitializer... initializers) {
    if (this.disableMBeanRegistry) {
        Registry.disableRegistry();
    }
    Tomcat tomcat = new Tomcat();
    File baseDir = (this.baseDirectory != null) ? this.baseDirectory : createTempDir("tomcat");
    tomcat.setBaseDir(baseDir.getAbsolutePath());
    for (LifecycleListener listener : this.serverLifecycleListeners) {
        tomcat.getServer().addLifecycleListener(listener);
    }
    Connector connector = new Connector(this.protocol);
    connector.setThrowOnFailure(true);
    tomcat.getService().addConnector(connector);
    customizeConnector(connector);
    tomcat.setConnector(connector);
    tomcat.getHost().setAutoDeploy(false);
    configureEngine(tomcat.getEngine());
    for (Connector additionalConnector : this.additionalTomcatConnectors) {
        tomcat.getService().addConnector(additionalConnector);
    }
    prepareContext(tomcat.getHost(), initializers);
    return getTomcatWebServer(tomcat);
	}
```
- Tomcat 생성 후 Connector를 생성하여 연결해주는 것을 볼 수 있다.

---

- 스프링 부트로 인해서 생성된 jar 파일 압축 해제했을 때
  - META-INF
    - MANIFEST.MF : 여기에서 main class를 찾아 실행한다.
  - org.springframework/boot/loader
    - JarLauncher.class -> 스프링 부트의 main 실행 클래스
  - BOOT-INF
    - WAR에서 WEB-INF와 같은 역할 
    - classes
      - 개발자가 생성한 class 파일과 리소스 파일
    - libs
      - 외부 라이브러리의 jar 파일들
    - classpath.idx
      - 외부 라이브러리 경로
    - layers.idx
      - 스프링 부트의 구조 경로

- libs에 jar들이 또 들어있는데 어떻게 가능한 것일까?
  - 실행 가능 Jar (Executable Jar) 파일이기 때문에 가능하다.
  - 자바 표준이 아니다. 스프링 부트에서 자체적으로 정의한 구조

---

### 실행 가능 Jar
- java -jar를 실행하면 META-INF/MANIFEST.MF 파일을 바탕으로 main class를 찾는다.
- META-INF/MANIFEST.MF
  - Main-Class
    - JarLauncher를 포함하고 있는데, 이는 스프링 부트가 빌드 시에 넣어주는 클래스이다.
    - jar 파일 내의 jar를 읽기 위해서 존재하는 클래스라고 볼 수 있다.
    - 이 속성을 제외하고 나머지는 스프링 부트가 정의한 정보이다.
  - Start-Class
    - 실제로 우리가 만든 메인 함수의 경우 여기에 들어가 있다.
    - JarLauncher가 로드 작업을 끝내고 나면 이제 우리의 main 함수를 호출하게 된다. (JarLauncher가 실행해줌)
  - 그 외 정보들
    - Spring-Boot-Classes: 개발한 클래스 경로
    - Spring-Boot-Lib: 라이브러리 경로
    - Spring-Boot-Version: 스프링 부트 버전
    - Spring-Boot-Classpath-Index: 외부 라이브러리 모음
    - Spring-Boot-Layers-Index: 스프링 부트의 레이어 구조
