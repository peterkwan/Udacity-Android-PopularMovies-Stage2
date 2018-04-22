package org.peterkwan.udacity.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.peterkwan.udacity.popularmovies.data.MovieTrailer;

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
public class MovieTrailerFragment extends BaseFragment implements MovieTrailerListAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<List<MovieTrailer>> {

    private static final int LOADER_ID = 3;
    private static final String MOVIE_TRAILER_LIST = "trailerList";
    private static final String MOVIE_ID = "movieId";

    private Unbinder unbinder;
    private MovieTrailerListAdapter trailerListAdapter;
    private List<MovieTrailer> trailerList;
    private String movieId;
    private Context context;

    @BindView(R.id.movieTrailerListView)
    RecyclerView movieTrailerListView;

    @BindView(R.id.emptyTrailerTextView)
    TextView emptyTrailerTextView;

    @BindString(R.string.tmdb_load_data_error_message)
    String loadDataErrorMessage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MOVIE_TRAILER_LIST))
                trailerList = savedInstanceState.getParcelableArrayList(MOVIE_TRAILER_LIST);

            if (savedInstanceState.containsKey(MOVIE_ID))
                movieId = savedInstanceState.getString(MOVIE_ID);
        } else {
            Bundle bundle = getArguments();
            if (bundle != null && bundle.containsKey(MOVIE_ID))
                movieId = bundle.getString(MOVIE_ID);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_trailer, container, false);

        unbinder = ButterKnife.bind(this, rootView);
        context = getActivity();

        trailerListAdapter = new MovieTrailerListAdapter(trailerList, this);
        movieTrailerListView.setAdapter(trailerListAdapter);
        movieTrailerListView.addItemDecoration(new DividerItemDecoration(movieTrailerListView.getContext(), DividerItemDecoration.VERTICAL));

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
        outState.putParcelableArrayList(MOVIE_TRAILER_LIST, trailerList == null ? null : new ArrayList<>(trailerList));
        outState.putString(MOVIE_ID, movieId);
        super.onSaveInstanceState(outState);
    }

    @NonNull
    @Override
    public Loader<List<MovieTrailer>> onCreateLoader(int id, @Nullable Bundle args) {
        return new MovieTrailerListAsyncTaskLoader(context, args);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<MovieTrailer>> loader) {

    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<MovieTrailer>> loader, List<MovieTrailer> data) {
        trailerList = data;
        trailerListAdapter.setTrailerList(data);
        trailerListAdapter.notifyDataSetChanged();

        if (data == null)
            showMessageDialog(context, R.string.movie_trailer_list_error_title, loadDataErrorMessage, android.R.drawable.ic_dialog_alert);
        else {
            boolean hasNoTrailer = data.isEmpty();
            movieTrailerListView.setVisibility(hasNoTrailer ? View.GONE : View.VISIBLE);
            emptyTrailerTextView.setVisibility(hasNoTrailer ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onItemClicked(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }


    private Bundle constructLoaderArgs(String movieId) {
        Bundle args = new Bundle();
        args.putString(MOVIE_ID, movieId);
        return args;
    }

}

