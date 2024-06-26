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
				<h3>게시물 작성</h3>
				<form action="<c:url value='/board'/>" method="post" enctype="multipart/form-data">
					<div>
						<label for="title">영화 제목</label>
						<input type="text" id="title" name="title" required>
					</div> 
					<div>
						<label for="rating">평점</label>
						<input type="number" id="rating" name="rating" min="1" max="5">
					</div> 
					<div>
						<label for="title">리뷰 내용</label>
						<textarea id="content" name="content" cols="80" rows="10"></textarea>
						<script>CKEDITOR.replace('content');</script>
					</div> 
					<div>
						<label for="title">이미지 첨부</label>
						<input type="file" id="fileName" name="fileName" >			
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