package com.andruby.live.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.andruby.live.R;
import com.andruby.live.base.BaseView;
import com.andruby.live.http.AsyncHttp;
import com.andruby.live.http.request.CreateLiveRequest;
import com.andruby.live.http.request.RequestComm;
import com.andruby.live.http.response.CreateLiveResp;
import com.andruby.live.http.response.Response;
import com.andruby.live.presenter.ipresenter.IPusherPresenter;
import com.andruby.live.utils.LogUtil;
import com.andruby.live.utils.UIUtils;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * Created by zhao on 2017/3/2.
 */

public class PusherPresenter extends IPusherPresenter implements ITXLivePushListener {

    private IPusherView mPusherView;
    private TXLivePusher mTXLivePusher;
    private TXCloudVideoView mTXCloudVideoView;
    private String mPushUrl;

    private PopupWindow mSettingPopup;
    private int mLocX;
    private int mLocY;

    private boolean mFlashOn = false;

    public PusherPresenter(IPusherView baseView) {
        super(baseView);
        mPusherView = baseView;
    }

    @Override
    public void getPusherUrl(String userId, String groupId, String title, String coverPic, String nickName, String headPic, String location) {
        final CreateLiveRequest req = new CreateLiveRequest(RequestComm.createLive, userId, groupId, title, coverPic, location, 0);
        AsyncHttp.instance().postJson(req, new AsyncHttp.IHttpListener() {
            @Override
            public void onStart(int requestId) {

            }

            @Override
            public void onSuccess(int requestId, Response response) {
                if (response.status == RequestComm.SUCCESS) {
                    CreateLiveResp resp = (CreateLiveResp) response.data;
                    if (resp != null) {
                        if (!TextUtils.isEmpty(resp.getPushUrl())) {
                            mPusherView.onGetPushUrl(resp.getPushUrl(), 0);
                        } else {
                            mPusherView.onGetPushUrl(null, 1);
                        }
                    } else {
                        mPusherView.onGetPushUrl(null, 1);
                    }
                } else {
                    mPusherView.onGetPushUrl(null, 1);
                }
            }

            @Override
            public void onFailure(int requestId, int httpStatus, Throwable error) {

            }
        });
    }

    @Override
    public void startPusher(TXCloudVideoView videoView, TXLivePushConfig pusherConfig, String pushUrl) {
        if (mTXLivePusher == null) {
            mTXLivePusher = new TXLivePusher(mPusherView.getContext());
            mTXLivePusher.setConfig(pusherConfig);
        }
        mTXCloudVideoView = videoView;
        mTXCloudVideoView.setVisibility(View.VISIBLE);
        mTXLivePusher.startCameraPreview(mTXCloudVideoView);
        mTXLivePusher.setPushListener(this);
        mTXLivePusher.startPusher(pushUrl);


    }

    @Override
    public void stopPusher() {
        if (mTXLivePusher != null) {
            mTXLivePusher.stopCameraPreview(false);
            mTXLivePusher.setPushListener(null);
            mTXLivePusher.stopPusher();
        }
    }

    @Override
    public void resumePusher() {
        if (mTXLivePusher != null) {
            mTXLivePusher.resumePusher();
            mTXLivePusher.startCameraPreview(mTXCloudVideoView);
            mTXLivePusher.resumeBGM();
        }
    }

    @Override
    public void pausePusher() {
        if (mTXLivePusher != null) {
            mTXLivePusher.pauseBGM();
        }
    }

    @Override
    public void showSettingPopupWindow(final View targetView, int[] location) {
        targetView.setBackgroundResource(R.drawable.icon_setting_down);
        if (mSettingPopup == null) {
            View contentView = LayoutInflater.from(mPusherView.getContext()).inflate(R.layout.live_host_setting, null);
            contentView.findViewById(R.id.ll_live_setting_flash).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTXLivePusher.turnOnFlashLight(!mFlashOn);
                    mFlashOn = !mFlashOn;
                    mSettingPopup.dismiss();
                }
            });
            contentView.findViewById(R.id.ll_live_setting_changecamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSettingPopup.dismiss();
                    mTXLivePusher.switchCamera();
                }
            });
            mSettingPopup = new PopupWindow(contentView, UIUtils.formatDipToPx(mPusherView.getContext(),
                    100), UIUtils.formatDipToPx(mPusherView.getContext(), 85));
            mSettingPopup.setFocusable(true);
            mSettingPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mLocX = location[0] - (mSettingPopup.getWidth() - targetView
                    .getWidth()) / 2;
            mLocY = location[1] - (mSettingPopup.getHeight());
            mSettingPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    targetView.setBackgroundResource(R.drawable.icon_setting_up);
                }
            });
        }
        mSettingPopup.showAtLocation(targetView, Gravity.NO_GRAVITY, mLocX, mLocY);

    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }

    @Override
    public void onPushEvent(int i, Bundle bundle) {
        //推流相关的事件
    }

    @Override
    public void onNetStatus(Bundle bundle) {
//网络变化后的回调
    }
}
