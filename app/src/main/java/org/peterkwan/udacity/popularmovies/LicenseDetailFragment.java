package org.peterkwan.udacity.popularmovies;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;


/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class LicenseDetailFragment extends Fragment {

    private static final String SOFTWARE = "software";

    private int softwareListIndex = -1;

    @BindView(R.id.licenseDetailTextView)
    TextView textView;

    @BindArray(R.array.software_license)
    String[] softwareLicenses;

    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null)
            softwareListIndex = savedInstanceState.getInt(SOFTWARE);
        else {
            Bundle args = getArguments();
            if (args != null && args.containsKey(SOFTWARE))
                softwareListIndex = args.getInt(SOFTWARE);
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_license_detail, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        if (softwareListIndex >= 0) {
            textView.setText(softwareLicenses[softwareListIndex]);
            textView.setMovementMethod(new ScrollingMovementMethod());
            textView.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(SOFTWARE, softwareListIndex);
        super.onSaveInstanceState(outState);
    }
}
