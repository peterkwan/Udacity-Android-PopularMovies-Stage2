package org.peterkwan.udacity.popularmovies;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.peterkwan.udacity.popularmovies.data.MovieTrailer;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Setter;

public class MovieTrailerListAdapter extends RecyclerView.Adapter<MovieTrailerListAdapter.MovieTrailerViewHolder> {

    @Setter
    private List<MovieTrailer> trailerList;
    private final OnItemClickListener itemClickListener;

    @BindString(R.string.trailer_title)
    String trailerTitle;

    public MovieTrailerListAdapter(List<MovieTrailer> trailerList, OnItemClickListener itemClickListener) {
        this.trailerList = trailerList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MovieTrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_trailer_item, parent, false);

        ButterKnife.bind(this, rootView);

        return new MovieTrailerViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieTrailerViewHolder holder, int position) {
        MovieTrailer movieTrailer = trailerList.get(position);

        Picasso.get()
                .load(movieTrailer.getImageUrl())
                .into(holder.thumbnailImageView);

        holder.trailerTextView.setText(String.format(trailerTitle, movieTrailer.getType(), movieTrailer.getName()));

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        holder.trailerItemLayout.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        if (trailerList == null || trailerList.isEmpty())
            return 0;

        return trailerList.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(String url);
    }

    public class MovieTrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.trailerThumbnailImageView)
        ImageView thumbnailImageView;

        @BindView(R.id.trailerTextView)
        TextView trailerTextView;

        @BindView(R.id.trailerItemLayout)
        View trailerItemLayout;

        public MovieTrailerViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            MovieTrailer movieTrailer = trailerList.get(position);
            if (itemClickListener != null)
                itemClickListener.onItemClicked(movieTrailer.getVideoUrl());
        }
    }
}
