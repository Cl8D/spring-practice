package hello.container;

import hello.servlet.HelloServlet;
import jakarta.servlet.ServletContext;


public class AppInitV1Servlet implements AppInit {
    @Override
    public void onStartup(final ServletContext servletContext) {
        System.out.println("AppInitV1Servlet.onStartup");

        /**
         * Register Servlet
         */
        servletContext
                .addServlet("helloServlet", new HelloServlet())
                .addMapping("/hello-servlet");
    }
}
