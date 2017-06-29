package com.andruby.live.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.andruby.live.logic.IMLogin;
import com.andruby.live.logic.UserInfoMgr;
import com.andruby.live.model.GiftWithUerInfo;
import com.andruby.live.model.SimpleUserInfo;
import com.andruby.live.presenter.ipresenter.IIMChatPresenter;
import com.andruby.live.utils.AsimpleCache.ACache;
import com.andruby.live.utils.Constants;
import com.andruby.live.utils.LogUtil;
import com.google.gson.Gson;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMGroupSystemElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.List;

/**
 * Created by zhao on 2017/3/13.
 */

public class IMChatPresenter extends IIMChatPresenter implements TIMMessageListener {

    private final static String TAG = IMChatPresenter.class.getSimpleName();
    private String mRoomId;

    private TIMConversation mGroupConversation;
    private IIMChatView mIMChatView;
    private Gson mGson = new Gson();

    public IMChatPresenter(IIMChatView baseView) {
        super(baseView);
        mIMChatView = baseView;
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {
        TIMManager.getInstance().removeMessageListener(this);
        mGroupConversation = null;
    }


    @Override
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
                        LogUtil.e(TAG, "create group failed. code: " + code + " errmsg: " + msg);
                    }

                    @Override
                    public void onSuccess(String roomId) {
                        LogUtil.e(TAG, "create group succ, groupId:" + roomId);
                        mRoomId = roomId;
                        mIMChatView.onJoinGroupResult(0, roomId);
                        mGroupConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, mRoomId);
                        TIMManager.getInstance().addMessageListener(IMChatPresenter.this);
                    }
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                IMLogin.getInstance().removeIMLoginListener();
            }
        });
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

    @Override
    public void deleteGroup() {
        sendMessage(Constants.AVIMCMD_LIVE_END, "");
        TIMGroupManager.getInstance().deleteGroup(mRoomId, new TIMCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.i(TAG, String.format("delete group error code = %d,msg = %s", code, msg));
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "delete group success");
                finish();
            }
        });
    }

    @Override
    public void joinGroup(String roomId) {
        mRoomId = roomId;
        TIMGroupManager.getInstance().applyJoinGroup(roomId, "", new TIMCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.i(TAG, String.format("join group error code = %d,msg = %s", code, msg));
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "join group success");
                mGroupConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, mRoomId);
                TIMManager.getInstance().addMessageListener(IMChatPresenter.this);
                mIMChatView.onJoinGroupResult(0, mRoomId);
                sendMessage(Constants.AVIMCMD_ENTER_LIVE, "");
            }
        });
    }

    @Override
    public void quitGroup(String roomId) {
        sendMessage(Constants.AVIMCMD_EXIT_LIVE, "");
        TIMGroupManager.getInstance().quitGroup(roomId, new TIMCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.i(TAG, String.format("quit group error code = %d,msg = %s", code, msg));
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "quit group success");
                finish();
            }
        });
    }

    @Override
    public void sendTextMsg(final String msg) {
        sendMessage(Constants.AVIMCMD_TEXT_TYPE, msg);
    }

    @Override
    public void sendPraiseMessage() {
        sendMessage(Constants.AVIMCMD_PRAISE, null);
    }

    public void sendPraiseFirstMessage() {
        sendMessage(Constants.AVIMCMD_PRAISE_FIRST, null);
    }

    public void sendMessage(int userAction, String msg) {
        Log.i(TAG, "sendMessage: userAction = " + userAction + " msg = " + msg);
        JSONObject json = new JSONObject();
        try {
            json.put("userAction", userAction);
            json.put("userId", ACache.get(mIMChatView.getContext()).getAsString("user_id"));
            json.put("nickname", ACache.get(mIMChatView.getContext()).getAsString("nickname"));
            json.put("headPic", ACache.get(mIMChatView.getContext()).getAsString("head_pic"));
            if (msg != null) {
                json.put("params", msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonMsg = json.toString();
        TIMMessage message = new TIMMessage();
        TIMTextElem textElem = new TIMTextElem();
        textElem.setText(jsonMsg);
        if (message.addElement(textElem) != 0) {
            return;
        }
        sendTIMMessage(message, new TIMValueCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.i(TAG, String.format("send message onError: code = %d,msg = %s", code, msg));
            }

            @Override
            public void onSuccess(Object o) {
                Log.i(TAG, "send message onSuccess: ");
            }
        });
    }

    private void sendTIMMessage(TIMMessage message, TIMValueCallBack callBack) {
        if (mGroupConversation != null) {
            mGroupConversation.sendMessage(message, callBack);
        }
    }

    public void sendGiftMessage(String msg) {
        sendMessage(Constants.AVIMCMD_GIFT, msg);
    }

    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        parserMessage(list);
        return false;
    }

    private void parserMessage(List<TIMMessage> list) {
        for (TIMMessage msg : list) {
            TIMElem elem = msg.getElement(0);
            if (elem.getType() == TIMElemType.Text) {
                TIMTextElem text = (TIMTextElem) elem;
                handleCustomTextMsg(text.getText());
                Log.i(TAG, "onNewMessages: msg = " + text.getText());
            } else if (elem.getType() == TIMElemType.GroupSystem) {
                TIMGroupSystemElem systemElem = (TIMGroupSystemElem) elem;
                Log.i(TAG, "parserMessage: group msg");
                if (systemElem.getSubtype() == TIMGroupSystemElemType.TIM_GROUP_SYSTEM_DELETE_GROUP_TYPE) {
                    Log.i(TAG, "parserMessage: delete group msg");
                    //观众 退出直播观看
                    mIMChatView.onGroupDeleteResult();
                }
            }
        }
    }

    private void handleCustomTextMsg(String jsonMsg) {
        JSONTokener jsonTokener = new JSONTokener(jsonMsg);
        try {
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            int userAction = jsonObject.getInt("userAction");
            String nickname = null;
            String userId = null;
            String headPic = null;
            String msg = null;
            if (jsonObject.has("userId")) {
                userId = jsonObject.getString("userId");
            }
            if (jsonObject.has("nickname")) {
                nickname = jsonObject.getString("nickname");
            }
            if (jsonObject.has("headPic")) {
                headPic = jsonObject.getString("headPic");
            }
            if (jsonObject.has("msg")) {
                msg = jsonObject.getString("msg");
            }
            switch (userAction) {
                case Constants.AVIMCMD_TEXT_TYPE:
                    mIMChatView.handleTextMsg(new SimpleUserInfo(userId, nickname, headPic), msg);
                    break;
                case Constants.AVIMCMD_PRAISE_FIRST:
                    mIMChatView.handlePraiseFirstMsg(new SimpleUserInfo(userId, nickname, headPic));
                    break;
                case Constants.AVIMCMD_PRAISE:
                    mIMChatView.handlePraiseMsg(new SimpleUserInfo(userId, nickname, headPic));
                    break;
                case Constants.AVIMCMD_ENTER_LIVE:
                    mIMChatView.handleEnterLiveMsg(new SimpleUserInfo(userId, nickname, headPic));
                    break;
                case Constants.AVIMCMD_EXIT_LIVE:
                    mIMChatView.handleExitLiveMsg(new SimpleUserInfo(userId, nickname, headPic));
                    break;
                case Constants.AVIMCMD_HOST_LEAVE:
                    Log.i(TAG, "handleCustomTextMsg: AVIMCMD_HOST_LEAVE");
                    break;
                case Constants.AVIMCMD_HOST_BACK:
                    Log.i(TAG, "handleCustomTextMsg: AVIMCMD_HOST_BACK");
                    break;
                case Constants.AVIMCMD_LIVE_END:
                    Log.i(TAG, "handleCustomTextMsg: AVIMCMD_LIVE_END");
                    mIMChatView.handleExitLiveMsg(new SimpleUserInfo(userId, nickname, headPic));
                    break;
                case Constants.AVIMCMD_GIFT:
                    GiftWithUerInfo giftWithUerInfo = mGson.fromJson(jsonObject.getString("params"), GiftWithUerInfo.class);
                    mIMChatView.handleGift(giftWithUerInfo);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
