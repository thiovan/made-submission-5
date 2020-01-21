package thiovan.submission5.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import thiovan.submission5.R;
import thiovan.submission5.fragments.MovieFavoriteFragment;
import thiovan.submission5.fragments.TvShowFavoriteFragment;

public class FavoritePagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;

    public FavoritePagerAdapter(Context context, FragmentManager fm) {
        //noinspection deprecation
        super(fm);
        mContext = context;

    }

    @StringRes
    private final int[] TAB_TITLES = new int[]{
            R.string.tab_text_1,
            R.string.tab_text_2
    };

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new MovieFavoriteFragment();
        switch (position) {
            case 0:
                fragment = new MovieFavoriteFragment();
                break;
            case 1:
                fragment = new TvShowFavoriteFragment();
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
