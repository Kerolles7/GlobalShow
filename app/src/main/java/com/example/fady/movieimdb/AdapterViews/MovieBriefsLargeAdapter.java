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
import com.example.fady.movieimdb.DataFeed.movies.MovieBrief;
import com.example.fady.movieimdb.MovieDetails;
import com.example.fady.movieimdb.R;
import com.example.fady.movieimdb.Utilities.FavouriteItems;
import com.example.fady.movieimdb.Utilities.MovieGenres;
import com.example.fady.movieimdb.Utilities.UrlsKey;

import java.util.List;

/**
 * Created by Fady on 2/3/2018.
 */

public class MovieBriefsLargeAdapter extends RecyclerView.Adapter<MovieBriefsLargeAdapter.MovieViewHolder> {

    private Context mContext;
    private List<MovieBrief> mMovies;

    public MovieBriefsLargeAdapter(Context context, List<MovieBrief> movies) {
        mContext = context;
        mMovies = movies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new MovieViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_show_large, parent, false));
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        Glide.with(mContext.getApplicationContext()).load(UrlsKey.IMAGE_LOADING_BASE_URL_780 +
                mMovies.get(position).getBackdropPath())
                .asBitmap()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.moviePosterImageView);


        if (mMovies.get(position).getTitle() != null) {
            holder.movieTitleTextView.setText(mMovies.get(position).getTitle());
        }
        else {
            holder.movieTitleTextView.setText("");
        }

        if (mMovies.get(position).getVoteAverage() != null && mMovies.get(position).getVoteAverage() > 0) {

            holder.movieRatingTextView.setVisibility(View.VISIBLE);

            holder.movieRatingTextView.setText(String.format("%.1f", mMovies.get(position).getVoteAverage())
                    + UrlsKey.RATING_SYMBOL);
        } else {
            holder.movieRatingTextView.setVisibility(View.GONE);
        }




    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }




    public class MovieViewHolder extends RecyclerView.ViewHolder {

        public CardView movieCard;
        public RelativeLayout imageLayout;
        public ImageView moviePosterImageView;
        public TextView movieTitleTextView, movieRatingTextView;



        public MovieViewHolder(View itemView) {
            super(itemView);


            movieCard = itemView.findViewById(R.id.card_view_show_card);
            imageLayout =  itemView.findViewById(R.id.image_layout_show_card);
            moviePosterImageView = itemView.findViewById(R.id.image_view_show_card);
            movieTitleTextView =  itemView.findViewById(R.id.text_view_title_show_card);
            movieRatingTextView =  itemView.findViewById(R.id.text_view_rating_show_card);


            imageLayout.getLayoutParams().width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.9);
            imageLayout.getLayoutParams().height = (int) ((mContext.getResources().getDisplayMetrics().widthPixels * 0.9) / 1.5);


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
