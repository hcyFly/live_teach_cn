package com.andruby.live.fragment;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.andruby.live.R;
import com.andruby.live.presenter.LiveMainPresenter;
import com.andruby.live.ui.pagersliding.PagerSlidingTabStrip;

/**
 * @description: 直播列表主页
 * @author: Andruby
 * @time: 2016/9/3 16:19
 */
public class LiveMainFragment extends BaseFragment implements ViewPager.OnPageChangeListener ,View.OnClickListener{
    private ViewPager mViewPager;
    private LiveMainPresenter mLiveMainPresenter;
    private PagerSlidingTabStrip pagerSlidingTabStrip;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_live_main;
    }

    @Override
    protected void initView(View view) {
        mViewPager =obtainView(R.id.viewpager);
        pagerSlidingTabStrip = obtainView(R.id.circle_index_indicator);
        pagerSlidingTabStrip.setTextColorResource(R.color.white);
        pagerSlidingTabStrip.setIndicatorColorResource(R.color.white);
        pagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);
        pagerSlidingTabStrip.setTextSelectedColorResource(R.color.white);
        pagerSlidingTabStrip.setTextSize(getResources().getDimensionPixelSize(R.dimen.h6));
        pagerSlidingTabStrip.setTextSelectedSize(getResources().getDimensionPixelSize(R.dimen.h10));
        pagerSlidingTabStrip.setUnderlineHeight(1);
    }

    @Override
    protected void initData() {
        mLiveMainPresenter = new LiveMainPresenter(mContext);
        mViewPager.setAdapter(mLiveMainPresenter.getAdapter());
        mViewPager.addOnPageChangeListener(this);
        pagerSlidingTabStrip.setViewPager(mViewPager);
        pagerSlidingTabStrip.setOnPageChangeListener(this);
    }

    protected void setListener(View view) {
        obtainView(R.id.search).setOnClickListener(this);
        obtainView(R.id.message).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search:
//                SearchActivity.invoke(mContext);
                break;
            case R.id.message:
//                MessegeActivity.invoke(mContext);
                break;
        }
    }
}
