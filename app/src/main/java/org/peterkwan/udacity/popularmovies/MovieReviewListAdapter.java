package org.peterkwan.udacity.popularmovies;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.peterkwan.udacity.popularmovies.data.MovieReview;

import java.util.List;

import lombok.Setter;

public class MovieReviewListAdapter extends StringArrayListAdapter {

    @Setter
    private List<MovieReview> reviewList;

    public MovieReviewListAdapter(List<MovieReview> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public StringViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return onCreateViewHolder(R.layout.movie_review_item, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull StringViewHolder holder, int position) {
        MovieReview review = reviewList.get(position);
        holder.itemTextView.setText(review.getAuthor());

        if (holder.itemSecondaryTextView != null) {
            ((ExpandableTextView)holder.itemSecondaryTextView).setText(review.getContent());
        }

        if (holder.itemLayout != null) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            holder.itemLayout.setLayoutParams(layoutParams);
        }
    }

    @Override
    public int getItemCount() {
        if (reviewList == null || reviewList.isEmpty())
            return 0;

        return reviewList.size();
    }
}
