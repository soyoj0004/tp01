package com.javalab.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.javalab.dao.BoardDAO;
import com.javalab.vo.BoardVO;
import com.javalab.vo.MemberVO;

/**
 * 
 */
@WebServlet("/reply")
public class ReplyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ReplyServlet() {
		super();
	}

	/**
	 * 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
						throws ServletException, IOException {
		// 세션확인
		HttpSession ses = request.getSession();
		MemberVO memberVO = (MemberVO)ses.getAttribute("member");

		int bno = 0;
		if(memberVO == null) {
			String contextPath = request.getContextPath();
			response.sendRedirect(contextPath + "/loginForm.jsp");
			return;	// 더이상 다음 코드를 실행하지 말고 종료 지시
		}else {
			bno = Integer.parseInt(request.getParameter("bno"));
		}

		// BoardDAO 객체 생성
		BoardDAO boardDAO = BoardDAO.getInstance();

		// 원 게시물 조회 
		BoardVO boardVO = boardDAO.getBoard(bno);

		// 원 게시물을 request 영역에 저장
		request.setAttribute("boardVO", boardVO);

		// 답글 등록 페이지 이동
		RequestDispatcher rd = request.getRequestDispatcher("/boardReplyForm.jsp");
		rd.forward(request, response);

	}

	/**
	 * 답변 게시물 등록
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("ReplyServlet doPost");
		
		// message body 파라미터 인코딩
		request.setCharacterEncoding("utf-8");
		
		// 세션확인해서 로그인 안했으면 로그인폼으로 이동
		HttpSession ses = request.getSession();
		MemberVO memberVO = (MemberVO)ses.getAttribute("member");
		if(memberVO == null) {
			String contextPath = request.getContextPath();
			response.sendRedirect(contextPath + "/loginForm.jsp");
			return;	// 더이상 다음 코드를 실행하지 말고 종료 지시		
		}

		// 파라미터를 받을 임시변수
		int bno = 0; // 원글 게시물 번호
		String title = "";	//답글 제목
		String content = ""; // 답글 내용
		String memberId = ""; // 답글 작성자
		int replyGroup = 0;
		int replyOrder = 0;
		int replyIndent = 0;
		
		// 게시물 작성폼에서 파라미터 전달
		if(request.getParameter("bno") != null) {
			bno = Integer.parseInt(request.getParameter("bno"));
		}
		if(request.getParameter("title") != null) {
			title = request.getParameter("title");
		}
		if(request.getParameter("content") != null) {
			content = request.getParameter("content");
		}
		if(request.getParameter("replyGroup") != null) {
			replyGroup = Integer.parseInt(request.getParameter("replyGroup"));
		}
		if(request.getParameter("replyOrder") != null) {
			replyOrder = Integer.parseInt(request.getParameter("replyOrder"));
		}
		if(request.getParameter("replyIndent") != null) {
			replyIndent = Integer.parseInt(request.getParameter("replyIndent"));
		}
		
		// 전달받은 파라미터로 BoardVO객체 저장
		BoardVO boardVO = new BoardVO();
		boardVO.setBno(bno);
		boardVO.setTitle(title);
		boardVO.setContent(content);
		boardVO.setMemberId(memberVO.getMemberId());
		boardVO.setReplyGroup(replyGroup);
		boardVO.setReplyOrder(replyOrder);
		boardVO.setReplyIndent(replyIndent);
		
		// 답변글 저장
	    BoardDAO boardDAO = BoardDAO.getInstance();
	    int row = boardDAO.insertReplyBoard(boardVO);
	    
	    if(row > 0) {	// 게시물 정상 등록
	    	String contextPath = request.getContextPath();
	    	response.sendRedirect(contextPath + "/boardList");	// 게시물 목록 이동
	    }else {
	    	request.setAttribute("error", "게시물 작성에 실패했습니다.");
	    	RequestDispatcher rd = request.getRequestDispatcher("/boardReplyForm.jsp");
	    	rd.forward(request, response);	    	
	    }
	}

}
