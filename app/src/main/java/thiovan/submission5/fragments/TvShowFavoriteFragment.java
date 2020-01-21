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
import thiovan.submission5.adapters.TvShowListAdapter;
import thiovan.submission5.dao.FavoriteDao;
import thiovan.submission5.databases.AppDatabase;
import thiovan.submission5.models.TvShow;

/**
 * A simple {@link Fragment} subclass.
 */
public class TvShowFavoriteFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private final String STATE_LIST = "state_list";

    private View rootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView rvTvShow;
    private TextView txtEmpty;
    private ArrayList<TvShow> listTvShow = new ArrayList<>();
    private AppDatabase database;

    public TvShowFavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tv_show_favorite, container, false);

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark);

        rvTvShow = rootView.findViewById(R.id.rv_tv_show);
        rvTvShow.setHasFixedSize(true);

        txtEmpty = rootView.findViewById(R.id.txt_empty);

        database = Room.databaseBuilder(rootView.getContext(), AppDatabase.class, AppDatabase.DB)
                .allowMainThreadQueries()
                .build();

        if (savedInstanceState == null) {
            getTvShowFavorite();
            showRecyclerView();
        } else {
            listTvShow = savedInstanceState.getParcelableArrayList(STATE_LIST);
            showRecyclerView();
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_LIST, listTvShow);
    }

    @Override
    public void onResume() {
        super.onResume();

        getTvShowFavorite();
    }

    @Override
    public void onRefresh() {
        getTvShowFavorite();
    }

    private void showRecyclerView() {
        rvTvShow.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        TvShowListAdapter tvShowListAdapter = new TvShowListAdapter(listTvShow);
        rvTvShow.setAdapter(tvShowListAdapter);

        tvShowListAdapter.setOnItemClickCallback(new TvShowListAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(TvShow data) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_ID, data.getId());
                intent.putExtra(DetailActivity.EXTRA_TYPE, "tv");
                startActivity(intent);
            }
        });
    }

    private void getTvShowFavorite() {
        mSwipeRefreshLayout.setRefreshing(true);

        FavoriteDao favoriteDao = database.getFavoriteDao();
        listTvShow = new ArrayList<>(favoriteDao.getTvShowList());
        showRecyclerView();

        if (listTvShow.isEmpty()) {
            rvTvShow.setVisibility(View.GONE);
            txtEmpty.setVisibility(View.VISIBLE);
        } else {
            rvTvShow.setVisibility(View.VISIBLE);
            txtEmpty.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }

}
