package com.javalab.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.javalab.vo.BoardVO;

/**
 * 게시물 관련 DAO - 게시물 등록 - 게시물 목록/상세조회 - 게시물 수정/삭제
 */
public class BoardDAO {
   private DataSource dataSource;
   private Connection conn = null; // 커넥션 객체
   private PreparedStatement pstmt = null; // 쿼리문 생성 및 실행 객체
   private ResultSet rs = null; // 쿼리 실행 결과 반환 객체

   // BoarDAO를 싱글톤 패턴으로 단 한개의 객체만 생성하기 위한 변수 선언
   private static BoardDAO instance;

   /**
    * private 생성자 - 밖에서는 절대로 호출할 수 없다. 즉, 밖에서는 객체 생성 불가.
    */
   private BoardDAO() {
      try {
         Context ctx = new InitialContext();
         dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/oracle");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * BoardDAO 자신의 인스턴스(객체)를 반환해주는 메소드 - 이 메소드는 최초로 호출 될 때는 아직 객체가 생성되어 있지 않으므로 그 때
    * 단 한 번 객체로 생성된다. - 그 다음 부터는 이미 생성되어 있는 그 객체의 참조 주소값을 반환한다. - static 객체 생성 없이도
    * 밖에서 호출할 수 있도록 하기 위해서
    */
   public static BoardDAO getInstance() {
      if (instance == null) {
         instance = new BoardDAO();
      }
      return instance;
   }

   /**
    * 게시물 등록 처리 메소드 - 트랜잭션 적용 : 다음 bno를 조회하는 SQL과 insert구문을 실행하는 코드를 하나의 작업 단위로 묶음
    */
   public int insertBoard(BoardVO boardVO) {
      int row = 0;

      try {
         conn = dataSource.getConnection();

         conn.setAutoCommit(false); // 트랜잭션 시작(오토 커밋 중지)

         // 1. 새글 등록시 사용할 bno, reply_group 번호 조회
         String getNextVal = "select seq_board.nextval as nextval from dual ";
         pstmt = conn.prepareStatement(getNextVal);
         rs = pstmt.executeQuery();
         int nextVal = 0;
         if (rs.next()) {
            nextVal = rs.getInt("nextval");
         }

         // 2. 위에서 얻은 nextVal 사용하여 bno, reply_group에 값세팅
         // String sql = "Insert into board (bno, title, content, member_id, reply_group,
         // reply_order, reply_indent, file_name) ";
         // sql += " values (?,?,?,?,?,0,0, ?) "; // 맨뒤 ? fileName

         // 2. 첨부 파일의 유무에 따른 쿼리문 변경
         StringBuilder sql = new StringBuilder();
         sql.append(
               "Insert into board (bno, title, content, member_id, rating, reply_group, reply_order, reply_indent");

         if (boardVO.getFileName() != null && !boardVO.getFileName().isEmpty()) {
            sql.append(", file_name");
         }

         sql.append(") values (?, ?, ?, ?, ?, ?, 0, 0");

         if (boardVO.getFileName() != null && !boardVO.getFileName().isEmpty()) {
            sql.append(", ?");
         }

         sql.append(" )");

         pstmt = conn.prepareStatement(sql.toString());

         pstmt.setInt(1, nextVal); // bno
         pstmt.setString(2, boardVO.getTitle()); // 제목
         pstmt.setString(3, boardVO.getContent()); // 내용
         pstmt.setString(4, boardVO.getMemberId()); // 작성자
         pstmt.setInt(5, boardVO.getRating()); // 평점
         pstmt.setInt(6, nextVal); // reply_group

         if (boardVO.getFileName() != null && !boardVO.getFileName().isEmpty()) {
            pstmt.setString(7, boardVO.getFileName()); // filename
         }

         row = pstmt.executeUpdate(); // 저장처리
         conn.commit(); // 트랜잭션 커밋

      } catch (SQLException e) {
         e.printStackTrace();
         try {
            conn.rollback();
         } catch (SQLException e1) {
            e1.printStackTrace();
         }
         System.out.println("게시물 등록중 오류가 발생했습니다.");
      } finally {
         closeResource(); // 자원해제(반납)
      }
      return row;
   }

   /**
    * 답글 등록 처리 메소드[트랜잭션] - 기존 답글들의 replyOrder + 1 증가 시키는 작업 - 답변 게시물 등록 작업
    */
   public int insertReplyBoard(BoardVO boardVO) {
      int row = 0;

      try {
         conn = dataSource.getConnection();
         conn.setAutoCommit(false); // 트랜잭션 시작(오토컷밋 중지)

         // 1단계 기존 답글들의 replyOrder 를 +1씩 증가
         String updateQuery = "update board set reply_order = reply_order + 1 " + "where reply_group = ? "
               + "and reply_order > ?";
         pstmt = conn.prepareStatement(updateQuery);
         pstmt.setInt(1, boardVO.getReplyGroup()); // 부모의 그룹번호
         pstmt.setInt(2, boardVO.getReplyOrder()); // 부모의 그룹내 순번(order)
         row = pstmt.executeUpdate();
         if (row > 0) {
            System.out.println("답글 저장전 기존 답글들의 reply_order 업데이트 성공");
         }

         // 2단계 답글 등록
         String sql = "insert into board(bno, title, content, member_id, reply_group, reply_order, reply_indent) ";
         sql += " values(seq_board.nextval, ?, ?, ?, ?, ?, ?) ";
         // PreparedStatment 객체 얻기
         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, boardVO.getTitle()); // 제목
         pstmt.setString(2, boardVO.getContent()); // 내용
         pstmt.setString(3, boardVO.getMemberId()); // 작성자
         pstmt.setInt(4, boardVO.getReplyGroup()); // 부모의 replyGroup을 넣어줌(그래야 부모와 묶임)
         pstmt.setInt(5, boardVO.getReplyOrder() + 1); // 부모의 replyOrder + 1을 넣어줌(그래야 부모 바로 밑에 위치함)
         pstmt.setInt(6, boardVO.getReplyIndent() + 1); // 부모의 replyIndent + 1을 넣어줌(부모 보다 한칸더 들여써짐)

         row = pstmt.executeUpdate(); // 저장처리
         if (row > 0) {
            conn.commit();
         } else {
            conn.rollback();
         }
      } catch (Exception e) {
         e.printStackTrace();
         try {
            conn.rollback();
         } catch (SQLException e1) {
            e1.printStackTrace();
         }
         System.out.println("답글 등록중 오류가 발생했습니다.");
      } finally {
         closeResource(); // 자원해제(반납)
      }
      return row;
   }

   /**
    * 게시물 목록 조회 메소드
    */
   /*
    * public List<BoardVO> getBoardList(){ List<BoardVO> boardList = new
    * ArrayList<>(); try { conn = dataSource.getConnection();
    * 
    * String sql =
    * "select bno, title, content, member_id, reg_date, hit_no from board"; pstmt =
    * conn.prepareStatement(sql); rs = pstmt.executeQuery();
    * 
    * while(rs.next()) { BoardVO boardVO = new BoardVO();
    * boardVO.setBno(rs.getInt("bno")); boardVO.setTitle(rs.getString("title"));
    * boardVO.setContent(rs.getString("content"));
    * boardVO.setMemberId(rs.getString("member_id"));
    * boardVO.setRegDate(rs.getDate("reg_date"));
    * boardVO.setHitNo(rs.getInt("hit_no")); boardList.add(boardVO); } }catch
    * (SQLException e) { System.out.println("getBoardList ERR : " +
    * e.getMessage()); e.printStackTrace(); // 콘솔에 오류 }finally { closeResource(); }
    * return boardList; }
    */

   public List<BoardVO> getBoardList(BoardVO boardVO) {
      List<BoardVO> boardList = new ArrayList<>();

      int start = 0; // 시작 게시물 번호
      int end = 0; // 끝 게시물 번호

      /*
       * [첫게시물과 끝 게시물 구하는 공식] 1. row_number() 함수 사용 - 시작 게시물 번호 : (사용자가요청한페이지 -1) *
       * 한페이지에보여줄 게시물수 + 1 - 끝 게시물 번호 : 시작번호 + 한페이지에 보여줄 게시물 수 -1 2. Fetch ~ Next 구문
       * 사용 - 건너띌 게시물수 : (사용자가요청한페이지 -1) * 한페이지에보여줄 게시물수 - 가져올 게시물수 : 한페이지에 보여줄 게시물 수
       */
      // 건너띌 게시물수
      start = (Integer.parseInt(boardVO.getPageNum()) - 1) * boardVO.getListCount();
      // 가져올 게시물수
      end = boardVO.getListCount();

      try {
         StringBuffer sql = new StringBuffer(); // String 유사한 문자열 객체

//         sql.append("select a.bno, a.title, a.member_id, a.hit_no, a.reg_date ");
//         sql.append("from( ");
//         sql.append("select b.*, row_number() over(order by b.reg_date asc) row_num ");
//         sql.append("from board b ");
//         sql.append(")a ");
//         sql.append("where a.row_num between ? and ?");

         sql.append("select bno, title, content, rating, member_id, reg_date, hit_no ");
         sql.append("from board ");
         sql.append("order by reply_group desc, reply_order asc, reply_indent asc ");
         sql.append("offset ? rows  ");
         sql.append("fetch next ? rows only ");

         conn = dataSource.getConnection(); // 커넥션 얻어오기
         pstmt = conn.prepareStatement(sql.toString());
         pstmt.setInt(1, start);
         pstmt.setInt(2, end);

         rs = pstmt.executeQuery();

         while (rs.next()) {
            BoardVO board = new BoardVO();
            board.setBno(rs.getInt("bno"));
            board.setTitle(rs.getString("title"));
            board.setContent(rs.getString("content"));
            board.setRating(rs.getInt("rating"));
            board.setMemberId(rs.getString("member_id"));
            board.setRegDate(rs.getDate("reg_date"));
            board.setHitNo(rs.getInt("hit_no"));
            System.out.println("BoardDAO board.toString() : " + board.toString());
            boardList.add(board);
         }
      } catch (SQLException e) {
         System.out.println("getBoardList ERR : " + e.getMessage());
         e.printStackTrace(); // 콘솔에 오류
      } finally {
         closeResource();
      }
      return boardList;
   }

   /**
    * 검색 기능 메소드
    */
   public List<BoardVO> searchBoardList(String keyword) {
      List<BoardVO> boardList = new ArrayList<>();
      try {
         conn = dataSource.getConnection();

         /*
          * 2. 기존 sql문 영화 제목으로만 검색 가능하게 boarddList.jsp 33줄 수정 String sql =
          * "select bno, title, content, member_id, reg_date, hit_no from board " +
          * " where title like ? or content like ? ";
          */
         // 제목으로만 검색 기능
         String sql = "select bno, title, content, member_id, reg_date, hit_no from board " + " where title like ? ";
         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, "%" + keyword + "%");
         rs = pstmt.executeQuery();

         while (rs.next()) {
            BoardVO boardVO = new BoardVO();
            boardVO.setBno(rs.getInt("bno"));
            boardVO.setTitle(rs.getString("title"));
            boardVO.setContent(rs.getString("content"));
            boardVO.setMemberId(rs.getString("member_id"));
            boardVO.setRegDate(rs.getDate("reg_date"));
            boardVO.setHitNo(rs.getInt("hit_no"));
            boardList.add(boardVO);
         }
      } catch (SQLException e) {
         System.out.println("getBoardList ERR : " + e.getMessage());
         e.printStackTrace(); // 콘솔에 오류
      } finally {
         closeResource();
      }
      return boardList;
   }

