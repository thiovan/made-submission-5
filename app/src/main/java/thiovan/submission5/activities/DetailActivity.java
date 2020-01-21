package thiovan.submission5.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import thiovan.submission5.R;
import thiovan.submission5.dao.FavoriteDao;
import thiovan.submission5.databases.AppDatabase;
import thiovan.submission5.models.Movie;
import thiovan.submission5.models.TvShow;
import thiovan.submission5.widgets.FavoriteWidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_TYPE = "extra_type";

    private ImageView mPhoto;
    private TextView mName, mScore, mRuntime, mDescription;
    private ProgressBar mLoading;
    private String id, type;
    private String currentLanguage = "en";
    private FavoriteDao favoriteDao;
    private JSONObject mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id = getIntent().getStringExtra(EXTRA_ID);
        type = getIntent().getStringExtra(EXTRA_TYPE);

        initDB();

        initView();

        currentLanguage = Locale.getDefault().getLanguage();
        if (currentLanguage.equals("in")) {
            currentLanguage = "id";
        }

        fetchDetail();
    }

    private void initDB() {
        AppDatabase database = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        favoriteDao = database.getFavoriteDao();
    }

    private void initView() {
        mPhoto = findViewById(R.id.img_photo);
        mName = findViewById(R.id.txt_name);
        mScore = findViewById(R.id.txt_user_score);
        mRuntime = findViewById(R.id.txt_runtime);
        mDescription = findViewById(R.id.txt_description);
        mLoading = findViewById(R.id.loading);
    }

    private void setValue(String photo, String name, String score, String runtime, String description) {
        Glide.with(DetailActivity.this)
                .load(photo)
                .centerCrop()
                .placeholder(R.drawable.image_placeholder)
                .into(mPhoto);
        mName.setText(name);
        mScore.setText(score);
        mRuntime.setText(runtime);
        mDescription.setText(description);
    }

    private void fetchDetail() {
        mLoading.setVisibility(View.VISIBLE);

        AndroidNetworking.get(String.format("https://api.themoviedb.org/3/%s/%s", type, id))
                .addQueryParameter("api_key", "a050df5725f01a6d3fe03f86baecd970")
                .addQueryParameter("language", currentLanguage)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mLoading.setVisibility(View.GONE);
                        mResponse = response;
                        try {
                            if (type.equals(("movie"))) {
                                setValue(
                                        "https://image.tmdb.org/t/p/w185" + response.getString("poster_path"),
                                        response.getString("original_title"),
                                        response.getString("vote_average"),
                                        response.getString("runtime") + "m",
                                        !response.getString("overview").isEmpty() ? response.getString("overview") : getResources().getString(R.string.error_no_transalation)
                                );
                            } else {
                                setValue(
                                        "https://image.tmdb.org/t/p/w185" + response.getString("poster_path"),
                                        response.getString("name"),
                                        response.getString("vote_average"),
                                        response.getJSONArray("episode_run_time").length() != 0 ? response.getJSONArray("episode_run_time").getString(0) + "m" : getResources().getString(R.string.error_no_runtime),
                                        !response.getString("overview").isEmpty() ? response.getString("overview") : getResources().getString(R.string.error_no_transalation)
                                );
                            }
                            invalidateOptionsMenu();
                        } catch (JSONException e) {
                            Toast.makeText(DetailActivity.this, getResources().getString(R.string.error_parse), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        mLoading.setVisibility(View.GONE);
                        Toast.makeText(DetailActivity.this, getResources().getString(R.string.error_fetch), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem actionFavorite = menu.findItem(R.id.action_favorite);
        if (type.equals("movie")) {
            if (favoriteDao.findMovie(id) != null) {
                actionFavorite.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp));
            } else {
                actionFavorite.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_white_24dp));
            }
        } else {
            if (favoriteDao.findTvShow(id) != null) {
                actionFavorite.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp));
            } else {
                actionFavorite.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_white_24dp));
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_favorite) {
            if (type.equals("movie")) {

                Movie movie = new Movie();
                try {
                    movie.setId(id);
                    movie.setPhoto("https://image.tmdb.org/t/p/w185" + mResponse.getString("poster_path"));
                    movie.setName(mResponse.getString("original_title"));
                    movie.setDescription(!mResponse.getString("overview").isEmpty() ? mResponse.getString("overview") : getResources().getString(R.string.error_no_transalation));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (favoriteDao.findMovie(id) != null) {
                    favoriteDao.deleteMovie(movie);
                } else {
                    favoriteDao.insertMovie(movie);
                    Toast.makeText(this, getResources().getString(R.string.success_add_favorite), Toast.LENGTH_SHORT).show();
                }

                invalidateOptionsMenu();

            } else {

                TvShow tvShow = new TvShow();
                try {
                    tvShow.setId(id);
                    tvShow.setPhoto("https://image.tmdb.org/t/p/w185" + mResponse.getString("poster_path"));
                    tvShow.setName(mResponse.getString("name"));
                    tvShow.setDescription(!mResponse.getString("overview").isEmpty() ? mResponse.getString("overview") : getResources().getString(R.string.error_no_transalation));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (favoriteDao.findTvShow(id) != null) {
                    favoriteDao.deleteTvShow(tvShow);
                } else {
                    favoriteDao.insertTvShow(tvShow);
                    Toast.makeText(this, getResources().getString(R.string.success_add_favorite), Toast.LENGTH_SHORT).show();
                }

                invalidateOptionsMenu();
            }

            updateWidget();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWidget() {
        Intent intent = new Intent(this, FavoriteWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), FavoriteWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }
}
