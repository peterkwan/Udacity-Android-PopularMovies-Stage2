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
import org.peterkwan.udacity.popularmovies.data.MovieReview;
import org.peterkwan.udacity.popularmovies.provider.MovieContract.MovieReviewEntry;
import org.peterkwan.udacity.popularmovies.utils.JsonUtils;
import org.peterkwan.udacity.popularmovies.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MovieReviewListAsyncTaskLoader extends AsyncTaskLoader<List<MovieReview>> {

    private static final String LOG_TAG = MovieReviewListAsyncTaskLoader.class.getSimpleName();
    private static final String MOVIE_ID = "movieId";

    private List<MovieReview> reviewList;
    private final Bundle loaderArgs;
    private final ContentResolver resolver;

    public MovieReviewListAsyncTaskLoader(Context context, Bundle args) {
        super(context);
        loaderArgs = args;
        resolver = context.getContentResolver();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (reviewList != null)
            deliverResult(reviewList);
        else
            forceLoad();
    }

    @Nullable
    @Override
    public List<MovieReview> loadInBackground() {
        if (loaderArgs == null || !loaderArgs.containsKey(MOVIE_ID))
            return null;

        String movieId = loaderArgs.getString(MOVIE_ID);

        if (NetworkUtils.isNetworkDisconnected(getContext()))
            return retrieveReviewDataFromDatabase(movieId);

        try {
            String resultJson = NetworkUtils.retrieveMovieReviewListFromTMdb(loaderArgs.getString(MOVIE_ID));
            List<MovieReview> reviewList = JsonUtils.constructMovieReviewListFromJson(resultJson);

            updateReviewListInDatabase(movieId, reviewList);

            return reviewList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error loading data from network", e);
            return retrieveReviewDataFromDatabase(movieId);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing data", e);
            return null;
        }
    }

    @Override
    public void deliverResult(@Nullable List<MovieReview> data) {
        reviewList = data;
        super.deliverResult(data);
    }

    private List<MovieReview> retrieveReviewDataFromDatabase(String movieId) {
        List<MovieReview> reviewList = null;

        if (resolver != null && movieId != null) {
            Cursor cursor = resolver.query(
                    MovieReviewEntry.CONTENT_URI,
                    null,
                    null,
                    new String[] { movieId },
                    null);

            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    reviewList = new ArrayList<>();

                    cursor.moveToFirst();
                    for (int i = 0; i < cursor.getCount(); i++) {
                        MovieReview review = MovieReview.builder()
                                .id(cursor.getLong(cursor.getColumnIndex(MovieReviewEntry.COLUMN_MOVIE_ID)))
                                .author(cursor.getString(cursor.getColumnIndex(MovieReviewEntry.COLUMN_AUTHOR)))
                                .content(cursor.getString(cursor.getColumnIndex(MovieReviewEntry.COLUMN_CONTENT)))
                                .build();
                        reviewList.add(review);

                        cursor.moveToNext();
                    }
                }

                cursor.close();
            }
        }

        return reviewList;
    }

    private void updateReviewListInDatabase(String movieId, List<MovieReview> reviewList) {
        if (resolver != null && movieId != null) {
            resolver.delete(MovieReviewEntry.CONTENT_URI,
                    null,
                    new String[] { movieId });

            if (reviewList != null) {
                List<ContentValues> contentValueList = new ArrayList<>();
                for (MovieReview review : reviewList)
                    contentValueList.add(constructContentValues(review));

                resolver.bulkInsert(MovieReviewEntry.CONTENT_URI,
                        contentValueList.toArray(new ContentValues[0]));
            }
        }
    }

    private ContentValues constructContentValues(MovieReview review) {
        ContentValues values = new ContentValues();
        values.put(MovieReviewEntry.COLUMN_MOVIE_ID, review.getId());
        values.put(MovieReviewEntry.COLUMN_AUTHOR, review.getAuthor());
        values.put(MovieReviewEntry.COLUMN_CONTENT, review.getContent());
        return values;
    }
}
