package org.peterkwan.udacity.popularmovies;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import butterknife.BindArray;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SoftwareItemListAdapter extends StringArrayListAdapter {

    @BindArray(R.array.software_list)
    String[] softwareList;

    @NonNull
    @Override
    public StringViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return onCreateViewHolder(R.layout.software_item, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull StringViewHolder holder, int position) {
        holder.itemTextView.setText(softwareList[position]);
    }

    @Override
    public int getItemCount() {
        return softwareList == null ? 0 : softwareList.length;
    }
}
