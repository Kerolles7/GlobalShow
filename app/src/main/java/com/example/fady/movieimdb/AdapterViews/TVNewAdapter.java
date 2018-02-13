package com.example.fady.movieimdb.AdapterViews;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.fady.movieimdb.DataFeed.tvShow.TVShowBrief;
import com.example.fady.movieimdb.R;
import com.example.fady.movieimdb.TVShowDetails;
import com.example.fady.movieimdb.Utilities.UrlsKey;

import java.util.List;

/**
 * Created by Fady on 2/13/2018.
 */

public class TVNewAdapter extends RecyclerView.Adapter<TVNewAdapter.TVShowViewHolder> {

    private Context mContext;
    private List<TVShowBrief> mTVShows;

    public TVNewAdapter(Context context, List<TVShowBrief> tvShows) {
        mContext = context;
        mTVShows = tvShows;
    }

    @Override
    public TVShowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TVShowViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_show_small, parent, false));
    }

    @Override
    public void onBindViewHolder(TVShowViewHolder holder, int position) {

        Glide.with(mContext.getApplicationContext()).load(UrlsKey.IMAGE_LOADING_BASE_URL_342 +
                mTVShows.get(position).getPosterPath())
                .asBitmap()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.tvShowPosterImageView);


    }

    @Override
    public int getItemCount() {
        return mTVShows.size();
    }

    public class TVShowViewHolder extends RecyclerView.ViewHolder {

        public CardView tvShowCard;
        public ImageView tvShowPosterImageView;


        public TVShowViewHolder(View itemView) {
            super(itemView);
            tvShowCard =  itemView.findViewById(R.id.card_view_show_card);
            tvShowPosterImageView =  itemView.findViewById(R.id.image_view_show_card);


            tvShowPosterImageView.getLayoutParams().width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.5);
            tvShowPosterImageView.getLayoutParams().height = (int) ((mContext.getResources().getDisplayMetrics().widthPixels * 0.5) / 0.6);


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
