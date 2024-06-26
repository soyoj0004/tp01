<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<%-- now : 현재 시간의 시분초를 now 변수에 세팅 --%>
<c:set var="now" value="<%= new java.util.Date() %>" />

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="<c:url value='/css/board.css' />?v=${now}" />
<script	src='<c:url value="/ckeditor/ckeditor.js" />'>
</script>
<script	src='<c:url value="/ckeditor/config.js" />'>
</script>
</head>
<body>
   <div class="container">
		<%-- 헤더부분 include 액션 태그 사용, c:url 사용금지, 경로 직접 지정해야함. --%>
		<jsp:include page="/common/header.jsp" />
		
        <main>
			<c:if test="${ not empty sessionScope.member }">
				<h3>답변 게시물 작성</h3>
				<form action="<c:url value='reply'/>" method="post">
				
					<input type="hidden" name="bno" value="<c:out value='${boardVO.bno }'/>" />
					<input type="hidden" name="replyGroup" value="<c:out value='${boardVO.replyGroup }'/>" />
					<input type="hidden" name="replyOrder" value="<c:out value='${boardVO.replyOrder }'/>" />
					<input type="hidden" name="replyIndent" value="<c:out value='${boardVO.replyIndent }'/>" />
				
					<div>
						<label for="title">제목</label>
						<input type="text" id="title" name="title" required>
					</div> 
					<div>
						<label for="title">작성자</label>
						<input type="text" id="memberId" name="memberId" 
								value="${sessionScope.member.memberId}" required readonly>					
					</div>
					<div>
						<label for="title">내용</label>
						<textarea id="content" name="content" cols="80" rows="10"></textarea>
						<script>CKEDITOR.replace('content');</script>
					</div> 
					<div>
						<input type="submit" value="저장">
						<input type="reset" value="다시쓰기">
					</div>	
				</form>
			</c:if>
			<c:if test="${ empty sessionScope.member }">
				<script>
					alert('회원만 게시물을 작성할 수 있습니다');
					window.location.href="${contextPath}/loginForm.jsp";
				</script>
			</c:if>	
		</main>
	</div>		
</body>
</html>