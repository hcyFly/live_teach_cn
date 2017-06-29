package com.andruby.live.presenter;

import android.text.TextUtils;

import com.andruby.live.presenter.ipresenter.IReviewPlayPresenter;
import com.tencent.rtmp.TXLivePlayer;

/**
 * author : Andruby on 2017/4/17 10:18
 * description :
 */

public class ReviewPlayPresenter implements IReviewPlayPresenter {

    IReviewPlayView mIReViewPlayView;
    public ReviewPlayPresenter(IReviewPlayView view){
        mIReViewPlayView = view;
    }

    @Override
    public int checkPlayUrl(String mPlayUrl) {
        int mPlayType = -1;
        if (TextUtils.isEmpty(mPlayUrl) || (!mPlayUrl.startsWith("http://") && !mPlayUrl.startsWith("https://") && !mPlayUrl.startsWith("rtmp://"))) {
            mIReViewPlayView.showMsg("播放地址不合法，目前仅支持rtmp,flv,hls,mp4播放方式!");
            return mPlayType;
        }
        if (mPlayUrl.startsWith("http://") || mPlayUrl.startsWith("https://")) {
            if (mPlayUrl.contains(".flv")) {
                mPlayType = TXLivePlayer.PLAY_TYPE_VOD_FLV;
            } else if (mPlayUrl.contains(".m3u8")) {
                mPlayType = TXLivePlayer.PLAY_TYPE_VOD_HLS;
            } else if (mPlayUrl.toLowerCase().contains(".mp4")) {
                mPlayType = TXLivePlayer.PLAY_TYPE_VOD_MP4;
            } else {
                mIReViewPlayView.showMsg("播放地址不合法，目前仅支持rtmp,flv,hls,mp4播放方式!");
            }
        } else {
            mIReViewPlayView.showMsg("播放地址不合法，目前仅支持rtmp,flv,hls,mp4播放方式!");
        }
        return mPlayType;
    }
}
