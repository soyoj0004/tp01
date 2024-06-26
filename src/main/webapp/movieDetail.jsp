<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.javalab.dao.MovieDAO" %>
<%@ page import="java.sql.*, java.util.*, com.javalab.vo.*" %>

<%
    String movieIdParam = request.getParameter("movieId");
    if (movieIdParam == null || movieIdParam.isEmpty()) {
        out.println("영화 ID가 제공되지 않았습니다.");
        return;
    }
    
    int movieId = Integer.parseInt(movieIdParam);
    MovieDAO movieDAO = new MovieDAO();
    MovieVO movie = movieDAO.getMovieById(movieId);
    if (movie == null) {
        out.println("해당 영화 정보를 찾을 수 없습니다.");
        return;
    }

    request.setAttribute("movie", movie);
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>${movie.title} 상세정보</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/movieDetail.css">
</head>
<body>
    <header>
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/main.jsp">홈</a></li>
                <li><a href="${pageContext.request.contextPath}/movieList.jsp">영화 목록</a></li>
                <li><a href="${pageContext.request.contextPath}/mypage.jsp">마이페이지</a></li>
                <li><a href="${pageContext.request.contextPath}/review.jsp">리뷰작성</a></li>
            </ul>
        </nav>
    </header>

    <div id="content">
        <h2>영화 상세 정보</h2>

        <div class="gallery">
            <c:choose>
                <c:when test="${movie.title == '괴물'}">
                    <img src="${pageContext.request.contextPath}/image/2006_1.jpg" alt="괴물 이미지 1">
                    <img src="${pageContext.request.contextPath}/image/2006_2.jpg" alt="괴물 이미지 2">
                    <img src="${pageContext.request.contextPath}/image/2006_3.jpg" alt="괴물 이미지 3">
                </c:when>
                <c:when test="${movie.title == '미션임파서블'}">
                    <img src="${pageContext.request.contextPath}/image/2023_1.jpg" alt="미션임파서블 이미지 1">
                    <img src="${pageContext.request.contextPath}/image/2023_1.jpg" alt="미션임파서블 이미지 2">
                    <img src="${pageContext.request.contextPath}/image/2023_1.jpg" alt="미션임파서블 이미지 3">
                </c:when>
                <c:when test="${movie.title == '헐크'}">
                    <img src="${pageContext.request.contextPath}/image/202312_1.jpg" alt="헐크 이미지 1">
                    <img src="${pageContext.request.contextPath}/image/202312_1.jpg" alt="헐크 이미지 2">
                    <img src="${pageContext.request.contextPath}/image/202312_1.jpg" alt="헐크 이미지 3">
                </c:when>
                <c:when test="${movie.title == '스파이더맨'}">
                    <img src="${pageContext.request.contextPath}/image/0401_1.png" alt="스파이더맨 이미지 1">
                    <img src="${pageContext.request.contextPath}/image/0401_1.png" alt="스파이더맨이미지 2">
                    <img src="${pageContext.request.contextPath}/image/0401_1.png" alt="스파이더맨 이미지 3">
                </c:when>
                <c:when test="${movie.title == '토르'}">
                    <img src="${pageContext.request.contextPath}/image/202322.jpg" alt="스파이더맨 이미지 1">
                    <img src="${pageContext.request.contextPath}/image/202322.jpg" alt="스파이더맨이미지 2">
                    <img src="${pageContext.request.contextPath}/image/202322.jpg" alt="스파이더맨 이미지 3">
                </c:when>
                <c:otherwise>
                    <img src="${pageContext.request.contextPath}/image/default1.jpg" alt="기본 이미지 1">
                    <img src="${pageContext.request.contextPath}/image/default2.jpg" alt="기본 이미지 2">
                    <img src="${pageContext.request.contextPath}/image/default3.jpg" alt="기본 이미지 3">
                </c:otherwise>
            </c:choose>
        </div>

        <div id="overview">
            <h3>영화 개요</h3>
            <p><strong>제목:</strong> ${movie.title}</p>
            <p><strong>감독:</strong> ${movie.director}</p>
            <p><strong>주연 배우:</strong> ${movie.actors}</p>
            <p><strong>장르:</strong> ${movie.genre}</p>
            <p><strong>개봉일:</strong> ${movie.releaseDate}</p>
            <p><strong>상영 시간:</strong> ${movie.runningTime} 분</p>
            <p><strong>평점:</strong> ${movie.rating}</p>
        </div>
    </div>

    <footer>
        <p>&copy; 2024 영화 정보 사이트. All rights reserved.</p>
    </footer>
</body>
</html>
