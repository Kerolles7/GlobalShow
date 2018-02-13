package com.example.fady.movieimdb.Utilities;

import com.example.fady.movieimdb.DataFeed.movies.ArabicMovieResponse;
import com.example.fady.movieimdb.DataFeed.movies.GenresList;
import com.example.fady.movieimdb.DataFeed.movies.Movie;
import com.example.fady.movieimdb.DataFeed.movies.MovieCastsOfPersonResponse;
import com.example.fady.movieimdb.DataFeed.movies.MovieCreditsResponse;
import com.example.fady.movieimdb.DataFeed.movies.NowShowingMoviesResponse;
import com.example.fady.movieimdb.DataFeed.movies.PopularMoviesResponse;
import com.example.fady.movieimdb.DataFeed.movies.SimilarMoviesResponse;
import com.example.fady.movieimdb.DataFeed.movies.TopRatedMoviesResponse;
import com.example.fady.movieimdb.DataFeed.movies.UpcomingMoviesResponse;
import com.example.fady.movieimdb.DataFeed.people.Person;
import com.example.fady.movieimdb.DataFeed.tvShow.AiringTodayTVShowsResponse;
import com.example.fady.movieimdb.DataFeed.tvShow.OnTheAirTVShowsResponse;
import com.example.fady.movieimdb.DataFeed.tvShow.PopularTVShowsResponse;
import com.example.fady.movieimdb.DataFeed.tvShow.SimilarTVShowsResponse;
import com.example.fady.movieimdb.DataFeed.tvShow.TVCastsOfPersonResponse;
import com.example.fady.movieimdb.DataFeed.tvShow.TVShow;
import com.example.fady.movieimdb.DataFeed.tvShow.TVShowCreditsResponse;
import com.example.fady.movieimdb.DataFeed.tvShow.TopRatedTVShowsResponse;
import com.example.fady.movieimdb.DataFeed.videos.VideosResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Fady on 2/3/2018.
 */

public interface ApiInterfaceClient {


    @GET("movie/now_playing")
    Call<NowShowingMoviesResponse> getNowShowingMovies(@Query("api_key") String apiKey, @Query("page") Integer page, @Query("region") String region);

    @GET("movie/popular")
    Call<PopularMoviesResponse> getPopularMovies(@Query("api_key") String apiKey, @Query("page") Integer page, @Query("region") String region);

    @GET("movie/upcoming")
    Call<UpcomingMoviesResponse> getUpcomingMovies(@Query("api_key") String apiKey, @Query("page") Integer page, @Query("region") String region);

    @GET("movie/top_rated")
    Call<TopRatedMoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey, @Query("page") Integer page, @Query("region") String region);

    @GET("movie/{id}")
    Call<Movie> getMovieDetails(@Path("id") Integer movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<VideosResponse> getMovieVideos(@Path("id") Integer movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/credits")
    Call<MovieCreditsResponse> getMovieCredits(@Path("id") Integer movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/similar")
    Call<SimilarMoviesResponse> getSimilarMovies(@Path("id") Integer movieId, @Query("api_key") String apiKey, @Query("page") Integer page);

    @GET("genre/movie/list")
    Call<GenresList> getMovieGenresList(@Query("api_key") String apiKey);


    @GET("discover/movie")
    Call<ArabicMovieResponse> getArabicMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("region") String region,
                                              @Query("sort_by") String sortby, @Query("page") Integer page);





    @GET("tv/airing_today")
    Call<AiringTodayTVShowsResponse> getAiringTodayTVShows(@Query("api_key") String apiKey, @Query("page") Integer page);

    @GET("tv/on_the_air")
    Call<OnTheAirTVShowsResponse> getOnTheAirTVShows(@Query("api_key") String apiKey, @Query("page") Integer page);

    @GET("tv/popular")
    Call<PopularTVShowsResponse> getPopularTVShows(@Query("api_key") String apiKey, @Query("page") Integer page);

    @GET("tv/top_rated")
    Call<TopRatedTVShowsResponse> getTopRatedTVShows(@Query("api_key") String apiKey, @Query("page") Integer page);

    @GET("tv/{id}")
    Call<TVShow> getTVShowDetails(@Path("id") Integer tvShowId, @Query("api_key") String apiKey);

    @GET("tv/{id}/videos")
    Call<VideosResponse> getTVShowVideos(@Path("id") Integer movieId, @Query("api_key") String apiKey);

    @GET("tv/{id}/credits")
    Call<TVShowCreditsResponse> getTVShowCredits(@Path("id") Integer movieId, @Query("api_key") String apiKey);

    @GET("tv/{id}/similar")
    Call<SimilarTVShowsResponse> getSimilarTVShows(@Path("id") Integer movieId, @Query("api_key") String apiKey, @Query("page") Integer page);

    @GET("genre/tv/list")
    Call<com.example.fady.movieimdb.DataFeed.tvShow.GenresList> getTVShowGenresList(@Query("api_key") String apiKey);




    @GET("person/{id}")
    Call<Person> getPersonDetails(@Path("id") Integer personId, @Query("api_key") String apiKey);

    @GET("person/{id}/movie_credits")
    Call<MovieCastsOfPersonResponse> getMovieCastsOfPerson(@Path("id") Integer personId, @Query("api_key") String apiKey);

    @GET("person/{id}/tv_credits")
    Call<TVCastsOfPersonResponse> getTVCastsOfPerson(@Path("id") Integer personId, @Query("api_key") String apiKey);
}
