package com.example.yusuf.mobilnakliyeyc.Profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.TabLayout;

import com.example.yusuf.mobilnakliyeyc.Me;
import com.example.yusuf.mobilnakliyeyc.Mesajlarim.MesajlarimFragment;
import com.example.yusuf.mobilnakliyeyc.Profile.Musteri.MusteriIlanlar;
import com.example.yusuf.mobilnakliyeyc.Profile.Nakliye.GelenTekliflerFragment;
import com.example.yusuf.mobilnakliyeyc.Profile.Nakliye.NakliyeIlanlarimFragment;
import com.example.yusuf.mobilnakliyeyc.R;
import com.example.yusuf.mobilnakliyeyc.Tampon.SharedObjects;

/**
 * Created by yusuf on 1.03.2017.
 */

public class ProfileTabFragment extends Fragment {
    public TabLayout tabLayout;
    public ViewPager viewPager;
    public int int_items = 3;
    private Me me;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View x = inflater.inflate(R.layout.fragment_profile_tab, null);
        me = SharedObjects.me;
        if (me.getState().equals("musteri")) {
            int_items = 2;
        } else {
            int_items = 3;
        }
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        // viewPager.setAdapter(new CustomAdapter(getSupportFragmentManager(), getContext()));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        return x;
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (me.getState().equals("musteri")) {
                switch (position) {
                    case 0:
                        return new MusteriIlanlar();
                    case 1:
                        return new MesajlarimFragment();

                }
                return null;
            } else {
                switch (position) {
                    case 0:
                        return new NakliyeIlanlarimFragment();
                    case 1:
                        return new MesajlarimFragment();
                    case 2:
                        return new GelenTekliflerFragment();
                }
                return null;
            }
        }

        @Override
        public int getCount() {
            return int_items;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (me.getState().equals("musteri")) {
                switch (position) {
                    case 0:
                        return "Nakliye İlanları";
                    case 1:
                        return "Mesajlarım";

                }
                return null;

            } else {
                switch (position) {
                    case 0:
                        return "İlanlarim";
                    case 1:
                        return "Mesajlarım";
                    case 2:
                        return "Teklifler";
                }
                return null;
            }
        }

    }
}
