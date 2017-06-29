package com.andruby.live.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.andruby.live.R;
import com.andruby.live.adapter.ReviewListAdapter;
import com.andruby.live.logic.UserInfoMgr;
import com.andruby.live.model.ReviewInfo;
import com.andruby.live.presenter.ReviewListPresenter;
import com.andruby.live.ui.customviews.ActivityTitle;
import com.andruby.live.utils.Constants;
import com.andruby.live.utils.ToastUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;


/**
 * @Description: 回看 列表页面
 * @author: Andruby
 * @date: 2016/12/21 10:12
 */
public class ReviewListActivity extends IMBaseActivity implements ReviewListPresenter.IReviewListView {


    private PullToRefreshListView mPullToRefreshListView;
    private ReviewListAdapter mReviewListAdapter;
    private List<ReviewInfo> mReviewInfoList = new ArrayList<>();
    private ReviewListPresenter mReviewListPresenter;
    private int mPageIndex = 1;
    private boolean isLoading; //判断是否正在加载中
    private ActivityTitle atTitle;

    public static void invoke(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ReviewListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reviewlist;
    }

    @Override
    protected void initView() {
        mPullToRefreshListView = obtainView(R.id.id_listview);
        atTitle = obtainView(R.id.at_title);
    }

    @Override
    protected void initData() {
        mReviewListPresenter = new ReviewListPresenter(this);
        mReviewListAdapter = new ReviewListAdapter(this, mReviewInfoList);
        mPullToRefreshListView.setAdapter(mReviewListAdapter);
        mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReviewInfo data = mReviewListAdapter.getItem(position - 1);
                ReviewPlayActivity.invoke(mContext, data.getVideoUrl());
            }
        });

        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (!isLoading) {
                    mPageIndex = 1;
                    loadData();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (!isLoading) {
                    mPageIndex++;
                    loadData();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void loadData() {
        isLoading = true;
        mReviewListPresenter.getReviewList(UserInfoMgr.getInstance().getUserId(), mPageIndex, Constants.PAGESIZE, 0);

    }

    @Override
    public void onReviewList(int retCode, ArrayList<ReviewInfo> result, boolean refresh) {
        if (retCode == 0) {
            if (result != null && result.size() > 0) {
                if (mPageIndex == 1) {
                    mReviewListAdapter.clear();
                }
                mReviewListAdapter.addAll(result);
            }
        } else {
            ToastUtils.showShort(mContext, "刷新列表失败");
        }
        if (mPageIndex == 1) {
            if (result != null && result.size() == Constants.PAGESIZE) {
                mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
            } else {
                mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        } else {

        }
        mPullToRefreshListView.onRefreshComplete();
        isLoading = false;
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
}
