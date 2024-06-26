package com.javalab.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.javalab.dao.LoginDAO;
import com.javalab.vo.MemberVO;

/**
 * 로그인 화면(폼) 제공 및 로그인 처리 서블릿 - doGet : 로그인 화면 제공 - doPost : 로그인 처리 로직
 * - @WebServlet("/login") : 애노테이션이 서블릿의 호출명 설정 - /login : 서블릿 매핑은 웹 애플리케이션의
 * 컨텍스트 루트를 기준으로 경로를 지정한다. 절대경로를 말한다.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public LoginServlet() {
		super();
	}

	/**
	 * 로그인 화면 제공, 사용자를 로그인 화면으로 이동시킨다.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("login 서블릿의 doGet()");
		/*
		 * 1. RequestDispatcher - RequestDispatcher는 웹 애플리케이션 내에서 페이지를 이동할때 사용하는 도구 -
		 * 웹사이트 내에서 현재 페이지에서 다른 페이지로 요청을 전달 역할 - 현재 서블릿의 실행을 중단하고 지정된 페이지로 이동하거나 다른 서블릿을
		 * 호출할 수 있다. 2. getRequestDispatcher() - request 객체로 부터 RequestDispatcher 를 얻어주는
		 * 역할 3. forward(request, response) - 페이지를 실제로 이동하는 역할 - 요청을 다른 페이지로 이동해도 클라이언트는
		 * 알지못하며 URL 변경되지 않는다. 4. request 객체 저장한 정보를 이동해간 페이지에서 고스란히 사용할 수 있다. [요약]
		 * RequestDispatcher와 forward를 사용하면 사용자가 요청한 작업을 다른 페이지로 이동해서 처리하도록 할 수 있다.
		 */
		RequestDispatcher rd = request.getRequestDispatcher("/loginForm.jsp");
		rd.forward(request, response);
	}

	/**
	 * 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("login 서블릿의 doPost()");

		// message body 담겨오는 파라미터 인코딩
		request.setCharacterEncoding("utf-8");

		/*
		 * 웹브라우저에 응답할 정보 즉 MIME type, 문자 인코딩 설정 응답 헤더에 content-type과 문자 인코딩 방식을 설정한다.
		 * 그래야 클라이언트가 이 정보를 참고해서 올바른 렌더링할 수 있다. 현재 메소드에서 웹브라우저에 직접 내용을 쓰는 경우는 필수지만 그렇지
		 * 않을 경우에는 생략할 수 있다.
		 */
		response.setContentType("text/html; charset=utf-8");

		String memberId = request.getParameter("memberId");
		String pwd = request.getParameter("password");

		// DAO 영역의 메소드 호출
		LoginDAO loginDAO = new LoginDAO();
		MemberVO member = loginDAO.login(memberId, pwd);

		if (member != null) {
			// 세션에 사용자 저장
			HttpSession ses = request.getSession(); // 세션 객체 얻기
			ses.setAttribute("member", member); // 세션에 member라는 이름으로 사용자 자바빈즈 저장

			// 로그인 성공으로 main.jsp페이지로 이동
			String contextPath = request.getContextPath();
			response.sendRedirect(contextPath + "/main.jsp");
		} else { // 로그인 실패
			// 오류 메시지 세팅
			request.setAttribute("error", "아이디와 비밀번호를 확인하세요");

			// loginForm.jsp
			RequestDispatcher rd = request.getRequestDispatcher("/loginForm.jsp");
			rd.forward(request, response);
		}

	}
}
