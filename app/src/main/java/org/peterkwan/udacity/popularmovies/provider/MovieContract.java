package org.peterkwan.udacity.popularmovies.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "org.peterkwan.udacity.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_MOST_POPULAR = "popular";
    public static final String PATH_TOP_RATED = "top_rated";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_PLOT_SYNOPSIS = "plot_synopsis";
        public static final String COLUMN_POSTER_IMAGE = "poster_image";
        public static final String COLUMN_BACKDROP_IMAGE = "backdrop_image";
        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_FAVORITE = "favorite";
    }

    public static final class MovieReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).appendPath(PATH_REVIEW).build();

        public static final String TABLE_NAME = "movie_review";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
    }

    public static final class MovieTrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).appendPath(PATH_TRAILER).build();

        public static final String TABLE_NAME = "movie_trailer";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_VIDEO_URL = "video_url";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";
    }

    public static final class MostPopularMovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).appendPath(PATH_MOST_POPULAR).build();

        public static final String TABLE_NAME = "popular_movie";
        public static final String COLUMN_MOVIE_ID = "movie_id";
    }

    public static final class TopRatedMovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).appendPath(PATH_TOP_RATED).build();

        public static final String TABLE_NAME = "top_rated_movie";
        public static final String COLUMN_MOVIE_ID = "movie_id";

    }
}
