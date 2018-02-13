package com.example.fady.movieimdb.FragmentLayout;


import android.annotation.SuppressLint;
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

import com.example.fady.movieimdb.AdapterViews.TVShowBriefsLargeAdapter;
import com.example.fady.movieimdb.AdapterViews.TVShowBriefsSmallAdapter;
import com.example.fady.movieimdb.DataFeed.tvShow.AiringTodayTVShowsResponse;
import com.example.fady.movieimdb.DataFeed.tvShow.GenresList;
import com.example.fady.movieimdb.DataFeed.tvShow.OnTheAirTVShowsResponse;
import com.example.fady.movieimdb.DataFeed.tvShow.PopularTVShowsResponse;
import com.example.fady.movieimdb.DataFeed.tvShow.TVShowBrief;
import com.example.fady.movieimdb.DataFeed.tvShow.TopRatedTVShowsResponse;
import com.example.fady.movieimdb.R;
import com.example.fady.movieimdb.Utilities.ApiClient;
import com.example.fady.movieimdb.Utilities.ApiInterfaceClient;
import com.example.fady.movieimdb.Utilities.ConnectivityBroadcastReceiver;
import com.example.fady.movieimdb.Utilities.NetworkConnection;
import com.example.fady.movieimdb.Utilities.TVShowGenres;
import com.example.fady.movieimdb.Utilities.UrlsKey;
import com.example.fady.movieimdb.ViewAllTVShowsActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class TVShowsFragment extends Fragment {

    private ProgressBar mProgressBar;

    private boolean mAiringTodaySectionLoaded;
    private boolean mOnTheAirSectionLoaded;
    private boolean mPopularSectionLoaded;
    private boolean mTopRatedSectionLoaded;

    private FrameLayout mAiringTodayLayout;
    private RecyclerView mAiringTodayRecyclerView;
    private List<TVShowBrief> mAiringTodayTVShows;
    private TVShowBriefsLargeAdapter mAiringTodayAdapter;

    private FrameLayout mOnTheAirLayout;
    private RecyclerView mOnTheAirRecyclerView;
    private List<TVShowBrief> mOnTheAirTVShows;
    private TVShowBriefsSmallAdapter mOnTheAirAdapter;

    private FrameLayout mPopularLayout;
    private RecyclerView mPopularRecyclerView;
    private List<TVShowBrief> mPopularTVShows;
    private TVShowBriefsLargeAdapter mPopularAdapter;

    private FrameLayout mTopRatedLayout;
    private RecyclerView mTopRatedRecyclerView;
    private List<TVShowBrief> mTopRatedTVShows;
    private TVShowBriefsSmallAdapter mTopRatedAdapter;

    private Snackbar mConnectivitySnackbar;
    private ConnectivityBroadcastReceiver mConnectivityBroadcastReceiver;

    private boolean isBroadcastReceiverRegistered;
    private boolean isFragmentLoaded;

    private Call<GenresList> mGenresListCall;
    private Call<AiringTodayTVShowsResponse> mAiringTodayTVShowsCall;
    private Call<OnTheAirTVShowsResponse> mOnTheAirTVShowsCall;
    private Call<PopularTVShowsResponse> mPopularTVShowsCall;
    private Call<TopRatedTVShowsResponse> mTopRatedTVShowsCall;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tvshows, container, false);

        mProgressBar = view.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        mAiringTodaySectionLoaded = false;
        mOnTheAirSectionLoaded = false;
        mPopularSectionLoaded = false;
        mTopRatedSectionLoaded = false;

        mAiringTodayLayout =  view.findViewById(R.id.layout_airing_today);
        mOnTheAirLayout =  view.findViewById(R.id.layout_on_the_air);
        mPopularLayout =  view.findViewById(R.id.layout_popular_Tv);
        mTopRatedLayout =  view.findViewById(R.id.layout_top_rated_Tv);


        TextView mAiringTodayViewAllTextView = view.findViewById(R.id.text_view_view_all_airing_today);
        TextView mOnTheAirViewAllTextView = view.findViewById(R.id.text_view_view_all_on_the_air);
        TextView mPopularViewAllTextView = view.findViewById(R.id.text_view_view_all_popular);
        TextView mTopRatedViewAllTextView = view.findViewById(R.id.text_view_view_all_top_rated);

        mAiringTodayRecyclerView =  view.findViewById(R.id.recycler_view_airing_today);
        (new LinearSnapHelper()).attachToRecyclerView(mAiringTodayRecyclerView);

        mOnTheAirRecyclerView =  view.findViewById(R.id.recycler_view_on_the_air);

        mPopularRecyclerView =  view.findViewById(R.id.recycler_view_popular);
        (new LinearSnapHelper()).attachToRecyclerView(mPopularRecyclerView);

        mTopRatedRecyclerView =  view.findViewById(R.id.recycler_view_top_rated);

        mAiringTodayTVShows = new ArrayList<>();
        mOnTheAirTVShows = new ArrayList<>();
        mPopularTVShows = new ArrayList<>();
        mTopRatedTVShows = new ArrayList<>();

        mAiringTodayAdapter = new TVShowBriefsLargeAdapter(getContext(), mAiringTodayTVShows);
        mOnTheAirAdapter = new TVShowBriefsSmallAdapter(getContext(), mOnTheAirTVShows);
        mPopularAdapter = new TVShowBriefsLargeAdapter(getContext(), mPopularTVShows);
        mTopRatedAdapter = new TVShowBriefsSmallAdapter(getContext(), mTopRatedTVShows);

        mAiringTodayRecyclerView.setAdapter(mAiringTodayAdapter);
        mAiringTodayRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        mOnTheAirRecyclerView.setAdapter(mOnTheAirAdapter);
        mOnTheAirRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        mPopularRecyclerView.setAdapter(mPopularAdapter);
        mPopularRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        mTopRatedRecyclerView.setAdapter(mTopRatedAdapter);
        mTopRatedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));


        mAiringTodayViewAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetworkConnection.isConnected(getContext())) {
                    Toast.makeText(getContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getContext(), ViewAllTVShowsActivity.class);
                intent.putExtra(UrlsKey.VIEW_ALL_TV_SHOWS_TYPE, UrlsKey.AIRING_TODAY_TV_SHOWS_TYPE);
                startActivity(intent);
            }
        });


        mOnTheAirViewAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetworkConnection.isConnected(getContext())) {
                    Toast.makeText(getContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getContext(), ViewAllTVShowsActivity.class);
                intent.putExtra(UrlsKey.VIEW_ALL_TV_SHOWS_TYPE, UrlsKey.ON_THE_AIR_TV_SHOWS_TYPE);
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
                Intent intent = new Intent(getContext(), ViewAllTVShowsActivity.class);
                intent.putExtra(UrlsKey.VIEW_ALL_TV_SHOWS_TYPE, UrlsKey.POPULAR_TV_SHOWS_TYPE);
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
                Intent intent = new Intent(getContext(), ViewAllTVShowsActivity.class);
                intent.putExtra(UrlsKey.VIEW_ALL_TV_SHOWS_TYPE, UrlsKey.TOP_RATED_TV_SHOWS_TYPE);
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

        mAiringTodayAdapter.notifyDataSetChanged();
        mOnTheAirAdapter.notifyDataSetChanged();
        mPopularAdapter.notifyDataSetChanged();
        mTopRatedAdapter.notifyDataSetChanged();
    }


    @Override
    public void onResume() {
        super.onResume();

        if (!isFragmentLoaded && !NetworkConnection.isConnected(getContext())) {

            mConnectivitySnackbar = Snackbar.make(getActivity().findViewById(R.id.main_activity_fragment_container),
                    R.string.no_network, Snackbar.LENGTH_INDEFINITE);

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

        } else if (!isFragmentLoaded && NetworkConnection.isConnected(getContext())) {

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
        if (mAiringTodayTVShowsCall != null) mAiringTodayTVShowsCall.cancel();
        if (mOnTheAirTVShowsCall != null) mOnTheAirTVShowsCall.cancel();
        if (mPopularTVShowsCall != null) mPopularTVShowsCall.cancel();
        if (mTopRatedTVShowsCall != null) mTopRatedTVShowsCall.cancel();
    }


    private void loadFragment() {

        if (TVShowGenres.isGenresListLoaded()) {

            loadAiringTodayTVShows();
            loadOnTheAirTVShows();
            loadPopularTVShows();
            loadTopRatedTVShows();


        } else {

            ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);

            mProgressBar.setVisibility(View.VISIBLE);

            mGenresListCall = apiService.getTVShowGenresList(getResources().getString(R.string.MOVIE_DB_API_KEY));
            mGenresListCall.enqueue(new Callback<GenresList>() {
                @Override
                public void onResponse(@NonNull Call<GenresList> call,@NonNull Response<GenresList> response) {

                    if (!response.isSuccessful()) {

                        mGenresListCall = call.clone();
                        mGenresListCall.enqueue(this);
                        return;
                    }

                    if (response.body() == null) return;
                    if (response.body().getGenres() == null) return;

                    TVShowGenres.loadGenresList(response.body().getGenres());

                    loadAiringTodayTVShows();
                    loadOnTheAirTVShows();
                    loadPopularTVShows();
                    loadTopRatedTVShows();
                }

                @Override
                public void onFailure(@NonNull Call<GenresList> call,@NonNull Throwable t) {

                }
            });
        }

    }




    private void loadAiringTodayTVShows() {


        final ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);

        mProgressBar.setVisibility(View.VISIBLE);

        mAiringTodayTVShowsCall = apiService.getAiringTodayTVShows(getResources().getString(R.string.MOVIE_DB_API_KEY), 1);
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

                mAiringTodaySectionLoaded = true;

                checkAllDataLoaded();

                for (TVShowBrief TVShowBrief : response.body().getResults()) {

                    if (TVShowBrief != null && TVShowBrief.getBackdropPath() != null) {
                        mAiringTodayTVShows.add(TVShowBrief);
                    }
                }

                mAiringTodayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<AiringTodayTVShowsResponse> call,@NonNull Throwable t) {

            }
        });
    }

    private void loadOnTheAirTVShows() {


        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);

        mProgressBar.setVisibility(View.VISIBLE);

        mOnTheAirTVShowsCall = apiService.getOnTheAirTVShows(getResources().getString(R.string.MOVIE_DB_API_KEY), 1);
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

                mOnTheAirSectionLoaded = true;

                checkAllDataLoaded();

                for (TVShowBrief TVShowBrief : response.body().getResults()) {

                    if (TVShowBrief != null && TVShowBrief.getPosterPath() != null) {
                        mOnTheAirTVShows.add(TVShowBrief);
                    }
                }
                mOnTheAirAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<OnTheAirTVShowsResponse> call,@NonNull Throwable t) {

            }
        });
    }

    private void loadPopularTVShows() {


        final ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);

        mProgressBar.setVisibility(View.VISIBLE);

        mPopularTVShowsCall = apiService.getPopularTVShows(getResources().getString(R.string.MOVIE_DB_API_KEY), 1);
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

                mPopularSectionLoaded = true;

                checkAllDataLoaded();

                for (TVShowBrief TVShowBrief : response.body().getResults()) {

                    if (TVShowBrief != null && TVShowBrief.getBackdropPath() != null) {
                        mPopularTVShows.add(TVShowBrief);
                    }
                }
                mPopularAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<PopularTVShowsResponse> call,@NonNull Throwable t) {

            }
        });
    }

    private void loadTopRatedTVShows() {


        ApiInterfaceClient apiService = ApiClient.getClient().create(ApiInterfaceClient.class);

        mProgressBar.setVisibility(View.VISIBLE);
        mTopRatedTVShowsCall = apiService.getTopRatedTVShows(getResources().getString(R.string.MOVIE_DB_API_KEY), 1);
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

                mTopRatedSectionLoaded = true;
                checkAllDataLoaded();

                for (TVShowBrief TVShowBrief : response.body().getResults()) {

                    if (TVShowBrief != null && TVShowBrief.getPosterPath() != null)
                        mTopRatedTVShows.add(TVShowBrief);
                }
                mTopRatedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<TopRatedTVShowsResponse> call,@NonNull Throwable t) {

            }
        });
    }

    private void checkAllDataLoaded() {


        if (mAiringTodaySectionLoaded && mOnTheAirSectionLoaded && mPopularSectionLoaded && mTopRatedSectionLoaded) {

            mProgressBar.setVisibility(View.GONE);

            mAiringTodayLayout.setVisibility(View.VISIBLE);
            mAiringTodayRecyclerView.setVisibility(View.VISIBLE);

            mOnTheAirLayout.setVisibility(View.VISIBLE);
            mOnTheAirRecyclerView.setVisibility(View.VISIBLE);

            mPopularLayout.setVisibility(View.VISIBLE);
            mPopularRecyclerView.setVisibility(View.VISIBLE);

            mTopRatedLayout.setVisibility(View.VISIBLE);
            mTopRatedRecyclerView.setVisibility(View.VISIBLE);
        }
    }

}
