package com.javalab.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import com.javalab.vo.MovieVO;

public class MovieDAO {
    private DataSource dataSource;

    public MovieDAO() {
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/oracle");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public MovieVO getMovieById(int movieId) {
        MovieVO movie = null;
        String sql = "SELECT * FROM movie WHERE movie_id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    movie = new MovieVO();
                    movie.setMovieId(rs.getInt("movie_id"));
                    movie.setTitle(rs.getString("title"));
                    movie.setDirector(rs.getString("director"));
                    movie.setActors(rs.getString("actors").split(","));
                    movie.setGenre(rs.getString("genre"));
                    movie.setReleaseDate(rs.getDate("release_date"));
                    movie.setRunningTime(rs.getInt("running_time"));
                    movie.setRating(rs.getDouble("rating"));
                    movie.setPosterUrl(rs.getString("poster_url"));
                    movie.setTrailerUrl(rs.getString("trailer_url"));
                    movie.setSynopsis(rs.getString("synopsis"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movie;
    }

    public List<MovieVO> getAllMovies() {
        List<MovieVO> movieList = new ArrayList<>();
        String sql = "SELECT * FROM movie";
        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                MovieVO movie = new MovieVO();
                movie.setMovieId(rs.getInt("movie_id"));
                movie.setTitle(rs.getString("title"));
                movie.setDirector(rs.getString("director"));
                movie.setActors(rs.getString("actors").split(","));
                movie.setGenre(rs.getString("genre"));
                movie.setReleaseDate(rs.getDate("release_date"));
                movie.setRunningTime(rs.getInt("running_time"));
                movie.setRating(rs.getDouble("rating"));
                movie.setPosterUrl(rs.getString("poster_url"));
                movie.setTrailerUrl(rs.getString("trailer_url"));
                movie.setSynopsis(rs.getString("synopsis"));
                movieList.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movieList;
    }

    public int insertMovie(MovieVO movieVO) {
        int result = 0;
        String sql = "INSERT INTO movie (movie_id, title, director, actors, genre, release_date, running_time, rating, poster_url, trailer_url, synopsis) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, movieVO.getMovieId());
            pstmt.setString(2, movieVO.getTitle());
            pstmt.setString(3, movieVO.getDirector());
            pstmt.setString(4, String.join(",", movieVO.getActors()));
            pstmt.setString(5, movieVO.getGenre());
            pstmt.setDate(6, movieVO.getReleaseDate());
            pstmt.setInt(7, movieVO.getRunningTime());
            pstmt.setDouble(8, movieVO.getRating());
            pstmt.setString(9, movieVO.getPosterUrl());
            pstmt.setString(10, movieVO.getTrailerUrl());
            pstmt.setString(11, movieVO.getSynopsis());
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int updateMovie(MovieVO movieVO) {
        int result = 0;
        String sql = "UPDATE movie SET title=?, director=?, actors=?, genre=?, release_date=?, running_time=?, rating=?, " +
                     "poster_url=?, trailer_url=?, synopsis=? WHERE movie_id=?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, movieVO.getTitle());
            pstmt.setString(2, movieVO.getDirector());
            pstmt.setString(3, String.join(",", movieVO.getActors()));
            pstmt.setString(4, movieVO.getGenre());
            pstmt.setDate(5, movieVO.getReleaseDate());
            pstmt.setInt(6, movieVO.getRunningTime());
            pstmt.setDouble(7, movieVO.getRating());
            pstmt.setString(8, movieVO.getPosterUrl());
            pstmt.setString(9, movieVO.getTrailerUrl());
            pstmt.setString(10, movieVO.getSynopsis());
            pstmt.setInt(11, movieVO.getMovieId());
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
