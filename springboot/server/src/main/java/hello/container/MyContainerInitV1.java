package hello.container;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

import java.util.Set;

/**
 * ServletContainerInitializer 덕분에 서버가 뜰 때 알아서 처음에 실행된다.
 */
public class MyContainerInitV1 implements ServletContainerInitializer {
    @Override
    public void onStartup(final Set<Class<?>> c, final ServletContext ctx) throws ServletException {
        System.out.println("MyContainerInitV1.onStartup");
        System.out.println("c = " + c);
        System.out.println("ctx = " + ctx);
    }
}
