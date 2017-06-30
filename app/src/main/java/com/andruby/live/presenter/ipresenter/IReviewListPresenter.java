package com.andruby.live.presenter.ipresenter;

import com.andruby.live.base.BaseView;
import com.andruby.live.model.ReviewInfo;

import java.util.ArrayList;

/**
 * author : qubian on 2017/5/4 18:05
 * description :
 */

public abstract class IReviewListPresenter {

    public abstract void getReviewList(String userId, int pageIndex, int pageSize, int lastId);

    protected BaseView mBaseView;

    public IReviewListPresenter(BaseView baseView) {
        mBaseView = baseView;
    }


    public interface IReviewListView extends BaseView {

        /**
         * @param retCode 获取结果，0表示成功
         * @param result  列表数据
         * @param refresh 是否需要刷新界面，首页需要刷新
         */
        public void onReviewList(int retCode, final ArrayList<ReviewInfo> result, boolean refresh);
    }
}
