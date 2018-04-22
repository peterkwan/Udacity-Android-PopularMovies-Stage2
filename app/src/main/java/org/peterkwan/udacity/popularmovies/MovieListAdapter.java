package org.peterkwan.udacity.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.peterkwan.udacity.popularmovies.data.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Setter;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private final MovieListItemClickListener mListener;

    @Setter
    private int imageWidth;

    @Setter
    private int imageHeight;

    public MovieListAdapter(MovieListItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        Picasso.get()
                .load(movie.getPosterImagePath())
                .into(holder.imageView);
        holder.imageView.getLayoutParams().width = imageWidth;
        holder.imageView.getLayoutParams().height = imageHeight;
        holder.imageView.setContentDescription(movie.getTitle());
    }

    @Override
    public int getItemCount() {
        if (movieList == null || movieList.isEmpty())
            return 0;

        return movieList.size();
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    public interface MovieListItemClickListener {
        void onItemClicked(Movie movie);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.thumbnailImageView)
        ImageView imageView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Movie movie = movieList.get(getAdapterPosition());
            mListener.onItemClicked(movie);
        }
    }
}
