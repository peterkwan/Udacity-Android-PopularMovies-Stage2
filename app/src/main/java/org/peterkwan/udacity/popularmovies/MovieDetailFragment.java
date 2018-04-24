package org.peterkwan.udacity.popularmovies;


import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.peterkwan.udacity.popularmovies.data.Movie;
import org.peterkwan.udacity.popularmovies.provider.MovieContract.MovieEntry;
import org.peterkwan.udacity.popularmovies.utils.StringUtils;

import java.lang.ref.WeakReference;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class MovieDetailFragment extends Fragment {

    private static final String MOVIE = "movie";
    private static final String FROM_DATE_FORMAT = "yyyy-MM-dd";
    private static final String TO_DATE_FORMAT = "MMM dd, yyyy";
    private static final int UPDATE_TOKEN = 1;

    private Unbinder unbinder;
    private boolean isFavorite;
    private Context context;

    @Setter
    private Movie movie = null;

    @BindView(R.id.movieDetailContentLayout)
    View contentLayoutView;

    @BindView(R.id.backdropImageView)
    ImageView backdropImageView;

    @BindView(R.id.posterImageView)
    ImageView posterImageView;

    @BindView(R.id.titleTextView)
    TextView titleTextView;

    @BindView(R.id.originalTitleTextView)
    TextView originalTitleTextView;

    @BindView(R.id.releaseDateTextView)
    TextView releaseDateTextView;

    @BindView(R.id.plotSynopsisTextView)
    TextView plotSynopsisTextView;

    @BindView(R.id.userRatingTextView)
    TextView userRatingTextView;

    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    @BindView(R.id.detailTabLayout)
    TabLayout tabLayout;

    @BindView(R.id.detailViewPager)
    ViewPager viewPager;

    @BindView(R.id.moreDetailLayout)
    View moreDetailLayout;

    @BindView(R.id.favoriteButton)
    ImageButton favoriteButton;

    @BindString(R.string.original_title)
    String originalTitle;

    @BindString(R.string.release_date)
    String releasedDate;

    @BindString(R.string.add_to_favorite)
    String addToFavorite;

    @BindString(R.string.added_favorite)
    String addedFavorite;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (savedInstanceState != null)
            movie = savedInstanceState.getParcelable(MOVIE);
        else {
            Bundle bundle = getArguments();
            if (bundle != null && bundle.containsKey(MOVIE))
                movie = bundle.getParcelable(MOVIE);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        context = getActivity();

        unbinder = ButterKnife.bind(this, rootView);

        if (movie != null)
            setItemSelectedView();
        else
            setNoItemSelectedView();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(MOVIE, movie);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.favoriteButton)
    public void onFavoriteButtonClicked() {
        ContentResolver resolver = context.getContentResolver();
        if (resolver != null) {
            MovieAsyncQueryHandler handler = new MovieAsyncQueryHandler(resolver, this);
            handler.startUpdate(UPDATE_TOKEN,
                    null,
                    ContentUris.withAppendedId(MovieEntry.CONTENT_URI, movie.getId()),
                    constructContentValues(),
                    null,
                    null);
        }

    }

    private void setNoItemSelectedView() {
        contentLayoutView.setVisibility(View.GONE);
    }

    private void setItemSelectedView() {
        Picasso.get()
                .load(movie.getBackdropImagePath())
                .into(backdropImageView);
        backdropImageView.setContentDescription(movie.getTitle());

        Picasso.get()
                .load(movie.getPosterImagePath())
                .into(posterImageView);
        posterImageView.setContentDescription(movie.getTitle());

        titleTextView.setText(movie.getTitle());
        originalTitleTextView.setText(String.format(originalTitle, movie.getOriginalTitle()));
        if (movie.getTitle().equals(movie.getOriginalTitle()))
            originalTitleTextView.setVisibility(View.GONE);

        releaseDateTextView.setText(String.format(releasedDate, StringUtils.convertDateFormat(movie.getReleaseDate(), FROM_DATE_FORMAT, TO_DATE_FORMAT)));
        plotSynopsisTextView.setText(movie.getPlotSynopsis());

        double userRating = movie.getUserRating();
        userRatingTextView.setText(String.format(Locale.getDefault(), "%.1f", userRating));
        ratingBar.setRating((float) userRating);

        isFavorite = movie.isFavorite();
        setFavoriteDisplay();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = tabLayout.getTabAt(position);
                if (tab != null)
                    tab.select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setAdapter(new MovieTabViewPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount(), String.valueOf(movie.getId())));

        contentLayoutView.setVisibility(View.VISIBLE);
    }

    private void setFavoriteDisplay() {
        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.ic_favorite_on);
            favoriteButton.setContentDescription(addedFavorite);
        }
        else {
            favoriteButton.setImageResource(R.drawable.ic_favorite_off);
            favoriteButton.setContentDescription(addToFavorite);
        }
    }

    private void toggleFavoriteFlag() {
        isFavorite = !isFavorite;
        setFavoriteDisplay();
    }

    private ContentValues constructContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieEntry.COLUMN_FAVORITE, !isFavorite);
        return contentValues;
    }

    private static class MovieAsyncQueryHandler extends AsyncQueryHandler {
        private final WeakReference<MovieDetailFragment> fragmentRef;

        public MovieAsyncQueryHandler(ContentResolver cr, MovieDetailFragment fragment) {
            super(cr);
            this.fragmentRef = new WeakReference<>(fragment);
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            if (result > 0) {
                MovieDetailFragment fragment = this.fragmentRef.get();
                fragment.toggleFavoriteFlag();
            }
        }
    }
}
