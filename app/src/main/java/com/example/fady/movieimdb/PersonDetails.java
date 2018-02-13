package com.example.fady.movieimdb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.fady.movieimdb.AdapterViews.MovieCastsOfPersonAdapter;
import com.example.fady.movieimdb.AdapterViews.TVCastsOfPersonAdapter;
import com.example.fady.movieimdb.DataFeed.movies.MovieCastOfPerson;
import com.example.fady.movieimdb.DataFeed.movies.MovieCastsOfPersonResponse;
import com.example.fady.movieimdb.DataFeed.people.Person;
import com.example.fady.movieimdb.DataFeed.tvShow.TVCastOfPerson;
import com.example.fady.movieimdb.DataFeed.tvShow.TVCastsOfPersonResponse;
import com.example.fady.movieimdb.Utilities.ApiClient;
import com.example.fady.movieimdb.Utilities.ApiInterfaceClient;
import com.example.fady.movieimdb.Utilities.ConnectivityBroadcastReceiver;
import com.example.fady.movieimdb.Utilities.NetworkConnection;
import com.example.fady.movieimdb.Utilities.UrlsKey;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonDetails extends AppCompatActivity {

    private int mPersonId;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout mAppBarLayout;

    private ImageView mCastImageView;
    private AVLoadingIndicatorView mProgressBar;

    private TextView mCastNameTextView,mCastAgeTextView,mCastBirthPlaceTextView;

    private TextView mCastBioHeaderTextView,mCastBioTextView,mCastReadMoreBioTextView;

    private TextView mMovieCastTextView;
    private List<MovieCastOfPerson> mMovieCastOfPersons;
    private MovieCastsOfPersonAdapter mMovieCastsOfPersonAdapter;

    private TextView mTVCastTextView;
    private List<TVCastOfPerson> mTVCastOfPersons;
    private TVCastsOfPersonAdapter mTVCastsOfPersonAdapter;

    private Snackbar mConnectivitySnackbar;
    private ConnectivityBroadcastReceiver mConnectivityBroadcastReceiver;

    private boolean isBroadcastReceiverRegistered;
    private boolean isActivityLoaded;

    private Call<Person> mPersonDetailsCall;
    private Call<MovieCastsOfPersonResponse> mMovieCastsOfPersonsCall;
    private Call<TVCastsOfPersonResponse> mTVCastsOfPersonsCall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("");

        Intent receivedIntent = getIntent();
        mPersonId = receivedIntent.getIntExtra(UrlsKey.PERSON_ID, -1);

        if (mPersonId == -1) finish();

        mCollapsingToolbarLayout =  findViewById(R.id.toolbar_layout);
        mAppBarLayout =  findViewById(R.id.app_bar);

        CardView mCastImageCardView = findViewById(R.id.card_view_cast_detail);

        int mCastImageSideSize = (int) (getResources().getDisplayMetrics().widthPixels * 0.33);


        mCastImageCardView.getLayoutParams().height = mCastImageSideSize;
        mCastImageCardView.getLayoutParams().width = mCastImageSideSize;
        mCastImageCardView.setRadius(mCastImageSideSize / 2);


        mCastImageView =  findViewById(R.id.image_view_cast_detail);

        mProgressBar =  findViewById(R.id.progress_bar_cast_detail);
        mProgressBar.setVisibility(View.GONE);


        mCastNameTextView =  findViewById(R.id.text_view_name_cast_detail);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mCastNameTextView.getLayoutParams();
        params.setMargins(params.leftMargin, mCastImageSideSize / 3, params.rightMargin, params.bottomMargin);


        mCastAgeTextView =  findViewById(R.id.text_view_age_cast_detail);
        mCastBirthPlaceTextView =  findViewById(R.id.text_view_birthplace_cast_detail);

        mCastBioHeaderTextView =  findViewById(R.id.text_view_bio_header_person_detail);
        mCastBioTextView =  findViewById(R.id.text_view_bio_person_detail);
        mCastReadMoreBioTextView =  findViewById(R.id.text_view_read_more_person_detail);

        mMovieCastTextView =  findViewById(R.id.text_view_movie_cast_person_detail);


        RecyclerView mMovieCastRecyclerView = findViewById(R.id.recycler_view_movie_cast_person_detail);

        mMovieCastOfPersons = new ArrayList<>();
        mMovieCastsOfPersonAdapter = new MovieCastsOfPersonAdapter(this, mMovieCastOfPersons);

        mMovieCastRecyclerView.setAdapter(mMovieCastsOfPersonAdapter);
        mMovieCastRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        mTVCastTextView = findViewById(R.id.text_view_tv_cast_person_detail);
        RecyclerView mTVCastRecyclerView = findViewById(R.id.recycler_view_tv_cast_person_detail);

        mTVCastOfPersons = new ArrayList<>();
        mTVCastsOfPersonAdapter = new TVCastsOfPersonAdapter(this, mTVCastOfPersons);

        mTVCastRecyclerView.setAdapter(mTVCastsOfPersonAdapter);
        mTVCastRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        if (NetworkConnection.isConnected(this)) {
            isActivityLoaded = true;
            loadActivity();
        }




    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!isActivityLoaded && !NetworkConnection.isConnected(this)) {

            mConnectivitySnackbar = Snackbar.make(mCastNameTextView, R.string.no_network, Snackbar.LENGTH_INDEFINITE);
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

            isBroadcastReceiverRegistered = false;
            unregisterReceiver(mConnectivityBroadcastReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPersonDetailsCall != null) mPersonDetailsCall.cancel();
        if (mMovieCastsOfPersonsCall != null) mMovieCastsOfPersonsCall.cancel();
        if (mTVCastsOfPersonsCall != null) mTVCastsOfPersonsCall.cancel();
    }


    private void loadActivity()
    {

        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);

        mProgressBar.setVisibility(View.VISIBLE);

        mPersonDetailsCall = apiService.getPersonDetails(mPersonId, getResources().getString(R.string.MOVIE_DB_API_KEY));

        mPersonDetailsCall.enqueue(new Callback<Person>() {
            @Override
            public void onResponse(@NonNull Call<Person> call,@NonNull final Response<Person> response) {

                if (!response.isSuccessful()) {

                    mPersonDetailsCall = call.clone();
                    mPersonDetailsCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;


                mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                        if (appBarLayout.getTotalScrollRange() + verticalOffset == 0) {

                            if (response.body().getName() != null)
                            {
                                mCollapsingToolbarLayout.setTitle(response.body().getName());}
                            else
                            {  mCollapsingToolbarLayout.setTitle("");}
                        }
                        else {
                            mCollapsingToolbarLayout.setTitle("");
                        }
                    }
                });


                Glide.with(getApplicationContext()).load(UrlsKey.IMAGE_LOADING_BASE_URL_342 + response.body().getProfilePath())
                        .asBitmap()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<String, Bitmap>() {

                            @Override
                            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {

                                mProgressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target,
                                                           boolean isFromMemoryCache, boolean isFirstResource) {

                                mProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(mCastImageView);


                if (response.body().getName() != null)
                    mCastNameTextView.setText(response.body().getName());
                else {
                    mCastNameTextView.setText("");
                }


                setAge(response.body().getDateOfBirth());


                if (response.body().getPlaceOfBirth() != null && !response.body().getPlaceOfBirth().trim().isEmpty()) {
                    mCastBirthPlaceTextView.setText(response.body().getPlaceOfBirth());
                }


                if (response.body().getBiography() != null && !response.body().getBiography().trim().isEmpty()) {

                    mCastBioHeaderTextView.setVisibility(View.VISIBLE);
                    mCastReadMoreBioTextView.setVisibility(View.VISIBLE);

                    mCastBioTextView.setText(response.body().getBiography());

                    mCastReadMoreBioTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            mCastBioTextView.setMaxLines(Integer.MAX_VALUE);
                            mCastReadMoreBioTextView.setVisibility(View.GONE);
                        }
                    });
                }


                setMovieCast(response.body().getId());

                setTVShowCast(response.body().getId());
            }

            @Override
            public void onFailure(@NonNull Call<Person> call,@NonNull Throwable t) {

            }
        });
    }


    @SuppressLint("SetTextI18n")
    private void setAge(String dateOfBirthString) {

        if (dateOfBirthString != null && !dateOfBirthString.trim().isEmpty()) {

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");


            try {
                Date releaseDate = sdf1.parse(dateOfBirthString);

                mCastAgeTextView.setText((Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(sdf2.format(releaseDate))) + "");
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    private void setMovieCast(Integer personId) {

        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);
        mMovieCastsOfPersonsCall = apiService.getMovieCastsOfPerson(personId, getResources().getString(R.string.MOVIE_DB_API_KEY));

        mMovieCastsOfPersonsCall.enqueue(new Callback<MovieCastsOfPersonResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieCastsOfPersonResponse> call,@NonNull Response<MovieCastsOfPersonResponse> response)
            {

                if (!response.isSuccessful()) {

                    mMovieCastsOfPersonsCall = call.clone();
                    mMovieCastsOfPersonsCall.enqueue(this);

                    return;
                }

                if (response.body() == null && response.body().getCasts() == null) return;


                for (MovieCastOfPerson movieCastOfPerson : response.body().getCasts()) {

                    if (movieCastOfPerson == null) return;

                    if (movieCastOfPerson.getTitle() != null && movieCastOfPerson.getPosterPath() != null) {

                        mMovieCastTextView.setVisibility(View.VISIBLE);
                        mMovieCastOfPersons.add(movieCastOfPerson);
                    }
                }
                mMovieCastsOfPersonAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<MovieCastsOfPersonResponse> call,@NonNull Throwable t) {

            }
        });
    }

    private void setTVShowCast(Integer personId) {

        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);
        mTVCastsOfPersonsCall = apiService.getTVCastsOfPerson(personId, getResources().getString(R.string.MOVIE_DB_API_KEY));

        mTVCastsOfPersonsCall.enqueue(new Callback<TVCastsOfPersonResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVCastsOfPersonResponse> call,@NonNull Response<TVCastsOfPersonResponse> response) {

                if (!response.isSuccessful()) {

                    mTVCastsOfPersonsCall = call.clone();
                    mTVCastsOfPersonsCall.enqueue(this);
                    return;
                }

                if (response.body() == null && response.body().getCasts() == null) return;


                for (TVCastOfPerson tvCastOfPerson : response.body().getCasts()) {

                    if (tvCastOfPerson == null) return;

                    if (tvCastOfPerson.getName() != null && tvCastOfPerson.getPosterPath() != null) {

                        mTVCastTextView.setVisibility(View.VISIBLE);
                        mTVCastOfPersons.add(tvCastOfPerson);
                    }
                }


                mTVCastsOfPersonAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<TVCastsOfPersonResponse> call,@NonNull Throwable t) {

            }
        });
    }

}