   /**
    * 게시물 내용 보기
    */
   public BoardVO getBoard(int bno) {
      System.out.println("getBoard");
      BoardVO boardVO = null;

      try {
         conn = dataSource.getConnection();

         // 게시물 조회 쿼리
         String sql = "select bno, title, content, member_id, rating, reg_date, hit_no, " + "file_name "
               + "from board where bno=? ";
         pstmt = conn.prepareStatement(sql); // PreparedStatement 객체 얻기(쿼리문 전달)
         pstmt.setInt(1, bno);
         rs = pstmt.executeQuery(); // 게시물 1건 반환

         if (rs.next()) {
            boardVO = new BoardVO();
            boardVO.setBno(rs.getInt("bno"));
            boardVO.setTitle(rs.getString("title"));
            boardVO.setContent(rs.getString("content")); // 게시물 내용
            boardVO.setMemberId(rs.getString("member_id"));
            boardVO.setRating(rs.getInt("rating")); // 평점
            boardVO.setRegDate(rs.getDate("reg_date"));
            boardVO.setHitNo(rs.getInt("hit_no")); // 조회수
            boardVO.setFileName(rs.getString("file_name")); // 첨부 파일 이름 세팅
         }
      } catch (SQLException e) {
         System.out.println("getBoard() ERR => " + e.getMessage());
         e.printStackTrace();
      } finally {
         closeResource();
      }
      return boardVO;
   }

