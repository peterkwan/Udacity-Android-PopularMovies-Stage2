package org.peterkwan.udacity.popularmovies;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Setter;

public abstract class StringArrayListAdapter extends RecyclerView.Adapter<StringArrayListAdapter.StringViewHolder> {

    @Setter
    protected ListItemClickListener itemClickListener;

    protected StringViewHolder onCreateViewHolder(int layoutId, @NonNull ViewGroup parent) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new StringViewHolder(rootView);
    }

    public interface ListItemClickListener {
        void onItemClick(int position);
    }

    public class StringViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.itemTextView)
        TextView itemTextView;

        @Nullable
        @BindView(R.id.itemSecondaryTextView)
        View itemSecondaryTextView;

        @Nullable
        @BindView(R.id.itemLayout)
        LinearLayout itemLayout;

        public StringViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null)
                itemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
