package com.andruby.live.presenter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.andruby.live.activity.BaseActivity;
import com.andruby.live.fragment.LiveListFragment;
import com.andruby.live.utils.LogUtil;

/**
 * @description: 首页
 * @author: Andruby
 * @time: 2016/12/18 14:04
 */
public class LiveMainPresenter {

    private static final int TYPE_LIST = 0;
    private static final int TYPE_DOYEN = 1;

    public static final String[] TITLE = new String[]{"最新", "最热", "达人", "活力", "英雄联盟", "王者荣耀"};
    public static final int[] TYPE = new int[]{TYPE_LIST, TYPE_LIST, TYPE_DOYEN, TYPE_LIST, TYPE_LIST, TYPE_LIST};

    private static final String TAG = LiveMainPresenter.class.getName();

    private BaseActivity mContext;

    public LiveMainPresenter(BaseActivity context) {
        mContext = context;
    }

    public FragmentStatePagerAdapter getAdapter() {
        return new LiveMainPresenter.PagerAdapter(mContext.getSupportFragmentManager());
    }


    class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            LogUtil.e(TAG, "FragmentStatePagerAdapter.getItem : " + position);
            if (TYPE[position] == TYPE_LIST) {
                LogUtil.e(TAG, " LiveListFragment.newInstance ");
                return LiveListFragment.newInstance(position);
            } else {
                LogUtil.e(TAG, "LivePupolarUserFragment.newInstance ");
//                return LivePupolarUserFragment.newInstance(position);
                return LiveListFragment.newInstance(position);
            }

        }

        @Override
        public int getCount() {
            return TITLE.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLE[position];
        }
    }
}
