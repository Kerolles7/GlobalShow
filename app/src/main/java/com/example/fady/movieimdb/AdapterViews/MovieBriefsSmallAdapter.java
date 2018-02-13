package com.example.fady.movieimdb.AdapterViews;

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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.fady.movieimdb.DataFeed.movies.MovieBrief;
import com.example.fady.movieimdb.MovieDetails;
import com.example.fady.movieimdb.R;
import com.example.fady.movieimdb.Utilities.FavouriteItems;
import com.example.fady.movieimdb.Utilities.UrlsKey;

import java.util.List;

/**
 * Created by Fady on 2/3/2018.
 */

public class MovieBriefsSmallAdapter extends RecyclerView.Adapter<MovieBriefsSmallAdapter.MovieViewHolder>{

    private Context mContext;
    private List<MovieBrief> mMovies;

    public MovieBriefsSmallAdapter(Context context, List<MovieBrief> movies) {
        mContext = context;
        mMovies = movies;
    }


    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_show_small, parent, false));
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        Glide.with(mContext.getApplicationContext()).load(UrlsKey.IMAGE_LOADING_BASE_URL_342 +
                mMovies.get(position).getPosterPath())
                .asBitmap()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.moviePosterImageView);


    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        public CardView movieCard;
        public ImageView moviePosterImageView;



        public MovieViewHolder(View itemView) {
            super(itemView);

            movieCard =  itemView.findViewById(R.id.card_view_show_card);
            moviePosterImageView =  itemView.findViewById(R.id.image_view_show_card);


            moviePosterImageView.getLayoutParams().width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.45);
            moviePosterImageView.getLayoutParams().height = (int) ((mContext.getResources().getDisplayMetrics().widthPixels * 0.45) / 0.6);


            movieCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(mContext, MovieDetails.class);
                    intent.putExtra(UrlsKey.MOVIE_ID, mMovies.get(getAdapterPosition()).getId());
                    mContext.startActivity(intent);
                }
            });




        }
    }
}
