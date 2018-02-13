package com.example.fady.movieimdb.FragmentLayout;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fady.movieimdb.AdapterViews.MovieBriefsLargeAdapter;
import com.example.fady.movieimdb.AdapterViews.MovieBriefsSmallAdapter;
import com.example.fady.movieimdb.DataFeed.movies.GenresList;
import com.example.fady.movieimdb.DataFeed.movies.MovieBrief;
import com.example.fady.movieimdb.DataFeed.movies.NowShowingMoviesResponse;
import com.example.fady.movieimdb.DataFeed.movies.PopularMoviesResponse;
import com.example.fady.movieimdb.DataFeed.movies.TopRatedMoviesResponse;
import com.example.fady.movieimdb.DataFeed.movies.UpcomingMoviesResponse;
import com.example.fady.movieimdb.R;
import com.example.fady.movieimdb.Utilities.ApiClient;
import com.example.fady.movieimdb.Utilities.ApiInterfaceClient;
import com.example.fady.movieimdb.Utilities.ConnectivityBroadcastReceiver;
import com.example.fady.movieimdb.Utilities.MovieGenres;
import com.example.fady.movieimdb.Utilities.NetworkConnection;
import com.example.fady.movieimdb.Utilities.UrlsKey;
import com.example.fady.movieimdb.ViewAllMoviesActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesFragment extends Fragment {

    private ProgressBar mProgressBar;
    private boolean mNowShowingSectionLoaded;
    private boolean mPopularSectionLoaded;
    private boolean mUpcomingSectionLoaded;
    private boolean mTopRatedSectionLoaded;

    private FrameLayout mNowShowingLayout;
    private RecyclerView mNowShowingRecyclerView;
    private List<MovieBrief> mNowShowingMovies;
    private MovieBriefsLargeAdapter mNowShowingAdapter;

    private FrameLayout mPopularLayout;
    private RecyclerView mPopularRecyclerView;
    private List<MovieBrief> mPopularMovies;
    private MovieBriefsSmallAdapter mPopularAdapter;

    private FrameLayout mUpcomingLayout;
    private RecyclerView mUpcomingRecyclerView;
    private List<MovieBrief> mUpcomingMovies;
    private MovieBriefsLargeAdapter mUpcomingAdapter;

    private FrameLayout mTopRatedLayout;
    private RecyclerView mTopRatedRecyclerView;
    private List<MovieBrief> mTopRatedMovies;
    private MovieBriefsSmallAdapter mTopRatedAdapter;

    private Snackbar mConnectivitySnackbar;
    private ConnectivityBroadcastReceiver mConnectivityBroadcastReceiver;

    private boolean isBroadcastReceiverRegistered;
    private boolean isFragmentLoaded;

    private Call<GenresList> mGenresListCall;
    private Call<NowShowingMoviesResponse> mNowShowingMoviesCall;
    private Call<PopularMoviesResponse> mPopularMoviesCall;
    private Call<UpcomingMoviesResponse> mUpcomingMoviesCall;
    private Call<TopRatedMoviesResponse> mTopRatedMoviesCall;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        mProgressBar =  view.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);


        mNowShowingSectionLoaded = false;
        mPopularSectionLoaded = false;
        mUpcomingSectionLoaded = false;
        mTopRatedSectionLoaded = false;

        mNowShowingLayout =  view.findViewById(R.id.layout_now_showing);
        mPopularLayout =  view.findViewById(R.id.layout_popular);
        mUpcomingLayout =  view.findViewById(R.id.layout_upcoming);
        mTopRatedLayout =  view.findViewById(R.id.layout_top_rated);

        TextView mNowShowingViewAllTextView = view.findViewById(R.id.text_view_view_all_now_showing);
        TextView mPopularViewAllTextView = view.findViewById(R.id.text_view_view_all_popular);
        TextView mUpcomingViewAllTextView = view.findViewById(R.id.text_view_view_all_upcoming);
        TextView mTopRatedViewAllTextView = view.findViewById(R.id.text_view_view_all_top_rated);


        mNowShowingRecyclerView = view.findViewById(R.id.recycler_view_now_showing);
        (new LinearSnapHelper()).attachToRecyclerView(mNowShowingRecyclerView);

        mPopularRecyclerView =  view.findViewById(R.id.recycler_view_popular);

        mUpcomingRecyclerView =  view.findViewById(R.id.recycler_view_upcoming);
        (new LinearSnapHelper()).attachToRecyclerView(mUpcomingRecyclerView);

        mTopRatedRecyclerView =  view.findViewById(R.id.recycler_view_top_rated);

        mNowShowingMovies = new ArrayList<>();
        mPopularMovies = new ArrayList<>();
        mUpcomingMovies = new ArrayList<>();
        mTopRatedMovies = new ArrayList<>();

        mNowShowingAdapter = new MovieBriefsLargeAdapter(getContext(), mNowShowingMovies);
        mPopularAdapter = new MovieBriefsSmallAdapter(getContext(), mPopularMovies);
        mUpcomingAdapter = new MovieBriefsLargeAdapter(getContext(), mUpcomingMovies);
        mTopRatedAdapter = new MovieBriefsSmallAdapter(getContext(), mTopRatedMovies);

        mNowShowingRecyclerView.setAdapter(mNowShowingAdapter);
        mNowShowingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        mPopularRecyclerView.setAdapter(mPopularAdapter);
        mPopularRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        mUpcomingRecyclerView.setAdapter(mUpcomingAdapter);
        mUpcomingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        mTopRatedRecyclerView.setAdapter(mTopRatedAdapter);
        mTopRatedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));



        mNowShowingViewAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetworkConnection.isConnected(getContext())) {
                    Toast.makeText(getContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getContext(), ViewAllMoviesActivity.class);
                intent.putExtra(UrlsKey.VIEW_ALL_MOVIES_TYPE, UrlsKey.NOW_SHOWING_MOVIES_TYPE);
                startActivity(intent);
            }
        });


        mPopularViewAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetworkConnection.isConnected(getContext())) {
                    Toast.makeText(getContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getContext(), ViewAllMoviesActivity.class);
                intent.putExtra(UrlsKey.VIEW_ALL_MOVIES_TYPE, UrlsKey.POPULAR_MOVIES_TYPE);
                startActivity(intent);
            }
        });


        mUpcomingViewAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetworkConnection.isConnected(getContext())) {
                    Toast.makeText(getContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getContext(), ViewAllMoviesActivity.class);
                intent.putExtra(UrlsKey.VIEW_ALL_MOVIES_TYPE, UrlsKey.UPCOMING_MOVIES_TYPE);
                startActivity(intent);
            }
        });

        mTopRatedViewAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetworkConnection.isConnected(getContext())) {
                    Toast.makeText(getContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getContext(), ViewAllMoviesActivity.class);
                intent.putExtra(UrlsKey.VIEW_ALL_MOVIES_TYPE, UrlsKey.TOP_RATED_MOVIES_TYPE);
                startActivity(intent);
            }
        });




        if (NetworkConnection.isConnected(getContext())) {
            isFragmentLoaded = true;
            loadFragment();
        }


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mNowShowingAdapter.notifyDataSetChanged();
        mPopularAdapter.notifyDataSetChanged();
        mUpcomingAdapter.notifyDataSetChanged();
        mTopRatedAdapter.notifyDataSetChanged();
    }


    @Override
    public void onResume() {
        super.onResume();

        if (!isFragmentLoaded && !NetworkConnection.isConnected(getContext())) {

            mConnectivitySnackbar = Snackbar.make(getActivity().findViewById(R.id.main_activity_fragment_container)
                    , R.string.no_network, Snackbar.LENGTH_INDEFINITE);

            mConnectivitySnackbar.show();

            mConnectivityBroadcastReceiver = new ConnectivityBroadcastReceiver(new ConnectivityBroadcastReceiver.ConnectivityReceiverListener() {
                @Override
                public void onNetworkConnectionConnected() {

                    mConnectivitySnackbar.dismiss();
                    isFragmentLoaded = true;

                    loadFragment();
                    isBroadcastReceiverRegistered = false;
                    getActivity().unregisterReceiver(mConnectivityBroadcastReceiver);
                }
            });

            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");

            isBroadcastReceiverRegistered = true;
            getActivity().registerReceiver(mConnectivityBroadcastReceiver, intentFilter);

        }

        else if (!isFragmentLoaded && NetworkConnection.isConnected(getContext())) {
            isFragmentLoaded = true;
            loadFragment();
        }
    }



    @Override
    public void onPause() {
        super.onPause();

        if (isBroadcastReceiverRegistered) {
            mConnectivitySnackbar.dismiss();
            isBroadcastReceiverRegistered = false;
            getActivity().unregisterReceiver(mConnectivityBroadcastReceiver);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mGenresListCall != null) mGenresListCall.cancel();
        if (mNowShowingMoviesCall != null) mNowShowingMoviesCall.cancel();
        if (mPopularMoviesCall != null) mPopularMoviesCall.cancel();
        if (mUpcomingMoviesCall != null) mUpcomingMoviesCall.cancel();
        if (mTopRatedMoviesCall != null) mTopRatedMoviesCall.cancel();
    }



    private void loadFragment() {

        if (MovieGenres.isGenresListLoaded()) {

            loadNowShowingMovies();
            loadPopularMovies();
            loadUpcomingMovies();
            loadTopRatedMovies();
        }

        else {

            ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);
            mProgressBar.setVisibility(View.VISIBLE);

            mGenresListCall = apiService.getMovieGenresList(getResources().getString(R.string.MOVIE_DB_API_KEY));

            mGenresListCall.enqueue(new Callback<GenresList>() {
                @Override
                public void onResponse(@NonNull Call<GenresList> call, @NonNull Response<GenresList> response) {
                    if (!response.isSuccessful()) {

                        mGenresListCall = call.clone();
                        mGenresListCall.enqueue(this);
                        return;
                    }

                    if (response.body() == null) return;
                    if (response.body().getGenres() == null) return;

                    MovieGenres.loadGenresList(response.body().getGenres());

                    loadNowShowingMovies();
                    loadPopularMovies();
                    loadUpcomingMovies();
                    loadTopRatedMovies();
                }

                @Override
                public void onFailure(@NonNull Call<GenresList> call, @NonNull Throwable t) {

                }
            });
        }

    }



    private void loadNowShowingMovies() {


        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);
        mProgressBar.setVisibility(View.VISIBLE);

        mNowShowingMoviesCall = apiService.getNowShowingMovies(getResources().getString(R.string.MOVIE_DB_API_KEY), 1, "US");

        mNowShowingMoviesCall.enqueue(new Callback<NowShowingMoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<NowShowingMoviesResponse> call, @NonNull Response<NowShowingMoviesResponse> response) {
                if (!response.isSuccessful()) {

                    mNowShowingMoviesCall = call.clone();
                    mNowShowingMoviesCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                mNowShowingSectionLoaded = true;

                checkAllDataLoaded();

                for (MovieBrief movieBrief : response.body().getResults()) {

                    if (movieBrief != null && movieBrief.getBackdropPath() != null)
                        mNowShowingMovies.add(movieBrief);
                }
                mNowShowingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<NowShowingMoviesResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void loadPopularMovies() {
        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);

        mProgressBar.setVisibility(View.VISIBLE);

        mPopularMoviesCall = apiService.getPopularMovies(getResources().getString(R.string.MOVIE_DB_API_KEY), 1, "US");
        mPopularMoviesCall.enqueue(new Callback<PopularMoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<PopularMoviesResponse> call, @NonNull Response<PopularMoviesResponse> response) {
                if (!response.isSuccessful()) {
                    mPopularMoviesCall = call.clone();
                    mPopularMoviesCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                mPopularSectionLoaded = true;
                checkAllDataLoaded();
                for (MovieBrief movieBrief : response.body().getResults()) {
                    if (movieBrief != null && movieBrief.getPosterPath() != null)
                        mPopularMovies.add(movieBrief);
                }
                mPopularAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<PopularMoviesResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void loadUpcomingMovies() {
        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);
        mProgressBar.setVisibility(View.VISIBLE);
        mUpcomingMoviesCall = apiService.getUpcomingMovies(getResources().getString(R.string.MOVIE_DB_API_KEY), 1, "US");
        mUpcomingMoviesCall.enqueue(new Callback<UpcomingMoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<UpcomingMoviesResponse> call,@NonNull  Response<UpcomingMoviesResponse> response) {
                if (!response.isSuccessful()) {
                    mUpcomingMoviesCall = call.clone();
                    mUpcomingMoviesCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                mUpcomingSectionLoaded = true;
                checkAllDataLoaded();
                for (MovieBrief movieBrief : response.body().getResults()) {
                    if (movieBrief != null && movieBrief.getBackdropPath() != null)
                        mUpcomingMovies.add(movieBrief);
                }
                mUpcomingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<UpcomingMoviesResponse> call,@NonNull  Throwable t) {

            }
        });
    }

    private void loadTopRatedMovies() {
        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);
        mProgressBar.setVisibility(View.VISIBLE);
        mTopRatedMoviesCall = apiService.getTopRatedMovies(getResources().getString(R.string.MOVIE_DB_API_KEY), 1, "US");
        mTopRatedMoviesCall.enqueue(new Callback<TopRatedMoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<TopRatedMoviesResponse> call,@NonNull  Response<TopRatedMoviesResponse> response) {
                if (!response.isSuccessful()) {
                    mTopRatedMoviesCall = call.clone();
                    mTopRatedMoviesCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                mTopRatedSectionLoaded = true;
                checkAllDataLoaded();
                for (MovieBrief movieBrief : response.body().getResults()) {
                    if (movieBrief != null && movieBrief.getPosterPath() != null)
                        mTopRatedMovies.add(movieBrief);
                }
                mTopRatedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<TopRatedMoviesResponse> call,@NonNull  Throwable t) {

            }
        });
    }

    private void checkAllDataLoaded() {
        if (mNowShowingSectionLoaded && mPopularSectionLoaded && mUpcomingSectionLoaded && mTopRatedSectionLoaded) {
            mProgressBar.setVisibility(View.GONE);

            mNowShowingLayout.setVisibility(View.VISIBLE);
            mNowShowingRecyclerView.setVisibility(View.VISIBLE);

            mPopularLayout.setVisibility(View.VISIBLE);
            mPopularRecyclerView.setVisibility(View.VISIBLE);

            mUpcomingLayout.setVisibility(View.VISIBLE);
            mUpcomingRecyclerView.setVisibility(View.VISIBLE);

            mTopRatedLayout.setVisibility(View.VISIBLE);
            mTopRatedRecyclerView.setVisibility(View.VISIBLE);
        }
    }

}
