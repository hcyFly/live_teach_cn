package com.andruby.live.presenter.ipresenter;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;

import com.andruby.live.base.BasePresenter;
import com.andruby.live.base.BaseView;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.ui.TXCloudVideoView;


/**
 * @description: 推流
 * @author: Andruby
 * @time: 2016/12/15 11:54
 */
public abstract class IPusherPresenter implements BasePresenter {
    protected BaseView mBaseView;

    public IPusherPresenter(BaseView baseView) {
        mBaseView = baseView;
    }

    public abstract void getPusherUrl(final String userId, final String groupId, final String title,
                                      final String coverPic, final String nickName, final String headPic, final String location);

    public abstract void startPusher(TXCloudVideoView videoView, TXLivePushConfig pusherConfig, String pushUrl);

    public abstract void setConfig(TXLivePushConfig pusherConfig);

    public abstract void stopPusher();

    public abstract void resumePusher();

    public abstract void pausePusher();

    public abstract void showSettingPopupWindow(View targetView, int[] locations);

    /**
     * 直播状态改变1，在线，直播中，0不在线，直播结束
     * @param userId
     * @param status
     */
    public abstract void changeLiveStatus(String userId, int status);

    /**
     * 结束直播
     * @param userId
     * @param groupId
     */
    public abstract void stopLive(String userId, String groupId);

    public interface IPusherView extends BaseView {
        /**
         * @param pushUrl
         * @param errorCode 0表示成功 1表示失败
         */
        void onGetPushUrl(String pushUrl, int errorCode);

        void onPushEvent(int event, Bundle bundle);

        void onNetStatus(android.os.Bundle bundle);

        FragmentManager getFragmentMgr();

        void finish();
    }

}