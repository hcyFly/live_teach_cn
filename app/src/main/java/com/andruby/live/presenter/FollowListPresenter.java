package com.andruby.live.presenter;

import com.andruby.live.http.AsyncHttp;
import com.andruby.live.http.request.FollowListRequest;
import com.andruby.live.http.request.RequestComm;
import com.andruby.live.http.response.ResList;
import com.andruby.live.http.response.Response;
import com.andruby.live.model.UserInfo;
import com.andruby.live.presenter.ipresenter.IFollowListPresenter;

import java.util.ArrayList;

/**
 * author : qubian on 2016/12/26 15:20
 * description :
 */

public class FollowListPresenter extends IFollowListPresenter {
    private IFollowView mFollowView;

    public FollowListPresenter(IFollowView baseView) {
        super(baseView);
        mFollowView = baseView;
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }

    @Override
    public void followList(String userId, int pageIndex, int pageSize, int lastId) {
        FollowListRequest followListRequest = new FollowListRequest(RequestComm.follow_list, userId, pageIndex, pageSize, lastId);
        AsyncHttp.instance().postJson(followListRequest, new AsyncHttp.IHttpListener() {
            @Override
            public void onStart(int requestId) {

            }

            @Override
            public void onSuccess(int requestId, Response response) {
                if (response != null && response.status == RequestComm.SUCCESS) {
                    ResList<UserInfo> resList = (ResList<UserInfo>) response.data;
                    if (resList != null && resList.items != null && resList.items.size() > 0) {
                        mFollowView.onFollowList(0, resList.items, true);
                    } else {
                        mFollowView.onFollowList(1, null, false);
                    }
                }
            }

            @Override
            public void onFailure(int requestId, int httpStatus, Throwable error) {
                mFollowView.onFollowList(1, null, false);
            }
        });
    }
}
