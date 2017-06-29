package com.andruby.live.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.andruby.live.R;
import com.andruby.live.presenter.LivePlayerPresenter;
import com.andruby.live.presenter.ipresenter.ILivePlayerPresenter;
import com.andruby.live.utils.Constants;
import com.andruby.live.utils.LogUtil;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * @Description: 观众 观看播放页面  (  页面元素空)
 * @author: Andruby
 * @date: 2016年7月8日 下午4:46:44
 */
public class LivePlayerActivity extends LiveBaseActivity implements View.OnClickListener, ILivePlayerPresenter.ILivePlayerView {

    private static final String TAG = LivePlayerActivity.class.getSimpleName();

    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePlayer mTXLivePlayer;
    private TXLivePlayConfig mTXPlayConfig = new TXLivePlayConfig();
    private boolean mPausing = false;
    private String mPlayUrl = "";
    private int mPlayType = TXLivePlayer.PLAY_TYPE_VOD_FLV;
    private boolean mPlaying = false;

    public final static int LIVE_PLAYER_REQUEST_CODE = 1000;
    private LivePlayerPresenter mLivePlayerPresenter;

    @Override
    protected void setBeforeLayout() {
        super.setBeforeLayout();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_player;
    }

    @Override
    protected void initView() {
        getDataFormIntent();
        initLiveView();

        //mPlayerView即step1中添加的界面view
        mTXCloudVideoView = obtainView(R.id.video_view);
        mLivePlayerPresenter = new LivePlayerPresenter(this);
        mTXPlayConfig.setConnectRetryCount(3);
        mTXPlayConfig.setConnectRetryInterval(3);
        mLivePlayerPresenter.initPlayerView(mTXCloudVideoView, mTXPlayConfig);

        mPlayUrl = getIntent().getStringExtra(Constants.PLAY_URL);
        if (mPlayUrl != null) {
            mLivePlayerPresenter.startPlay(mPlayUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV); //推荐FLV
        } else {
            showToast("play url is empty");
        }
    }

    @Override
    protected void initData() {

    }

    private void getDataFormIntent() {
        Intent intent = getIntent();
        mPlayUrl = intent.getStringExtra(Constants.PLAY_URL);
        LogUtil.e(TAG, "mPlayUrl:" + mPlayUrl);
    }

    @Override
    protected void setListener() {

    }


    /**
     * 初始化观看直播界面
     */
    private void initLiveView() {
        mTXCloudVideoView = obtainView(R.id.video_view);
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLivePlayerPresenter.playerPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLivePlayerPresenter.playerResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLivePlayerPresenter.stopPlay(true);
    }

    public static void invoke(Activity activity, String playUrl) {
        Intent intent = new Intent(activity, LivePlayerActivity.class);
        intent.putExtra(Constants.PLAY_URL, playUrl);
        activity.startActivityForResult(intent, LIVE_PLAYER_REQUEST_CODE);
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

    @Override
    public void onPlayEvent(int i, Bundle bundle) {

    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }
}
