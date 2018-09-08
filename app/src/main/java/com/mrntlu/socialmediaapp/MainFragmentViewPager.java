package com.mrntlu.socialmediaapp;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MainFragmentViewPager extends FragmentStatePagerAdapter {

    Activity activity;

    public MainFragmentViewPager(FragmentManager fm, Activity activity) {
        super(fm);
        this.activity=activity;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment returnFragment;
        switch (position){
            case 0:
                returnFragment=new MainScreenFragments(activity,"featured");
                break;
            case 1:
                returnFragment=new MainScreenFragments(activity,"newest");
                break;
            case 2:
                returnFragment=new MainScreenFragments(activity,"highest_rated");
                break;
            case 3:
                returnFragment=new MainScreenFragments(activity,"popular");
                break;
            default:
                return null;
        }
        return returnFragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence title;
        switch (position){
            case 0:
                title="Featured";
                break;
            case 1:
                title="Newest";
                break;
            case 2:
                title="Highest Rate";
                break;
            case 3:
                title="Popular";
                break;
            default:
                return null;
        }
        return title;
    }
}
