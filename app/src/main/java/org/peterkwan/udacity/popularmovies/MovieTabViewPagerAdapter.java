package org.peterkwan.udacity.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MovieTabViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final int REVIEW_TAB = 1;
    private static final int TRAILER_TAB = 0;
    private static final String MOVIE_ID = "movieId";

    private final int numOfTabs;
    private final String movieId;

    public MovieTabViewPagerAdapter(FragmentManager fragmentManager, int numOfTabs, String movieId) {
        super(fragmentManager);
        this.numOfTabs = numOfTabs;
        this.movieId = movieId;
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {
            case TRAILER_TAB:
                MovieTrailerFragment trailerFragment = new MovieTrailerFragment();
                trailerFragment.setArguments(constructFragmentArgs(movieId));
                return trailerFragment;
            case REVIEW_TAB:
                MovieReviewFragment reviewFragment = new MovieReviewFragment();
                reviewFragment.setArguments(constructFragmentArgs(movieId));
                return reviewFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

    private Bundle constructFragmentArgs(String movieId) {
        Bundle args = new Bundle();
        args.putString(MOVIE_ID, movieId);
        return args;
    }
}
