package thiovan.favoritemovie.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import thiovan.favoritemovie.R;
import thiovan.favoritemovie.adapters.MovieListAdapter;
import thiovan.favoritemovie.models.Movie;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String AUTHORITY = "thiovan.submission5";
    private static final String SCHEME = "content";
    private final String STATE_LIST = "state_list";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView rvMovie;
    private TextView txtEmpty;
    private ArrayList<Movie> listMovie = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        if (savedInstanceState == null) {
            getMovieFavorite();
            showRecyclerView();
        } else {
            listMovie = savedInstanceState.getParcelableArrayList(STATE_LIST);
            showRecyclerView();
        }
    }

    private void initView() {
        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark);
        rvMovie = findViewById(R.id.rv_movie);
        rvMovie.setHasFixedSize(true);
        txtEmpty = findViewById(R.id.txt_empty);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_LIST, listMovie);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMovieFavorite();
    }

    @Override
    public void onRefresh() {
        getMovieFavorite();
    }

    private void showRecyclerView() {
        rvMovie.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        MovieListAdapter movieListAdapter = new MovieListAdapter(listMovie);
        rvMovie.setAdapter(movieListAdapter);

        movieListAdapter.setOnItemClickCallback(new MovieListAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(Movie data) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_ID, data.getId());
                intent.putExtra(DetailActivity.EXTRA_TYPE, "movie");
                startActivity(intent);
            }
        });
    }

    private void getMovieFavorite() {
        mSwipeRefreshLayout.setRefreshing(true);

        Uri CONTENT_URI = new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(Movie.TABLE_NAME)
                .build();

        Cursor dataCursor = getApplicationContext().getContentResolver().query(CONTENT_URI, null, null, null, null);
        assert dataCursor != null;
        listMovie = new ArrayList<>(mapCursorToArrayList(dataCursor));
        showRecyclerView();

        if (listMovie.isEmpty()) {
            rvMovie.setVisibility(View.GONE);
            txtEmpty.setVisibility(View.VISIBLE);
        } else {
            rvMovie.setVisibility(View.VISIBLE);
            txtEmpty.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }

    private static ArrayList<Movie> mapCursorToArrayList(Cursor favoritesCursor) {
        ArrayList<Movie> favoritesList = new ArrayList<>();

        if (favoritesCursor == null) {
            return  favoritesList;
        }

        while (favoritesCursor.moveToNext()) {
            String id = favoritesCursor.getString(favoritesCursor.getColumnIndexOrThrow(Movie.COLUMN_ID));
            String photo = favoritesCursor.getString(favoritesCursor.getColumnIndexOrThrow(Movie.COLUMN_PHOTO));
            String name = favoritesCursor.getString(favoritesCursor.getColumnIndexOrThrow(Movie.COLUMN_NAME));
            String description = favoritesCursor.getString(favoritesCursor.getColumnIndexOrThrow(Movie.COLUMN_DESCRIPTION));
            favoritesList.add(new Movie(id, photo, name, description));
        }

        return favoritesList;
    }

}
