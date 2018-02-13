package com.example.fady.movieimdb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.fady.movieimdb.AdapterViews.TVShowBriefsSmallAdapter;
import com.example.fady.movieimdb.AdapterViews.TVShowCastAdapter;
import com.example.fady.movieimdb.AdapterViews.VideoAdapter;
import com.example.fady.movieimdb.DataFeed.tvShow.Genre;
import com.example.fady.movieimdb.DataFeed.tvShow.Network;
import com.example.fady.movieimdb.DataFeed.tvShow.SimilarTVShowsResponse;
import com.example.fady.movieimdb.DataFeed.tvShow.TVShow;
import com.example.fady.movieimdb.DataFeed.tvShow.TVShowBrief;
import com.example.fady.movieimdb.DataFeed.tvShow.TVShowCastBrief;
import com.example.fady.movieimdb.DataFeed.tvShow.TVShowCreditsResponse;
import com.example.fady.movieimdb.DataFeed.videos.Video;
import com.example.fady.movieimdb.DataFeed.videos.VideosResponse;
import com.example.fady.movieimdb.Utilities.ApiClient;
import com.example.fady.movieimdb.Utilities.ApiInterfaceClient;
import com.example.fady.movieimdb.Utilities.ConnectivityBroadcastReceiver;
import com.example.fady.movieimdb.Utilities.FavouriteItems;
import com.example.fady.movieimdb.Utilities.NetworkConnection;
import com.example.fady.movieimdb.Utilities.UrlsKey;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TVShowDetails extends AppCompatActivity {

    private int mTVShowId;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;

    private ImageView mPosterImageView;
    private AVLoadingIndicatorView mPosterProgressBar;

    private ImageView mBackdropImageView;
    private AVLoadingIndicatorView mBackdropProgressBar;

    private TextView mTitleTextView,mGenreTextView,mYearTextView;
    private ImageButton mFavImageButton;

    private LinearLayout mRatingLayout;
    private TextView mRatingTextView,mOverviewTextView,
            mOverviewReadMoreTextView,mDetailsTextView;
    private LinearLayout mDetailsLayout;

    private TextView mVideosTextView;
    private List<Video> mVideos;
    private VideoAdapter mVideosAdapter;

    private View mHorizontalLine;

    private TextView mCastTextView;
    private List<TVShowCastBrief> mCasts;
    private TVShowCastAdapter mCastAdapter;

    private TextView mSimilarTVShowsTextView;
    private List<TVShowBrief> mSimilarTVShows;
    private TVShowBriefsSmallAdapter mSimilarTVShowsAdapter;

    private Snackbar mConnectivitySnackbar;
    private ConnectivityBroadcastReceiver mConnectivityBroadcastReceiver;

    private boolean isBroadcastReceiverRegistered;
    private boolean isActivityLoaded;

    private Call<TVShow> mTVShowDetailsCall;
    private Call<VideosResponse> mVideosCall;
    private Call<TVShowCreditsResponse> mTVShowCreditsCall;
    private Call<SimilarTVShowsResponse> mSimilarTVShowsCall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvshow_details);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        setTitle("");

        Intent receivedIntent = getIntent();
        mTVShowId = receivedIntent.getIntExtra(UrlsKey.TV_SHOW_ID, -1);

        if (mTVShowId == -1) finish();

        mCollapsingToolbarLayout =  findViewById(R.id.toolbar_layout);
        mAppBarLayout =  findViewById(R.id.app_bar);

        int mPosterWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.25);
        int mPosterHeight = (int) (mPosterWidth / 0.66);

        int mBackdropWidth = getResources().getDisplayMetrics().widthPixels;
        int mBackdropHeight = (int) (mBackdropWidth / 1.77);

        ConstraintLayout mTVShowTabLayout = findViewById(R.id.layout_toolbar_tv_show);
        mTVShowTabLayout.getLayoutParams().height = mBackdropHeight + (int) (mPosterHeight * 0.9);

        mPosterImageView = findViewById(R.id.image_view_poster);
        mPosterImageView.getLayoutParams().width = mPosterWidth;
        mPosterImageView.getLayoutParams().height = mPosterHeight;

        mPosterProgressBar =  findViewById(R.id.progress_bar_poster);

        mPosterProgressBar.setVisibility(View.GONE);

        mBackdropImageView =  findViewById(R.id.image_view_backdrop);

        mBackdropImageView.getLayoutParams().height = mBackdropHeight;

        mBackdropProgressBar =  findViewById(R.id.progress_bar_backdrop);

        mBackdropProgressBar.setVisibility(View.GONE);

        mTitleTextView =  findViewById(R.id.text_view_title_tv_show_detail);
        mGenreTextView =  findViewById(R.id.text_view_genre_tv_show_detail);
        mYearTextView =  findViewById(R.id.text_view_year_tv_show_detail);

        mFavImageButton =  findViewById(R.id.image_button_fav_tv_show_detail);

        mRatingLayout =  findViewById(R.id.layout_rating_tv_show_detail);
        mRatingTextView = findViewById(R.id.text_view_rating_tv_show_detail);

        mOverviewTextView =  findViewById(R.id.text_view_overview_tv_show_detail);
        mOverviewReadMoreTextView =  findViewById(R.id.text_view_read_more_tv_show_detail);

        mDetailsLayout =  findViewById(R.id.layout_details_tv_show_detail);
        mDetailsTextView =  findViewById(R.id.text_view_details_tv_show_detail);

        mVideosTextView =  findViewById(R.id.text_view_trailer_tv_show_detail);
        RecyclerView mVideosRecyclerView = findViewById(R.id.recycler_view_trailers_tv_show_detail);

        (new LinearSnapHelper()).attachToRecyclerView(mVideosRecyclerView);

        mVideos = new ArrayList<>();

        mVideosAdapter = new VideoAdapter(this, mVideos);
        mVideosRecyclerView.setAdapter(mVideosAdapter);

        mVideosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mHorizontalLine = findViewById(R.id.view_horizontal_line);

        mCastTextView =  findViewById(R.id.text_view_cast_tv_show_detail);

        RecyclerView mCastRecyclerView = findViewById(R.id.recycler_view_cast_tv_show_detail);

        mCasts = new ArrayList<>();

        mCastAdapter = new TVShowCastAdapter(this, mCasts);

        mCastRecyclerView.setAdapter(mCastAdapter);
        mCastRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mSimilarTVShowsTextView =  findViewById(R.id.text_view_similar_tv_show_detail);
        RecyclerView mSimilarTVShowsRecyclerView = findViewById(R.id.recycler_view_similar_tv_show_detail);

        mSimilarTVShows = new ArrayList<>();

        mSimilarTVShowsAdapter = new TVShowBriefsSmallAdapter(this, mSimilarTVShows);
        mSimilarTVShowsRecyclerView.setAdapter(mSimilarTVShowsAdapter);

        mSimilarTVShowsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        if (NetworkConnection.isConnected(this)) {
            isActivityLoaded = true;
            loadActivity();
        }


    }


    @Override
    protected void onStart() {
        super.onStart();

        mSimilarTVShowsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isActivityLoaded && !NetworkConnection.isConnected(this)) {

            mConnectivitySnackbar =
                    Snackbar.make(mTitleTextView, R.string.no_network, Snackbar.LENGTH_INDEFINITE);
            mConnectivitySnackbar.show();

            mConnectivityBroadcastReceiver = new ConnectivityBroadcastReceiver(
                    new ConnectivityBroadcastReceiver.ConnectivityReceiverListener() {
                @Override
                public void onNetworkConnectionConnected() {

                    mConnectivitySnackbar.dismiss();
                    isActivityLoaded = true;

                    loadActivity();
                    isBroadcastReceiverRegistered = false;
                    unregisterReceiver(mConnectivityBroadcastReceiver);
                }
            });

            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            isBroadcastReceiverRegistered = true;

            registerReceiver(mConnectivityBroadcastReceiver, intentFilter);

        }
        else if (!isActivityLoaded && NetworkConnection.isConnected(this)) {
            isActivityLoaded = true;
            loadActivity();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isBroadcastReceiverRegistered) {
            {
            isBroadcastReceiverRegistered = false;
            unregisterReceiver(mConnectivityBroadcastReceiver);}
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mTVShowDetailsCall != null) mTVShowDetailsCall.cancel();
        if (mVideosCall != null) mVideosCall.cancel();
        if (mTVShowCreditsCall != null) mTVShowCreditsCall.cancel();
        if (mSimilarTVShowsCall != null) mSimilarTVShowsCall.cancel();
    }



    private void loadActivity() {

        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);

        mPosterProgressBar.setVisibility(View.VISIBLE);
        mBackdropProgressBar.setVisibility(View.VISIBLE);

        mTVShowDetailsCall = apiService.getTVShowDetails(mTVShowId, getResources().getString(R.string.MOVIE_DB_API_KEY));

        mTVShowDetailsCall.enqueue(new Callback<TVShow>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(@NonNull Call<TVShow> call,@NonNull final Response<TVShow> response) {

                if (!response.isSuccessful()) {

                    mTVShowDetailsCall = call.clone();
                    mTVShowDetailsCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                        if (appBarLayout.getTotalScrollRange() + verticalOffset == 0) {

                            if (response.body().getName() != null)
                            {  mCollapsingToolbarLayout.setTitle(response.body().getName());}
                            else
                            {   mCollapsingToolbarLayout.setTitle("");
                            mToolbar.setVisibility(View.VISIBLE);}
                        }
                        else {
                            mCollapsingToolbarLayout.setTitle("");
                            mToolbar.setVisibility(View.INVISIBLE);
                        }
                    }
                });

                Glide.with(getApplicationContext()).load(UrlsKey.IMAGE_LOADING_BASE_URL_342 + response.body().getPosterPath())
                        .asBitmap()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<String, Bitmap>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {

                                mPosterProgressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target,
                                                           boolean isFromMemoryCache, boolean isFirstResource) {

                                mPosterProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(mPosterImageView);

                Glide.with(getApplicationContext()).load(UrlsKey.IMAGE_LOADING_BASE_URL_780 + response.body().getBackdropPath())
                        .asBitmap()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<String, Bitmap>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {

                                mBackdropProgressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {

                                mBackdropProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(mBackdropImageView);



                if (response.body().getName() != null)
                {
                    mTitleTextView.setText(response.body().getName());}
                else
                {  mTitleTextView.setText("");}


                setGenres(response.body().getGenres());

                setYear(response.body().getFirstAirDate());


                mFavImageButton.setVisibility(View.VISIBLE);

                setImageButtons(response.body().getId(), response.body().getPosterPath(), response.body().getName(),
                        response.body().getHomepage());

                if (response.body().getVoteAverage() != null && response.body().getVoteAverage() != 0) {
                    mRatingLayout.setVisibility(View.VISIBLE);

                    mRatingTextView.setText(String.format("%.1f", response.body().getVoteAverage()));
                }

                if (response.body().getOverview() != null && !response.body().getOverview().trim().isEmpty()) {

                    mOverviewReadMoreTextView.setVisibility(View.VISIBLE);

                    mOverviewTextView.setText(response.body().getOverview());

                    mOverviewReadMoreTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            mOverviewTextView.setMaxLines(Integer.MAX_VALUE);
                            mDetailsLayout.setVisibility(View.VISIBLE);
                            mOverviewReadMoreTextView.setVisibility(View.GONE);
                        }
                    });
                }
                else {
                    mOverviewTextView.setText("");
                }

                setDetails(response.body().getFirstAirDate(), response.body().getEpisodeRunTime(),
                        response.body().getStatus(), response.body().getOriginCountries(), response.body().getNetworks());

                setVideos();

                mHorizontalLine.setVisibility(View.VISIBLE);

                setCasts();

                setSimilarTVShows();
            }

            @Override
            public void onFailure(@NonNull Call<TVShow> call, @NonNull Throwable t) {

            }
        });
    }

    private void setGenres(List<Genre> genresList) {

        String genres = "";

        if (genresList != null) {

            for (int i = 0; i < genresList.size(); i++) {

                if (genresList.get(i) == null) continue;

                if (i == genresList.size() - 1) {

                    genres = genres.concat(genresList.get(i).getGenreName());
                }
                else {
                    genres = genres.concat(genresList.get(i).getGenreName() + ", ");
                }
            }
        }
        mGenreTextView.setText(genres);
    }

    private void setYear(String firstAirDateString) {

        if (firstAirDateString != null && !firstAirDateString.trim().isEmpty()) {

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");

            try {

                Date firstAirDate = sdf1.parse(firstAirDateString);
                mYearTextView.setText(sdf2.format(firstAirDate));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {

            mYearTextView.setText("");
        }
    }

    private void setImageButtons(final Integer tvShowId, final String posterPath, final String tvShowName, final String homepage) {

        if (tvShowId == null) return;

        if (FavouriteItems.isTVShowFav(this, tvShowId)) {

            mFavImageButton.setTag(UrlsKey.TAG_FAV);
            mFavImageButton.setImageResource(R.mipmap.ic_favorite_white_24dp);
        }
        else {
            mFavImageButton.setTag(UrlsKey.TAG_NOT_FAV);
            mFavImageButton.setImageResource(R.mipmap.ic_favorite_border_white_24dp);
        }


        mFavImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                if ((int) mFavImageButton.getTag() == UrlsKey.TAG_FAV) {

                    FavouriteItems.removeTVShowFromFav(TVShowDetails.this, tvShowId);

                    mFavImageButton.setTag(UrlsKey.TAG_NOT_FAV);
                    mFavImageButton.setImageResource(R.mipmap.ic_favorite_border_white_24dp);
                }
                else {

                    FavouriteItems.addTVShowToFav(TVShowDetails.this, tvShowId, posterPath, tvShowName);
                    mFavImageButton.setTag(UrlsKey.TAG_FAV);
                    mFavImageButton.setImageResource(R.mipmap.ic_favorite_white_24dp);
                }
            }
        });

    }

    private void setDetails(String firstAirDateString, List<Integer> runtime, String status,
                            List<String> originCountries, List<Network> networks) {

        String detailsString = "";

        if (firstAirDateString != null && !firstAirDateString.trim().isEmpty()) {

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("MMM d, yyyy");

            try {

                Date releaseDate = sdf1.parse(firstAirDateString);
                detailsString += sdf2.format(releaseDate) + "\n";
            }
            catch (ParseException e) {
                e.printStackTrace();
            }

        }
        else {
            detailsString = "-\n";
        }



        if (runtime != null && !runtime.isEmpty() && runtime.get(0) != 0) {

            if (runtime.get(0) < 60) {
                detailsString += runtime.get(0) + " min(s)" + "\n";
            }
            else {
                detailsString += runtime.get(0) / 60 + " hr " + runtime.get(0) % 60 + " mins" + "\n";
            }
        }
            else {
            detailsString += "-\n";
        }

        if (status != null && !status.trim().isEmpty()) {
            detailsString += status + "\n";
        }
        else {
            detailsString += "-\n";
        }

        String originCountriesString = "";

        if (originCountries != null && !originCountries.isEmpty()) {

            for (String country : originCountries) {

                if (country == null || country.trim().isEmpty()) continue;

                originCountriesString += country + ", ";
            }
            if (!originCountriesString.isEmpty())
            {
                detailsString += originCountriesString.substring(0, originCountriesString.length() - 2) + "\n";}
            else
            {  detailsString += "-\n";}
        }
        else {
            detailsString += "-\n";
        }

        String networksString = "";

        if (networks != null && !networks.isEmpty()) {

            for (Network network : networks) {

                if (network == null || network.getName() == null || network.getName().isEmpty())
                    continue;

                networksString += network.getName() + ", ";
            }
            if (!networksString.isEmpty())
                detailsString += networksString.substring(0, networksString.length() - 2);
            else
                detailsString += "-\n";
        }
        else {
            detailsString += "-\n";
        }

        mDetailsTextView.setText(detailsString);
    }

    private void setVideos() {

        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);
        mVideosCall = apiService.getTVShowVideos(mTVShowId, getResources().getString(R.string.MOVIE_DB_API_KEY));

        mVideosCall.enqueue(new Callback<VideosResponse>() {
            @Override
            public void onResponse(@NonNull Call<VideosResponse> call,@NonNull Response<VideosResponse> response) {

                if (!response.isSuccessful()) {

                    mVideosCall = call.clone();
                    mVideosCall.enqueue(this);
                    return;
                }

                if (response.body() == null && response.body().getVideos() == null) return;

                for (Video video : response.body().getVideos()) {

                    if (video != null && video.getSite() != null && video.getSite().equals("YouTube") &&
                            video.getType() != null && video.getType().equals("Trailer"))
                    {
                        mVideos.add(video);}
                }


                if (!mVideos.isEmpty())
                {
                    mVideosTextView.setVisibility(View.VISIBLE);
                mVideosAdapter.notifyDataSetChanged();}
            }

            @Override
            public void onFailure(@NonNull Call<VideosResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void setCasts() {

        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);
        mTVShowCreditsCall = apiService.getTVShowCredits(mTVShowId, getResources().getString(R.string.MOVIE_DB_API_KEY));

        mTVShowCreditsCall.enqueue(new Callback<TVShowCreditsResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowCreditsResponse> call,@NonNull Response<TVShowCreditsResponse> response) {

                if (!response.isSuccessful()) {

                    mTVShowCreditsCall = call.clone();
                    mTVShowCreditsCall.enqueue(this);
                    return;
                }

                if (response.body() == null && response.body().getCasts() == null) return;

                for (TVShowCastBrief castBrief : response.body().getCasts()) {

                    if (castBrief != null && castBrief.getName() != null)

                    {   mCasts.add(castBrief);}
                }

                if (!mCasts.isEmpty())
                {
                    mCastTextView.setVisibility(View.VISIBLE);
                mCastAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVShowCreditsResponse> call,@NonNull Throwable t) {

            }
        });
    }

    private void setSimilarTVShows() {

        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);
        mSimilarTVShowsCall = apiService.getSimilarTVShows(mTVShowId, getResources().getString(R.string.MOVIE_DB_API_KEY), 1);

        mSimilarTVShowsCall.enqueue(new Callback<SimilarTVShowsResponse>() {
            @Override
            public void onResponse(@NonNull Call<SimilarTVShowsResponse> call,@NonNull Response<SimilarTVShowsResponse> response) {

                if (!response.isSuccessful()) {

                    mSimilarTVShowsCall = call.clone();
                    mSimilarTVShowsCall.enqueue(this);
                    return;
                }

                if (response.body() == null && response.body().getResults() == null) return;

                for (TVShowBrief tvShowBrief : response.body().getResults()) {

                    if (tvShowBrief != null && tvShowBrief.getName() != null && tvShowBrief.getPosterPath() != null)
                    {
                        mSimilarTVShows.add(tvShowBrief);}
                }

                if (!mSimilarTVShows.isEmpty())
                {
                    mSimilarTVShowsTextView.setVisibility(View.VISIBLE);
                mSimilarTVShowsAdapter.notifyDataSetChanged();}
            }

            @Override
            public void onFailure(@NonNull Call<SimilarTVShowsResponse> call,@NonNull Throwable t) {

            }
        });
    }

}
