package thiovan.submission5.fragments;


import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import thiovan.submission5.R;
import thiovan.submission5.adapters.CatalogPagerAdapter;
import thiovan.submission5.events.CatalogEvent;
import thiovan.submission5.widgets.FavoriteWidget;

/**
 * A simple {@link Fragment} subclass.
 */
public class CatalogFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    public CatalogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_catalog, container, false);

        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setTitle(R.string.header_main);

        CatalogPagerAdapter catalogPagerAdapter = new CatalogPagerAdapter(rootView.getContext(), getChildFragmentManager());
        ViewPager viewPager = rootView.findViewById(R.id.view_pager);
        viewPager.setAdapter(catalogPagerAdapter);
        TabLayout tabs = rootView.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.setOnTabSelectedListener(this);

        return rootView;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
            EventBus.getDefault().post(new CatalogEvent("movie"));
        } else {
            EventBus.getDefault().post(new CatalogEvent("tv_show"));
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
