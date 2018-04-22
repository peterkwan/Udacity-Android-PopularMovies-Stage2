package org.peterkwan.udacity.popularmovies;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.peterkwan.udacity.popularmovies.data.MovieReview;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;


/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class MovieReviewFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<MovieReview>> {

    private static final int LOADER_ID = 2;
    private static final String MOVIE_REVIEW_LIST = "reviewList";
    private static final String MOVIE_ID = "movieId";

    private Unbinder unbinder;
    private MovieReviewListAdapter reviewListAdapter;
    private List<MovieReview> reviewList;
    private String movieId;
    private Context context;

    @BindView(R.id.movieReviewListView)
    RecyclerView movieReviewListView;

    @BindView(R.id.emptyReviewTextView)
    TextView emptyReviewTextView;

    @BindString(R.string.tmdb_load_data_error_message)
    String loadDataErrorMessage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MOVIE_REVIEW_LIST))
                reviewList = savedInstanceState.getParcelableArrayList(MOVIE_REVIEW_LIST);

            if (savedInstanceState.containsKey(MOVIE_ID))
                movieId = savedInstanceState.getString(MOVIE_ID);
        }
        else {
            Bundle bundle = getArguments();
            if (bundle != null && bundle.containsKey(MOVIE_ID))
                movieId = bundle.getString(MOVIE_ID);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_review, container, false);

        unbinder = ButterKnife.bind(this, rootView);
        context = getActivity();

        reviewListAdapter = new MovieReviewListAdapter(reviewList);
        movieReviewListView.setAdapter(reviewListAdapter);
        movieReviewListView.addItemDecoration(new DividerItemDecoration(movieReviewListView.getContext(), DividerItemDecoration.VERTICAL));

        if (savedInstanceState == null)
            getLoaderManager().initLoader(LOADER_ID, constructLoaderArgs(movieId), this);
        else
            getLoaderManager().restartLoader(LOADER_ID, constructLoaderArgs(movieId), this);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(MOVIE_REVIEW_LIST, reviewList == null ? null : new ArrayList<>(reviewList));
        outState.putString(MOVIE_ID, movieId);
        super.onSaveInstanceState(outState);
    }

    @NonNull
    @Override
    public Loader<List<MovieReview>> onCreateLoader(int id, Bundle args) {
        return new MovieReviewListAsyncTaskLoader(context,  args);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<MovieReview>> loader) {

    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<MovieReview>> loader, List<MovieReview> data) {
        reviewList = data;
        reviewListAdapter.setReviewList(data);
        reviewListAdapter.notifyDataSetChanged();

        if (data == null)
            showMessageDialog(context, R.string.movie_review_list_error_title, loadDataErrorMessage, android.R.drawable.ic_dialog_alert);
        else {
            boolean hasNoReview = data.isEmpty();
            movieReviewListView.setVisibility(hasNoReview ? View.GONE : View.VISIBLE);
            emptyReviewTextView.setVisibility(hasNoReview ? View.VISIBLE : View.GONE);
        }
    }

    private Bundle constructLoaderArgs(String movieId) {
        Bundle args = new Bundle();
        args.putString(MOVIE_ID, movieId);
        return args;
    }
}
