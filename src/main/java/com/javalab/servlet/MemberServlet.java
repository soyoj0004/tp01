package com.javalab.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.javalab.dao.MemberDAO;
import com.javalab.vo.MemberVO;

/**
 * 회원 가입폼 제공 및 등록 처리
 * - doGet : 회원가입 jsp 화면 제공
 * - doPost : 회원 등록(저장) 처리
 */
@WebServlet("/member")
public class MemberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public MemberServlet() {
        super();
    }
	/**
	 * 회원 가입 화면 제공
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("MeberServlet doGet");
		// 회원 가입 화면으로 이동
		RequestDispatcher rd = request.getRequestDispatcher("/memberForm.jsp");
		rd.forward(request, response);
	}

	/**
	 * 회원 가입 처리
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("MeberServlet doPost");
		
		request.setCharacterEncoding("utf-8");
		
		// 회원 가입 폼에서 전달된 파라미터 추출
		String memberId = request.getParameter("memberId"); 
		String pwd = request.getParameter("password"); 
		String name = request.getParameter("name"); 
		String email = request.getParameter("email"); 
		
		MemberVO memberVO = new MemberVO(memberId, pwd, name, email);

		MemberDAO memberDAO = new MemberDAO();
		int row = memberDAO.insertMember(memberVO);		
		
		if(row > 0) {	// 회원등록 성공
			System.out.println("회원가입 성공, 로그인 페이지로 이동");
			
			// 컨텍스트패스(경로) 얻기
			String contextPath = request.getContextPath();
			
			// 로그인 페이지로 이동(로그인폼을 띄워주는 서블릿 호출)
			response.sendRedirect(contextPath + "/login");	// 로그인 서블릿 호출			
		}else {	// 회원가입중 오류 발생
			System.out.println("회원가입 실패, 회원가입 페이지로 이동");
			RequestDispatcher rd = request.getRequestDispatcher("/memberForm.jsp");
			rd.forward(request, response);
		}
	}	// end post	
}
