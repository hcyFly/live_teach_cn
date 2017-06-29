package com.andruby.live.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.andruby.live.R;
import com.andruby.live.fragment.LiveListFragment;
import com.andruby.live.logic.UserInfoMgr;
import com.andruby.live.presenter.ReviewPlayPresenter;
import com.andruby.live.presenter.ipresenter.IReviewPlayPresenter;
import com.andruby.live.utils.Constants;
import com.andruby.live.utils.LogUtil;
import com.andruby.live.utils.ToastUtils;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.Locale;


/**
 * @Description: 回看 播放页面
 * @author: Andruby
 * @date: 2016/12/21 10:12
 */
public class ReviewPlayActivity extends BaseActivity implements ITXLivePlayListener,IReviewPlayPresenter.IReviewPlayView,View.OnClickListener {
    private static final String TAG = ReviewPlayActivity.class.getName();
    private long mTrackingTouchTS = 0;
    private boolean mStartSeek = false;
    private boolean mVideoPause = false;
    private SeekBar mSeekBar;
    private ImageView mPlayIcon;
    private TextView mTextProgress;
    private ImageView mBgImageView;

    private boolean mPausing = false;
    private boolean mPlaying = false;

    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePlayer mTXLivePlayer;
    private TXLivePlayConfig mTXPlayConfig = new TXLivePlayConfig();
    private int mPlayType = TXLivePlayer.PLAY_TYPE_VOD_MP4;
    private String mPlayUrl = "";
    private String mUserId = "";
    private String mNickname = "";
    private String mHeadPic = "";
    private IReviewPlayPresenter mReViewPlayPresenter;

    public static void invoke(Context context,String url){
        Intent intent = new Intent(context,ReviewPlayActivity.class);
        intent.putExtra(Constants.PLAY_URL,url);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reviewplay;
    }

    @Override
    protected void initView() {
        mBgImageView = obtainView(R.id.background);
        mTXCloudVideoView = obtainView(R.id.video_view);
        mTextProgress = obtainView(R.id.progress_time);
        mPlayIcon = obtainView(R.id.play_btn);
        mSeekBar = obtainView(R.id.seekbar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean bFromUser) {
                if (mTextProgress != null) {
                    mTextProgress.setText(String.format(Locale.CHINA, "%02d:%02d:%02d/%02d:%02d:%02d", progress / 3600, (progress % 3600) / 60, (progress % 3600) % 60, seekBar.getMax() / 3600, (seekBar.getMax() % 3600) / 60, (seekBar.getMax() % 3600) % 60));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mStartSeek = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mTXLivePlayer.seek(seekBar.getProgress());
                mTrackingTouchTS = System.currentTimeMillis();
                mStartSeek = false;
            }
        });


    }

    private void getDataFormIntent() {
        Intent intent = getIntent();
        mPlayUrl = intent.getStringExtra(Constants.PLAY_URL);
        mUserId = UserInfoMgr.getInstance().getUserId();
        mNickname = UserInfoMgr.getInstance().getNickname();
        mHeadPic = UserInfoMgr.getInstance().getHeadPic();
    }

    @Override
    protected void initData() {
        mReViewPlayPresenter = new ReviewPlayPresenter(this);
        getDataFormIntent();
        startPlay();
    }

    @Override
    protected void setListener() {
        obtainView(R.id.btn_vod_back).setOnClickListener(this);
        obtainView(R.id.play_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_vod_back:
                finish();
                break;
            case R.id.play_btn: {
                if (mPlaying) {
                    if (mVideoPause) {
                        mTXLivePlayer.resume();
                        if (mPlayIcon != null) {
                            mPlayIcon.setBackgroundResource(R.drawable.play_pause);
                        }
                    } else {
                        mTXLivePlayer.pause();
                        if (mPlayIcon != null) {
                            mPlayIcon.setBackgroundResource(R.drawable.play_start);
                        }
                    }
                    mVideoPause = !mVideoPause;
                } else {
                    if (mPlayIcon != null) {
                        mPlayIcon.setBackgroundResource(R.drawable.play_pause);
                    }
                    startPlay();
                }

            }
            break;
        }
    }
    @Override
    public void onPlayEvent(int event, Bundle param) {
        LogUtil.e(TAG, "onPlayEvent:" + event + ",param:" + param.toString());
        if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            if (mStartSeek) {
                return;
            }
            int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);
            int duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION);
            long curTS = System.currentTimeMillis();
            // 避免滑动进度条松开的瞬间可能出现滑动条瞬间跳到上一个位置
            if (Math.abs(curTS - mTrackingTouchTS) < 500) {
                return;
            }
            mTrackingTouchTS = curTS;

            if (mSeekBar != null) {
                mSeekBar.setProgress(progress);
            }
            if (mTextProgress != null) {
                mTextProgress.setText(String.format(Locale.CHINA, "%02d:%02d:%02d/%02d:%02d:%02d", progress / 3600, (progress % 3600) / 60, progress % 60, duration / 3600, (duration % 3600) / 60, duration % 60));
            }

            if (mSeekBar != null) {
                mSeekBar.setMax(duration);
            }
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
            ToastUtils.showShort(mContext,getString(R.string.net_discnnect));
            finish();
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            stopPlay(false);
            mVideoPause = false;
            if (mTextProgress != null) {
                mTextProgress.setText(String.format(Locale.CHINA, "%s", "00:00:00/00:00:00"));
            }
            if (mSeekBar != null) {
                mSeekBar.setProgress(0);
            }
            if (mPlayIcon != null) {
                mPlayIcon.setBackgroundResource(R.drawable.play_start);
            }
        }
    }

    @Override
    public void onNetStatus(Bundle status) {
        LogUtil.e(TAG, "Current status: " + status.toString());
        if (status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH) > status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)) {
            if (mTXLivePlayer != null)
                mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
        } else if (mTXLivePlayer != null)
            mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
    }


    private void startPlay() {
        int check = mReViewPlayPresenter.checkPlayUrl(mPlayUrl);
        if (check == -1) {
            return;
        }
        mPlayType = check;
        if (mTXLivePlayer == null) {
            mTXLivePlayer = new TXLivePlayer(this);
        }
        mTXLivePlayer.setPlayerView(mTXCloudVideoView);
        mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mTXLivePlayer.setPlayListener(this);
        mTXLivePlayer.setConfig(mTXPlayConfig);
        int result;
        result = mTXLivePlayer.startPlay(mPlayUrl, mPlayType);
        if (0 != result) {
            Intent rstData = new Intent();
            mTXCloudVideoView.onPause();
            stopPlay(true);
            setResult(LiveListFragment.START_LIVE_PLAY, rstData);
            finish();
        } else {
            mPlaying = true;
        }
    }

    private void stopPlay(boolean clearLastFrame) {
        if (mTXLivePlayer != null) {
            mTXLivePlayer.setPlayListener(null);
            mTXLivePlayer.stopPlay(clearLastFrame);
            mPlaying = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTXCloudVideoView.onResume();
        if (!mVideoPause) {
            mTXLivePlayer.resume();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        mTXCloudVideoView.onPause();
        mTXLivePlayer.pause();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTXCloudVideoView.onDestroy();
        stopPlay(true);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showMsg(String msg) {
        ToastUtils.showShort(this,msg);
    }

    @Override
    public void showMsg(int msg) {
        ToastUtils.showShort(this,msg);
    }

    @Override
    public Context getContext() {
        return this;
    }
}
