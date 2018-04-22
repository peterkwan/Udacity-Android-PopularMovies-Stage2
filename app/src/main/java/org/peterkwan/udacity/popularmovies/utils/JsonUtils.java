package org.peterkwan.udacity.popularmovies.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.peterkwan.udacity.popularmovies.data.Movie;
import org.peterkwan.udacity.popularmovies.data.MovieReview;
import org.peterkwan.udacity.popularmovies.data.MovieTrailer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class JsonUtils {

    private static final String RESULTS = "results";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String PLOT_SYNOPSIS = "overview";
    private static final String BACKDROP_PATH = "backdrop_path";
    private static final String POSTER_PATH = "poster_path";
    private static final String RELEASE_DATE = "release_date";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String POPULARITY = "popularity";
    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";
    private static final String YOUTUBE = "youtube";
    private static final String NAME = "name";
    private static final String SOURCE = "source";
    private static final String TYPE = "type";

    private static final String IMAGE_URL = "http://image.tmdb.org/t/p/w%d%s";
    private static final String YOUTUBE_IMAGE_URL = "https://i.ytimg.com/vi/%s/hqdefault.jpg";
    private static final String YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v=%s";

    private static final int POSTER_IMAGE_WIDTH = 500;
    private static final int BACKDROP_IMAGE_WIDTH = 1280;

    public static List<Movie> constructMovieListFromJson(@Nullable String jsonString) throws JSONException {
        if (StringUtils.isEmpty(jsonString))
            return null;

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonArray = jsonObject.optJSONArray(RESULTS);
        if (jsonArray == null || jsonArray.length() == 0)
            return null;

        List<Movie> movieList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Movie movie = constructMovieFromJson(jsonArray.optJSONObject(i));
            if (movie != null)
                movieList.add(movie);
        }
        return movieList;
    }

    public static List<MovieReview> constructMovieReviewListFromJson(@Nullable String jsonString) throws JSONException {
        if (StringUtils.isEmpty(jsonString))
            return null;

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonArray = jsonObject.optJSONArray(RESULTS);
        if (jsonArray == null)
            return null;

        List<MovieReview> reviewList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            MovieReview review = constructMovieReviewFromJson(jsonArray.optJSONObject(i));
            if (review != null) {
                review.setId(jsonObject.optLong(ID));
                reviewList.add(review);
            }
        }

        return reviewList;
    }

    public static List<MovieTrailer> constructMovieTrailerListFromJson(@Nullable String jsonString) throws JSONException {
        if (StringUtils.isEmpty(jsonString))
            return null;

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonArray = jsonObject.optJSONArray(YOUTUBE);
        if (jsonArray == null)
            return null;

        List<MovieTrailer> trailerList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            MovieTrailer trailer = constructMovieTrailerFromJson(jsonArray.optJSONObject(i));
            if (trailer != null) {
                trailer.setId(jsonObject.optLong(ID));
                trailerList.add(trailer);
            }
        }

        return trailerList;
    }

    private static Movie constructMovieFromJson(@Nullable JSONObject jsonObject) {
        if (jsonObject == null)
            return null;

        return Movie.builder()
                .id(jsonObject.optLong(ID))
                .title(jsonObject.optString(TITLE))
                .originalTitle(jsonObject.optString(ORIGINAL_TITLE))
                .plotSynopsis(jsonObject.optString(PLOT_SYNOPSIS))
                .backdropImagePath(constructImagePath(BACKDROP_IMAGE_WIDTH, jsonObject.optString(BACKDROP_PATH)))
                .posterImagePath(constructImagePath(POSTER_IMAGE_WIDTH, jsonObject.optString(POSTER_PATH)))
                .releaseDate(jsonObject.optString(RELEASE_DATE))
                .userRating(jsonObject.optDouble(VOTE_AVERAGE))
                .popularity(jsonObject.optDouble(POPULARITY))
                .isFavorite(false)
                .build();
    }

    private static MovieReview constructMovieReviewFromJson(@Nullable JSONObject jsonObject) {
        if (jsonObject == null)
            return null;

        return MovieReview.builder()
                .author(jsonObject.optString(AUTHOR))
                .content(jsonObject.optString(CONTENT))
                .build();
    }

    private static MovieTrailer constructMovieTrailerFromJson(@Nullable JSONObject jsonObject) {
        if (jsonObject == null)
            return null;

        return MovieTrailer.builder()
                .name(jsonObject.optString(NAME))
                .imageUrl(constructYoutubePath(YOUTUBE_IMAGE_URL, jsonObject.optString(SOURCE)))
                .videoUrl(constructYoutubePath(YOUTUBE_VIDEO_URL, jsonObject.optString(SOURCE)))
                .type(jsonObject.optString(TYPE))
                .build();
    }

    private static String constructImagePath(int width, @NonNull String path) {
        if (StringUtils.isEmpty(path))
            return null;
        return String.format(Locale.getDefault(), IMAGE_URL, width, path);
    }

    private static String constructYoutubePath(String path, @NonNull String videoId) {
        if (StringUtils.isEmpty(videoId))
            return null;

        return String.format(path, videoId);
    }
}
