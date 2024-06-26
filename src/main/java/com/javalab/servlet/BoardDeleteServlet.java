package com.javalab.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.javalab.dao.BoardDAO;

/**
 * 게시물 삭제 서블릿
 */
@WebServlet("/boardDelete")
public class BoardDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public BoardDeleteServlet() {
        super();
    }

	/**
	 * 게시물 삭제 메소드
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 파라미터 인코딩
		request.setCharacterEncoding("utf-8");
		
		// 삭제할 게시물 번호 추출
		int bno = Integer.parseInt(request.getParameter("bno"));
		
		// DAO에 게시물 삭제 요청
		//BoardDAO boardDAO = new BoardDAO();
		BoardDAO boardDAO = BoardDAO.getInstance();
		
		int row = boardDAO.deleteBoard(bno);
		
		// 데이터베이스 작업 결과로 분기
		if(row > 0) {
			String contextPath = request.getContextPath();
			response.sendRedirect(contextPath + "/boardList");
		}else {
			request.setAttribute("error", "게시물 삭제에 실패했습니다.");
			RequestDispatcher rd = request.getRequestDispatcher("/boardDetail?bno=" + bno);
			rd.forward(request, response);
		}
	}

}
