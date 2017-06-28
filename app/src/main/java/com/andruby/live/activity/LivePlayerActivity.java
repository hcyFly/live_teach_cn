package com.andruby.live.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.andruby.live.R;
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
public class LivePlayerActivity extends LiveBaseActivity implements View.OnClickListener {

    private static final String TAG = LivePlayerActivity.class.getSimpleName();

    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePlayer mTXLivePlayer;
    private TXLivePlayConfig mTXPlayConfig = new TXLivePlayConfig();
    private boolean mPausing = false;
    private String mPlayUrl = "";
    private int mPlayType = TXLivePlayer.PLAY_TYPE_VOD_FLV;
    private boolean mPlaying = false;

    public final static int LIVE_PLAYER_REQUEST_CODE = 1000;

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
//创建player对象
        mTXLivePlayer = new TXLivePlayer(this);
        //必须添加播放监听 hcy
        mTXLivePlayer.setPlayListener(new ITXLivePlayListener() {
            @Override
            public void onPlayEvent(int i, Bundle bundle) {
                //播放事件逻辑
            }

            @Override
            public void onNetStatus(Bundle bundle) {
                //处理网络状态相关
            }
        });
//关键player对象与界面view
        mTXLivePlayer.setPlayerView(mTXCloudVideoView);


        mPlayUrl = getIntent().getStringExtra(Constants.PLAY_URL);
        if (mPlayUrl != null) {
            mTXLivePlayer.startPlay(mPlayUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV); //推荐FLV
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
        mTXLivePlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTXLivePlayer.resume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTXLivePlayer.stopPlay(true);
        mTXCloudVideoView.onDestroy();
    }

    public static void invoke(Activity activity, String playUrl) {
        Intent intent = new Intent(activity, LivePlayerActivity.class);
        intent.putExtra(Constants.PLAY_URL, playUrl);
        activity.startActivityForResult(intent, LIVE_PLAYER_REQUEST_CODE);
    }

}
