package com.boileryao.whisper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by boiler-yao on 2016/8/29.
 * ViewPager Adapter
 */
class ViewPagerAdapter extends FragmentPagerAdapter {
    private static final String[] TAB = new String[]{"发现", "消息"};

    ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            default:
            case 0:
                return new DeviceListFragment();
            case 1:
                return new DialogListFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TAB[position];
    }

    @Override
    public int getCount() {
        return TAB.length;
    }
}
