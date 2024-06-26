<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    
<!-- 컨텍스트패스(진입점폴더) 변수 설정 -->
<c:set var="contextPath" value="${pageContext.request.contextPath }" />
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Index.jsp</title>
</head>
<body>
	<h3>여기는 index page</h3>
	
	<p><a href="${contextPath}/login">로그인</a></p>
	<!-- c:url : 컨텍스트패스를 자동으로 넣어준다. -->
	<p><a href="<c:url value='/login'/>">로그인</a></p>


</body>
</html>