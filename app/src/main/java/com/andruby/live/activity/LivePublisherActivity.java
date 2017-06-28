package com.andruby.live.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.andruby.live.R;
import com.andruby.live.logic.IMLogin;
import com.andruby.live.logic.UserInfoMgr;
import com.andruby.live.model.ChatEntity;
import com.andruby.live.presenter.PusherPresenter;
import com.andruby.live.presenter.SwipeAnimationController;
import com.andruby.live.presenter.ipresenter.IPusherPresenter;
import com.andruby.live.utils.Constants;
import com.andruby.live.utils.LogUtil;
import com.andruby.live.utils.ToastUtils;
import com.tencent.TIMGroupManager;
import com.tencent.TIMManager;
import com.tencent.TIMValueCallBack;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXRtmpApi;
import com.tencent.rtmp.audio.TXAudioPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;
import java.util.Timer;


/**
 * @Description: 主播 推流
 * @author: Andruby
 * @date: 2016年7月8日 下午4:46:44
 */
public class LivePublisherActivity extends LiveBaseActivity implements View.OnClickListener, IPusherPresenter.IPusherView {
    private static final String TAG = LivePublisherActivity.class.getSimpleName();


    private TXCloudVideoView mTXCloudVideoView;

    private ArrayList<ChatEntity> mArrayListChatEntity = new ArrayList<>();

    private long mSecond = 0;
    private Timer mBroadcastTimer;

    private int mBeautyLevel = 100;
    private int mWhiteningLevel = 0;

    private long lTotalMemberCount = 0;
    private long lMemberCount = 0;
    private long lHeartCount = 0;

    private TXLivePushConfig mTXPushConfig = new TXLivePushConfig();

    private Handler mHandler = new Handler();

    private boolean mFlashOn = false;
    private boolean mPasuing = false;

    private String mPushUrl;
    private String mRoomId;
    private String mUserId;
    private String mTitle;
    private String mCoverPicUrl;
    private String mHeadPicUrl;
    private String mNickName;
    private String mLocation;
    private boolean mIsRecord;


    private LinearLayout mAudioPluginLayout;
    private Button mBtnAudioEffect;
    private Button mBtnAudioClose;
    private TXAudioPlayer mAudioPlayer;
    private RelativeLayout mControllLayer;
    private SwipeAnimationController mTCSwipeAnimationController;

    private PusherPresenter mPusherPresenter;
    private int[] mSettingLocation = new int[2];
    private View btnSettingView;

    @Override
    protected void setBeforeLayout() {
        super.setBeforeLayout();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

    }

    private void getDataFormIntent() {
        Intent intent = getIntent();
        mUserId = intent.getStringExtra(Constants.USER_ID);
        mPushUrl = intent.getStringExtra(Constants.PUBLISH_URL);
        mTitle = intent.getStringExtra(Constants.ROOM_TITLE);
        mCoverPicUrl = intent.getStringExtra(Constants.COVER_PIC);
        mHeadPicUrl = intent.getStringExtra(Constants.USER_HEADPIC);
        mNickName = intent.getStringExtra(Constants.USER_NICK);
        mLocation = intent.getStringExtra(Constants.USER_LOC);
        mIsRecord = intent.getBooleanExtra(Constants.IS_RECORD, false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_publisher;
    }


    @Override
    public void onReceiveExitMsg() {
        super.onReceiveExitMsg();

        LogUtil.e(TAG, "publisher broadcastReceiver receive exit app msg");
        //在被踢下线的情况下，执行退出前的处理操作：停止推流、关闭群组
        mTXCloudVideoView.onPause();
        stopPublish();
    }

    @Override
    protected void initView() {
        getDataFormIntent();

        mTXCloudVideoView = obtainView(R.id.video_view);
        btnSettingView = obtainView(R.id.btn_setting);

        mTCSwipeAnimationController = new SwipeAnimationController(this);
        mTCSwipeAnimationController.setAnimationView(mControllLayer);

        mPusherPresenter = new PusherPresenter(this);
    }

    @Override
    protected void initData() {
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.disableLog(false);
        }
        createGroup();
    }

    @Override
    protected void setListener() {

    }

    private void startPublish() {
        mTXPushConfig.setAutoAdjustBitrate(false);
        mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960);
        mTXPushConfig.setVideoBitrate(1000);
        mTXPushConfig.setVideoFPS(20);
        mTXPushConfig.setHardwareAcceleration(true);
        //切后台推流图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pause_publish, options);
        mTXPushConfig.setPauseImg(bitmap);

