package org.peterkwan.udacity.popularmovies.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.peterkwan.udacity.popularmovies.provider.MovieContract.MostPopularMovieEntry;
import org.peterkwan.udacity.popularmovies.provider.MovieContract.MovieEntry;
import org.peterkwan.udacity.popularmovies.provider.MovieContract.MovieReviewEntry;
import org.peterkwan.udacity.popularmovies.provider.MovieContract.MovieTrailerEntry;
import org.peterkwan.udacity.popularmovies.provider.MovieContract.TopRatedMovieEntry;

public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pop_movie.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_MOVIE_TABLE_SQL
            = "CREATE TABLE " + MovieEntry.TABLE_NAME + "("
            + MovieEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE, "
            + MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, "
            + MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT, "
            + MovieEntry.COLUMN_RELEASE_DATE + " TEXT, "
            + MovieEntry.COLUMN_PLOT_SYNOPSIS + " TEXT, "
            + MovieEntry.COLUMN_USER_RATING + " REAL, "
            + MovieEntry.COLUMN_POPULARITY + " REAL, "
            + MovieEntry.COLUMN_FAVORITE + " INTEGER, "
            + MovieEntry.COLUMN_BACKDROP_IMAGE + " TEXT, "
            + MovieEntry.COLUMN_POSTER_IMAGE + " TEXT);";

    private static final String CREATE_MOVIE_REVIEW_TABLE_SQL
            = "CREATE TABLE " + MovieReviewEntry.TABLE_NAME + "("
            + MovieReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
            + MovieReviewEntry.COLUMN_AUTHOR + " TEXT, "
            + MovieReviewEntry.COLUMN_CONTENT + " TEXT);";

    private static final String CREATE_MOVIE_TRAILER_TABLE_SQL
            = "CREATE TABLE " + MovieTrailerEntry.TABLE_NAME + "("
            + MovieTrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
            + MovieTrailerEntry.COLUMN_NAME + " TEXT NOT NULL, "
            + MovieTrailerEntry.COLUMN_TYPE + " TEXT, "
            + MovieTrailerEntry.COLUMN_THUMBNAIL_URL + " TEXT NOT NULL, "
            + MovieTrailerEntry.COLUMN_VIDEO_URL + " TEXT NOT NULL);";

    private static final String CREATE_MOST_POPULAR_MOVIE_TABLE_SQL
            = "CREATE TABLE " + MostPopularMovieEntry.TABLE_NAME + "("
            + MostPopularMovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE);";

    private static final String CREATE_TOP_RATED_MOVIE_TABLE_SQL
            = "CREATE TABLE " + TopRatedMovieEntry.TABLE_NAME + "("
            + TopRatedMovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE);";

    MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MOVIE_TABLE_SQL);
        db.execSQL(CREATE_MOST_POPULAR_MOVIE_TABLE_SQL);
        db.execSQL(CREATE_TOP_RATED_MOVIE_TABLE_SQL);
        db.execSQL(CREATE_MOVIE_REVIEW_TABLE_SQL);
        db.execSQL(CREATE_MOVIE_TRAILER_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion <= 1) {
            db.execSQL("DROP TABLE IF EXISTS " + TopRatedMovieEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + MostPopularMovieEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + MovieTrailerEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + MovieReviewEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
            onCreate(db);
        }

    }

}