   /**
    * 게시물 조회수 증가 메소드
    */
   public void incrementHitNo(int bno) {
      try {
         conn = dataSource.getConnection();

         // 게시물의 조회수 증가 쿼리
         // 조회수 증가 쿼리문 실행
         String updateHitSql = "UPDATE board SET hit_no = hit_no + 1 WHERE bno = ?";
         pstmt = conn.prepareStatement(updateHitSql);
         pstmt.setInt(1, bno);
         pstmt.executeUpdate();
         pstmt.close();
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         closeResource();
      }
   }

   /**
    * 게시물 수정
    */
   /*
    * public int updateBoard(BoardVO boardVO) { int row = 0; try { conn =
    * dataSource.getConnection(); String sql =
    * "update board set title=?, content=? where bno=?" ;
    * 
    * pstmt = conn.prepareStatement(sql); // PreparedStatement 객체 얻기(쿼리문 전달)
    * pstmt.setString(1, boardVO.getTitle()); // title pstmt.setString(2,
    * boardVO.getContent()); // content pstmt.setInt(3, boardVO.getBno()); // bno
    * 
    * row = pstmt.executeUpdate(); // 쿼리문 실행 영향 받은 행수 반환 }catch (SQLException e) {
    * e.printStackTrace(); }finally { closeResource(); } return row; }
    */
   public int updateBoard(BoardVO boardVO) {
      int row = 0;
      try {
         conn = dataSource.getConnection();

         // SQL 문 작성 및 실행
         StringBuilder sql = new StringBuilder();
         sql.append("UPDATE board SET title = ?, content = ?, rating = ?");

         if (boardVO.getFileName() != null && !boardVO.getFileName().isEmpty()) {
            sql.append(", file_name = ?");
         }

         sql.append(" WHERE bno = ?");

         pstmt = conn.prepareStatement(sql.toString());
         pstmt.setString(1, boardVO.getTitle()); // title
         pstmt.setString(2, boardVO.getContent()); // content
         pstmt.setInt(3, boardVO.getRating()); // title

         if (boardVO.getFileName() != null && !boardVO.getFileName().isEmpty()) {
            pstmt.setString(5, boardVO.getFileName()); // fileName
            pstmt.setInt(4, boardVO.getBno()); // bno
         } else {
            pstmt.setInt(4, boardVO.getBno()); // bno
         }

         row = pstmt.executeUpdate(); // 쿼리문 실행 영향 받은 행수 반환
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         closeResource();
      }
      return row;
   }

