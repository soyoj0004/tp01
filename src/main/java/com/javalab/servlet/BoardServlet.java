package com.javalab.servlet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.javalab.dao.BoardDAO;
import com.javalab.vo.BoardVO;
import com.javalab.vo.MemberVO;

/**
 * 게시물 등록폼 제공 및 등록 처리
 * - doGet : 게시물 등록 jsp 화면 제공
 * - doPost : 게시물 등록(저장) 처리
 */
@WebServlet("/board") // /board : url pattern
public class BoardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(BoardServlet.class.getName());

    public BoardServlet() {
        super();
    }

    /**
     * 게시물 등록 화면 제공
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("BoardServlet doGet");
        // 게시물 등록 화면으로 이동
        RequestDispatcher rd = request.getRequestDispatcher("/boardInsertForm.jsp");
        rd.forward(request, response);
    }

    /**
     * 게시물 등록 처리 - 코드 수정, 로그인을 가장 먼저 확인
     * - 파일 업로드 처리 : Apache commons fileupload 라이브러리 사용 버전
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("BoardServlet doPost");

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
        String memberId = memberVO.getMemberId();

        // 2. 파라미터 처리

        // 2.1. message body 파라미터 인코딩
        request.setCharacterEncoding("utf-8");

        // 3. 파일 업로드 처리 시작
        String title = null;
        String content = null;
        String fileName = null;
        int rating = 0;

        // 업로드 폴더 경로 설정 및 생성
        String uploadPath = getServletContext().getRealPath("/") + "upload";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        // 파일 업로드 설정
        DiskFileItemFactory factory = new DiskFileItemFactory(); // 파일 업로드를 위한 객체 생성
        factory.setRepository(uploadDir);   // 업로드 경로 설정
        factory.setSizeThreshold(1024 * 1024); // 1MB   // 업로드 사이즈 설정

        ServletFileUpload upload = new ServletFileUpload(factory); // 파일 업로드를 위한 객체 생성
        upload.setFileSizeMax(1024 * 1024 * 10); // 10 MB           // 파일 업로드 사이즈 설정
        upload.setSizeMax(1024 * 1024 * 15); // 15 MB               // 전체 업로드 사이즈 설정

        try {
            List<FileItem> items = upload.parseRequest(request); // request에서 파일을 추출
            for (FileItem item : items) {   // 파일을 하나씩 처리
                if (item.isFormField()) {   // 일반 폼 데이터인 경우, title, content
                    if (item.getFieldName().equals("title")) {
                        title = item.getString("utf-8");
                    } else if (item.getFieldName().equals("content")) {
                        content = item.getString("utf-8");
                    } else if (item.getFieldName().equals("rating")) {
                       rating = Integer.parseInt(item.getString("utf-8"));
                    }
                } else {    // 파일인 경우, fileName
                    if (item.getFieldName().equals("fileName") && item.getSize() > 0) { // 파일이 있을 때만 업로드
                        fileName = item.getName();  // 파일명
                        File uploadFile = new File(uploadPath + File.separator + fileName); // 파일 객체 생성
                        item.write(uploadFile); // 파일 업로드
                        logger.info("Uploaded file: " + uploadFile.getAbsolutePath() + " (" + item.getSize() + " bytes)");
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "파일 업로드 중 오류 발생", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생했습니다.");
            return;
        }

      BoardVO board = new BoardVO(title, content, memberId, rating, fileName);

        // BoardDAO 호출
        BoardDAO boardDAO = BoardDAO.getInstance();
        int row = boardDAO.insertBoard(board);

        if (row > 0) { // 게시물 정상 등록
            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/boardList"); // 임시 이동
        } else {
            request.setAttribute("error", "게시물 작성에 실패했습니다.");
            // /boardInsertForm.jsp 에서 슬래시를 붙이면 루트로 감, 슬래시를 제거하면 현재 경로에서 찾음
            RequestDispatcher rd = request.getRequestDispatcher("/boardInsertForm.jsp");
            rd.forward(request, response);
        }
    } // end post   
}
