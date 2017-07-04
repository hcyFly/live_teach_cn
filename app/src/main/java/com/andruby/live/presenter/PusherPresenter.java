package com.andruby.live.presenter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.andruby.live.R;
import com.andruby.live.http.AsyncHttp;
import com.andruby.live.http.request.CreateLiveRequest;
import com.andruby.live.http.request.LiveStatusRequest;
import com.andruby.live.http.request.RequestComm;
import com.andruby.live.http.request.StopLiveRequest;
import com.andruby.live.http.response.CreateLiveResp;
import com.andruby.live.http.response.Response;
import com.andruby.live.presenter.ipresenter.IPusherPresenter;
import com.andruby.live.ui.customviews.BeautyDialogFragment;
import com.andruby.live.ui.customviews.FilterDialogFragment;
import com.andruby.live.utils.Constants;
import com.andruby.live.utils.LogUtil;
import com.andruby.live.utils.OtherUtils;
import com.andruby.live.utils.ToastUtils;
import com.andruby.live.utils.UIUtils;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * Created by zhao on 2017/3/2.
 */

public class PusherPresenter extends IPusherPresenter implements ITXLivePushListener, BeautyDialogFragment.SeekBarCallback, FilterDialogFragment.FilterCallback {

    public static final int LIVE_STATUS_ONLINE = 1;
    public static final int LIVE_STATUS_OFFLINE = 0;

    private final static String TAG = PusherPresenter.class.getSimpleName();
    private IPusherView mPusherView;
    private TXLivePusher mTXLivePusher;
    private TXCloudVideoView mTXCloudVideoView;
    private String mPushUrl;

    private PopupWindow mSettingPopup;
    private int mLocX;
    private int mLocY;

    private boolean mFlashOn = false;
    private BeautyDialogFragment mBeautyDialogFragment;
    private FilterDialogFragment mFilterDialogFragment;

    private int mBeautyLevel;
    private int mWhiteLevel;
    private boolean isBeauty;

    public PusherPresenter(IPusherView baseView) {
        super(baseView);
        mPusherView = baseView;
        mBeautyDialogFragment = new BeautyDialogFragment();
        mBeautyDialogFragment.setSeekBarListener(this);


        mFilterDialogFragment = new FilterDialogFragment();
        mFilterDialogFragment.setFilterCallback(this);
    }

    @Override
    public void getPusherUrl(String userId, String groupId, String title, String coverPic, String nickName, String headPic, String location, boolean isRecord) {
        final CreateLiveRequest req = new CreateLiveRequest(RequestComm.createLive, userId, groupId, title, coverPic, location, isRecord ? 1 : 0);
        AsyncHttp.instance().postJson(req, new AsyncHttp.IHttpListener() {
            @Override
            public void onStart(int requestId) {

            }

            @Override
            public void onSuccess(int requestId, Response response) {
                if (response.status == RequestComm.SUCCESS) {
                    CreateLiveResp resp = (CreateLiveResp) response.data;
                    if (resp != null) {
                        if (!TextUtils.isEmpty(resp.pushUrl)) {
                            mPusherView.onGetPushUrl(resp.liveId, resp.pushUrl, 0);
                        } else {
                            mPusherView.onGetPushUrl(null, null, 1);
                        }
                    } else {
                        mPusherView.onGetPushUrl(null, null, 1);
                    }
                } else {
                    mPusherView.showMsg(response.msg);
                    mPusherView.finish();
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
    public void setConfig(TXLivePushConfig pusherConfig) {
        if (mTXLivePusher == null) {
            mTXLivePusher.setConfig(pusherConfig);
        }
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

            contentView.findViewById(R.id.ll_live_setting_beauty).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSettingPopup.dismiss();
                    //beautyLevel:0-9,默认为0，不开启美颜
                    //whiteLevel 0-3,默认为0，不开启美白

                    if (isBeauty) {
                        mTXLivePusher.setBeautyFilter(0, 0);
                        isBeauty = !isBeauty;
                    } else {
                        if (!mTXLivePusher.setBeautyFilter(7, 3)) {
                            ToastUtils.makeText(mPusherView.getContext(), R.string.beauty_disenable, Toast.LENGTH_SHORT);
                        } else {
                            isBeauty = !isBeauty;
                        }
                    }
//                    mBeautyDialogFragment.show(mPusherView.getFragmentMgr(), "");

                }
            });

            contentView.findViewById(R.id.ll_live_setting_filter).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSettingPopup.dismiss();
                    mFilterDialogFragment.show(mPusherView.getFragmentMgr(), "");
                }
            });


            mSettingPopup = new PopupWindow(contentView, UIUtils.formatDipToPx(mPusherView.getContext(),
                    100), UIUtils.formatDipToPx(mPusherView.getContext(), 170));
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

    /**
     * 更改直播状态
     *
     * @param userId 主播ID
     * @param status 状态 LIVE_STATUS_OFFLINE = 0; LIVE_STATUS_ONLINE = 1;
     */
    @Override
    public void changeLiveStatus(String userId, int status) {
        try {
            LiveStatusRequest req = new LiveStatusRequest(RequestComm.livestatus, userId, status);
            AsyncHttp.instance().postJson(req, new AsyncHttp.IHttpListener() {
                @Override
                public void onStart(int requestId) {

                }

                @Override
                public void onSuccess(int requestId, Response response) {
                    if (response.status == RequestComm.SUCCESS) {
                    }
                }

                @Override
                public void onFailure(int requestId, int httpStatus, Throwable error) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void stopLive(String userId, String groupId) {
        StopLiveRequest stopLiveRequest = new StopLiveRequest(RequestComm.stopLive, userId, groupId);
        AsyncHttp.instance().postJson(stopLiveRequest, new AsyncHttp.IHttpListener() {
            @Override
            public void onStart(int requestId) {

            }

            @Override
            public void onSuccess(int requestId, Response response) {

            }

            @Override
            public void onFailure(int requestId, int httpStatus, Throwable error) {

            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }

    @Override
    public void onPushEvent(int event, Bundle bundle) {
        //推流相关的事件
        mPusherView.onPushEvent(event, bundle);
    }

    @Override
    public void onNetStatus(Bundle bundle) {
        //网络变化后的回调
        mPusherView.onNetStatus(bundle);
    }

    @Override
    public void onProgressChanged(int progress, int state) {
        switch (state) {
            case BeautyDialogFragment.STATE_BEAUTY:
                mBeautyLevel = OtherUtils.filtNumber(9, 100, progress);
                break;
            case BeautyDialogFragment.STATE_WHITE:
                mWhiteLevel = OtherUtils.filtNumber(3, 100, progress);
                break;
        }
        mTXLivePusher.setBeautyFilter(mBeautyLevel, mWhiteLevel);
    }

    @Override
    public void setFilter(Bitmap filterBitmap) {
        mTXLivePusher.setFilter(filterBitmap);
    }
}
