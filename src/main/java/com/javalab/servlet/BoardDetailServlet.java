package com.javalab.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.javalab.dao.BoardDAO;
import com.javalab.vo.BoardVO;

/**
 * 게시물 내용 보기 서블릿
 */
@WebServlet("/boardDetail")
public class BoardDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public BoardDetailServlet() {
        super();
    }

	/**
	 * 게시물 내용 조회
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 파라미터 추출
		int bno = Integer.parseInt(request.getParameter("bno"));
		// 업데이트 서블릿에서 실제 사용자가 아닌 경우 오류 메시지를 보내줌
		String message = request.getParameter("message");
		 
		// BoardDAO 객체 생성
		//BoardDAO boardDAO = new BoardDAO();
		BoardDAO boardDAO = BoardDAO.getInstance();
		
		// 1. 조회증가
		boardDAO.incrementHitNo(bno);
		
		// 2. 게시물 조회
		BoardVO boardVO = boardDAO.getBoard(bno);
		
		// request 영역에 저장
		request.setAttribute("boardVO", boardVO);
		
        if (message != null && !message.isEmpty()) {
            request.setAttribute("message", message);
        }
        
		// 게시물 내용보기 페이지 이동
		RequestDispatcher rd = request.getRequestDispatcher("/boardDetail.jsp");
		rd.forward(request, response);
	}

}
