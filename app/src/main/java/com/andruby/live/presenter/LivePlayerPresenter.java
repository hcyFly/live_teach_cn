package com.andruby.live.presenter;

import android.os.Bundle;
import android.util.Log;

import com.andruby.live.http.AsyncHttp;
import com.andruby.live.http.request.EnterGroupRequest;
import com.andruby.live.http.request.FetchGroupMemberListReuest;
import com.andruby.live.http.request.GroupMemberReuest;
import com.andruby.live.http.request.LiveLikeRequest;
import com.andruby.live.http.request.QuitGroupRequest;
import com.andruby.live.http.request.RequestComm;
import com.andruby.live.http.response.GroupMemberList;
import com.andruby.live.http.response.ResList;
import com.andruby.live.http.response.Response;
import com.andruby.live.model.SimpleUserInfo;
import com.andruby.live.presenter.ipresenter.ILivePlayerPresenter;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;

/**
 * Created by zhao on 2017/3/11.
 */

public class LivePlayerPresenter extends ILivePlayerPresenter implements ITXLivePlayListener {

    private final static String TAG = LivePlayerPresenter.class.getSimpleName();
    private ILivePlayerView mLivePlayerView;
    private TXCloudVideoView mCloudVideoView;
    private TXLivePlayer mLivePLayer;

    public LivePlayerPresenter(ILivePlayerView baseView) {
        super(baseView);
        mLivePlayerView = baseView;
    }

    @Override
    public void initPlayerView(TXCloudVideoView cloudVideoView, TXLivePlayConfig livePlayConfig) {
        mCloudVideoView = cloudVideoView;
        mLivePLayer = new TXLivePlayer(mLivePlayerView.getContext());
        mLivePLayer.enableHardwareDecode(true);
        mLivePLayer.setPlayerView(cloudVideoView);
        mLivePLayer.setPlayListener(this);
        mLivePLayer.setConfig(livePlayConfig);
    }

    public void  enableHardwareDecode(boolean decode){
        mLivePLayer.enableHardwareDecode(false);
    }

    @Override
    public void playerPause() {
        mLivePLayer.pause();
    }

    @Override
    public void playerResume() {
        mLivePLayer.resume();
    }

    @Override
    public void startPlay(String playUrl, int playType) {
        mLivePLayer.startPlay(playUrl, playType);
    }

    public void stopPlay(boolean isClearLastImg) {
        mLivePLayer.stopPlay(isClearLastImg);
        mCloudVideoView.onDestroy();
    }

    @Override
    public void doLike(String userId, String liveId, String hostId, String groupId) {
        LiveLikeRequest req = new LiveLikeRequest(1000, userId, liveId, hostId, groupId);
        AsyncHttp.instance().postJson(req, new AsyncHttp.IHttpListener() {
            @Override
            public void onStart(int requestId) {

            }

            @Override
            public void onSuccess(int requestId, Response response) {
                if (response.status == RequestComm.SUCCESS) {
                    mLivePlayerView.doLikeResult(0);
                } else {
                    mLivePlayerView.doLikeResult(1);
                }
                Log.i("log", "onSuccess: doLike");
            }

            @Override
            public void onFailure(int requestId, int httpStatus, Throwable error) {
                mLivePlayerView.doLikeResult(1);
                Log.i("log", "onFailure: doLike");
            }
        });
    }

    @Override
    public void enterGroup(String userId, String liveId, String hostId, String groupId) {
        EnterGroupRequest req = new EnterGroupRequest(RequestComm.enterlive, userId, liveId, hostId, groupId);
        AsyncHttp.instance().postJson(req, null);
    }

    @Override
    public void quitGroup(String userId, String liveId, String hostId, String groupId) {
        QuitGroupRequest quiteRequest = new QuitGroupRequest(RequestComm.QuitGroup, userId, liveId, hostId, groupId);
        AsyncHttp.instance().postJson(quiteRequest, null);
    }

    @Override
    public void groupMember(String userId, String liveId, String hostId, String groupId, int pageIndex, int pageSize) {
        GroupMemberReuest quiteRequest = new GroupMemberReuest(RequestComm.memberList, userId, liveId, hostId, groupId, 1, 20);
        AsyncHttp.instance().postJson(quiteRequest, new AsyncHttp.IHttpListener() {
            @Override
            public void onStart(int requestId) {

            }

            @Override
            public void onSuccess(int requestId, Response response) {
                if (response != null && response.status == RequestComm.SUCCESS) {
                    ResList resList = (ResList) response.data;
                    if (resList != null && resList.items != null) {
                        mLivePlayerView.onGroupMembersResult(0, resList.totalCount, (ArrayList<SimpleUserInfo>) resList.items);
                    } else {
                        mLivePlayerView.onGroupMembersResult(1, 0, null);
                    }
                } else {
                    mLivePlayerView.onGroupMembersResult(1, 0, null);
                }
            }

            @Override
            public void onFailure(int requestId, int httpStatus, Throwable error) {
                mLivePlayerView.onGroupMembersResult(1, 0, null);
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
    public void onPlayEvent(int event, Bundle bundle) {
        Log.i(TAG, "onPlayEvent: event = " + event);
        mLivePlayerView.onPlayEvent(event,bundle);
    }

    @Override
    public void onNetStatus(Bundle bundle) {
        Log.i(TAG, "onNetStatus: cpu = " + bundle.getString(TXLiveConstants.NET_STATUS_CPU_USAGE));
        mLivePlayerView.onNetStatus(bundle);
    }
}
