package com.javalab.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.javalab.vo.MemberVO;

/**
 * DAO(Data Access Object)
 *  - 로그인 관련 업무에서 데이터베이스 관련된 메소드만 모듈화한 클래스로서
 *    데이터베이스와 관련된 모든 메소드를 갖고 있다.
 *  - 서블릿에 중복으로 존재하던 보일러 플레이트 코드를 한곳으로 집약시켰다.
 *  - 서블릿에서는 DAO에 데이터베이스 관련된 업무를 요청하고 해당 결과를 
 *    반환 받아서 다음 처리를 진행한다.  
 */
public class LoginDAO {

	private DataSource dataSource;	// 커넥션 풀 객체 Type
	private Connection conn = null; // 커넥션 객체
	private PreparedStatement pstmt = null; // 쿼리문 생성 및 실행 객체
	private ResultSet rs = null; // 쿼리 실행 결과 반환 객체

	/**
	 * 생성자
	 * 1. JNDI(java naming and directory interface)
	 *  - 네이밍 인터페이스 : 이름으로 특정 자운에 대한 정보를 참조하는 방법.
	 *  - InitialContext : 기본 네이밍 컨텍스트를 제공하는 클래스, 이 객체를 통해서
	 *    초기 네이밍 컨텍스트 객체를 생성
	 * 2. java:comp/env :  네이밍 컨텍스트에서 애플리케이션의 환경변수 루트 경로를 말한다.
	 */
	public LoginDAO() {
		try {
			// 커넥션 풀을 참조하기위한 환경변수(네이밍컨텍스트) 정보를 가지고 있는 객체
			Context ctx = new InitialContext();	
			dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/oracle"); // 커넥션풀
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 로그인 관련 데이터베이스 처리
	 * - 파라미터로 전달받은 아이디/비밀번호로 데이터베이스에서 해당 사용자 조회해서
	 *   객체로 만들고 호출한 곳으로 반환.
	 */
	public MemberVO login(String memberId, String pwd) {
		System.out.println("LoginDAO login() 메소드");
		
		MemberVO member = null;
		try {
			conn = dataSource.getConnection();
			
			String sql = "SELECT member_id, name, email FROM member WHERE member_id = ? AND password = ?";
			pstmt = conn.prepareStatement(sql); // PreparedStatement 객체 얻기(쿼리문 전달)
			pstmt.setString(1, memberId); // 첫번째 ? 표에 파라미터 세팅
			pstmt.setString(2, pwd); // 두번째 ? 표에 파라미터 세팅
			rs = pstmt.executeQuery(); // 쿼리문 실행해서 결과 전달 받기
			
			if (rs.next()) { // 결과가 있으면 즉, 로그인 성공
				member = new MemberVO(); // 자바빈즈 객체 생성 -> 생성된 객체를 세션에 보관
				member.setMemberId(rs.getString("member_id")); // 자바빈즈에 데이터베이스 조회 값 세팅
				member.setName(rs.getString("name"));
				member.setEmail(rs.getString("email"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeResource(); 	// 자원해제 메소드 호출
		}
		return member; // 조회한 객체 반환
		
	}	// end of login
	
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
