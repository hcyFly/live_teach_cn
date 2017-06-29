package com.andruby.live.presenter;

import com.andruby.live.http.AsyncHttp;
import com.andruby.live.http.request.FanceListRequest;
import com.andruby.live.http.request.RequestComm;
import com.andruby.live.http.response.ResList;
import com.andruby.live.http.response.Response;
import com.andruby.live.model.UserInfo;
import com.andruby.live.presenter.ipresenter.IFanceListPresenter;

import java.util.ArrayList;

/**
 * author : Andruby on 2016/12/26 15:20
 * description :
 */

public class FanceListPresenter extends IFanceListPresenter {
    private IFanceView mFanceListView;

    public FanceListPresenter(IFanceView baseView) {
        super(baseView);
        mFanceListView = baseView;
    }

    @Override
    public void getFanceList(String userId, int pageIndex, int pageSize, int lastId) {
        FanceListRequest fanceListRequest = new FanceListRequest(RequestComm.fance_list, userId, pageIndex, pageSize, lastId);
        AsyncHttp.instance().postJson(fanceListRequest, new AsyncHttp.IHttpListener() {
            @Override
            public void onStart(int requestId) {

            }

            @Override
            public void onSuccess(int requestId, Response response) {
                if (response != null && response.status == RequestComm.SUCCESS) {
                    ResList<UserInfo> resList = (ResList<UserInfo>) response.data;
                    mFanceListView.onFanceList(0, resList.items, true);
                } else {
                    mFanceListView.onFanceList(1, null, true);
                }
            }

            @Override
            public void onFailure(int requestId, int httpStatus, Throwable error) {
                mFanceListView.onFanceList(1, null, true);
            }
        });

    }
}
