package thiovan.submission5.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import thiovan.submission5.R;
import thiovan.submission5.activities.DetailActivity;
import thiovan.submission5.adapters.MovieListAdapter;
import thiovan.submission5.dao.FavoriteDao;
import thiovan.submission5.databases.AppDatabase;
import thiovan.submission5.models.Movie;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFavoriteFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private final String STATE_LIST = "state_list";

    private View rootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView rvMovie;
    private TextView txtEmpty;
    private ArrayList<Movie> listMovie = new ArrayList<>();
    private AppDatabase database;

    public MovieFavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_movie_favorite, container, false);

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark);

        rvMovie = rootView.findViewById(R.id.rv_movie);
        rvMovie.setHasFixedSize(true);

        txtEmpty = rootView.findViewById(R.id.txt_empty);

        database = Room.databaseBuilder(rootView.getContext(), AppDatabase.class, AppDatabase.DB)
                .allowMainThreadQueries()
                .build();

        if (savedInstanceState == null) {
            getMovieFavorite();
            showRecyclerView();
        } else {
            listMovie = savedInstanceState.getParcelableArrayList(STATE_LIST);
            showRecyclerView();
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_LIST, listMovie);
    }

    @Override
    public void onResume() {
        super.onResume();

        getMovieFavorite();
    }

    @Override
    public void onRefresh() {
        getMovieFavorite();
    }

    private void showRecyclerView() {
        rvMovie.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        MovieListAdapter movieListAdapter = new MovieListAdapter(listMovie);
        rvMovie.setAdapter(movieListAdapter);

        movieListAdapter.setOnItemClickCallback(new MovieListAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(Movie data) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_ID, data.getId());
                intent.putExtra(DetailActivity.EXTRA_TYPE, "movie");
                startActivity(intent);
            }
        });
    }

    private void getMovieFavorite() {
        mSwipeRefreshLayout.setRefreshing(true);

        FavoriteDao favoriteDao = database.getFavoriteDao();
        listMovie = new ArrayList<>(favoriteDao.getMovieList());
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

}
