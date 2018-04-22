package org.peterkwan.udacity.popularmovies;

import android.content.Intent;
import android.os.Bundle;

import org.peterkwan.udacity.popularmovies.data.Movie;

import butterknife.BindBool;
import butterknife.ButterKnife;

public class MovieDetailActivity extends BaseActivity {

    private static final String MOVIE = "movie";
    private Movie movie = null;

    @BindBool(R.bool.two_pane_layout)
    boolean isTwoPaneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && intent.getParcelableExtra(MOVIE) != null)
            movie = intent.getParcelableExtra(MOVIE);

        ButterKnife.bind(this);
        if (isTwoPaneLayout) {
            finish();
            return;
        }

        setContentView(R.layout.activity_movie_detail);
        setTitle(movie.getTitle());
        initMovieDetailFragment();

    }

    private void initMovieDetailFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(MOVIE, movie);

        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.movieDetailFragmentContainer, fragment)
                .commit();
    }

}
