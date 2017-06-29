package com.andruby.live.activity;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.GridView;

import com.andruby.live.R;
import com.andruby.live.adapter.UserListAdapter;
import com.andruby.live.http.request.RequestComm;
import com.andruby.live.logic.UserInfoMgr;
import com.andruby.live.model.UserInfo;
import com.andruby.live.model.UserInfoCache;
import com.andruby.live.presenter.FollowListPresenter;
import com.andruby.live.presenter.ipresenter.IFollowListPresenter;
import com.andruby.live.ui.customviews.ActivityTitle;
import com.andruby.live.utils.Constants;
import com.andruby.live.utils.ToastUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 关注页面
 * @author: Andruby
 * @date: 2016/12/21 10:12
 */
public class FollowActivity extends IMBaseActivity implements IFollowListPresenter.IFollowView {


    private PullToRefreshGridView mGridView;
    private UserListAdapter mUserListAdapter;
    private List<UserInfo> mUserInfoList = new ArrayList<>();
    private FollowListPresenter mFollowListPresenter;
    private int mPageIndex = 1;
    private ActivityTitle atTitle;
    private boolean isLoading; //判断是否正在加载中

    @Override
    protected int getLayoutId() {
        return R.layout.activity_follow;
    }

    @Override
    protected void initView() {
        mGridView = obtainView(R.id.gv_userinfo);
        atTitle = obtainView(R.id.at_title);
    }

    @Override
    protected void initData() {
        mFollowListPresenter = new FollowListPresenter(this);
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
                if (isLoading) {
                    mPageIndex++;
                    loadData();
                }
            }
        });

    }

    private void loadData() {
        isLoading = true;
        mFollowListPresenter.followList(UserInfoCache.getUserId(this), mPageIndex, Constants.PAGESIZE, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageIndex = 1;
        loadData();
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
        intent.setClass(context, FollowActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onFollowList(int code, List<UserInfo> userInfoList, boolean isRefresh) {
        isLoading = false;
        if (code == 0 && userInfoList != null && userInfoList.size() > 0) {
            if (mPageIndex == 1) {
                mUserListAdapter.clear();
            }
            mUserListAdapter.addAll(userInfoList);
            mUserListAdapter.notifyDataSetChanged();
        } else {
            ToastUtils.showShort(this, "load data failed");
        }
        if (userInfoList != null && userInfoList.size() == Constants.PAGESIZE) {
            mGridView.setMode(PullToRefreshBase.Mode.BOTH);
        } else {
            mGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }
        mGridView.onRefreshComplete();
    }
}
