package com.andruby.live.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.andruby.live.R;
import com.andruby.live.fragment.LiveMainFragment;
import com.andruby.live.fragment.UserInfoFragment;
import com.andruby.live.presenter.MainPresenter;
import com.andruby.live.presenter.ipresenter.IMainPresenter;


/**
 * @Description: 主界面， 包括直播列表，用户信息页
 * UI使用FragmentTabHost+Fragment
 * 直播列表：LiveMainFragment
 * 个人信息页：UserInfoFragment
 * @author: Andruby
 * @date: 2016年7月8日 下午4:46:44
 */
public class MainActivity extends IMBaseActivity implements IMainPresenter.IMainView {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FragmentTabHost mTabHost;

    private final Class mFragmentArray[] = {LiveMainFragment.class, Fragment.class, UserInfoFragment.class};
    private int mImageViewArray[] = {R.drawable.tab_live_selector, R.drawable.tab_pubish_selector, R.drawable.tab_my_selector};
    private String mTextviewArray[] = {"live", "publish", "my"};

    private MainPresenter mMainPresenter;

    public static final void invoke(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mTabHost = obtainView(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.contentPanel);
    }

    @Override
    protected void initData() {
        int fragmentCount = mFragmentArray.length;
        for (int i = 0; i < fragmentCount; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, mFragmentArray[i], null);
            mTabHost.getTabWidget().setDividerDrawable(null);
        }
        mMainPresenter = new MainPresenter(this);
    }

    @Override
    protected void setListener() {
        mTabHost.getTabWidget().getChildTabViewAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PublishSettingActivity.invoke(MainActivity.this);
                PublishLiveActivity.invoke(MainActivity.this);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMainPresenter.checkCacheAndLogin();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showMsg(String msg) {

    }

    @Override
    public void showMsg(int msg) {

    }

    @Override
    public Context getContext() {
        return this;
    }

    /**
     * 动态获取tabicon
     *
     * @param index tab index
     * @return
     */
    private View getTabItemView(int index) {
        View view;
        if (index % 2 == 0) {
            view = LayoutInflater.from(this).inflate(R.layout.tab_live, null);
        } else {
            view = LayoutInflater.from(this).inflate(R.layout.tab_button, null);
        }
        ImageView icon = (ImageView) view.findViewById(R.id.tab_icon);
        icon.setImageResource(mImageViewArray[index]);
        return view;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getSupportFragmentManager().findFragmentByTag(mTabHost.getCurrentTabTag()).onActivityResult(requestCode, resultCode, data);
    }
}
