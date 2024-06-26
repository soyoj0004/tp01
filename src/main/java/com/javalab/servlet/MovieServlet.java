package com.javalab.servlet;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.javalab.dao.MovieDAO;
import com.javalab.vo.MovieVO;

/**
 * 영화 정보 관리 서블릿
 */
@WebServlet("/movie")
public class MovieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private MovieDAO movieDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        movieDAO = new MovieDAO(); // MovieDAO 초기화
    }

    /**
     * 영화 정보 조회 및 처리 (GET 요청)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("getMovieById".equals(action)) {
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            MovieVO movie = movieDAO.getMovieById(movieId);
            if (movie != null) {
                request.setAttribute("movie", movie); // 영화 정보를 request에 저장
                request.getRequestDispatcher("/movieDetail.jsp").forward(request, response); // 상세 페이지로 포워딩
            } else {
                response.getWriter().println("해당 영화를 찾을 수 없습니다.");
            }
        } else if ("getAllMovies".equals(action)) {
            List<MovieVO> movies = movieDAO.getAllMovies();
            request.setAttribute("movies", movies); // 모든 영화 정보를 request에 저장
            request.getRequestDispatcher("/movieList.jsp").forward(request, response); // 목록 페이지로 포워딩
        } else {
            response.getWriter().println("유효하지 않은 요청입니다.");
        }
    }

    /**
     * 영화 정보 등록 또는 수정 (POST 요청)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("addMovie".equals(action)) {
            // 폼에서 입력된 영화 정보 파라미터 가져오기
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            String title = request.getParameter("title");
            String director = request.getParameter("director");
            String[] actors = request.getParameterValues("actors");
            String genre = request.getParameter("genre");
            Date releaseDate = Date.valueOf(request.getParameter("releaseDate"));
            int runningTime = Integer.parseInt(request.getParameter("runningTime"));
            double rating = Double.parseDouble(request.getParameter("rating"));
            String posterUrl = request.getParameter("posterUrl");
            String trailerUrl = request.getParameter("trailerUrl");
            String synopsis = request.getParameter("synopsis");

            // MovieVO 객체 생성
            MovieVO movieVO = new MovieVO(movieId, title, director, actors, genre, releaseDate, runningTime, rating,
                    posterUrl, trailerUrl, synopsis);

            // 영화 정보 등록 처리
            int result = movieDAO.insertMovie(movieVO);
            if (result > 0) {
                response.sendRedirect(request.getContextPath() + "/movie?action=getAllMovies"); // 등록 후 목록 페이지로 리다이렉트
            } else {
                response.getWriter().println("영화 등록에 실패했습니다.");
            }
        } else if ("updateMovie".equals(action)) {
            // 폼에서 입력된 영화 정보 파라미터 가져오기
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            String title = request.getParameter("title");
            String director = request.getParameter("director");
            String[] actors = request.getParameterValues("actors");
            String genre = request.getParameter("genre");
            Date releaseDate = Date.valueOf(request.getParameter("releaseDate"));
            int runningTime = Integer.parseInt(request.getParameter("runningTime"));
            double rating = Double.parseDouble(request.getParameter("rating"));
            String posterUrl = request.getParameter("posterUrl");
            String trailerUrl = request.getParameter("trailerUrl");
            String synopsis = request.getParameter("synopsis");

            // MovieVO 객체 생성
            MovieVO movieVO = new MovieVO(movieId, title, director, actors, genre, releaseDate, runningTime, rating,
                    posterUrl, trailerUrl, synopsis);

            // 영화 정보 수정 처리
            int result = movieDAO.updateMovie(movieVO);
            if (result > 0) {
                response.sendRedirect(request.getContextPath() + "/movie?action=getMovieById&movieId=" + movieId); // 수정 후 상세 페이지로 리다이렉트
            } else {
                response.getWriter().println("영화 정보 수정에 실패했습니다.");
            }
        } else {
            response.getWriter().println("유효하지 않은 요청입니다.");
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        // 리소스 정리 (예: 데이터베이스 연결 해제 등)
    }
}
