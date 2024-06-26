package com.javalab.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.javalab.dao.BoardDAO;
import com.javalab.vo.BoardVO;
import com.javalab.vo.MemberVO;

/**
 * 게시물 수정 서블릿 - doGet : 게시물 수정폼(화면) 제공 - doPst : 게시물 수정 처리
 */
@WebServlet("/boardUpdate")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
      maxFileSize = 1024 * 1024 * 10, // 10 MB
      maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class BoardUpdateServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;

   public BoardUpdateServlet() {
      super();
   }

   /**
    * 회원 수정폼 제공 - 수정화면으로 이동
    */
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
      // 전달된 bno 파라미터 추출
      int bno = Integer.parseInt(request.getParameter("bno"));

      // DAO 게시물 한개 조회
      BoardDAO boardDAO = BoardDAO.getInstance();
      BoardVO boardVO = boardDAO.getBoard(bno);

      // 로그인 여부 먼저 확인, 미로그인시 로그인 폼으로 이동
      MemberVO memberVO = null;
      HttpSession session = request.getSession(false); // 세션객체 얻기, 없으면 생성안함.
      if (session != null) {
         memberVO = (MemberVO) session.getAttribute("member");
      }
      if (memberVO == null) {
         String contextPath = request.getContextPath();
         response.sendRedirect(contextPath + "/login");
         return; // 더이상 다음 코드 실행 안함.
      }
      String memberId = memberVO.getMemberId(); // 세션에서 작성자 추출

      // 현재 로그인된 사용자가 해당 게시물의 작성자인지 확인, 아니면 상세보기로 강제이동
      if (boardVO == null || !boardVO.getMemberId().equals(memberId)) {
         String contextPath = request.getContextPath();
         //response.sendRedirect(contextPath + "/boardDetail?bno=" + bno);
            response.sendRedirect(contextPath + "/boardDetail?bno=" + bno + "&message=" + 
                    java.net.URLEncoder.encode("실제 작성자만 게시물을 수정할 수 있습니다", "UTF-8"));

         return; // 더이상 다음 코드 실행 안함.
      }

      // request 영역에 수정할 게시물 저장
      request.setAttribute("boardVO", boardVO);

      // 게시물 수정 화면으로 이동
      RequestDispatcher rd = request.getRequestDispatcher("/boardUpdateForm.jsp");
      rd.forward(request, response);
   }

   /**
    * 게시물 수정 처리 - 데이터베이스에 수정된 게시물 저장 - 코드 수정 : 저장하려고 하는 사용자와 디비에 저장된 실제 작성자 일치 여부
    * 확인
    */
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {

      // 1. 로그인 여부 먼저 확인, 미로그인시 로그인 폼으로 이동
      MemberVO memberVO = null;

      HttpSession session = request.getSession(false); // 세션객체 얻기, 없으면 생성안함.
      if (session != null) {
         memberVO = (MemberVO) session.getAttribute("member");
      }
      if (memberVO == null) {
         String contextPath = request.getContextPath();
         response.sendRedirect(contextPath + "/login");
         return; // 더이상 다음 코드 실행 안함.
      }
      String memberId = memberVO.getMemberId(); // 세션에서 작성자 추출

      // 파라미터 처리, 게시물 pk(bno) 추출
      // 여기서 추출한 bno로 데이터베이스 조회
      int bno = Integer.parseInt(request.getParameter("bno"));

      // 2. 디비에서 현재 게시물의 실제 작성자 조회
      BoardDAO boardDAO = BoardDAO.getInstance();
      BoardVO boardVO = boardDAO.getBoard(bno);

      System.out.println("업데이트 서블릿 디비에 저장된 작성자 : " + boardVO.getMemberId());

      // 2.1. 현재 로그인된 사용자가 해당 게시물의 작성자인지 확인, 아니면 상세보기로 강제이동
      if (boardVO == null || !boardVO.getMemberId().equals(memberId)) {
         String contextPath = request.getContextPath();
         // 게시물 상세 페이지로 이동
         //response.sendRedirect(contextPath + "/boardDetail?bno=" + bno);
            response.sendRedirect(contextPath + "/boardDetail?bno=" + bno + "&message=" + 
                    java.net.URLEncoder.encode("실제 작성자만 게시물을 수정할 수 있습니다", "UTF-8"));

         return; // 더이상 다음 코드 실행 안함.
      }

      // 메시지 바디 부분으로 전달되는 파라미터 인코딩
      request.setCharacterEncoding("UTF-8");

      // 3. 게시물 작성폼에서 전달된 파라미터 받기
      String title = request.getParameter("title"); // 제목
      String content = request.getParameter("content"); // 내용
      int rating = Integer.parseInt(request.getParameter("rating")); // 평점
      // Part 클래스 : 파일 업로드 처리, 파일 데이터를 받아옴
      // - enctype="multipart/form-data"로 전송된 데이터를 받아옴
      Part filePart = request.getPart("fileName"); // 첨부파일 이름 추출
      String fileName = null;

      // 4. 파일 업로드 처리 시작(첨부파일이 있을 경우)
      if (filePart != null && filePart.getSize() > 0) { // 첨부파일이 존재하면
         fileName = filePart.getSubmittedFileName(); // 파일명 추출
         if (fileName != null && !fileName.isEmpty()) {
            /*
             * getServletContext : GenericServlet 클래스의 메소드로 ServletContext 객체를 반환
             * ServletContext : - 웹 어플리케이션의 환경 정보를 가지고 있는 객체. - 예를들면 웹 어플리케이션의 루트 경로를 얻어낼 수
             * 있음. - getRealPath("/") : 웹 어플리케이션의 루트 경로를 갖고옴 - "upload" : 현재 웹 어플리케이션의 루트에서
             * upload 폴더의 실제 경로를 갖고옴 이클립스에서 미리 upload 폴더를 생성해놓아야함.
             */
            String uploadPath = getServletContext().getRealPath("/") + "upload"; // 파일 저장 경로
            // 위에서 만든 경로로 파일 객체 생성, 없으면 생성, 있으면 생성하지 않음
            // 생성된 파일 경로 객체는 파일을 저장할 경로를 나타냄
            // File 객체는 경로와 파일을 핸들링 할 수 있는 객체
            File uploadDir = new File(uploadPath);
            // upload 폴더가 없으면 생성
            if (!uploadDir.exists())
               uploadDir.mkdir();
            // File.separator : 파일 경로 구분자
            // 파일 저장 경로에 파일명으로 파일 저장, 물리적으로 파일을 저장
            filePart.write(uploadPath + File.separator + fileName);
         }
      } // end 첨부파일이 존재하면

      boardVO = new BoardVO(bno, title, content, memberId, rating);
      System.out.println("updateServlet boardVO : " + boardVO);

      // BoardVO객체를 DAO에 전달해서 수정
      boardDAO = BoardDAO.getInstance();
      int row = boardDAO.updateBoard(boardVO); // 게시물 수정 메소드 호출

      // row를 통해서 분기
      if (row > 0) { // 수정 성공
         // 컨텍스트패스
         String contextPath = request.getContextPath();
         // 페이지 이동
         response.sendRedirect(contextPath + "/boardDetail?bno=" + bno);
      } else { // 수정 실패
         // 오류 메시지 세팅
         request.setAttribute("error", "게시물 수정에 실패했습니다.");
         RequestDispatcher rd = request.getRequestDispatcher("/boardUpdate?bno=" + bno);
         rd.forward(request, response);
      }
   }

}
