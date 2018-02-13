package com.example.fady.movieimdb.AdapterViews;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.fady.movieimdb.DataFeed.movies.MovieCastBrief;
import com.example.fady.movieimdb.PersonDetails;
import com.example.fady.movieimdb.R;
import com.example.fady.movieimdb.Utilities.UrlsKey;

import java.util.List;

/**
 * Created by Fady on 2/6/2018.
 */

public class MovieCastsAdapter extends RecyclerView.Adapter<MovieCastsAdapter.CastViewHolder> {

    private Context mContext;
    private List<MovieCastBrief> mCasts;

    public MovieCastsAdapter(Context mContext, List<MovieCastBrief> mCasts) {
        this.mContext = mContext;
        this.mCasts = mCasts;
    }

    @Override
    public CastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new CastViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_cast_view, parent, false));
    }

    @Override
    public void onBindViewHolder(CastViewHolder holder, int position) {

        Glide.with(mContext.getApplicationContext()).load(UrlsKey.IMAGE_LOADING_BASE_URL_342 +
                mCasts.get(position).getProfilePath())
                .asBitmap()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.castImageView);



        if (mCasts.get(position).getName() != null) {
            holder.nameTextView.setText(mCasts.get(position).getName());
        }
        else {
            holder.nameTextView.setText("");
        }

        if (mCasts.get(position).getCharacter() != null) {
            holder.characterTextView.setText(mCasts.get(position).getCharacter());
        }
        else {
            holder.characterTextView.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return mCasts.size();
    }


    public class CastViewHolder extends RecyclerView.ViewHolder {

        public ImageView castImageView;
        public TextView nameTextView;
        public TextView characterTextView;

        public CastViewHolder(View itemView) {
            super(itemView);

            castImageView = itemView.findViewById(R.id.image_view_cast);
            nameTextView =  itemView.findViewById(R.id.text_view_cast_name);
            characterTextView =  itemView.findViewById(R.id.text_view_cast_as);

            castImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(mContext, PersonDetails.class);
                    intent.putExtra(UrlsKey.PERSON_ID, mCasts.get(getAdapterPosition()).getId());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
