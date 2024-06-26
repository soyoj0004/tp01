package com.javalab.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 로그아웃 서블릿
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public LogoutServlet() {
        super();
    }

	/**
	 * 로그아웃 메소드
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// request 객체에서 세션 객체 얻기
		HttpSession ses = request.getSession();
		// 세션에서 member라는 이름으로 저장된 객체 삭제
		ses.removeAttribute("member");
		// 세션 전체를 무효화
		ses.invalidate();
		// 로그인 페이지로 이동
		String contextPath = request.getContextPath();
		response.sendRedirect(contextPath + "/loginForm.jsp");	}
}
