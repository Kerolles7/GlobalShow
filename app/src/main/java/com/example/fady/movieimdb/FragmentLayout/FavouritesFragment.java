package com.example.fady.movieimdb.FragmentLayout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fady.movieimdb.AdapterViews.FavouritesPagerAdapter;
import com.example.fady.movieimdb.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        SmartTabLayout mSmartTabLayout = view.findViewById(R.id.tab_view_pager_fav);
        ViewPager mViewPager = view.findViewById(R.id.view_pager_fav);

        mViewPager.setAdapter(new FavouritesPagerAdapter(getChildFragmentManager(), getContext()));

        mSmartTabLayout.setViewPager(mViewPager);

        return view;
    }

}
