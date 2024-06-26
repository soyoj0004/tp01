<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<header class="header">
	<nav>
		<a href="#">회원제 게시판</a>
	</nav>
	<div class="user-info">
		<c:if test="${not empty sessionScope.member}">
			<p>${sessionScope.member.name}님</p>
			<a href="<c:url value='/logout'/>">Logout</a>
		</c:if>
		<c:if test="${empty sessionScope.member}">
			<a href="<c:url value='/login'/>">Login</a>
		</c:if>
	</div>
</header>