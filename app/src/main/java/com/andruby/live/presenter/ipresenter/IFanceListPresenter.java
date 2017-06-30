package com.andruby.live.presenter.ipresenter;

import com.andruby.live.base.BaseView;
import com.andruby.live.model.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * author : qubian on 2016/12/26 14:31
 * description :
 */

public abstract class IFanceListPresenter {

    public abstract void getFanceList(String userId, int pageIndex, int pageSize, int lastId);

    protected BaseView mBaseView;

    public IFanceListPresenter(BaseView baseView) {
        mBaseView = baseView;
    }


    public interface IFanceView extends BaseView {

        /**
         * @param retCode 获取结果，0表示成功
         * @param result  列表数据
         * @param refresh 是否需要刷新界面，首页需要刷新
         */
        public void onFanceList(int retCode, final List<UserInfo> result, boolean refresh);
    }
}
