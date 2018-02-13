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
import com.example.fady.movieimdb.AdapterViews.MovieBriefsSmallAdapter;
import com.example.fady.movieimdb.AdapterViews.MovieCastsAdapter;
import com.example.fady.movieimdb.AdapterViews.VideoAdapter;
import com.example.fady.movieimdb.DataFeed.movies.Genre;
import com.example.fady.movieimdb.DataFeed.movies.Movie;
import com.example.fady.movieimdb.DataFeed.movies.MovieBrief;
import com.example.fady.movieimdb.DataFeed.movies.MovieCastBrief;
import com.example.fady.movieimdb.DataFeed.movies.MovieCreditsResponse;
import com.example.fady.movieimdb.DataFeed.movies.SimilarMoviesResponse;
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

public class MovieDetails extends AppCompatActivity {

    private int mMovieId;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;

    private ImageView mPosterImageView;
    private AVLoadingIndicatorView mPosterProgressBar;

    private ImageView mBackdropImageView;
    private AVLoadingIndicatorView mBackdropProgressBar;

    private TextView mTitleTextView, mGenreTextView, mYearTextView;
    private ImageButton mFavImageButton;

    private LinearLayout mRatingLayout;
    private TextView mRatingTextView;

    private TextView mOverviewTextView, mOverviewReadMoreTextView, mDetailsTextView;
    private LinearLayout mDetailsLayout;

    private TextView mTrailerTextView;
    private List<Video> mTrailers;
    private VideoAdapter mTrailerAdapter;

    private View mHorizontalLine;

    private TextView mCastTextView;
    private List<MovieCastBrief> mCasts;
    private MovieCastsAdapter mCastAdapter;

    private TextView mSimilarMoviesTextView;
    private List<MovieBrief> mSimilarMovies;
    private MovieBriefsSmallAdapter mSimilarMoviesAdapter;

    private Snackbar mConnectivitySnackbar;
    private ConnectivityBroadcastReceiver mConnectivityBroadcastReceiver;

    private boolean isBroadcastReceiverRegistered;
    private boolean isActivityLoaded;

    private Call<Movie> mMovieDetailsCall;
    private Call<VideosResponse> mMovieTrailersCall;
    private Call<MovieCreditsResponse> mMovieCreditsCall;
    private Call<SimilarMoviesResponse> mSimilarMoviesCall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mToolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        setTitle("");

        Intent receivedIntent = getIntent();
        mMovieId = receivedIntent.getIntExtra(UrlsKey.MOVIE_ID, -1);

        if (mMovieId == -1) finish();

        mCollapsingToolbarLayout =  findViewById(R.id.toolbar_layout);
        mAppBarLayout = findViewById(R.id.app_bar);

