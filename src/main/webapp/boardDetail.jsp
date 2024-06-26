<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page import="java.sql.*, java.util.*, com.javalab.vo.*"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<%-- now : 현재 시간의 시분초를 now 변수에 세팅 --%>
<c:set var="now" value="<%=new java.util.Date()%>" />

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>boardDetail.jsp</title>
<link rel="stylesheet" type="text/css"
	href="<c:url value='/css/board.css' />?v=${now}" />
</head>
<body>
	<c:if test="${not empty message}">
		<script>
			alert('${message}');
		</script>
	</c:if>
	<div class="container">
		<%-- 헤더부분 include 액션 태그 사용, c:url 사용금지, 경로 직접 지정해야함. --%>
		<jsp:include page="/common/headerDetail.jsp" />
		<main>
			<table border="1">
				<tr>
					<th>게시물번호</th>
					<td><c:out value="${boardVO.bno }" /></td>
				</tr>
				<tr>
					<th>제목</th>
					<td><c:out value="${boardVO.title }" /></td>
				</tr>
				<tr>
					<th>내용</th>
					<td>${boardVO.content }</td>
				</tr>
				<tr>
					<th>평점</th>
					<td><c:out value="${boardVO.rating }" /></td>
				</tr>
				<tr>
					<th>작성자</th>
					<td><c:out value="${boardVO.memberId }" /></td>
				</tr>
				<tr>
					<th>작성일자</th>
					<td><fmt:formatDate value="${boardVO.regDate}"
							pattern="yyyy-MM-dd HH:mm:ss" /></td>
				</tr>
				<tr>
					<th>조회수</th>
					<td><c:out value="${boardVO.hitNo }" /></td>
				</tr>
				<c:if test="${not empty boardVO.fileName}">
					<tr>
						<th>첨부 파일</th>
						<td><img src="<c:url value='/upload/${boardVO.fileName}'/>"
							alt="첨부 이미지"> <br> <a
							href="<c:url value='/upload/${boardVO.fileName}'/>"
							download="${boardVO.fileName}">다운로드</a></td>
					</tr>
				</c:if>
			</table>
			<div>
				<!-- 3. 수정 로그인 했을 때 안 했을 떄  -->
				<br> <a href="<c:url value='/boardList'/>">목록</a> <a
					href="<c:url value='/reply'/>?bno=${boardVO.bno}">답글작성</a>
				<c:choose>
					<c:when test="${not empty sessionScope.member}">
						<a href="<c:url value='/boardUpdate'/>?bno=${boardVO.bno}">수정</a>
						<form action="<c:url value='/boardDelete'/>" method="post">
							<input type="hidden" name="bno" value="${boardVO.bno}"> <input
								type="submit" value="삭제"
								onclick="return confirm('정말 삭제하시겠습니까?');">
						</form>
					</c:when>
				</c:choose>
			</div>
		</main>
	</div>
</body>
</html>
