package org.peterkwan.udacity.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import java.util.List;

import butterknife.BindBool;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity implements AboutFragment.OnListItemClickListener,
        SoftwareListFragment.OnListItemClickListener {

    private static final String SOFTWARE = "software";
    private static final String SOFTWARE_LIST_FRAGMENT = "softwareListFragment";
    private static final String LICENSE_DETAIL_FRAGMENT = "licenseDetailFragment";
    private static final String FRAGMENT = "fragment";

    @BindBool(R.bool.two_pane_layout)
    boolean isTwoPaneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ButterKnife.bind(this);

        if (savedInstanceState == null || !savedInstanceState.containsKey(FRAGMENT)) {
            fragmentManager.beginTransaction()
                    .add(R.id.aboutFragmentContainer, new AboutFragment())
                    .commit();
        }
        else
            restoreFragmentStates(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onAboutItemSelected(int index) {
        if (index > 0)
            replaceFragment(R.id.aboutFragmentContainer, new SoftwareListFragment(), SOFTWARE_LIST_FRAGMENT);
    }

    @Override
    public void onSoftwareItemSelected(int index) {
        Bundle args = new Bundle();
        args.putInt(SOFTWARE, index);

        LicenseDetailFragment fragment = new LicenseDetailFragment();
        fragment.setArguments(args);

        replaceFragment(isTwoPaneLayout ? R.id.licenseDetailFragmentContainer : R.id.aboutFragmentContainer, fragment,  LICENSE_DETAIL_FRAGMENT);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        List<Fragment> fragmentList = fragmentManager.getFragments();
        outState.putString(FRAGMENT, fragmentList.get(fragmentList.size() - 1).getTag());
        super.onSaveInstanceState(outState);
    }

    private void restoreFragmentStates(Bundle savedInstanceState) {
        String fragmentTag = savedInstanceState.getString(FRAGMENT);

        if (LICENSE_DETAIL_FRAGMENT.equals(fragmentTag)) {
            LicenseDetailFragment fragment = (LicenseDetailFragment) fragmentManager.findFragmentByTag(fragmentTag);
            if (fragment != null) {
                fragmentManager.popBackStack();
            }
        }
    }
}
