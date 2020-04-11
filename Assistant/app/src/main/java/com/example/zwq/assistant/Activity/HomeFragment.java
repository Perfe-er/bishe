package com.example.zwq.assistant.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidkun.xtablayout.XTabLayout;
import com.example.zwq.assistant.Adapter.ViewPageAdapter;
import com.example.zwq.assistant.R;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class HomeFragment extends BaseFragment {

    private ViewPager vpHome;
    private XTabLayout tlTabs;
    private AnnoFragment mAnnoFragment;
    private ActionFragment mActionFragment;
    private MoreFragment mMoreFragment;
    private AwardsFragment mAwardsFragment;
    private ViewPageAdapter mViewPageAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        initView(view);
        return view;
    }

    public void initView(View view){
        tlTabs = view.findViewById(R.id.tlTabs);
        vpHome = view.findViewById(R.id.vpHome);
        ArrayList<String> titles = new ArrayList<>();
        titles.add("通知");
        titles.add("活动");
        titles.add("评选");
        titles.add("更多");
        tlTabs.addTab(tlTabs.newTab().setText(titles.get(0)));
        tlTabs.addTab(tlTabs.newTab().setText(titles.get(1)));
        tlTabs.addTab(tlTabs.newTab().setText(titles.get(2)));
        tlTabs.addTab(tlTabs.newTab().setText(titles.get(3)));
        ArrayList<Fragment> mFragments = new ArrayList<>();
        mActionFragment = new ActionFragment();
        mAnnoFragment = new AnnoFragment();
        mAwardsFragment = new AwardsFragment();
        mMoreFragment = new MoreFragment();
        mFragments.add(mAnnoFragment);
        mFragments.add(mActionFragment);
        mFragments.add(mAwardsFragment);
        mFragments.add(mMoreFragment);
        mViewPageAdapter = new ViewPageAdapter(getChildFragmentManager(),mFragments,titles);
        vpHome.setAdapter(mViewPageAdapter);
        //将TabLayout与ViewPager关联起来
        tlTabs.setupWithViewPager(vpHome);
        tlTabs.setTabsFromPagerAdapter(mViewPageAdapter);//给TabLayout设置适配器
    }

}
