package org.peterkwan.udacity.popularmovies.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.peterkwan.udacity.popularmovies.provider.MovieContract.MostPopularMovieEntry;
import org.peterkwan.udacity.popularmovies.provider.MovieContract.MovieEntry;
import org.peterkwan.udacity.popularmovies.provider.MovieContract.MovieReviewEntry;
import org.peterkwan.udacity.popularmovies.provider.MovieContract.MovieTrailerEntry;
import org.peterkwan.udacity.popularmovies.provider.MovieContract.TopRatedMovieEntry;

public class MovieContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;
    public static final int MOVIE_TRAILERS = 110;
    public static final int MOVIE_REVIEWS = 120;
    public static final int MOST_POPULAR_MOVIES = 130;
    public static final int TOP_RATED_MOVIES = 140;

    private static final String LOG_TAG = MovieContentProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/" + MovieContract.PATH_TRAILER, MOVIE_TRAILERS);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/" + MovieContract.PATH_REVIEW, MOVIE_REVIEWS);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/" + MovieContract.PATH_MOST_POPULAR, MOST_POPULAR_MOVIES);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/" + MovieContract.PATH_TOP_RATED, TOP_RATED_MOVIES);
    }

    private MovieDBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                cursor = db.query(
                        MovieEntry.TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        MovieEntry.COLUMN_ID
                );
                break;
            case MOVIE_WITH_ID:
                cursor = db.query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{uri.getLastPathSegment()},
                        null,
                        null,
                        null
                );
                break;
            case MOVIE_TRAILERS:
                cursor = db.query(
                        MovieTrailerEntry.TABLE_NAME,
                        null,
                        MovieTrailerEntry.COLUMN_MOVIE_ID + " = ?",
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            case MOVIE_REVIEWS:
                cursor = db.query(
                        MovieReviewEntry.TABLE_NAME,
                        null,
                        MovieReviewEntry.COLUMN_MOVIE_ID + " = ?",
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            case MOST_POPULAR_MOVIES:
                cursor = db.rawQuery("SELECT " + MovieEntry.TABLE_NAME + ".* FROM " + MovieEntry.TABLE_NAME + ", " + MostPopularMovieEntry.TABLE_NAME
                        + " WHERE " + MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_MOVIE_ID + " = " + MostPopularMovieEntry.TABLE_NAME + "." + MostPopularMovieEntry.COLUMN_MOVIE_ID,
                        null);
                break;
            case TOP_RATED_MOVIES:
                cursor = db.rawQuery("SELECT " + MovieEntry.TABLE_NAME + ".* FROM " + MovieEntry.TABLE_NAME + ", " + TopRatedMovieEntry.TABLE_NAME
                                + " WHERE " + MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_MOVIE_ID + " = " + TopRatedMovieEntry.TABLE_NAME + "." + TopRatedMovieEntry.COLUMN_MOVIE_ID,
                        null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI : " + uri);
        }

        Context context = getContext();
        if (context != null)
            cursor.setNotificationUri(context.getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long rowId;

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                rowId = dbHelper.getWritableDatabase().insert(
                        MovieEntry.TABLE_NAME,
                        null,
                        values
                );
                if (rowId > 0)
                    return ContentUris.withAppendedId(MovieEntry.CONTENT_URI, rowId);
                else {
                    Log.e(LOG_TAG, "Error inserting data for " + uri);
                    return null;
                }
            default:
                throw new UnsupportedOperationException("Unknown URI : " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int recordCount;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case MOVIE_TRAILERS:
                recordCount = db.delete(
                        MovieTrailerEntry.TABLE_NAME,
                        MovieTrailerEntry.COLUMN_MOVIE_ID + " = ?",
                        selectionArgs
                );
                break;
            case MOVIE_REVIEWS:
                recordCount = db.delete(
                        MovieReviewEntry.TABLE_NAME,
                        MovieReviewEntry.COLUMN_MOVIE_ID + " = ?",
                        selectionArgs
                );
                break;
            case MOST_POPULAR_MOVIES:
                recordCount = db.delete(
                        MostPopularMovieEntry.TABLE_NAME,
                        null,
                        null
                );
                break;
            case TOP_RATED_MOVIES:
                recordCount = db.delete(
                        TopRatedMovieEntry.TABLE_NAME,
                        null,
                        null
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI : " + uri);
        }

        if (recordCount > 0)
            notifyDataSetChanged(uri);

        return recordCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int recordCount;

        switch (sUriMatcher.match(uri)) {
            case MOVIE_WITH_ID:
                recordCount = dbHelper.getWritableDatabase().update(
                        MovieEntry.TABLE_NAME,
                        values,
                        MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{uri.getLastPathSegment()}
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI : " + uri);
        }

        if (recordCount > 0)
            notifyDataSetChanged(uri);

        return recordCount;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch (sUriMatcher.match(uri)) {
            case MOVIE_TRAILERS:
                return bulkInsert(uri, MovieTrailerEntry.TABLE_NAME, values);
            case MOVIE_REVIEWS:
                return bulkInsert(uri, MovieReviewEntry.TABLE_NAME, values);
            case MOST_POPULAR_MOVIES:
                return bulkInsert(uri, MostPopularMovieEntry.TABLE_NAME, values);
            case TOP_RATED_MOVIES:
                return bulkInsert(uri, TopRatedMovieEntry.TABLE_NAME, values);
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private int bulkInsert(@NonNull Uri uri, @NonNull String tableName, @NonNull ContentValues[] values) {
        int recordCount = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                long id = db.insert(
                        tableName,
                        null,
                        value
                );
                if (id != -1)
                    recordCount++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (recordCount > 0)
            notifyDataSetChanged(uri);

        return recordCount;
    }

    private void notifyDataSetChanged(@NonNull Uri uri) {
        Context context = getContext();
        if (context != null && context.getContentResolver() != null)
            context.getContentResolver().notifyChange(uri, null);
    }
}
