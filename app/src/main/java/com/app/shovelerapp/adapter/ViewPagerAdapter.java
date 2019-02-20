package com.app.shovelerapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.app.shovelerapp.activity.JobStatusActivity;


/**
 * Created by supriya.n on 11-06-2016.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
               /* JobStatusActivity tab1 = new JobStatusActivity();
                return tab1;*/
            case 1:
                /*ShovelerJobStatusActivity tab2 = new ShovelerJobStatusActivity();
                return tab2;*/
            default:
                return null;
        }

    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 2;           // As there are only 3 Tabs
    }

}
