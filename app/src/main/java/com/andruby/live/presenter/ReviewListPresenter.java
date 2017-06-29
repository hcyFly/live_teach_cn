package com.andruby.live.presenter;

import com.andruby.live.http.AsyncHttp;
import com.andruby.live.http.request.RequestComm;
import com.andruby.live.http.request.ReviewListRequest;
import com.andruby.live.http.response.ResList;
import com.andruby.live.http.response.Response;
import com.andruby.live.model.ReviewInfo;
import com.andruby.live.presenter.ipresenter.IReviewListPresenter;

import java.util.ArrayList;

/**
 * author : qubian on 2017/5/4 18:04
 * description :
 */

public class ReviewListPresenter extends IReviewListPresenter {

    private IReviewListView mIFanceListView;

    public ReviewListPresenter(IReviewListView baseView) {
        super(baseView);
        mIFanceListView = baseView;
    }

    @Override
    public void getReviewList(String userId, int pageIndex, int pageSize, int lastId) {
        ReviewListRequest fanceListRequest = new ReviewListRequest(RequestComm.follow_list, userId, pageIndex, pageSize, lastId);
        AsyncHttp.instance().postJson(fanceListRequest, new AsyncHttp.IHttpListener() {
            @Override
            public void onStart(int requestId) {

            }

            @Override
            public void onSuccess(int requestId, Response response) {
                if (response.status == RequestComm.SUCCESS) {
                    ResList<ReviewInfo> resList = (ResList<ReviewInfo>) response.data;
                    ArrayList<ReviewInfo> result = null;
                    if (resList != null) {
                        result = (ArrayList<ReviewInfo>) resList.items;
                    }
                    mIFanceListView.onReviewList(0, result, true);
                } else {
                    mIFanceListView.onReviewList(1, null, true);
                }
            }

            @Override
            public void onFailure(int requestId, int httpStatus, Throwable error) {
                mIFanceListView.onReviewList(1, null, true);
            }
        });

    }
}
