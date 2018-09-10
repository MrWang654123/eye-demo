package com.cheersmind.cheersgenie.features.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Pair;

import java.util.List;

/**
 * viewpager适配器，配合tab使用的
 */
public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

    //key：tab名称；value：fragment
    List<Pair<String, Fragment>> items;

    public TabFragmentPagerAdapter(FragmentManager fm, List<Pair<String, Fragment>> items) {
        super(fm);
        this.items = items;
    }

    @Override
    public Fragment getItem(int position) {
        return items.get(position).second;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return items.get(position).first;
    }

}

