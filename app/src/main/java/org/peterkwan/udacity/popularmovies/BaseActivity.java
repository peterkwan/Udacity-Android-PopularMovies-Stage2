package org.peterkwan.udacity.popularmovies;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected final FragmentManager fragmentManager;

    public BaseActivity() {
        fragmentManager = getSupportFragmentManager();
    }

    protected void replaceFragment(int containerId, Fragment newFragment, @Nullable String fragmentTag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragmentTag == null)
            transaction.replace(containerId, newFragment)
                    .commit();
        else
            transaction.replace(containerId, newFragment, fragmentTag)
                    .addToBackStack(fragmentTag)
                    .commit();
    }

}
