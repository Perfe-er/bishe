package com.example.zwq.assistant.Adapter;

import java.util.ArrayList;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPageAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> mFragments;
    ArrayList<String> tlTabs;
    public ViewPageAdapter(FragmentManager fragmentManager, ArrayList<Fragment> fragments, ArrayList<String> tlTabs) {
        super(fragmentManager);
        this.mFragments = fragments;
        this.tlTabs = tlTabs;
    }



    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tlTabs.get(position);
    }
}