        int mPosterWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.25);
        int mPosterHeight = (int) (mPosterWidth / 0.66);

        int mBackdropWidth = getResources().getDisplayMetrics().widthPixels;
        int mBackdropHeight = (int) (mBackdropWidth / 1.77);

        ConstraintLayout mMovieTabLayout = findViewById(R.id.layout_toolbar_movie);
        mMovieTabLayout.getLayoutParams().height = mBackdropHeight + (int) (mPosterHeight * 0.9);

        mPosterImageView =  findViewById(R.id.image_view_poster);

        mPosterImageView.getLayoutParams().width = mPosterWidth;
        mPosterImageView.getLayoutParams().height = mPosterHeight;

        mPosterProgressBar =  findViewById(R.id.progress_bar_poster);

        mPosterProgressBar.setVisibility(View.GONE);

        mBackdropImageView =  findViewById(R.id.image_view_backdrop);
        mBackdropImageView.getLayoutParams().height = mBackdropHeight;

        mBackdropProgressBar =  findViewById(R.id.progress_bar_backdrop);
        mBackdropProgressBar.setVisibility(View.GONE);

        mTitleTextView =  findViewById(R.id.text_view_title_movie_detail);
        mGenreTextView =  findViewById(R.id.text_view_genre_movie_detail);
        mYearTextView =   findViewById(R.id.text_view_year_movie_detail);

        mFavImageButton = findViewById(R.id.image_button_fav_movie_detail);

        mRatingLayout =  findViewById(R.id.layout_rating_movie_detail);
        mRatingTextView = findViewById(R.id.text_view_rating_movie_detail);

        mOverviewTextView =  findViewById(R.id.text_view_overview_movie_detail);
        mOverviewReadMoreTextView =  findViewById(R.id.text_view_read_more_movie_detail);
        mDetailsLayout =  findViewById(R.id.layout_details_movie_detail);
        mDetailsTextView =  findViewById(R.id.text_view_details_movie_detail);

        mTrailerTextView =  findViewById(R.id.text_view_trailer_movie_detail);
        RecyclerView mTrailerRecyclerView = findViewById(R.id.recycler_view_trailers_movie_detail);

        (new LinearSnapHelper()).attachToRecyclerView(mTrailerRecyclerView);

        mTrailers = new ArrayList<>();

        mTrailerAdapter = new VideoAdapter(this, mTrailers);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);

        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mHorizontalLine =  findViewById(R.id.view_horizontal_line);

        mCastTextView = findViewById(R.id.text_view_cast_movie_detail);
        RecyclerView mCastRecyclerView = findViewById(R.id.recycler_view_cast_movie_detail);

        mCasts = new ArrayList<>();

        mCastAdapter = new MovieCastsAdapter(this, mCasts);
        mCastRecyclerView.setAdapter(mCastAdapter);
        mCastRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mSimilarMoviesTextView = findViewById(R.id.text_view_similar_movie_detail);
        RecyclerView mSimilarMoviesRecyclerView = findViewById(R.id.recycler_view_similar_movie_detail);

        mSimilarMovies = new ArrayList<>();

        mSimilarMoviesAdapter = new MovieBriefsSmallAdapter(this, mSimilarMovies);
        mSimilarMoviesRecyclerView.setAdapter(mSimilarMoviesAdapter);

        mSimilarMoviesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        if (NetworkConnection.isConnected(this)) {
            isActivityLoaded = true;
            loadActivity();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        mSimilarMoviesAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!isActivityLoaded && !NetworkConnection.isConnected(this)) {

            mConnectivitySnackbar = Snackbar.make(mTitleTextView, R.string.no_network, Snackbar.LENGTH_INDEFINITE);
            mConnectivitySnackbar.show();

            mConnectivityBroadcastReceiver = new ConnectivityBroadcastReceiver
                    (new ConnectivityBroadcastReceiver.ConnectivityReceiverListener() {
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

        } else if (!isActivityLoaded && NetworkConnection.isConnected(this)) {
            isActivityLoaded = true;
            loadActivity();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isBroadcastReceiverRegistered) {
            isBroadcastReceiverRegistered = false;
            unregisterReceiver(mConnectivityBroadcastReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMovieDetailsCall != null) mMovieDetailsCall.cancel();
        if (mMovieTrailersCall != null) mMovieTrailersCall.cancel();
        if (mMovieCreditsCall != null) mMovieCreditsCall.cancel();
        if (mSimilarMoviesCall != null) mSimilarMoviesCall.cancel();
    }


    private void loadActivity()
    {
        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);

        mPosterProgressBar.setVisibility(View.VISIBLE);
        mBackdropProgressBar.setVisibility(View.VISIBLE);


        mMovieDetailsCall = apiService.getMovieDetails(mMovieId, getResources().getString(R.string.MOVIE_DB_API_KEY));

        mMovieDetailsCall.enqueue(new Callback<Movie>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull final Response<Movie> response) {

                if (!response.isSuccessful()) {
                    mMovieDetailsCall = call.clone();
                    mMovieDetailsCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                        if (appBarLayout.getTotalScrollRange() + verticalOffset == 0) {

                            if (response.body().getOriginalTitle() != null) {
                                mCollapsingToolbarLayout.setTitle(response.body().getOriginalTitle());
                            }
                            else {
                                mCollapsingToolbarLayout.setTitle("");
                                mToolbar.setVisibility(View.VISIBLE);
                            }


                        } else {
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
                            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target,
                                                           boolean isFromMemoryCache, boolean isFirstResource) {

                                mBackdropProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(mBackdropImageView);

                if (response.body().getOriginalTitle() != null) {
                    mTitleTextView.setText(response.body().getOriginalTitle());
                }
                else {
                    mTitleTextView.setText("");
                }



                setGenres(response.body().getGenres());

                setYear(response.body().getReleaseDate());


                setImageButtons(response.body().getId(), response.body().getPosterPath(),
                        response.body().getOriginalTitle());



                mFavImageButton.setVisibility(View.VISIBLE);

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

                } else {
                    mOverviewTextView.setText("");
                }

                setDetails(response.body().getReleaseDate(), response.body().getRuntime());

                setTrailers();

                mHorizontalLine.setVisibility(View.VISIBLE);

                setCasts();

                setSimilarMovies();

            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {

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
                } else {
                    genres = genres.concat(genresList.get(i).getGenreName() + ", ");
                }
            }
        }
        mGenreTextView.setText(genres);
    }

    private void setYear(String releaseDateString) {

        if (releaseDateString != null && !releaseDateString.trim().isEmpty()) {

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");

            try {
                Date releaseDate = sdf1.parse(releaseDateString);
                mYearTextView.setText(sdf2.format(releaseDate));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            mYearTextView.setText("");
        }
    }


    private void setImageButtons(final Integer movieId, final String posterPath, final String movieTitle)
    {

        if (movieId == null) return;

        if (FavouriteItems.isMovieFav(this, movieId)) {

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

                    FavouriteItems.removeMovieFromFav(MovieDetails.this, movieId);
                    mFavImageButton.setTag(UrlsKey.TAG_NOT_FAV);
                    mFavImageButton.setImageResource(R.mipmap.ic_favorite_border_white_24dp);
                }
                else {
                    FavouriteItems.addMovieToFav(MovieDetails.this, movieId, posterPath, movieTitle);
                    mFavImageButton.setTag(UrlsKey.TAG_FAV);
                    mFavImageButton.setImageResource(R.mipmap.ic_favorite_white_24dp);
                }
            }
        });
    }


    private void setDetails(String releaseString, Integer runtime) {

        String detailsString = "";

        if (releaseString != null && !releaseString.trim().isEmpty()) {

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("MMM d, yyyy");

            try {
                Date releaseDate = sdf1.parse(releaseString);
                detailsString += sdf2.format(releaseDate) + "\n";
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            detailsString = "-\n";
        }

        if (runtime != null && runtime != 0) {
            if (runtime < 60) {
                detailsString += runtime + " min(s)";
            } else {
                detailsString += runtime / 60 + " hr " + runtime % 60 + " mins";
            }
        } else {
            detailsString += "-";
        }

        mDetailsTextView.setText(detailsString);
    }


    private void setTrailers() {

        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);

        mMovieTrailersCall = apiService.getMovieVideos(mMovieId, getResources().getString(R.string.MOVIE_DB_API_KEY));

        mMovieTrailersCall.enqueue(new Callback<VideosResponse>() {
            @Override
            public void onResponse(@NonNull Call<VideosResponse> call, @NonNull Response<VideosResponse> response) {

                if (!response.isSuccessful()) {

                    mMovieTrailersCall = call.clone();
                    mMovieTrailersCall.enqueue(this);
                    return;
                }

                if (response.body() == null && response.body().getVideos() == null) return;

                for (Video video : response.body().getVideos()) {

                    if (video != null && video.getSite() != null
                            && video.getSite().equals("YouTube") && video.getType() != null
                            && video.getType().equals("Trailer"))

                        mTrailers.add(video);
                }
                if (!mTrailers.isEmpty()) {

                    mTrailerTextView.setVisibility(View.VISIBLE);
                    mTrailerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<VideosResponse> call, @NonNull Throwable t) {

            }
        });
    }


    private void setCasts() {

        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);

        mMovieCreditsCall = apiService.getMovieCredits(mMovieId, getResources().getString(R.string.MOVIE_DB_API_KEY));

        mMovieCreditsCall.enqueue(new Callback<MovieCreditsResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieCreditsResponse> call, @NonNull Response<MovieCreditsResponse> response) {

                if (!response.isSuccessful()) {

                    mMovieCreditsCall = call.clone();
                    mMovieCreditsCall.enqueue(this);
                    return;
                }

                if (response.body() == null && response.body().getCasts() == null) return;

                for (MovieCastBrief castBrief : response.body().getCasts()) {

                    if (castBrief != null && castBrief.getName() != null)
                    {
                        mCasts.add(castBrief);}
                }

                if (!mCasts.isEmpty())
                {  mCastTextView.setVisibility(View.VISIBLE);
                mCastAdapter.notifyDataSetChanged();}
            }

            @Override
            public void onFailure(@NonNull Call<MovieCreditsResponse> call, @NonNull Throwable t) {

            }
        });
    }


    private void setSimilarMovies() {

        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);

        mSimilarMoviesCall = apiService.getSimilarMovies(mMovieId, getResources().getString(R.string.MOVIE_DB_API_KEY), 1);

        mSimilarMoviesCall.enqueue(new Callback<SimilarMoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<SimilarMoviesResponse> call, @NonNull Response<SimilarMoviesResponse> response) {

                if (!response.isSuccessful()) {
                    mSimilarMoviesCall = call.clone();
                    mSimilarMoviesCall.enqueue(this);
                    return;
                }

                if (response.body() == null && response.body().getResults() == null) return;

                for (MovieBrief movieBrief : response.body().getResults()) {

                    if (movieBrief != null && movieBrief.getTitle() != null && movieBrief.getPosterPath() != null)
                    {  mSimilarMovies.add(movieBrief);}
                }

                if (!mSimilarMovies.isEmpty())
                {
                    mSimilarMoviesTextView.setVisibility(View.VISIBLE);
                mSimilarMoviesAdapter.notifyDataSetChanged();}
            }

            @Override
            public void onFailure(@NonNull Call<SimilarMoviesResponse> call, @NonNull Throwable t) {

            }
        });
    }

}
