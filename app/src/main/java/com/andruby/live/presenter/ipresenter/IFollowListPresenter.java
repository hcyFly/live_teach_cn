package com.andruby.live.presenter.ipresenter;

import com.andruby.live.base.BasePresenter;
import com.andruby.live.base.BaseView;
import com.andruby.live.model.UserInfo;

import java.util.List;

/**
 * author : qubian on 2016/12/26 14:31
 * description :
 */

public abstract class IFollowListPresenter implements BasePresenter {

    protected BaseView mBaseView;

    public IFollowListPresenter(BaseView baseView) {
        mBaseView = baseView;
    }

    public abstract void followList(String userId, int pageIndex, int pageSize, int lastId);

    public interface IFollowView extends BaseView {
        void onFollowList(int code, List<UserInfo> userInfoList, boolean isRefresh);
    }
}
