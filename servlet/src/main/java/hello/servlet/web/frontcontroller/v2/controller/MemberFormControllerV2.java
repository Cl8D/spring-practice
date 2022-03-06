package hello.servlet.web.frontcontroller.v2.controller;

import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v2.ControllerV2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MemberFormControllerV2 implements ControllerV2 {


    @Override
    public MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 단순히 myView 객체를 생성하여 뷰 이름만 넣고 반환을 해주면 된다.
        // v1에 비해 중복이 제거된 걸 확인할 수 있다.
        return new MyView("/WEB-INF/views/new-form.jsp");
    }
}
