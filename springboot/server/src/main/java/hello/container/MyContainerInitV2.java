package hello.container;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HandlesTypes;

import java.util.Set;

@HandlesTypes({AppInit.class})
public class MyContainerInitV2 implements ServletContainerInitializer  {
    @Override
    public void onStartup(final Set<Class<?>> c, final ServletContext ctx) throws ServletException {
        System.out.println("MyContainerInitV2.onStartup");
        // AppInit 인터페이스를 구현한 클래스 정보가 넘어온다.
        // c = [class hello.container.AppInitV1Servlet]
        System.out.println("c = " + c);
        System.out.println("ctx = " + ctx);

        for (Class<?> appInitClass : c) {
            try {
                // 받은 클래스 정보를 바탕으로 객체 생성해주기
                final AppInit appInit = (AppInit) appInitClass.getDeclaredConstructor().newInstance();
                appInit.onStartup(ctx);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
