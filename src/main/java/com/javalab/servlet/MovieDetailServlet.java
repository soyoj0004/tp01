package com.javalab.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.javalab.dao.MovieDAO;
import com.javalab.vo.MovieVO;

@WebServlet("/moviedetail")
public class MovieDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 파라미터에서 movieId 가져오기
        int movieId = Integer.parseInt(request.getParameter("movieId"));
        
        // MovieDAO 인스턴스 생성
        MovieDAO movieDAO = new MovieDAO();
        
        // MovieDAO를 이용해 해당 movieId에 해당하는 영화 정보 가져오기
        MovieVO movie = movieDAO.getMovieById(movieId);
        
        // 영화 정보를 request 속성에 저장
        request.setAttribute("movie", movie);
        
        // moviedetail.jsp로 포워드
        RequestDispatcher dispatcher = request.getRequestDispatcher("/moviedetail.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
