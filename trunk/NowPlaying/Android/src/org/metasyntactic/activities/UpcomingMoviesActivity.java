package org.metasyntactic.activities;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.metasyntactic.INowPlaying;
import org.metasyntactic.NowPlayingApplication;
import org.metasyntactic.NowPlayingControllerWrapper;
import org.metasyntactic.data.Movie;
import org.metasyntactic.data.Score;
import org.metasyntactic.utilities.FileUtilities;
import org.metasyntactic.utilities.LogUtilities;
import org.metasyntactic.utilities.MovieViewUtilities;
import org.metasyntactic.utilities.StringUtilities;
import org.metasyntactic.views.CustomGridView;
import org.metasyntactic.views.FastScrollGridView;
import org.metasyntactic.views.NowPlayingPreferenceDialog;
import org.metasyntactic.views.Rotate3dAnimation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author mjoshi@google.com (Megha Joshi)
 */
public class UpcomingMoviesActivity extends Activity implements INowPlaying {
  private CustomGridView grid;
  private Intent intent;
  private Movie selectedMovie;
  private boolean isGridSetup;
  private List<Movie> movies;
  private String search;
  private final Map<Integer, Integer> movieIndexToSectionIndex = new HashMap<Integer, Integer>();
  private final Map<Integer, Integer> sectionIndexToMovieIndex = new HashMap<Integer, Integer>();
  private final List<String> actualSections = new ArrayList<String>();

  private RelativeLayout bottomBar;

  private PostersAdapter postersAdapter;
  private static final Map<String, SoftReference<Bitmap>> postersMap = new HashMap<String, SoftReference<Bitmap>>();
  private boolean scrolling;

