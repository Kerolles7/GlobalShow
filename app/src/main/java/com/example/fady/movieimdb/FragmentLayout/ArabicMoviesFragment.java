package com.example.fady.movieimdb.FragmentLayout;


import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.fady.movieimdb.AdapterViews.MoiveNewAdapter;
import com.example.fady.movieimdb.AdapterViews.MovieBriefsLargeAdapter;
import com.example.fady.movieimdb.DataFeed.movies.ArabicMovieResponse;
import com.example.fady.movieimdb.DataFeed.movies.GenresList;
import com.example.fady.movieimdb.DataFeed.movies.MovieBrief;
import com.example.fady.movieimdb.R;
import com.example.fady.movieimdb.Utilities.ApiClient;
import com.example.fady.movieimdb.Utilities.ApiInterfaceClient;
import com.example.fady.movieimdb.Utilities.ConnectivityBroadcastReceiver;
import com.example.fady.movieimdb.Utilities.MovieGenres;
import com.example.fady.movieimdb.Utilities.NetworkConnection;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArabicMoviesFragment extends Fragment {

    private ProgressBar mProgressBar;

    private List<MovieBrief> mArbMovies;
    private MoiveNewAdapter mArbMoviesAdapter;


    private Snackbar mConnectivitySnackbar;
    private ConnectivityBroadcastReceiver mConnectivityBroadcastReceiver;

    private boolean isBroadcastReceiverRegistered;
    private boolean isFragmentLoaded;

    private Call<GenresList> mGenresListCall;
    private Call<ArabicMovieResponse> mArbMoivesCall;


    private boolean pagesOver = false;
    private int presentPage = 1;
    private boolean loading = true;
    private int previousTotal = 0;
    private int visibleThreshold = 5;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_arabic_movies, container, false);

        mProgressBar =  view.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);


        RecyclerView mArbMoviesRecyclerView = view.findViewById(R.id.recycler_view_arbMoives);

        mArbMovies = new ArrayList<>();

        mArbMoviesAdapter = new MoiveNewAdapter(getContext(), mArbMovies);

        mArbMoviesRecyclerView.setAdapter(mArbMoviesAdapter);

        final GridLayoutManager gridLayoutManager =
                new GridLayoutManager(getContext(), 2);

        mArbMoviesRecyclerView.setLayoutManager(gridLayoutManager);

        mArbMoviesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                    loadFragment();
                    loading = true;
                }

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

        mArbMoviesAdapter.notifyDataSetChanged();

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
        if (mArbMoivesCall != null) mArbMoivesCall.cancel();

    }



    private void loadFragment() {

        if (MovieGenres.isGenresListLoaded()) {

           loadMovies();
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

                    loadMovies();
                }

                @Override
                public void onFailure(@NonNull Call<GenresList> call, @NonNull Throwable t) {

                }
            });
        }

    }





    private void loadMovies()
    {

        if (pagesOver) return;

        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);

        mProgressBar.setVisibility(View.VISIBLE);

        mArbMoivesCall = apiService.getArabicMovies(getResources().getString(R.string.MOVIE_DB_API_KEY),
                "ar-EG","EG","release_date.desc", presentPage );

        mArbMoivesCall.enqueue(new Callback<ArabicMovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<ArabicMovieResponse> call, @NonNull Response<ArabicMovieResponse> response) {

                if (!response.isSuccessful()) {
                    mArbMoivesCall = call.clone();
                    mArbMoivesCall.enqueue(this);
                    return;
                }

                if (response.body()== null && response.body().getResults() == null)return;

                mProgressBar.setVisibility(View.GONE);

                for (MovieBrief movieBrief : response.body().getResults()) {

                    if (movieBrief != null && movieBrief.getTitle() != null ) {
                        mArbMovies.add(movieBrief);
                    }

                }

                mArbMoviesAdapter.notifyDataSetChanged();

                if (response.body().getPage() == response.body().getTotalPages())
                    pagesOver = true;
                else
                { presentPage++;}
            }

            @Override
            public void onFailure(@NonNull Call<ArabicMovieResponse> call, @NonNull Throwable t) {

            }
        });

    }













}
