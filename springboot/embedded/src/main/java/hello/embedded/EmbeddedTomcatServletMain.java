package hello.embedded;

import hello.servlet.HelloServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

/**
 * Using Embedded Tomcat Server
 */
public class EmbeddedTomcatServletMain {
    public static void main(String[] args) throws LifecycleException {
        System.out.println("Starting EmbeddedTomcatServletMain");

        /**
         * Configuration Tomcat
         */
        Tomcat tomcat = new Tomcat();
        final Connector connector = new Connector();
        connector.setPort(8080);
        tomcat.setConnector(connector);

        /**
         * Register Servlet
         */
        final Context context = tomcat.addContext("", "/");
        tomcat.addServlet("", "helloServlet", new HelloServlet());
        context.addServletMappingDecoded("/hello-servlet", "helloServlet");
        tomcat.start();
    }
}
