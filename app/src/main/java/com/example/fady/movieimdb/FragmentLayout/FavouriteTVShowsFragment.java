package com.example.fady.movieimdb.FragmentLayout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.fady.movieimdb.AdapterViews.TVShowBriefsSmallAdapter;
import com.example.fady.movieimdb.DataFeed.tvShow.TVShowBrief;
import com.example.fady.movieimdb.R;
import com.example.fady.movieimdb.Utilities.FavouriteItems;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteTVShowsFragment extends Fragment {


    private List<TVShowBrief> mFavTVShows;
    private TVShowBriefsSmallAdapter mFavTVShowsAdapter;

    private LinearLayout mEmptyLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favourite_tvshows, container, false);

        RecyclerView mFavTVShowsRecyclerView = view.findViewById(R.id.recycler_view_fav_tv_shows);
        mFavTVShows = new ArrayList<>();

        mFavTVShowsAdapter = new TVShowBriefsSmallAdapter(getContext(), mFavTVShows);

        mFavTVShowsRecyclerView.setAdapter(mFavTVShowsAdapter);
        mFavTVShowsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        mEmptyLayout =  view.findViewById(R.id.layout_recycler_view_fav_tv_shows_empty);

        loadFavTVShows();



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mFavTVShowsAdapter.notifyDataSetChanged();
    }

    private void loadFavTVShows() {

        List<TVShowBrief> favTVShowBriefs = FavouriteItems.getFavTVShowBriefs(getContext());

        if (favTVShowBriefs.isEmpty()) {

            mEmptyLayout.setVisibility(View.VISIBLE);

            return;
        }

        for (TVShowBrief tvShowBrief : favTVShowBriefs) {

            mFavTVShows.add(tvShowBrief);
        }
        mFavTVShowsAdapter.notifyDataSetChanged();
    }

}
