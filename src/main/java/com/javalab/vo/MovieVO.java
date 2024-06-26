package com.javalab.vo;

import java.io.Serializable;
import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString

public class MovieVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int movieId; // 영화 ID
	private String title; // 영화 제목
	private String director; // 감독
	private String[] actors; // 주연 배우
	private String genre; // 장르
	private Date releaseDate; // 개봉일
	private int runningTime; // 상영 시간 (분)
	private double rating; // 평점
	private String posterUrl; // 포스터 이미지 URL
	private String trailerUrl; // 예고편 URL
	private String synopsis; // 줄거리

    // 필수 정보를 받는 생성자
    public MovieVO(int movieId, String title, String director, String[] actors, String genre, Date releaseDate, int runningTime) {
        this.movieId = movieId;
        this.title = title;
        this.director = director;
        this.actors = actors;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.runningTime = runningTime;
    }

}
