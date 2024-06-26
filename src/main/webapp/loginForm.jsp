<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- 컨텍스트패스(진입점폴더) 변수 설정 -->
<c:set var="contextPath" value="${pageContext.request.contextPath }" />

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>loginForm.jsp</title>
	<style>
		div {
			margin: 5px;
		}
	</style>
</head>
<body>
	<h3>로그인</h3>
	<form action="<c:url value='/login'/>" method="post">
		<div>
			<label for="id">아이디:</label> 
			<input type="text" id="memberId" name="memberId" required>
		</div>
		<div>
			<label for="password">비밀번호:</label> 
			<input type="password"	id="password" name="password" required>
		</div>
		<div>
			<input type="submit" value="로그인"> 
			<input type="reset"	value="다시쓰기">
		</div>
		<div>
			<a href="${contextPath}/member">회원가입</a>
		</div>
	</form>
	<%-- 오류 메시지 출력 --%>
	<c:if test="${not empty error }">
		<span style="color: red;">${error }</span>
	</c:if>
</body>
</html>