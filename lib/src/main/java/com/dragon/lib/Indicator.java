package com.dragon.lib;

import android.support.v4.view.ViewPager;

/**
 * Created by Administrator on 2015/10/8.
 */
public interface Indicator extends ViewPager.OnPageChangeListener {
    void setCurrentPage(int index);
    void setViewPage(ViewPager viewPage);
    void setViewPage(ViewPager viewPage, int initIndex);
    void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);
    void notifyDataSetChanged();
}
