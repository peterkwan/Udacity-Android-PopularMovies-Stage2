package org.peterkwan.udacity.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.peterkwan.udacity.popularmovies.data.Movie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindBool;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMovieListClickListener} interface
 * to handle interaction events.
 */
@NoArgsConstructor
public class MovieListFragment extends BaseFragment implements MovieListAdapter.MovieListItemClickListener,
        LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final int LOADER_ID = 1;
    private static final String MOVIE_LIST = "movieList";
    private static final String SORT_ORDER = "sortOrder";

    private OnMovieListClickListener mListener;
    private MovieListAdapter movieListAdapter;
    private Unbinder unbinder;
    private List<Movie> movieList;
    private Context context;
    private SharedPreferences sharedPreferences;

    @BindView(R.id.sortOrderSpinner)
    Spinner sortOrderSpinner;

    @BindView(R.id.movieListView)
    RecyclerView movieListView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindString(R.string.tmdb_credit)
    String tmdbCredit;

    @BindString(R.string.sort_order_pref_key)
    String sortOrderPrefKey;

    @BindString(R.string.tmdb_load_data_error_message)
    String loadDataErrorMessage;

    @BindArray(R.array.sort_order_values)
    String[] sortOrderValues;

    @BindInt(R.integer.grid_view_column_count)
    int gridViewColumnCount;

    @BindBool(R.bool.two_pane_layout)
    boolean isTwoPaneLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_LIST))
            movieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);

        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        unbinder = ButterKnife.bind(this, rootView);
        context = getActivity();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        sortOrderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortOrder = sortOrderValues[position];
                if (!sharedPreferences.getString(sortOrderPrefKey, sortOrderValues[0]).equals(sortOrder)) {
                    sharedPreferences.edit().putString(sortOrderPrefKey, sortOrder).apply();

                    if (isTwoPaneLayout)
                        mListener.onMovieItemSelected(null);

                    movieListAdapter.setMovieList(null);
                    movieList = null;

                    reloadMovieList(sortOrder);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String sortOrder = sharedPreferences.getString(sortOrderPrefKey, sortOrderValues[0]);
        sortOrderSpinner.setSelection(Arrays.asList(sortOrderValues).indexOf(sortOrder));

        movieListAdapter = new MovieListAdapter(this);
        movieListView.setAdapter(movieListAdapter);
        movieListView.setHasFixedSize(true);
        movieListView.setLayoutManager(new GridLayoutManager(context, gridViewColumnCount));

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final View rootView = getView();

        if (rootView != null && rootView.getViewTreeObserver() != null) {
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    int width = rootView.getWidth();
                    if (width > 0) {
                        int imageWidth = (width - gridViewColumnCount + 1 - 8) / gridViewColumnCount;
                        int imageHeight = (int) (imageWidth * 1.5);

                        movieListAdapter.resizeImageView(imageWidth, imageHeight);

                        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }

            });
        }

        String sortOrder = sharedPreferences.getString(sortOrderPrefKey, sortOrderValues[0]);
        if (savedInstanceState == null)
            getLoaderManager().initLoader(LOADER_ID, constructLoaderArgs(sortOrder), this);
        else
            reloadMovieList(sortOrder);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMovieListClickListener) {
            mListener = (OnMovieListClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMovieListClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.tmdbLogoImageView)
    public void onLogoClick() {
        showMessageDialog(getActivity(), R.string.tmdb, tmdbCredit, android.R.drawable.ic_dialog_info);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(MOVIE_LIST, movieList == null ? null : new ArrayList<>(movieList));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClicked(Movie movie) {
        if (mListener != null)
            mListener.onMovieItemSelected(movie);
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        if (movieList == null)
            progressBar.setVisibility(View.VISIBLE);

        return new MovieListAsyncTaskLoader(context, args);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
        progressBar.setVisibility(View.INVISIBLE);
        movieList = data;
        movieListAdapter.setMovieList(data);

        if (data == null)
            showMessageDialog(context, R.string.movie_list_error_title, loadDataErrorMessage, android.R.drawable.ic_dialog_alert);
        else
            movieListView.setVisibility(View.VISIBLE);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the context and potentially other fragments contained in that
     * context.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMovieListClickListener {
        void onMovieItemSelected(Movie movie);
    }

    private void reloadMovieList(String sortOrder) {
        getLoaderManager().restartLoader(LOADER_ID, constructLoaderArgs(sortOrder), this);
    }

    private Bundle constructLoaderArgs(String sortOrder) {
        Bundle args = new Bundle();
        args.putString(SORT_ORDER, sortOrder);
        return args;
    }

}
