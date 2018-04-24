package org.peterkwan.udacity.popularmovies;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONException;
import org.peterkwan.udacity.popularmovies.data.Movie;
import org.peterkwan.udacity.popularmovies.provider.MovieContract.MostPopularMovieEntry;
import org.peterkwan.udacity.popularmovies.provider.MovieContract.MovieEntry;
import org.peterkwan.udacity.popularmovies.provider.MovieContract.TopRatedMovieEntry;
import org.peterkwan.udacity.popularmovies.utils.JsonUtils;
import org.peterkwan.udacity.popularmovies.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MovieListAsyncTaskLoader extends AsyncTaskLoader<List<Movie>> {

    private static final String LOG_TAG = MovieListAsyncTaskLoader.class.getSimpleName();
    private static final String SORT_ORDER = "sortOrder";

    private static final String MOST_POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String FAVORITE = "favorite";

    private List<Movie> movieList;
    private final Bundle loaderArgs;
    private final ContentResolver resolver;

    public MovieListAsyncTaskLoader(Context context, Bundle args) {
        super(context);
        loaderArgs = args;
        resolver = context.getContentResolver();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        boolean isFavoriteSelected = false;
        if (loaderArgs != null && loaderArgs.containsKey(SORT_ORDER)) {
            isFavoriteSelected = FAVORITE.equals(loaderArgs.getString(SORT_ORDER));
        }

        if (movieList != null && !isFavoriteSelected)
            deliverResult(movieList);
        else
            forceLoad();
    }

    @Nullable
    @Override
    public List<Movie> loadInBackground() {
        if (loaderArgs == null || !loaderArgs.containsKey(SORT_ORDER))
            return null;

        String sortOrder = loaderArgs.getString(SORT_ORDER);
        Uri contentUri = null;
        String movieIdColumn = null;
        String selection = null;

        switch (sortOrder) {
            case MOST_POPULAR:
                contentUri = MostPopularMovieEntry.CONTENT_URI;
                movieIdColumn = MostPopularMovieEntry.COLUMN_MOVIE_ID;
                break;
            case TOP_RATED:
                contentUri = TopRatedMovieEntry.CONTENT_URI;
                movieIdColumn = TopRatedMovieEntry.COLUMN_MOVIE_ID;
                break;
            case FAVORITE:
                contentUri = MovieEntry.CONTENT_URI;
                selection = MovieEntry.COLUMN_FAVORITE + " = 1";
                break;
            default:
        }

        if (NetworkUtils.isNetworkDisconnected(getContext()) || FAVORITE.equals(sortOrder)) {
            // retrieve data from database
            return retrieveMoviesFromDatabase(contentUri, selection);
        }

        try {
            String resultJson = NetworkUtils.retrieveMovieListFromTMdb(sortOrder);
            List<Movie> movieList = JsonUtils.constructMovieListFromJson(resultJson);

            // update database
            updateDatabase(contentUri, movieIdColumn, movieList);

            return movieList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error loading data from network", e);
            return retrieveMoviesFromDatabase(contentUri, null);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing network data", e);
            return null;
        }
    }

    @Override
    public void deliverResult(@Nullable List<Movie> data) {
        movieList = data;
        super.deliverResult(data);
    }

    private void updateDatabase(Uri contentUri, @Nullable String movieIdColumn, List<Movie> movieList) {
        if (resolver != null && contentUri != null) {
            List<ContentValues> contentValueList = new ArrayList<>();

            for (Movie movie : movieList) {
                String movieId = String.valueOf(movie.getId());
                Cursor cursor = resolver.query(
                        ContentUris.withAppendedId(MovieEntry.CONTENT_URI, movie.getId()),
                        null,
                        null,
                        new String[]{movieId},
                        null
                );

                if (cursor == null || cursor.getCount() == 0) {
                    insertMovie(movie);
                }

                if (cursor != null)
                    cursor.close();

                contentValueList.add(constructMovieIdContentValues(movieIdColumn, movieId));
            }

            ContentValues[] contentValues = contentValueList.toArray(new ContentValues[0]);
            resolver.delete(contentUri, null, null);
            resolver.bulkInsert(contentUri, contentValues);
        }
    }

    private List<Movie> retrieveMoviesFromDatabase(Uri contentUri, @Nullable String selection) {
        List<Movie> movieList = new ArrayList<>();

        if (resolver != null && contentUri != null) {
            Cursor cursor = resolver.query(
                    contentUri,
                    null,
                    selection,
                    null,
                    null
            );

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    Movie movie = Movie.builder()
                            .id(cursor.getLong(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID)))
                            .title(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE)))
                            .originalTitle(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_ORIGINAL_TITLE)))
                            .releaseDate(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE)))
                            .plotSynopsis(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_PLOT_SYNOPSIS)))
                            .backdropImagePath(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_BACKDROP_IMAGE)))
                            .posterImagePath(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_IMAGE)))
                            .userRating(cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_USER_RATING)))
                            .popularity(cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_POPULARITY)))
                            .isFavorite(cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_FAVORITE)) != 0)
                            .build();

                    movieList.add(movie);
                    cursor.moveToNext();
                }
            }

            if (cursor != null)
                cursor.close();
        }

        return movieList;
    }

    private void insertMovie(Movie movie) {
        ContentValues values = constructMovieContentValues(movie);
        resolver.insert(MovieEntry.CONTENT_URI, values);
    }

    private ContentValues constructMovieIdContentValues(String colName, String movieId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(colName, movieId);
        return contentValues;
    }

    private ContentValues constructMovieContentValues(Movie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
        contentValues.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MovieEntry.COLUMN_PLOT_SYNOPSIS, movie.getPlotSynopsis());
        contentValues.put(MovieEntry.COLUMN_BACKDROP_IMAGE, movie.getBackdropImagePath());
        contentValues.put(MovieEntry.COLUMN_POSTER_IMAGE, movie.getPosterImagePath());
        contentValues.put(MovieEntry.COLUMN_USER_RATING, movie.getUserRating());
        contentValues.put(MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
        contentValues.put(MovieEntry.COLUMN_FAVORITE, movie.isFavorite());

        return contentValues;
    }
}
