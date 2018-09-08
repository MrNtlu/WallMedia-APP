package com.mrntlu.socialmediaapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.jcodecraeer.xrecyclerview.XRecyclerView;

public class MainFragment extends Fragment {

    View v;
    Activity activity;

    ViewPager viewPager;
    TabLayout tabLayout;

    @SuppressLint("ValidFragment")
    public MainFragment(Activity activity) {
        this.activity = activity;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_main, container, false);
        viewPager=(ViewPager)v.findViewById(R.id.view_pager);
        tabLayout=(TabLayout)v.findViewById(R.id.tablayout);

        MainFragmentViewPager adapter=new MainFragmentViewPager(getFragmentManager(),activity);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return v;
    }
}
