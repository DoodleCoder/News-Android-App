package com.example.newsapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;


public class HeadlinesFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private View v;

    public HeadlinesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_headlines, container, false);

        viewPager = v.findViewById(R.id.viewpager_id);
        tabLayout = v.findViewById(R.id.tablayout_id);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setUpViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getChildFragmentManager());

        adapter.addFragment(new FragmentWorld(), "WORLD");
        adapter.addFragment(new FragmentBusiness(), "BUSINESS");
        adapter.addFragment(new FragmentPolitics(), "POLITICS");
        adapter.addFragment(new FragmentSports(), "SPORTS");
        adapter.addFragment(new FragmentTechnology(), "TECHNOLOGY");
        adapter.addFragment(new FragmentScience(), "SCIENCE");

        viewPager.setAdapter(adapter);
    }
}
