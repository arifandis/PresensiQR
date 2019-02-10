package com.rookie.presensiqr.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.rookie.presensiqr.fragment.JadwalHariIniFragment;
import com.rookie.presensiqr.fragment.RekapFragment;

public class HomeMahasiswaViewAdapter extends FragmentPagerAdapter {
    final int page = 2;

    public HomeMahasiswaViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new JadwalHariIniFragment();
            case 1:
                return new RekapFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return page;
    }
}
