package com.example.fady.movieimdb.AdapterViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.fady.movieimdb.DataFeed.search.SearchResult;
import com.example.fady.movieimdb.MovieDetails;
import com.example.fady.movieimdb.PersonDetails;
import com.example.fady.movieimdb.R;
import com.example.fady.movieimdb.TVShowDetails;
import com.example.fady.movieimdb.Utilities.UrlsKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Fady on 2/10/2018.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ResultViewHolder> {

    private Context mContext;
    private List<SearchResult> mSearchResults;

    public SearchAdapter(Context mContext, List<SearchResult> mSearchResults) {


        this.mContext = mContext;
        this.mSearchResults = mSearchResults;
    }


    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ResultViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {

        Glide.with(mContext.getApplicationContext()).load(UrlsKey.IMAGE_LOADING_BASE_URL_342 +
                mSearchResults.get(position).getPosterPath()).asBitmap()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.posterImageView);



        if (mSearchResults.get(position).getName() != null && !mSearchResults.get(position).getName().trim().isEmpty())
        {
            holder.nameTextView.setText(mSearchResults.get(position).getName());}
        else{
            holder.nameTextView.setText("");}



        if (mSearchResults.get(position).getMediaType() != null && mSearchResults.get(position).getMediaType().equals("movie"))
        {
            holder.mediaTypeTextView.setText(R.string.movie);}

        else if (mSearchResults.get(position).getMediaType() != null && mSearchResults.get(position).getMediaType().equals("tv"))
        {
            holder.mediaTypeTextView.setText(R.string.tv_show);}

        else if (mSearchResults.get(position).getMediaType() != null && mSearchResults.get(position).getMediaType().equals("person"))
        {
            holder.mediaTypeTextView.setText(R.string.person);}

        else{
            holder.mediaTypeTextView.setText("");}




        if (mSearchResults.get(position).getOverview() != null && !mSearchResults.get(position).getOverview().trim().isEmpty())
        {
            holder.overviewTextView.setText(mSearchResults.get(position).getOverview());}
        else
        {
            holder.overviewTextView.setText("");}

        if (mSearchResults.get(position).getReleaseDate() != null && !mSearchResults.get(position).getReleaseDate().trim().isEmpty())
        {

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");

            try {

                Date releaseDate = sdf1.parse(mSearchResults.get(position).getReleaseDate());
                holder.yearTextView.setText(sdf2.format(releaseDate));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            holder.yearTextView.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return mSearchResults.size();
    }



    public class ResultViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView posterImageView;
        public TextView nameTextView, mediaTypeTextView, overviewTextView, yearTextView;


        public ResultViewHolder(View itemView) {
            super(itemView);

            cardView =  itemView.findViewById(R.id.card_view_search);
            posterImageView =  itemView.findViewById(R.id.image_view_poster_search);
            nameTextView =  itemView.findViewById(R.id.text_view_name_search);
            mediaTypeTextView =  itemView.findViewById(R.id.text_view_media_type_search);
            overviewTextView =  itemView.findViewById(R.id.text_view_overview_search);
            yearTextView =  itemView.findViewById(R.id.text_view_year_search);


            posterImageView.getLayoutParams().width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.31);
            posterImageView.getLayoutParams().height = (int) ((mContext.getResources().getDisplayMetrics().widthPixels * 0.31) / 0.66);


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mSearchResults.get(getAdapterPosition()).getMediaType().equals("movie")) {

                        Intent intent = new Intent(mContext, MovieDetails.class);
                        intent.putExtra(UrlsKey.MOVIE_ID, mSearchResults.get(getAdapterPosition()).getId());

                        mContext.startActivity(intent);
                    }

                    else if (mSearchResults.get(getAdapterPosition()).getMediaType().equals("tv")) {

                        Intent intent = new Intent(mContext, TVShowDetails.class);
                        intent.putExtra(UrlsKey.TV_SHOW_ID, mSearchResults.get(getAdapterPosition()).getId());
                        mContext.startActivity(intent);
                    }


                    else if (mSearchResults.get(getAdapterPosition()).getMediaType().equals("person")) {

                        Intent intent = new Intent(mContext, PersonDetails.class);
                        intent.putExtra(UrlsKey.PERSON_ID, mSearchResults.get(getAdapterPosition()).getId());

                        mContext.startActivity(intent);
                    }
                }
            });

        }
    }
}
