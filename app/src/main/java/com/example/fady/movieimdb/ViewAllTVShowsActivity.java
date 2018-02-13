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
import com.example.fady.movieimdb.AdapterViews.TVNewAdapter;
import com.example.fady.movieimdb.AdapterViews.TVShowBriefsSmallAdapter;
import com.example.fady.movieimdb.DataFeed.tvShow.AiringTodayTVShowsResponse;
import com.example.fady.movieimdb.DataFeed.tvShow.OnTheAirTVShowsResponse;
import com.example.fady.movieimdb.DataFeed.tvShow.PopularTVShowsResponse;
import com.example.fady.movieimdb.DataFeed.tvShow.TVShowBrief;
import com.example.fady.movieimdb.DataFeed.tvShow.TopRatedTVShowsResponse;
import com.example.fady.movieimdb.Utilities.ApiClient;
import com.example.fady.movieimdb.Utilities.ApiInterfaceClient;
import com.example.fady.movieimdb.Utilities.UrlsKey;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllTVShowsActivity extends AppCompatActivity {

    private SmoothProgressBar mSmoothProgressBar;

    private List<TVShowBrief> mTVShows;
    private TVNewAdapter mTVShowsAdapter;

    private int mTVShowType;

    private boolean pagesOver = false;
    private int presentPage = 1;
    private boolean loading = true;
    private int previousTotal = 0;
    private int visibleThreshold = 5;

    private Call<AiringTodayTVShowsResponse> mAiringTodayTVShowsCall;
    private Call<OnTheAirTVShowsResponse> mOnTheAirTVShowsCall;
    private Call<PopularTVShowsResponse> mPopularTVShowsCall;
    private Call<TopRatedTVShowsResponse> mTopRatedTVShowsCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_tvshows);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent receivedIntent = getIntent();
        mTVShowType = receivedIntent.getIntExtra(UrlsKey.VIEW_ALL_TV_SHOWS_TYPE, -1);

        if (mTVShowType == -1) finish();


        switch (mTVShowType) {
            case UrlsKey.AIRING_TODAY_TV_SHOWS_TYPE:
                setTitle(R.string.airing_today_tv_shows);
                break;
            case UrlsKey.ON_THE_AIR_TV_SHOWS_TYPE:
                setTitle(R.string.on_the_air_tv_shows);
                break;
            case UrlsKey.POPULAR_TV_SHOWS_TYPE:
                setTitle(R.string.popular_tv_shows);
                break;
            case UrlsKey.TOP_RATED_TV_SHOWS_TYPE:
                setTitle(R.string.top_rated_tv_shows);
                break;
        }


        mSmoothProgressBar =  findViewById(R.id.smooth_progress_bar);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view_view_all);

        mTVShows = new ArrayList<>();

        mTVShowsAdapter = new TVNewAdapter(this, mTVShows);

        mRecyclerView.setAdapter(mTVShowsAdapter);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(ViewAllTVShowsActivity.this, 2);
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

                    loadTVShows(mTVShowType);
                    loading = true;
                }
            }
        });

        loadTVShows(mTVShowType);


    }

    @Override
    protected void onStart() {
        super.onStart();

        mTVShowsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mAiringTodayTVShowsCall != null) mAiringTodayTVShowsCall.cancel();
        if (mOnTheAirTVShowsCall != null) mOnTheAirTVShowsCall.cancel();
        if (mPopularTVShowsCall != null) mPopularTVShowsCall.cancel();
        if (mTopRatedTVShowsCall != null) mTopRatedTVShowsCall.cancel();
    }



    private void loadTVShows(int tvShowType)
    {
        if (pagesOver) return;

        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);
        mSmoothProgressBar.progressiveStart();


        switch (tvShowType)
        {
            case UrlsKey.AIRING_TODAY_TV_SHOWS_TYPE:

                mAiringTodayTVShowsCall =
                        apiService.getAiringTodayTVShows(getResources().getString(R.string.MOVIE_DB_API_KEY), presentPage);

                mAiringTodayTVShowsCall.enqueue(new Callback<AiringTodayTVShowsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<AiringTodayTVShowsResponse> call,@NonNull Response<AiringTodayTVShowsResponse> response) {

                        if (!response.isSuccessful()) {

                            mAiringTodayTVShowsCall = call.clone();
                            mAiringTodayTVShowsCall.enqueue(this);
                            return;
                        }

                        if (response.body() == null) return;
                        if (response.body().getResults() == null) return;

                        mSmoothProgressBar.progressiveStop();

                        for (TVShowBrief tvShowBrief : response.body().getResults()) {

                            if (tvShowBrief != null && tvShowBrief.getName() != null && tvShowBrief.getPosterPath() != null)
                                mTVShows.add(tvShowBrief);
                        }

                        mTVShowsAdapter.notifyDataSetChanged();

                        if (response.body().getPage() == response.body().getTotalPages())

                            pagesOver = true;
                        else
                        {  presentPage++;}
                    }

                    @Override
                    public void onFailure(@NonNull Call<AiringTodayTVShowsResponse> call,@NonNull Throwable t) {

                    }
                });

                break;

            case UrlsKey.ON_THE_AIR_TV_SHOWS_TYPE:

                mOnTheAirTVShowsCall =
                        apiService.getOnTheAirTVShows(getResources().getString(R.string.MOVIE_DB_API_KEY), presentPage);

                mOnTheAirTVShowsCall.enqueue(new Callback<OnTheAirTVShowsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<OnTheAirTVShowsResponse> call,@NonNull Response<OnTheAirTVShowsResponse> response) {

                        if (!response.isSuccessful()) {

                            mOnTheAirTVShowsCall = call.clone();
                            mOnTheAirTVShowsCall.enqueue(this);
                            return;
                        }

                        if (response.body() == null) return;
                        if (response.body().getResults() == null) return;

                        mSmoothProgressBar.progressiveStop();

                        for (TVShowBrief tvShowBrief : response.body().getResults()) {

                            if (tvShowBrief != null && tvShowBrief.getName() != null && tvShowBrief.getPosterPath() != null)
                                mTVShows.add(tvShowBrief);
                        }
                        mTVShowsAdapter.notifyDataSetChanged();

                        if (response.body().getPage() == response.body().getTotalPages())
                            pagesOver = true;
                        else

                        {presentPage++;}
                    }

                    @Override
                    public void onFailure(@NonNull Call<OnTheAirTVShowsResponse> call,@NonNull Throwable t) {

                    }
                });
                break;

            case UrlsKey.POPULAR_TV_SHOWS_TYPE:

                mPopularTVShowsCall =
                        apiService.getPopularTVShows(getResources().getString(R.string.MOVIE_DB_API_KEY), presentPage);

                mPopularTVShowsCall.enqueue(new Callback<PopularTVShowsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PopularTVShowsResponse> call,@NonNull Response<PopularTVShowsResponse> response) {

                        if (!response.isSuccessful()) {

                            mPopularTVShowsCall = call.clone();
                            mPopularTVShowsCall.enqueue(this);
                            return;
                        }

                        if (response.body() == null) return;
                        if (response.body().getResults() == null) return;

                        mSmoothProgressBar.progressiveStop();

                        for (TVShowBrief tvShowBrief : response.body().getResults()) {

                            if (tvShowBrief != null && tvShowBrief.getName() != null && tvShowBrief.getPosterPath() != null)
                                mTVShows.add(tvShowBrief);
                        }


                        mTVShowsAdapter.notifyDataSetChanged();

                        if (response.body().getPage() == response.body().getTotalPages())
                            pagesOver = true;
                        else
                        {  presentPage++;}
                    }

                    @Override
                    public void onFailure(@NonNull Call<PopularTVShowsResponse> call,@NonNull Throwable t) {

                    }
                });
                break;

            case UrlsKey.TOP_RATED_TV_SHOWS_TYPE:

                mTopRatedTVShowsCall =
                        apiService.getTopRatedTVShows(getResources().getString(R.string.MOVIE_DB_API_KEY), presentPage);

                mTopRatedTVShowsCall.enqueue(new Callback<TopRatedTVShowsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TopRatedTVShowsResponse> call,@NonNull Response<TopRatedTVShowsResponse> response) {

                        if (!response.isSuccessful()) {
                            mTopRatedTVShowsCall = call.clone();
                            mTopRatedTVShowsCall.enqueue(this);
                            return;
                        }

                        if (response.body() == null) return;
                        if (response.body().getResults() == null) return;

                        mSmoothProgressBar.progressiveStop();

                        for (TVShowBrief tvShowBrief : response.body().getResults()) {

                            if (tvShowBrief != null && tvShowBrief.getName() != null && tvShowBrief.getPosterPath() != null)
                                mTVShows.add(tvShowBrief);
                        }
                        mTVShowsAdapter.notifyDataSetChanged();

                        if (response.body().getPage() == response.body().getTotalPages())
                            pagesOver = true;
                        else
                        {  presentPage++;}
                    }

                    @Override
                    public void onFailure(@NonNull Call<TopRatedTVShowsResponse> call,@NonNull Throwable t) {

                    }
                });
                break;
        }
    }

}
