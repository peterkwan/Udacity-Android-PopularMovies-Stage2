package org.peterkwan.udacity.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONException;
import org.peterkwan.udacity.popularmovies.data.MovieTrailer;
import org.peterkwan.udacity.popularmovies.provider.MovieContract.MovieTrailerEntry;
import org.peterkwan.udacity.popularmovies.utils.JsonUtils;
import org.peterkwan.udacity.popularmovies.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MovieTrailerListAsyncTaskLoader extends AsyncTaskLoader<List<MovieTrailer>> {

    private static final String LOG_TAG = MovieTrailerListAsyncTaskLoader.class.getSimpleName();
    private static final String MOVIE_ID = "movieId";

    private List<MovieTrailer> trailerList;
    private final Bundle loaderArgs;
    private final ContentResolver resolver;

    public MovieTrailerListAsyncTaskLoader(Context context, Bundle args) {
        super(context);
        loaderArgs = args;
        resolver = context.getContentResolver();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (trailerList != null)
            deliverResult(trailerList);
        else
            forceLoad();
    }

    @Nullable
    @Override
    public List<MovieTrailer> loadInBackground() {
        if (loaderArgs == null || !loaderArgs.containsKey(MOVIE_ID))
            return null;

        String movieId = loaderArgs.getString(MOVIE_ID);

        if (NetworkUtils.isNetworkDisconnected(getContext()))
            return retrieveTrailerDataFromDatabase(movieId);

        try {
            String resultJson = NetworkUtils.retrieveMovieTrailerListFromTMdb(loaderArgs.getString(MOVIE_ID));
            List<MovieTrailer> trailerList = JsonUtils.constructMovieTrailerListFromJson(resultJson);

            updateTrailerListInDatabase(movieId, trailerList);

            return trailerList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error loading data from network", e);
            return retrieveTrailerDataFromDatabase(movieId);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing data from network", e);
            return null;
        }
    }

    @Override
    public void deliverResult(@Nullable List<MovieTrailer> data) {
        trailerList = data;
        super.deliverResult(data);
    }

    private List<MovieTrailer> retrieveTrailerDataFromDatabase(String movieId) {
        List<MovieTrailer> trailerList = null;

        if (resolver != null && movieId != null) {
            Cursor cursor = resolver.query(
                    MovieTrailerEntry.CONTENT_URI,
                    null,
                    null,
                    new String[] { movieId },
                    null);

            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    trailerList = new ArrayList<>();

                    cursor.moveToFirst();
                    for (int i = 0; i < cursor.getCount(); i++) {
                        MovieTrailer trailer = MovieTrailer.builder()
                                .id(cursor.getLong(cursor.getColumnIndex(MovieTrailerEntry.COLUMN_MOVIE_ID)))
                                .type(cursor.getString(cursor.getColumnIndex(MovieTrailerEntry.COLUMN_TYPE)))
                                .name(cursor.getString(cursor.getColumnIndex(MovieTrailerEntry.COLUMN_NAME)))
                                .videoUrl(cursor.getString(cursor.getColumnIndex(MovieTrailerEntry.COLUMN_VIDEO_URL)))
                                .imageUrl(cursor.getString(cursor.getColumnIndex(MovieTrailerEntry.COLUMN_THUMBNAIL_URL)))
                                .build();
                        trailerList.add(trailer);

                        cursor.moveToNext();
                    }
                }

                cursor.close();
            }
        }

        return trailerList;
    }

    private void updateTrailerListInDatabase(String movieId, List<MovieTrailer> trailerList) {
        if (resolver != null && movieId != null) {
            resolver.delete(MovieTrailerEntry.CONTENT_URI,
                    null,
                    new String[] { movieId });

            List<ContentValues> contentValueList = new ArrayList<>();
            for (MovieTrailer trailer : trailerList)
                contentValueList.add(constructContentValues(trailer));

            resolver.bulkInsert(MovieTrailerEntry.CONTENT_URI,
                    contentValueList.toArray(new ContentValues[0]));
        }
    }

    private ContentValues constructContentValues(MovieTrailer trailer) {
        ContentValues values = new ContentValues();
        values.put(MovieTrailerEntry.COLUMN_MOVIE_ID, trailer.getId());
        values.put(MovieTrailerEntry.COLUMN_TYPE, trailer.getType());
        values.put(MovieTrailerEntry.COLUMN_NAME, trailer.getName());
        values.put(MovieTrailerEntry.COLUMN_VIDEO_URL, trailer.getVideoUrl());
        values.put(MovieTrailerEntry.COLUMN_THUMBNAIL_URL, trailer.getImageUrl());
        return values;
    }

}
