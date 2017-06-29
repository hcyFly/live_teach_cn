package com.andruby.live.activity;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.GridView;

import com.andruby.live.R;
import com.andruby.live.adapter.UserListAdapter;
import com.andruby.live.logic.UserInfoMgr;
import com.andruby.live.model.UserInfo;
import com.andruby.live.presenter.FanceListPresenter;
import com.andruby.live.presenter.ipresenter.IFanceListPresenter;
import com.andruby.live.ui.customviews.ActivityTitle;
import com.andruby.live.utils.Constants;
import com.andruby.live.utils.ToastUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 粉丝页面
 * @author: Andruby
 * @date: 2016/12/21 10:12
 */
public class FanceActivity extends IMBaseActivity implements IFanceListPresenter.IFanceView {

    private PullToRefreshGridView mGridView;
    private UserListAdapter mUserListAdapter;
    private List<UserInfo> mUserInfoList = new ArrayList<>();
    private FanceListPresenter mFanceListPresenter;
    private int mPageIndex = 1;
    private ActivityTitle atTitle;
    private boolean isLoading; //判断是否正在加载中

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fance;
    }

    @Override
    protected void initView() {
        mGridView = obtainView(R.id.gv_userinfo);
        atTitle = obtainView(R.id.at_title);
    }

    @Override
    protected void initData() {
        mFanceListPresenter = new FanceListPresenter(this);
        mUserListAdapter = new UserListAdapter(this, mUserInfoList);
        mGridView.setAdapter(mUserListAdapter);
        mGridView.setMode(PullToRefreshBase.Mode.BOTH);
        mGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                if (!isLoading) {
                    mPageIndex = 1;
                    loadData();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                if (!isLoading) {
                    mPageIndex++;
                    loadData();
                }
            }
        });

    }

    @Override
    protected void setListener() {
        atTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageIndex = 1;
        loadData();
    }

    private void loadData() {
        isLoading = true;
        mFanceListPresenter.getFanceList(UserInfoMgr.getInstance().getUserId(), mPageIndex, Constants.PAGESIZE, 0);
    }


    @Override
    public void onFanceList(int retCode, List<UserInfo> result, boolean refresh) {
        if (retCode == 0) {
            if (result != null && result.size() > 0) {
                if (mPageIndex == 1) {
                    mUserListAdapter.clear();
                }
                mUserListAdapter.addAll(result);
            }
        } else {
            ToastUtils.showShort(mContext, "load data failed");
        }
        if (result != null && result.size() == Constants.PAGESIZE) {
            mGridView.setMode(PullToRefreshBase.Mode.BOTH);
        } else {
            mGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }
        mGridView.onRefreshComplete();
        isLoading = false;// 加载结束
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showMsg(String msg) {
        ToastUtils.showShort(this, msg);
    }

    @Override
    public void showMsg(int msg) {
        ToastUtils.showShort(this, msg);
    }

    @Override
    public Context getContext() {
        return this;
    }

    public static void invoke(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, FanceActivity.class);
        context.startActivity(intent);
    }
}
