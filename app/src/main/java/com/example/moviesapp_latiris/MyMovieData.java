package com.example.moviesapp_latiris;

public class MyMovieData {
    private int id;
    private String movieName;
    private String movieDate;
    private String movieImage;
    private double rating;

    public MyMovieData(int id, String movieName, String movieDate, String movieImage) {
        this(id, movieName, movieDate, movieImage, 0.0);
    }

    public MyMovieData(int id, String movieName, String movieDate, String movieImage, double rating) {
        this.id = id;
        this.movieName = movieName;
        this.movieDate = movieDate;
        this.movieImage = movieImage;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieDate() {
        return movieDate;
    }

    public void setMovieDate(String movieDate) {
        this.movieDate = movieDate;
    }

    public String getMovieImage() {
        return movieImage;
    }

    public void setMovieImage(String movieImage) {
        this.movieImage = movieImage;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}