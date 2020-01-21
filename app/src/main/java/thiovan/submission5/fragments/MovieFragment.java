package thiovan.submission5.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import thiovan.submission5.R;
import thiovan.submission5.activities.DetailActivity;
import thiovan.submission5.adapters.MovieListAdapter;
import thiovan.submission5.events.SearchEvent;
import thiovan.submission5.models.Movie;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private final String STATE_LIST = "state_list";

    private View rootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView rvMovie;
    private ArrayList<Movie> listMovie = new ArrayList<>();
    private String currentLanguage = "en";

    public MovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark);

        rvMovie = rootView.findViewById(R.id.rv_movie);
        rvMovie.setHasFixedSize(true);

        currentLanguage = Locale.getDefault().getLanguage();
        if (currentLanguage.equals("in")) {
            currentLanguage = "id";
        }

        fetchMovieList();
        if (savedInstanceState == null) {
            fetchMovieList();
        } else {
            listMovie = savedInstanceState.getParcelableArrayList(STATE_LIST);
            showRecyclerView();
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_LIST, listMovie);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!currentLanguage.equals(Locale.getDefault().getLanguage())) {
            currentLanguage = Locale.getDefault().getLanguage();
            if (currentLanguage.equals("in")) {
                currentLanguage = "id";
            }
            fetchMovieList();
        }
    }

    @Override
    public void onRefresh() {
        fetchMovieList();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onSearchEvent(SearchEvent event) {
        if (event.type.equals("movie")) {
            fetchMovieSearch(event.keyword);
        }
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

    private void fetchMovieList() {
        mSwipeRefreshLayout.setRefreshing(true);

        AndroidNetworking.get("https://api.themoviedb.org/3/discover/movie")
                .addQueryParameter("api_key", "a050df5725f01a6d3fe03f86baecd970")
                .addQueryParameter("language", currentLanguage)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        try {
                            JSONArray results = response.getJSONArray("results");

                            listMovie = new ArrayList<>();
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject currentMovie = results.getJSONObject(i);

                                Movie movie = new Movie();
                                movie.setPhoto("https://image.tmdb.org/t/p/w185" + currentMovie.getString("poster_path"));
                                movie.setName(currentMovie.getString("original_title"));
                                movie.setDescription(currentMovie.getString("overview"));
                                movie.setId(currentMovie.getString("id"));
                                listMovie.add(movie);

                                showRecyclerView();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(rootView.getContext(), getResources().getString(R.string.error_parse), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(rootView.getContext(), getResources().getString(R.string.error_fetch), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchMovieSearch(String keyword) {
        mSwipeRefreshLayout.setRefreshing(true);

        AndroidNetworking.get("https://api.themoviedb.org/3/search/movie")
                .addQueryParameter("api_key", "a050df5725f01a6d3fe03f86baecd970")
                .addQueryParameter("language", currentLanguage)
                .addQueryParameter("query", keyword)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        try {
                            JSONArray results = response.getJSONArray("results");

                            if (results.length() == 0) {
                                Toast.makeText(rootView.getContext(), getResources().getString(R.string.search_not_found), Toast.LENGTH_SHORT).show();
                            }

                            listMovie = new ArrayList<>();
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject currentMovie = results.getJSONObject(i);

                                Movie movie = new Movie();
                                movie.setPhoto("https://image.tmdb.org/t/p/w185" + currentMovie.getString("poster_path"));
                                movie.setName(currentMovie.getString("original_title"));
                                movie.setDescription(currentMovie.getString("overview"));
                                movie.setId(currentMovie.getString("id"));
                                listMovie.add(movie);
                            }

                            showRecyclerView();
                        } catch (JSONException e) {
                            Toast.makeText(rootView.getContext(), getResources().getString(R.string.error_parse), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(rootView.getContext(), getResources().getString(R.string.error_fetch), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
