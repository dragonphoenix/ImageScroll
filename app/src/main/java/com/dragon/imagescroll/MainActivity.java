package com.dragon.imagescroll;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dragon.lib.IndicatorView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private IndicatorView mIndicatorView;
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mIndicatorView = (IndicatorView) findViewById(R.id.indicatorView);

        initFragment();
    }

    private void initFragment() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new ImageFragment(R.mipmap.a));
        fragmentList.add(new ImageFragment(R.mipmap.b));
        fragmentList.add(new ImageFragment(R.mipmap.c));
        fragmentList.add(new ImageFragment(R.mipmap.d));
        fragmentList.add(new ImageFragment(R.mipmap.e));

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });

        mIndicatorView.setViewPage(mViewPager);
    }
}
