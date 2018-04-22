package org.peterkwan.udacity.popularmovies;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindArray;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AboutItemListAdapter extends StringArrayListAdapter {

    @BindArray(R.array.about_list)
    String[] aboutList;

    @NonNull
    @Override
    public StringViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return onCreateViewHolder(R.layout.about_item, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull StringViewHolder holder, int position) {
        holder.itemTextView.setText(aboutList[position]);

        if (holder.itemSecondaryTextView != null) {
            if (position == 0)
                ((TextView)holder.itemSecondaryTextView).setText(BuildConfig.VERSION_NAME);
            else
                holder.itemSecondaryTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return aboutList == null ? 0 : aboutList.length;
    }
}
