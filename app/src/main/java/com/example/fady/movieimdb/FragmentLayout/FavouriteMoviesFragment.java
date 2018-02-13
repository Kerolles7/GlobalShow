package com.example.fady.movieimdb.FragmentLayout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.fady.movieimdb.AdapterViews.MovieBriefsSmallAdapter;
import com.example.fady.movieimdb.DataFeed.movies.MovieBrief;
import com.example.fady.movieimdb.R;
import com.example.fady.movieimdb.Utilities.FavouriteItems;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteMoviesFragment extends Fragment {

    private List<MovieBrief> mFavMovies;
    private MovieBriefsSmallAdapter mFavMoviesAdapter;

    private LinearLayout mEmptyLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favourite_movies, container, false);

        RecyclerView mFavMoviesRecyclerView = view.findViewById(R.id.recycler_view_fav_movies);
        mFavMovies = new ArrayList<>();

        mFavMoviesAdapter = new MovieBriefsSmallAdapter(getContext(), mFavMovies);

        mFavMoviesRecyclerView.setAdapter(mFavMoviesAdapter);
        mFavMoviesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        mEmptyLayout =  view.findViewById(R.id.layout_recycler_view_fav_movies_empty);

        loadFavMovies();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mFavMoviesAdapter.notifyDataSetChanged();
    }

    private void loadFavMovies() {

        List<MovieBrief> favMovieBriefs = FavouriteItems.getFavMovieBriefs(getContext());

        if (favMovieBriefs.isEmpty()) {
            mEmptyLayout.setVisibility(View.VISIBLE);
            return;
        }

        for (MovieBrief movieBrief : favMovieBriefs) {
            mFavMovies.add(movieBrief);
        }
        mFavMoviesAdapter.notifyDataSetChanged();
    }


}
