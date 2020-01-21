package thiovan.submission5.adapters;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import thiovan.submission5.R;
import thiovan.submission5.events.CatalogEvent;
import thiovan.submission5.fragments.MovieFragment;
import thiovan.submission5.fragments.TvShowFragment;

public class CatalogPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;

    public CatalogPagerAdapter(Context context, FragmentManager fm) {
        //noinspection deprecation
        super(fm);
        mContext = context;

    }

    @StringRes
    private final int[] TAB_TITLES = new int[]{
            R.string.tab_text_1,
            R.string.tab_text_2
    };
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new MovieFragment();
                break;
            case 1:
                fragment = new TvShowFragment();
                break;
        }
        return fragment;
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }
    @Override
    public int getCount() {
        return 2;
    }
}
