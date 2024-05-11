package hello.embedded;

import hello.spring.HelloConfig;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class EmbeddedTomcatSpringMain {
    public static void main(String[] args) throws LifecycleException {
        System.out.println("Starting EmbeddedTomcatSpringMain");

        Tomcat tomcat = new Tomcat();
        final Connector connector = new Connector();
        connector.setPort(8080);
        tomcat.setConnector(connector);

        /**
         * Create Spring Container
         */
        final AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(HelloConfig.class);

        /**
         * Create Dispatcher Servlet
         * Connect With Spring container
         */
        final DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);

        /**
         * Register Dispatcher Servlet
         */
        final Context context = tomcat.addContext("", "/");
        // tomcat에 context를 추가해준 다음에 서블릿을 추가해줘야 한다.
        // 왜냐하면, 서블릿을 추가할 때 context를 찾아서 추가하기 때문이다.
        tomcat.addServlet("", "dispatcherServlet", dispatcherServlet);
        context.addServletMappingDecoded("/", "dispatcherServlet");

        tomcat.start();
    }
}
