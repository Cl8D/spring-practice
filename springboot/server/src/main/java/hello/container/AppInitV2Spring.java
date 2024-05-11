package hello.container;

import hello.spring.HelloConfig;
import jakarta.servlet.ServletContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class AppInitV2Spring implements AppInit{
    @Override
    public void onStartup(final ServletContext servletContext) {
        System.out.println("AppInitV2Spring.onStartup");

        /**
         * Create Spring Container
         */
        final AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(HelloConfig.class);

        /**
         * Create Dispatcher Servlet (Spring MVC가 제공한다) -> Front Controller
         * Connect With Spring Container
         * - HTTP 요청이 왔을 때 Dispatcher Servlet은 스프링 컨테이너에 등록된 빈들을 호출해준다.
         */
        final DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

        /**
         * Register Dispatcher Servlet
         */
        servletContext.addServlet("dispatcherV2", dispatcherServlet)
                .addMapping("/spring/*");

    }
}
