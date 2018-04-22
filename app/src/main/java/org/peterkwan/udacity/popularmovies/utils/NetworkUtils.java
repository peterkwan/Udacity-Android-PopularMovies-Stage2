package org.peterkwan.udacity.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import org.peterkwan.udacity.popularmovies.BuildConfig;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class NetworkUtils {

    private static final String MOVIE_LIST_QUERY_URL = "http://api.themoviedb.org/3/movie/%s";
    private static final String MOVIE_REVIEW_LIST_QUERY_URL = "http://api.themoviedb.org/3/movie/%s/reviews";
    private static final String MOVIE_TRAILER_LIST_QUERY_URL = "http://api.themoviedb.org/3/movie/%s/trailers";

    private static final String API_KEY = "api_key";
    private static final String CLOSE = "close";
    private static final String CONNECTION = "connection";

    public static String retrieveMovieListFromTMdb(String sortOrder) throws IOException {
        return retrieveDataFromTMDb(String.format(MOVIE_LIST_QUERY_URL, sortOrder));
    }

    public static String retrieveMovieReviewListFromTMdb(String movieId) throws IOException {
        return retrieveDataFromTMDb(String.format(MOVIE_REVIEW_LIST_QUERY_URL, movieId));
    }

    public static String retrieveMovieTrailerListFromTMdb(String movieId) throws IOException {
        return retrieveDataFromTMDb(String.format(MOVIE_TRAILER_LIST_QUERY_URL, movieId));
    }

    public static boolean isNetworkDisconnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting();
        }

        return true;
    }

    private static String retrieveDataFromTMDb(String urlString) throws IOException {
        HttpUrl url = HttpUrl.parse(urlString);

        if (url == null)
            return null;

        HttpUrl.Builder builder = url.newBuilder();
        builder.addQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY);

        Request request = new Request.Builder()
                .url(builder.toString())
                .build();

        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader(CONNECTION, CLOSE)
                                .build();
                        return chain.proceed(request);
                    }
                }).build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();

        if (body == null)
            return null;
        else
            return body.string();
    }
}
