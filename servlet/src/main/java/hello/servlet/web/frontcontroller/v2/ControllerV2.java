package hello.servlet.web.frontcontroller.v2;

import hello.servlet.web.frontcontroller.MyView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ControllerV2 {
    // 기존의 ControllerV1과 거의 유사하지만, 반환을 MyView를 하도록!
    MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