   /**
    * 게시물 삭제
    */
   public int deleteBoard(int bno) {
      int row = 0;
      try {
         conn = dataSource.getConnection();
         String sql = "delete board where bno=?"; // ? 동적파라미터
         pstmt = conn.prepareStatement(sql); // PreparedStatement 객체 얻기(쿼리문 전달)
         pstmt.setInt(1, bno); // bno
         row = pstmt.executeUpdate(); // 쿼리문 실행 영향 받은 행수 반환
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         closeResource();
      }
      return row;
   }

   /**
    * 데이터베이스 관련 자원 해제(반납) 메소드
    */
   private void closeResource() {
      try {
         if (rs != null)
            rs.close();
         if (pstmt != null)
            pstmt.close();
         if (conn != null)
            conn.close(); // 컨넥션 반납
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * 전체 게시물의 갯수를 조회하는 메소드
    */
   public int getAllCount() {
      int totalCount = 0;
      StringBuffer sql = new StringBuffer();
      sql.append("select count(*) as totalCount ");
      sql.append(" from board");
      try {
         conn = dataSource.getConnection();
         pstmt = conn.prepareStatement(sql.toString());
         rs = pstmt.executeQuery();
         if (rs.next()) {
            totalCount = rs.getInt("totalCount");
            System.out.println("BoardDAO totlalCount : " + totalCount);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return totalCount;
   }

   private Connection getConnection() {
      // TODO Auto-generated method stub
      return null;
   }
}
