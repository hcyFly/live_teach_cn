package com.andruby.live.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andruby.live.R;
import com.andruby.live.adapter.UserAvatarListAdapter;
import com.andruby.live.logic.IMLogin;
import com.andruby.live.logic.UserInfoMgr;
import com.andruby.live.model.ChatEntity;
import com.andruby.live.presenter.IMChatPresenter;
import com.andruby.live.presenter.PusherPresenter;
import com.andruby.live.presenter.SwipeAnimationController;
import com.andruby.live.presenter.ipresenter.IIMChatPresenter;
import com.andruby.live.presenter.ipresenter.IPusherPresenter;
import com.andruby.live.ui.customviews.InputTextMsgDialog;
import com.andruby.live.utils.AsimpleCache.ACache;
import com.andruby.live.utils.Constants;
import com.andruby.live.utils.HWSupportList;
import com.andruby.live.utils.LogUtil;
import com.andruby.live.utils.OtherUtils;
import com.andruby.live.utils.ToastUtils;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.audio.TXAudioPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @Description: 主播 推流
 * @author: Andruby
 * @date: 2016年7月8日 下午4:46:44
 */
public class LivePublisherActivity extends IMBaseActivity implements View.OnClickListener,
        IPusherPresenter.IPusherView, IIMChatPresenter.IIMChatView, InputTextMsgDialog.OnTextSendListener {
    private static final String TAG = LivePublisherActivity.class.getSimpleName();


    private TXCloudVideoView mTXCloudVideoView;

    private ArrayList<ChatEntity> mArrayListChatEntity = new ArrayList<>();

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

    private IMChatPresenter mIMChatPresenter;

    //主播相关信息，头像、观众数
    private ImageView ivHeadIcon;
    private ImageView ivRecordBall;
    private TextView tvMemberCount;
    //播放信息：时间、红点
    private long mSecond = 0;
    private TextView tvBroadcastTime;
    private Timer mBroadcastTimer;
    private BroadcastTimerTask mBroadcastTimerTask;
    private ObjectAnimator mObjAnim;

    private InputTextMsgDialog mInputTextMsgDialog;

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


        //主播信息
        tvBroadcastTime = obtainView(R.id.tv_broadcasting_time);
        tvBroadcastTime.setText(String.format(Locale.US, "%s", "00:00:00"));
        ivRecordBall = obtainView(R.id.iv_record_ball);
        ivHeadIcon = obtainView(R.id.iv_head_icon);
        OtherUtils.showPicWithUrl(this, ivHeadIcon, ACache.get(this).getAsString("head_pic_small"), R.drawable.default_head);
        tvMemberCount = obtainView(R.id.tv_member_counts);
        tvMemberCount.setText("0");

        mPusherPresenter = new PusherPresenter(this);
        mIMChatPresenter = new IMChatPresenter(this);
        recordAnmination();

        mInputTextMsgDialog = new InputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);


    }

    private void recordAnmination() {
        mObjAnim = ObjectAnimator.ofFloat(ivRecordBall, "alpha", 1.0f, 0f, 1.0f);
        mObjAnim.setDuration(1000);
        mObjAnim.setRepeatCount(-1);
        mObjAnim.start();
    }

    @Override
    protected void initData() {
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.disableLog(false);
        }
        mIMChatPresenter.createGroup();
    }

    @Override
    protected void setListener() {

    }

    private void startPublish() {
        mTXPushConfig.setAutoAdjustBitrate(false);
        mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960);
        mTXPushConfig.setVideoBitrate(1000);
        mTXPushConfig.setVideoFPS(20);
        Log.i(TAG, "startPublish: MANUFACTURER " + Build.MANUFACTURER + " model:" + Build.MODEL);
        if (HWSupportList.isHWVideoEncodeSupport()) {
            mTXPushConfig.setHardwareAcceleration(true);
            Log.i(TAG, "startPublish: 手机型号硬编码设置成功！！！");
        } else {
            Log.i(TAG, "startPublish: 手机型号不支持硬编码！！！");
        }

        mTXPushConfig.setWatermark(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), 50, 50);
        //切后台推流图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pause_publish, options);
        mTXPushConfig.setPauseImg(bitmap);
        mPusherPresenter.startPusher(mTXCloudVideoView, mTXPushConfig, mPushUrl);


        if (mBroadcastTimer == null) {
            mBroadcastTimer = new Timer(true);
            mBroadcastTimerTask = new BroadcastTimerTask();
            mBroadcastTimer.schedule(mBroadcastTimerTask, 1000, 1000);
        }
    }

    @Override
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

    @Override
    public void onGroupDeleteResult() {

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
        mIMChatPresenter.deleteGroup();
        if (mObjAnim != null) {
            mObjAnim.cancel();
        }
        if (mBroadcastTimerTask != null) {
            mBroadcastTimerTask.cancel();
            mBroadcastTimer.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                stopPublish();
                finish();
                break;
            case R.id.btn_message_input:
                showInputMsgDialog();
                break;
            case R.id.btn_setting:
                //setting坐标
                mPusherPresenter.showSettingPopupWindow(btnSettingView, mSettingLocation);
                break;
        }

    }

    private void showInputMsgDialog() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();

        lp.width = display.getWidth(); //设置宽度
        mInputTextMsgDialog.getWindow().setAttributes(lp);
        mInputTextMsgDialog.setCancelable(true);
        mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mInputTextMsgDialog.show();
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
        intent.putExtra(Constants.USER_ID, ACache.get(activity).getAsString("user_id"));
        intent.putExtra(Constants.USER_NICK, ACache.get(activity).getAsString("nickname"));
        intent.putExtra(Constants.USER_HEADPIC, ACache.get(activity).getAsString("head_pic_small"));
        intent.putExtra(Constants.COVER_PIC, ACache.get(activity).getAsString("head_pic"));
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
        ToastUtils.makeText(this, msg, Toast.LENGTH_SHORT);
    }

    @Override
    public void showMsg(int msg) {
        ToastUtils.makeText(this, msg, Toast.LENGTH_SHORT);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public FragmentManager getFragmentMgr() {
        return getFragmentManager();
    }

    @Override
    public void onTextSend(String msg, boolean tanmuOpen) {
        mIMChatPresenter.sendTextMsg(msg);
    }


    class BroadcastTimerTask extends TimerTask {

        @Override
        public void run() {
            mSecond++;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvBroadcastTime.setText(OtherUtils.formattedTime(mSecond));
                }
            });
        }
    }
}
