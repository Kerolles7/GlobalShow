package com.example.fady.movieimdb;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.fady.movieimdb.AdapterViews.MoiveNewAdapter;
import com.example.fady.movieimdb.AdapterViews.MovieBriefsSmallAdapter;
import com.example.fady.movieimdb.DataFeed.movies.MovieBrief;
import com.example.fady.movieimdb.DataFeed.movies.NowShowingMoviesResponse;
import com.example.fady.movieimdb.DataFeed.movies.PopularMoviesResponse;
import com.example.fady.movieimdb.DataFeed.movies.TopRatedMoviesResponse;
import com.example.fady.movieimdb.DataFeed.movies.UpcomingMoviesResponse;
import com.example.fady.movieimdb.Utilities.ApiClient;
import com.example.fady.movieimdb.Utilities.ApiInterfaceClient;
import com.example.fady.movieimdb.Utilities.UrlsKey;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllMoviesActivity extends AppCompatActivity {

    private SmoothProgressBar mSmoothProgressBar;

    private List<MovieBrief> mMovies;
    private MoiveNewAdapter mMoviesAdapter;

    private int mMovieType;

    private boolean pagesOver = false;
    private int presentPage = 1;
    private boolean loading = true;
    private int previousTotal = 0;
    private int visibleThreshold = 5;

    private Call<NowShowingMoviesResponse> mNowShowingMoviesCall;
    private Call<PopularMoviesResponse> mPopularMoviesCall;
    private Call<UpcomingMoviesResponse> mUpcomingMoviesCall;
    private Call<TopRatedMoviesResponse> mTopRatedMoviesCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_movies);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent receivedIntent = getIntent();
        mMovieType = receivedIntent.getIntExtra(UrlsKey.VIEW_ALL_MOVIES_TYPE, -1);

        if (mMovieType == -1)
        {
            finish();}


        switch (mMovieType) {
            case UrlsKey.NOW_SHOWING_MOVIES_TYPE:
                setTitle(R.string.now_showing_movies);
                break;
            case UrlsKey.POPULAR_MOVIES_TYPE:
                setTitle(R.string.popular_movies);
                break;
            case UrlsKey.UPCOMING_MOVIES_TYPE:
                setTitle(R.string.upcoming_movies);
                break;
            case UrlsKey.TOP_RATED_MOVIES_TYPE:
                setTitle(R.string.top_rated_movies);
                break;
        }


        mSmoothProgressBar =findViewById(R.id.smooth_progress_bar);

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view_view_all);


        mMovies = new ArrayList<>();
        mMoviesAdapter = new MoiveNewAdapter(this, mMovies);

        mRecyclerView.setAdapter(mMoviesAdapter);

        final GridLayoutManager gridLayoutManager =
                new GridLayoutManager(this, 2);


        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                int visibleItemCount = gridLayoutManager.getChildCount();
                int totalItemCount = gridLayoutManager.getItemCount();
                int firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

                if (loading) {

                    if (totalItemCount > previousTotal) {

                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

                    loadMovies(mMovieType);
                    loading = true;
                }

            }
        });

        loadMovies(mMovieType);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mMoviesAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mNowShowingMoviesCall != null) mNowShowingMoviesCall.cancel();
        if (mPopularMoviesCall != null) mPopularMoviesCall.cancel();
        if (mUpcomingMoviesCall != null) mUpcomingMoviesCall.cancel();
        if (mTopRatedMoviesCall != null) mTopRatedMoviesCall.cancel();
    }




    private void loadMovies(int movieType)
    {
        if (pagesOver) return;

        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);
        mSmoothProgressBar.progressiveStart();


        switch (movieType)
        {
            case UrlsKey.NOW_SHOWING_MOVIES_TYPE:

                mNowShowingMoviesCall =
                        apiService.getNowShowingMovies(getResources().getString(R.string.MOVIE_DB_API_KEY), presentPage, "US");

                mNowShowingMoviesCall.enqueue(new Callback<NowShowingMoviesResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<NowShowingMoviesResponse> call,@NonNull Response<NowShowingMoviesResponse> response) {

                        if (!response.isSuccessful()) {
                            mNowShowingMoviesCall = call.clone();
                            mNowShowingMoviesCall.enqueue(this);
                            return;
                        }

                        if (response.body()== null && response.body().getResults() == null)return;

                        mSmoothProgressBar.progressiveStop();

                        for (MovieBrief movieBrief : response.body().getResults()) {

                            if (movieBrief != null && movieBrief.getTitle() != null && movieBrief.getPosterPath() != null)
                                mMovies.add(movieBrief);
                        }

                        mMoviesAdapter.notifyDataSetChanged();

                        if (response.body().getPage() == response.body().getTotalPages())
                            pagesOver = true;
                        else
                        { presentPage++;}

                    }

                    @Override
                    public void onFailure(@NonNull Call<NowShowingMoviesResponse> call,@NonNull Throwable t) {

                    }
                });
                break;

            case UrlsKey.POPULAR_MOVIES_TYPE:

                mPopularMoviesCall =
                        apiService.getPopularMovies(getResources().getString(R.string.MOVIE_DB_API_KEY), presentPage, "US");

                mPopularMoviesCall.enqueue(new Callback<PopularMoviesResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PopularMoviesResponse> call,@NonNull Response<PopularMoviesResponse> response) {
                        if (!response.isSuccessful()) {

                            mPopularMoviesCall = call.clone();
                            mPopularMoviesCall.enqueue(this);
                            return;
                        }

                        if (response.body()== null && response.body().getResults() == null)return;

                        mSmoothProgressBar.progressiveStop();


                        for (MovieBrief movieBrief : response.body().getResults()) {

                            if (movieBrief != null && movieBrief.getTitle() != null && movieBrief.getPosterPath() != null)
                                mMovies.add(movieBrief);
                        }

                        mMoviesAdapter.notifyDataSetChanged();

                        if (response.body().getPage() == response.body().getTotalPages())
                            pagesOver = true;
                        else

                        { presentPage++;}
                    }

                    @Override
                    public void onFailure(Call<PopularMoviesResponse> call, Throwable t) {

                    }
                });

                break;

            case UrlsKey.UPCOMING_MOVIES_TYPE:

                mUpcomingMoviesCall =
                        apiService.getUpcomingMovies(getResources().getString(R.string.MOVIE_DB_API_KEY), presentPage, "US");

                mUpcomingMoviesCall.enqueue(new Callback<UpcomingMoviesResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UpcomingMoviesResponse> call,@NonNull Response<UpcomingMoviesResponse> response) {
                        if (!response.isSuccessful()) {

                            mUpcomingMoviesCall = call.clone();
                            mUpcomingMoviesCall.enqueue(this);
                            return;
                        }

                        if (response.body()== null && response.body().getResults() == null)return;

                        mSmoothProgressBar.progressiveStop();

                        for (MovieBrief movieBrief : response.body().getResults()) {

                            if (movieBrief != null && movieBrief.getTitle() != null && movieBrief.getPosterPath() != null)
                                mMovies.add(movieBrief);
                        }
                        mMoviesAdapter.notifyDataSetChanged();

                        if (response.body().getPage() == response.body().getTotalPages())
                            pagesOver = true;
                        else
                        { presentPage++;}
                    }

                    @Override
                    public void onFailure(@NonNull Call<UpcomingMoviesResponse> call,@NonNull Throwable t) {

                    }
                });
                break;

            case UrlsKey.TOP_RATED_MOVIES_TYPE:

                mTopRatedMoviesCall =
                        apiService.getTopRatedMovies(getResources().getString(R.string.MOVIE_DB_API_KEY), presentPage, "US");

                mTopRatedMoviesCall.enqueue(new Callback<TopRatedMoviesResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TopRatedMoviesResponse> call,@NonNull Response<TopRatedMoviesResponse> response) {
                        if (!response.isSuccessful()) {

                            mTopRatedMoviesCall = call.clone();
                            mTopRatedMoviesCall.enqueue(this);
                            return;
                        }

                        if (response.body()== null && response.body().getResults() == null)return;

                        mSmoothProgressBar.progressiveStop();

                        for (MovieBrief movieBrief : response.body().getResults()) {

                            if (movieBrief != null && movieBrief.getTitle() != null && movieBrief.getPosterPath() != null)
                                mMovies.add(movieBrief);
                        }
                        mMoviesAdapter.notifyDataSetChanged();

                        if (response.body().getPage() == response.body().getTotalPages())
                            pagesOver = true;
                        else

                        {presentPage++;}
                    }

                    @Override
                    public void onFailure(@NonNull Call<TopRatedMoviesResponse> call,@NonNull Throwable t) {

                    }
                });
                break;
        }
    }

}
