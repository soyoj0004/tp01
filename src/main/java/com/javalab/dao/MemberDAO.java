package com.javalab.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.javalab.vo.MemberVO;

/**
 * 회원가입 처리 DAO
 */
public class MemberDAO {
	private DataSource dataSource;
	private Connection conn = null; // 커넥션 객체
	private PreparedStatement pstmt = null; // 쿼리문 생성 및 실행 객체
	private ResultSet rs = null; // 쿼리 실행 결과 반환 객체

	public MemberDAO() {
		try {
			Context ctx = new InitialContext();
			dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/oracle");
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 회원 가입 처리 메소드
	 */
	public int insertMember(MemberVO memberVO) {
		int row = 0;
		try{
			conn = dataSource.getConnection();
			
			String sql = "insert into member(member_id, password, name, email) values(?, ?, ?, ?)";
			// PreparedStatment 객체 얻기
			pstmt = conn.prepareStatement(sql);
			// 쿼리문의 ? 에 파라미터 세팅
			pstmt.setString(1, memberVO.getMemberId());
			pstmt.setString(2, memberVO.getPassword());
			pstmt.setString(3, memberVO.getName());
			pstmt.setString(4, memberVO.getEmail());
			
			row = pstmt.executeUpdate();	// 저장처리
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("회원 저장중 오류가 발생했습니다.");
		}finally{
			closeResource();	// 자원해제(반납)
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
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
