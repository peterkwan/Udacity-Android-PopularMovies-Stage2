package org.peterkwan.udacity.popularmovies;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.peterkwan.udacity.popularmovies.data.Movie;
import org.peterkwan.udacity.popularmovies.provider.MovieContract;

import butterknife.BindBool;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MovieListFragment.OnMovieListClickListener {

    private static final String MOVIE = "movie";

    private Movie movie;

    @BindBool(R.bool.two_pane_layout)
    boolean isTwoPaneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE))
            movie = savedInstanceState.getParcelable(MOVIE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (movie != null)
            displayMovieDetail();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieItemSelected(Movie movie) {
        this.movie = movie;
        displayMovieDetail();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MOVIE, movie);
        super.onSaveInstanceState(outState);
    }

    private void displayMovieDetail() {
        retrieveMovieFavoriteFlagFromDatabase();

        if (isTwoPaneLayout) {
            initMovieDetailFragment();
        }
        else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MOVIE, movie);
            startActivity(intent);
        }
    }

    private void initMovieDetailFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(MOVIE, movie);

        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);

        replaceFragment(R.id.movieDetailFragmentContainer, fragment, null);
    }

    private void retrieveMovieFavoriteFlagFromDatabase() {
        Cursor cursor = getContentResolver().query(
                ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, movie.getId()),
                new String[] {MovieContract.MovieEntry.COLUMN_FAVORITE},
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                movie.setFavorite(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE)) != 0);
            }
            cursor.close();
        }
    }
}
