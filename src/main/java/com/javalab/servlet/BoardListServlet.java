package com.javalab.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.javalab.dao.BoardDAO;
import com.javalab.util.PageNavigator;
import com.javalab.vo.BoardVO;

/**
 * 게시물 목록 서블릿
 */
@WebServlet("/boardList")
public class BoardListServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;

   public BoardListServlet() {
      super();
   }

   /**
    * 게시물 목록 조회
    */
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
      // 키워드 파라미터 추출
      String keyword = request.getParameter("keyword");

      // 사용자가 요청한 페이지(화면 하단의 페이지 번호 클릭했을때)
      String pageNum = request.getParameter("pageNum");

      // 처음 화면이 열릴 때는 기본적으로 1페이지가 보이도록 설정
      if (pageNum == null) {
         pageNum = "1";
      }

      BoardVO boardVO = new BoardVO();
      boardVO.setPageNum(pageNum);

      List<BoardVO> boardList = null;
      // 데이터베이스 전담 객체 생성
      // BoardDAO boardDAO = new BoardDAO();
      BoardDAO boardDAO = BoardDAO.getInstance();

      // 키워드 유무에 따른 분기
      if (keyword != null && !keyword.isEmpty()) {
         boardList = boardDAO.searchBoardList(keyword); // 검색기능 메소드 호출
      } else {
         boardList = boardDAO.getBoardList(boardVO); // getBoardList() 호출
      }

      // 전체 게시물 수 구하기
      int totalCount = boardDAO.getAllCount();

      // 이동해갈 페이지에서 사용할 수있게 request 영역 저장
      request.setAttribute("totalCount", totalCount); // 전체페이지 갯수
      request.setAttribute("pageNum", pageNum); // 요청 페이지 번호

      PageNavigator pageNavigator = new PageNavigator();
      // jsp 화면에 보여질 페이징 문자열 만들기
      String pageNums = pageNavigator.getPageNavigator(totalCount, // 전체 게시물수
            boardVO.getListCount(), // 한페이지에 보여줄 게시물수(10)
            boardVO.getPagerPerBlock(), // 페이지에 보여줄 페이지번호 갯수(10)
            Integer.parseInt(pageNum));
      // 디버깅 문자열
      System.out.println("pageNums : " + pageNums);

      // 페이징 문자열을 request영역에 저장
      request.setAttribute("page_navigator", pageNums);

      // request영역에 boardList 저장
      request.setAttribute("boardList", boardList);

      // 저장한 boardList 출력할 페이지인 boardList.jsp 이동
      // 여기서 "/" 슬래시는 웹 애플리케이션의 컨텍스트 루트를 말한다.(추가 주석)
      RequestDispatcher rd = request.getRequestDispatcher("/boardList.jsp");
      rd.forward(request, response);
   }
}