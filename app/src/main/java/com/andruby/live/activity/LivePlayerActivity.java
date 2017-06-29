package com.andruby.live.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andruby.live.R;
import com.andruby.live.adapter.ChatMsgListAdapter;
import com.andruby.live.logic.FrequeMgr;
import com.andruby.live.model.ChatEntity;
import com.andruby.live.model.LiveInfo;
import com.andruby.live.model.SimpleUserInfo;
import com.andruby.live.presenter.IMChatPresenter;
import com.andruby.live.presenter.LivePlayerPresenter;
import com.andruby.live.presenter.ipresenter.IIMChatPresenter;
import com.andruby.live.presenter.ipresenter.ILivePlayerPresenter;
import com.andruby.live.ui.customviews.HeartLayout;
import com.andruby.live.ui.customviews.InputTextMsgDialog;
import com.andruby.live.utils.Constants;
import com.andruby.live.utils.LogUtil;
import com.andruby.live.utils.OtherUtils;
import com.andruby.live.utils.ToastUtils;
import com.tencent.TIMMessage;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @Description: 观众 观看播放页面
 * @author: Andruby
 * @date: 2016年7月8日 下午4:46:44
 */
public class LivePlayerActivity extends IMBaseActivity implements View.OnClickListener,
        ILivePlayerPresenter.ILivePlayerView, IIMChatPresenter.IIMChatView, InputTextMsgDialog.OnTextSendListener {

    private static final String TAG = LivePlayerActivity.class.getSimpleName();
    public final static int LIVE_PLAYER_REQUEST_CODE = 1000;

    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePlayConfig mTXPlayConfig = new TXLivePlayConfig();
    private boolean mPausing = false;
    private String mPlayUrl = "";
    private boolean mPlaying = false;
    private LiveInfo mLiveInfo;

    private LivePlayerPresenter mLivePlayerPresenter;
    private IMChatPresenter mIMChatPresenter;
    //主播信息
    private ImageView ivHeadIcon;
    private ImageView ivRecordBall;
    private TextView tvPuserName;
    private TextView tvMemberCount;
    private long mMemberCount = 0;

    private InputTextMsgDialog mInputTextMsgDialog;

    //消息列表
    private ArrayList<ChatEntity> mArrayListChatEntity = new ArrayList<>();
    private ChatMsgListAdapter mChatMsgListAdapter;
    private ListView mListViewMsg;

    //点赞频率控制
    private FrequeMgr mLikeFrequeControl;

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
        mLivePlayerPresenter.initPlayerView(mTXCloudVideoView, mTXPlayConfig);
        mIMChatPresenter = new IMChatPresenter(this);

        //主播信息
        tvPuserName = obtainView(R.id.tv_broadcasting_time);
        tvPuserName.setText(OtherUtils.getLimitString(mLiveInfo.userInfo.nickname, 10));
        ivRecordBall = obtainView(R.id.iv_record_ball);
        ivRecordBall.setVisibility(View.GONE);
        ivHeadIcon = obtainView(R.id.iv_head_icon);
        OtherUtils.showPicWithUrl(this, ivHeadIcon, mLiveInfo.userInfo.headPic, R.drawable.default_head);
        tvMemberCount = obtainView(R.id.tv_member_counts);

        mInputTextMsgDialog = new InputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);

        mMemberCount++;
        tvMemberCount.setText(String.format(Locale.CHINA, "%d", mMemberCount));
        if (mPlayUrl != null) {
            mLivePlayerPresenter.startPlay(mPlayUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV); //推荐FLV
        } else {
            showToast("play url is empty");
        }

        mIMChatPresenter.joinGroup(mLiveInfo.groupId);

        mListViewMsg = obtainView(R.id.im_msg_listview);
        mChatMsgListAdapter = new ChatMsgListAdapter(this, mListViewMsg, mArrayListChatEntity);
        mListViewMsg.setAdapter(mChatMsgListAdapter);

    }

    @Override
    protected void initData() {

    }

    private void getDataFormIntent() {
        Intent intent = getIntent();
        mLiveInfo = (LiveInfo) intent.getSerializableExtra(Constants.LIVE_INFO);
        mPlayUrl = mLiveInfo.playUrl;
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
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_message_input:
                showInputMsgDialog();
                break;
            case R.id.btn_like:
                //点赞发送请求限制
                if (mLikeFrequeControl == null) {
                    mLikeFrequeControl = new FrequeMgr();
                    mLikeFrequeControl.init(2, 1);
                }
                if (mLikeFrequeControl.canTrigger()) {
                    //向后台发送点赞信息
                    mLivePlayerPresenter.doLike(mLiveInfo.userId, mLiveInfo.liveId);
                    //向ChatRoom发送点赞消息
//                    mIMChatPresenter.sendPraiseMessage();
                }
                break;

            default:
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
        mTXCloudVideoView.onDestroy();
        mIMChatPresenter.quitGroup(mLiveInfo.groupId);
    }

    public static void invoke(Activity activity, LiveInfo liveInfo) {
        Intent intent = new Intent(activity, LivePlayerActivity.class);
        intent.putExtra(Constants.LIVE_INFO, liveInfo);
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
    public void onPlayEvent(int i, Bundle bundle) {

    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    @Override
    public void doLikeResult(int result) {

    }

    @Override
    public void onJoinGroupResult(int code, String msg) {

    }

    @Override
    public void onGroupDeleteResult() {

    }

    @Override
    public void handleTextMsg(SimpleUserInfo userInfo, String text) {
        ChatEntity entity = new ChatEntity();
        entity.setSenderName(userInfo.nickname + ":");
        entity.setContext(text);
        entity.setType(Constants.AVIMCMD_TEXT_TYPE);
        notifyMsg(entity);
    }

    @Override
    public void onSendMsgResult(int code, TIMMessage timMessage) {

    }

    /**
     * 刷新消息列表
     *
     * @param entity
     */
    private void notifyMsg(final ChatEntity entity) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mArrayListChatEntity.add(entity);
                mChatMsgListAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onTextSend(String msg, boolean tanmuOpen) {
        mIMChatPresenter.sendTextMsg(msg);
        ChatEntity entity = new ChatEntity();
        entity.setSenderName("我:");
        entity.setContext(msg);
        entity.setType(Constants.AVIMCMD_TEXT_TYPE);
        notifyMsg(entity);
    }
}
