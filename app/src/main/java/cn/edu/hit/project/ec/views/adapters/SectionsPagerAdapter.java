package cn.edu.hit.project.ec.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cn.edu.hit.project.ec.models.base.DataScale;
import cn.edu.hit.project.ec.views.fragments.OverviewFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private final String[] mTitles;

    public SectionsPagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return OverviewFragment.getInstance(DataScale.HOURLY);
            case 1:
                return OverviewFragment.getInstance(DataScale.DAILY);
            case 2:
                return OverviewFragment.getInstance(DataScale.MONTHLY);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}