        mPusherPresenter.startPusher(mTXCloudVideoView, mTXPushConfig, mPushUrl);

    }

    public void createGroup() {
        //在特殊情况下未接收到kick out消息下会导致创建群组失败，在登录前做监测
        checkLoginState(new IMLogin.IMLoginListener() {
            @Override
            public void onSuccess() {
                IMLogin.getInstance().removeIMLoginListener();
                //用户登录，创建直播间
                TIMGroupManager.getInstance().createAVChatroomGroup("cniaow_live", new TIMValueCallBack<String>() {
                    @Override
                    public void onError(int code, String msg) {
                        LogUtil.e(TAG, "create av group failed. code: " + code + " errmsg: " + msg);
                    }

                    @Override
                    public void onSuccess(String roomId) {
                        LogUtil.e(TAG, "create av group succ, groupId:" + roomId);
                        mRoomId = roomId;
                        onJoinGroupResult(0, roomId);
                    }
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                IMLogin.getInstance().removeIMLoginListener();
            }
        });

    }

    public void onJoinGroupResult(int code, String msg) {
        if (0 == code) {
            //获取推流地址
            LogUtil.e(TAG, "onJoin group success" + msg);
            mRoomId = msg;
            mPusherPresenter.getPusherUrl(mUserId, msg, mTitle, mCoverPicUrl, mNickName, mHeadPicUrl, mLocation);
        } else if (Constants.NO_LOGIN_CACHE == code) {
            LogUtil.e(TAG, "onJoin group failed" + msg);
        } else {
            LogUtil.e(TAG, "onJoin group failed" + msg);
        }
    }

    private void checkLoginState(IMLogin.IMLoginListener loginListener) {

        IMLogin imLogin = IMLogin.getInstance();
        if (TextUtils.isEmpty(TIMManager.getInstance().getLoginUser())) {
            imLogin.setIMLoginListener(loginListener);
            imLogin.checkCacheAndLogin();
        } else {
            //已经处于登录态直接进行回调
            if (null != loginListener)
                loginListener.onSuccess();
        }
    }


    private void stopPublish() {
        mPusherPresenter.stopPusher();
        if (mAudioPlayer != null) {
            mAudioPlayer.stop();
            mAudioPlayer = null;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mTXCloudVideoView.onResume();

        if (mPasuing) {
            mPasuing = false;
            mPusherPresenter.resumePusher();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTXCloudVideoView.onPause();
        mPusherPresenter.pausePusher();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mPasuing = true;
        mPusherPresenter.stopPusher();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTXCloudVideoView.onDestroy();
        stopPublish();
        TXRtmpApi.setRtmpDataListener(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                stopPublish();
                finish();
                break;
            case R.id.btn_setting:
                //setting坐标
                mPusherPresenter.showSettingPopupWindow(btnSettingView, mSettingLocation);
                break;
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mSettingLocation[0] == 0 && mSettingLocation[1] == 0) {
            btnSettingView.getLocationOnScreen(mSettingLocation);
        }
    }

    public static void invoke(Activity activity, String roomTitle, String location, boolean isRecord, int bitrateType) {
        Intent intent = new Intent(activity, LivePublisherActivity.class);
        intent.putExtra(Constants.ROOM_TITLE,
                TextUtils.isEmpty(roomTitle) ? UserInfoMgr.getInstance().getNickname() : roomTitle);
        intent.putExtra(Constants.USER_ID, UserInfoMgr.getInstance().getUserId());
        intent.putExtra(Constants.USER_NICK, UserInfoMgr.getInstance().getNickname());
        intent.putExtra(Constants.USER_HEADPIC, UserInfoMgr.getInstance().getHeadPic());
        intent.putExtra(Constants.COVER_PIC, UserInfoMgr.getInstance().getCoverPic());
        intent.putExtra(Constants.USER_LOC, location);
        intent.putExtra(Constants.IS_RECORD, isRecord);
        intent.putExtra(Constants.BITRATE, bitrateType);
        activity.startActivity(intent);
    }

    @Override
    public void onGetPushUrl(String pushUrl, int errorCode) {
        mPushUrl = pushUrl;
        if (errorCode == 0) {
            startPublish();
        } else {
            ToastUtils.showShort(this, "push url is empty");
            finish();
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showMsg(String msg) {

    }

    @Override
    public void showMsg(int msg) {

    }

    @Override
    public Context getContext() {
        return this;
    }
}
