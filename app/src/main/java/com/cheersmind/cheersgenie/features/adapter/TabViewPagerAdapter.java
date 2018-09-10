package com.cheersmind.cheersgenie.features.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Pair;

import java.util.List;

/**
 * ViewPager的fragment适配器，配合TabLayout使用
 */
public class TabViewPagerAdapter extends FragmentPagerAdapter {

    //title、Fragment组合对象的集合
    private List<Pair<String, Fragment>> items;

    public TabViewPagerAdapter(FragmentManager fm, List<Pair<String, Fragment>> items) {
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