  private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override public void onReceive(final Context context, final Intent intent) {
      refresh();
    }
  };
  private final BroadcastReceiver scrollStatebroadcastReceiver = new BroadcastReceiver() {
    @Override public void onReceive(final Context context, final Intent intent) {
      if (NowPlayingApplication.SCROLLING_INTENT.equals(intent.getAction())) {
        scrolling = true;
      } else if (NowPlayingApplication.NOT_SCROLLING_INTENT.equals(intent.getAction())) {
        scrolling = false;
        postersAdapter.notifyDataSetChanged();
      }
    }
  };

  private static Bitmap getPoster(final Movie movie) {
    final String key = movie.getCanonicalTitle();
    final SoftReference<Bitmap> reference = postersMap.get(key);
    Bitmap bitmap = null;
    if (reference != null) {
      bitmap = reference.get();
    }
    if (bitmap == null) {
      final File file = NowPlayingControllerWrapper.getPosterFile_safeToCallFromBackground(movie);
      if (file != null) {
        final byte[] bytes = FileUtilities.readBytes(file);
        if (bytes != null && bytes.length > 0) {
          bitmap = createBitmap(bytes);
          if (bitmap != null) {
            postersMap.put(movie.getCanonicalTitle(), new SoftReference<Bitmap>(bitmap));
          }
        }
      }
    }

    return bitmap;
  }

  /**
   * Updates display of the list of movies.
   */
  public void refresh() {
    if (search == null) {
      movies = new ArrayList<Movie>(NowPlayingControllerWrapper.getUpcomingMovies());
    }
    // sort movies according to the default sort preference.
    final Comparator<Movie> comparator = MOVIE_ORDER.get(NowPlayingControllerWrapper.getUpcomingMoviesSelectedSortIndex());
    Collections.sort(movies, comparator);
    if (postersAdapter != null) {
      populateSections();

      FastScrollGridView.getSections();
      postersAdapter.notifyDataSetChanged();
    }
  }

  private void populateSections() {
    actualSections.clear();
    movieIndexToSectionIndex.clear();
    sectionIndexToMovieIndex.clear();

    if (NowPlayingControllerWrapper.getUpcomingMoviesSelectedSortIndex() == 0) {
      populateAlphaMovieSectionsAndPositions();
    } else if (NowPlayingControllerWrapper.getUpcomingMoviesSelectedSortIndex() == 2) {
      populateScoreMovieSectionsAndPositions();
    }
  }

  private List<Movie> getMatchingMoviesList(final String search2) {
    final String localSearch = search2.toLowerCase();
    final List<Movie> matchingMovies = new ArrayList<Movie>();
    for (final Movie movie : movies) {
      if (movie.getDisplayTitle().toLowerCase().contains(localSearch)) {
        matchingMovies.add(movie);
      }
    }

    return matchingMovies;
  }

  public Context getContext() {
    return this;
  }

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LogUtilities.i(getClass().getSimpleName(), "onCreate");

    // check for sdcard mounted properly
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      // Request the progress bar to be shown in the title
      requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
      NowPlayingControllerWrapper.addActivity(this);
      getUserLocation();
      refresh();
      if (movies != null && !movies.isEmpty()) {
        setup();
        isGridSetup = true;
      }

    } else {
      new AlertDialog.Builder(this).setTitle(R.string.insert_sdcard).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
        public void onClick(final DialogInterface dialog, final int whichButton) {
          finish();
        }
      }).show();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    LogUtilities.i(getClass().getSimpleName(), "onResume");
    scrolling = false;
    registerReceiver(broadcastReceiver, new IntentFilter(NowPlayingApplication.NOW_PLAYING_CHANGED_INTENT));
    registerReceiver(scrollStatebroadcastReceiver, new IntentFilter(NowPlayingApplication.SCROLLING_INTENT));
    registerReceiver(scrollStatebroadcastReceiver, new IntentFilter(NowPlayingApplication.NOT_SCROLLING_INTENT));
    if (isGridSetup) {
      grid.setVisibility(View.VISIBLE);
      postersAdapter.notifyDataSetChanged();
    }
  }

  @Override
  protected void onPause() {
    LogUtilities.i(getClass().getSimpleName(), "onPause");

    unregisterReceiver(broadcastReceiver);
    unregisterReceiver(scrollStatebroadcastReceiver);
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    LogUtilities.i(getClass().getSimpleName(), "onDestroy");

    NowPlayingControllerWrapper.removeActivity(this);
    clearBitmaps();
    super.onDestroy();
  }

  @Override
  public Object onRetainNonConfigurationInstance() {
    LogUtilities.i(getClass().getSimpleName(), "onRetainNonConfigurationInstance");
    final Object result = new Object();
    NowPlayingControllerWrapper.onRetainNonConfigurationInstance(this, result);
    return result;
  }

  @Override
  protected void onNewIntent(final Intent intent) {
    super.onNewIntent(intent);
    search = intent.getStringExtra("movie");
    if (search != null) {
      bottomBar.setVisibility(View.VISIBLE);
    }
    getSearchResults();
    refresh();
  }

  private void getUserLocation() {
    final String userLocation = NowPlayingControllerWrapper.getUserLocation();
    if (StringUtilities.isNullOrEmpty(userLocation)) {
      final Intent localIntent = new Intent();
      localIntent.setClass(this, SettingsActivity.class);
      startActivity(localIntent);
    }
  }

  private void getSearchResults() {
    if (search != null) {
      final List<Movie> matchingMovies = getMatchingMoviesList(search);
      if (matchingMovies.isEmpty()) {
        Toast.makeText(this, getResources().getString(R.string.no_results_found_for) + search, Toast.LENGTH_SHORT).show();
      } else {
        movies = matchingMovies;
      }
    }
  }

  private static void clearBitmaps() {
    for (final SoftReference<Bitmap> reference : postersMap.values()) {
      reference.clear();
    }
  }

  private void setup() {
    setContentView(R.layout.moviegrid_anim);
    bottomBar = (RelativeLayout)findViewById(R.id.bottom_bar);
    if (search == null) {
      bottomBar.setVisibility(View.GONE);
    }
    final View UpcomingMovies = findViewById(R.id.all_movies);
    UpcomingMovies.setOnClickListener(new OnClickListener() {
      public void onClick(final View arg0) {
        final Intent intent = new Intent();
        intent.setClass(UpcomingMoviesActivity.this, UpcomingMoviesActivity.class);
        startActivity(intent);
      }
    });
    grid = (CustomGridView)findViewById(R.id.grid);
    grid.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        selectedMovie = movies.get(position);
        setupRotationAnimation(view);
      }
    });

    populateSections();

    postersAdapter = new PostersAdapter();
    grid.setAdapter(postersAdapter);
    intent = new Intent();
    intent.setClass(this, UpcomingMovieDetailsActivity.class);
  }

  private void populateAlphaMovieSectionsAndPositions() {
    for (int i = 0; i < movies.size(); i++) {
      final Movie movie = movies.get(i);
      final String sectionTitle = movie.getDisplayTitle().substring(0, 1);

      if (!actualSections.contains(sectionTitle)) {
        actualSections.add(sectionTitle);
      }

      final int sectionIndex = actualSections.indexOf(sectionTitle);
      movieIndexToSectionIndex.put(i, sectionIndex);
      if (!sectionIndexToMovieIndex.containsKey(sectionIndex)) {
        sectionIndexToMovieIndex.put(sectionIndex, i);
      }
    }
  }

  private void populateScoreMovieSectionsAndPositions() {
    for (int i = 0; i < movies.size(); i++) {
      final Movie movie = movies.get(i);

      final Score localScore = NowPlayingControllerWrapper.getScore(movie);
      final int scoreValue = localScore == null ? 0 : localScore.getScoreValue();
      final int scoreLevel = scoreValue / 10 * 10;

      final String sectionTitle = scoreLevel + "%";

      if (!actualSections.contains(sectionTitle)) {
        actualSections.add(sectionTitle);
      }

      final int sectionIndex = actualSections.indexOf(sectionTitle);
      movieIndexToSectionIndex.put(i, sectionIndex);
      if (!sectionIndexToMovieIndex.containsKey(sectionIndex)) {
        sectionIndexToMovieIndex.put(sectionIndex, i);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final List<Comparator<Movie>> MOVIE_ORDER = Arrays.asList(Movie.TITLE_ORDER, Movie.RELEASE_ORDER, Movie.SCORE_ORDER);

  private enum ViewState {
    Blank,
    Loading,
    Loaded
  }

  private class PostersAdapter extends BaseAdapter implements FastScrollGridView.SectionIndexer {
    private final LayoutInflater inflater;
    private final Drawable loadingDrawable;
    private final Drawable backgroundDrawable;

    private PostersAdapter() {
      // Cache the LayoutInflate to avoid asking for a new one each time.
      inflater = LayoutInflater.from(UpcomingMoviesActivity.this);
      loadingDrawable = getResources().getDrawable(R.drawable.loader2);
      backgroundDrawable = getResources().getDrawable(R.drawable.gallery_background_1);
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
      // to findViewById() on each row.
      final ViewHolder holder;
      // When convertView is not null, we can reuse it directly, there is
      // no need to reinflate it. We only inflate a new View when the
      // convertView
      // supplied by GridView is null.
      final Movie movie = movies.get(position % movies.size());
      if (convertView == null) {
        convertView = inflater.inflate(R.layout.moviegrid_item, null);
        // Creates a ViewHolder and store references to the two children
        // views we want to bind data to.
        holder = new ViewHolder(movie, (TextView)convertView.findViewById(R.id.title), (ImageView)convertView.findViewById(R.id.poster));
        convertView.setTag(holder);
        convertView.setBackgroundDrawable(backgroundDrawable);
      } else {
        // Get the ViewHolder back to get fast access to the TextView
        // and the ImageView.
        holder = (ViewHolder)convertView.getTag();
      }

      holder.title.setText(movie.getDisplayTitle());
      holder.title.setEllipsize(TextUtils.TruncateAt.END);

      // decide what image to show if we're scrolling or not.
      if (scrolling) {
        if (movie == holder.movie) {
          // ok, we're scrolling, and we're still on the same movie.  Keep the
          // poster if it's been loaded.  But if we have no poster yet, just show
          // the 'loading' poster.
          if (holder.viewState == ViewState.Blank) {
            holder.poster.setImageDrawable(loadingDrawable);
            holder.viewState = ViewState.Loading;
          }
        } else {
          // we're scrolling, and we're reusing a view for a different movie.
          // show the 'loading' poster if it's not already up.
          if (holder.viewState != ViewState.Loading) {
            holder.poster.setImageDrawable(loadingDrawable);
            holder.viewState = ViewState.Loading;
          }
        }
      } else {
        NowPlayingControllerWrapper.prioritizeMovie(movie);
        // ok.  we've stopped scrolling.  either we're reusing this view for a
        // new movie, or we haven't loaded the image for this movie yet.  in
        // either case try to load it.  if we can, then we're done and don't
        // need to do anything else now.
        if (movie != holder.movie || holder.viewState != ViewState.Loaded) {
          final Bitmap bitmap = getPoster(movie);
          if (bitmap == null) {
            if (holder.viewState != ViewState.Loading) {
              holder.poster.setImageDrawable(loadingDrawable);
              holder.viewState = ViewState.Loading;
            }
          } else {
            holder.poster.setImageBitmap(bitmap);
            holder.viewState = ViewState.Loaded;
          }
        }
      }

      holder.movie = movie;

      return convertView;
    }

    private class ViewHolder {
      private final TextView title;
      private final ImageView poster;

      private Movie movie;
      private ViewState viewState;

      private ViewHolder(final Movie movie, final TextView title, final ImageView poster) {
        this.movie = movie;
        this.title = title;
        this.poster = poster;
        viewState = ViewState.Blank;
      }
    }

    public final int getCount() {
      if (movies != null) {
        return Math.min(100, movies.size());
      } else {
        return 0;
      }
    }

    public final Object getItem(final int position) {
      return movies.get(position % movies.size());
    }

    public final long getItemId(final int position) {
      return position;
    }

    public int getPositionForSection(final int section) {
      final Integer position = sectionIndexToMovieIndex.get(section);
      if (position == null) {
        return 0;
      }
      return position;
    }

    public int getSectionForPosition(final int position) {
      final Integer section = movieIndexToSectionIndex.get(position);
      if (section == null) {
        return 0;
      }
      return section;
    }

    public Object[] getSections() {
      // fast scroll is implemented only for alphabetic & score sort for release
      // 1.
      if (NowPlayingControllerWrapper.getUpcomingMoviesSelectedSortIndex() == 0) {
        return actualSections.toArray();
      }
      if (NowPlayingControllerWrapper.getUpcomingMoviesSelectedSortIndex() == 2) {
        return actualSections.toArray();
      }
      return null;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    menu.add(0, MovieViewUtilities.MENU_MOVIES, 0, R.string.menu_movies).setIcon(R.drawable.ic_menu_home)
    .setIntent(new Intent(this, NowPlayingActivity.class));
    menu.add(0, MovieViewUtilities.MENU_SEARCH, 0, R.string.search).setIcon(android.R.drawable.ic_menu_search);
    menu.add(0, MovieViewUtilities.MENU_SORT, 0, R.string.sort_movies).setIcon(R.drawable.ic_menu_switch);
    menu.add(0, MovieViewUtilities.MENU_SETTINGS, 0, R.string.settings).setIcon(android.R.drawable.ic_menu_preferences)
    .setIntent(new Intent(this, SettingsActivity.class).putExtra("from_menu", "yes")).setAlphabeticShortcut('s');
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    if (item.getItemId() == MovieViewUtilities.MENU_SORT) {
      final NowPlayingPreferenceDialog builder = new NowPlayingPreferenceDialog(this)
      .setKey(NowPlayingPreferenceDialog.PreferenceKeys.UPCOMING_MOVIES_SORT).setEntries(R.array.entries_movies_sort_preference)
      .setPositiveButton(android.R.string.ok).setNegativeButton(android.R.string.cancel);
      builder.setTitle(R.string.sort_movies);
      builder.show();
      return true;
    }
    if (item.getItemId() == MovieViewUtilities.MENU_SEARCH) {
      final Intent localIntent = new Intent();
      localIntent.setClass(this, SearchMovieActivity.class);
      localIntent.putExtra("activity", "UpcomingMoviesActivity");
      startActivity(localIntent);
      return true;
    }
    return false;
  }

  private void setupRotationAnimation(final View view) {
    final float centerX = view.getWidth() / 2.0f;
    final float centerY = view.getHeight() / 2.0f;
    // Create a new 3D rotation with the supplied parameter
    // The animation listener is used to trigger the next animation
    final Animation rotation = new Rotate3dAnimation(80, 0, centerX, centerY, 0.0f, true);
    rotation.setDuration(20);
    rotation.setFillAfter(true);
    rotation.setAnimationListener(new AnimationListener() {
      public void onAnimationEnd(final Animation animation) {
        intent.putExtra("movie", (Parcelable)selectedMovie);
        startActivity(intent);
      }

      public void onAnimationRepeat(final Animation animation) {
      }

      public void onAnimationStart(final Animation animation) {
      }
    });
    view.startAnimation(rotation);
  }

  private static Bitmap createBitmap(final byte[] bytes) {
    try {
      return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    } catch (final OutOfMemoryError ignored) {
      return null;
    }
    /*
     * final BitmapFactory.Options options = new BitmapFactory.Options(); final
     * int width = 90; final int height = 125; // Get the dimensions only.
     * options.inJustDecodeBounds = true; BitmapFactory.decodeByteArray(bytes,
     * 0, bytes.length, options); final int bitmapWidth = options.outWidth;
     * final int bitmapHeight = options.outHeight; final float scale =
     * Math.min((float) bitmapWidth / (float) width, (float) bitmapHeight /
     * (float) height) 2; options.inJustDecodeBounds = false;
     * options.inPreferredConfig = Bitmap.Config.ARGB_8888; options.inSampleSize
     * = (int) scale; final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,
     * 0, bytes.length, options); return bitmap;
     */
  }
}
