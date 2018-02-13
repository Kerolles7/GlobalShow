package com.example.fady.movieimdb.AdapterViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.fady.movieimdb.DataFeed.tvShow.TVShowBrief;
import com.example.fady.movieimdb.R;
import com.example.fady.movieimdb.TVShowDetails;
import com.example.fady.movieimdb.Utilities.FavouriteItems;
import com.example.fady.movieimdb.Utilities.TVShowGenres;
import com.example.fady.movieimdb.Utilities.UrlsKey;

import java.util.List;

/**
 * Created by Fady on 2/4/2018.
 */

public class TVShowBriefsLargeAdapter extends RecyclerView.Adapter<TVShowBriefsLargeAdapter.TVShowViewHolder> {

    private Context mContext;
    private List<TVShowBrief> mTVShows;

    public TVShowBriefsLargeAdapter(Context context, List<TVShowBrief> tvShows) {
        mContext = context;
        mTVShows = tvShows;
    }

    @Override
    public TVShowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new TVShowViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_show_large, parent, false));
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(TVShowViewHolder holder, int position) {

        Glide.with(mContext.getApplicationContext()).load(UrlsKey.IMAGE_LOADING_BASE_URL_780
                + mTVShows.get(position).getBackdropPath())
                .asBitmap()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.tvShowPosterImageView);




        if (mTVShows.get(position).getName() != null) {
            holder.tvShowTitleTextView.setText(mTVShows.get(position).getName());
        }
        else
        {
            holder.tvShowTitleTextView.setText("");}

        if (mTVShows.get(position).getVoteAverage() != null && mTVShows.get(position).getVoteAverage() > 0) {

            holder.tvShowRatingTextView.setVisibility(View.VISIBLE);

            holder.tvShowRatingTextView.setText(String.format("%.1f", mTVShows.get(position).getVoteAverage())
                    + UrlsKey.RATING_SYMBOL);
        }
        else {
            holder.tvShowRatingTextView.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mTVShows.size();
    }



    public class TVShowViewHolder extends RecyclerView.ViewHolder {

        public CardView tvShowCard;
        public RelativeLayout imageLayout;
        public ImageView tvShowPosterImageView;
        public TextView tvShowTitleTextView, tvShowRatingTextView;


        public TVShowViewHolder(View itemView) {
            super(itemView);
            tvShowCard =  itemView.findViewById(R.id.card_view_show_card);
            imageLayout =  itemView.findViewById(R.id.image_layout_show_card);
            tvShowPosterImageView =  itemView.findViewById(R.id.image_view_show_card);
            tvShowTitleTextView =  itemView.findViewById(R.id.text_view_title_show_card);
            tvShowRatingTextView = itemView.findViewById(R.id.text_view_rating_show_card);


            imageLayout.getLayoutParams().width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.9);
            imageLayout.getLayoutParams().height = (int) ((mContext.getResources().getDisplayMetrics().widthPixels * 0.9) / 1.5);


            tvShowCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, TVShowDetails.class);
                    intent.putExtra(UrlsKey.TV_SHOW_ID, mTVShows.get(getAdapterPosition()).getId());
                    mContext.startActivity(intent);
                }
            });


        }
    }


}
