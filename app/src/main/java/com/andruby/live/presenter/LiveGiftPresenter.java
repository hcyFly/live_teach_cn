package com.andruby.live.presenter;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.andruby.live.R;
import com.andruby.live.http.AsyncHttp;
import com.andruby.live.http.request.CoinCountReuqest;
import com.andruby.live.http.request.GiftListReuqest;
import com.andruby.live.http.request.RequestComm;
import com.andruby.live.http.request.SendGiftReuqest;
import com.andruby.live.http.response.ResList;
import com.andruby.live.http.response.Response;
import com.andruby.live.model.CoinCount;
import com.andruby.live.model.GiftInfo;
import com.andruby.live.model.GiftWithUerInfo;
import com.andruby.live.model.LiveUserInfo;
import com.andruby.live.presenter.ipresenter.ILiveGiftPresenter;
import com.andruby.live.ui.gift.CustomDialog;
import com.andruby.live.utils.AsimpleCache.ACache;
import com.andruby.live.utils.ToastUtils;

import java.util.ArrayList;

/**
 * @description: 礼物管理
 * @author: Andruby
 * @time: 2016/12/18 14:04
 */
public class LiveGiftPresenter extends ILiveGiftPresenter {
    private static final String TAG = "LiveGiftPresenter";
    public static final int GO_TO_PAY = 1;
    private CustomDialog mGotoPayDialog;
    private Handler mHandler = new Handler();
    private ILiveGiftView mLiveGiftView;

    public LiveGiftPresenter(ILiveGiftView baseView) {
        super(baseView);
        mLiveGiftView = baseView;
    }

    @Override
    public void giftList(String userId, String liveId) {
        mLiveGiftView.onCoinCount(10000);
        GiftListReuqest req = new GiftListReuqest(RequestComm.gift_list, userId, liveId);
        AsyncHttp.instance().postJson(req, new AsyncHttp.IHttpListener() {
            @Override
            public void onStart(int requestId) {

            }

            @Override
            public void onSuccess(int requestId, Response response) {
                if (response.status == RequestComm.SUCCESS) {
                    ResList<GiftInfo> resList = (ResList<GiftInfo>) response.data;
                    if (resList != null) {
                        ArrayList<GiftInfo> result = (ArrayList<GiftInfo>) resList.items;
                        mLiveGiftView.onGiftList(result);
                    }
                }
            }

            @Override
            public void onFailure(int requestId, int httpStatus, Throwable error) {

            }
        });
    }

    @Override
    public void sendGift(final GiftInfo sendGiftInfo, String hostId, String liveId) {
        SendGiftReuqest req = new SendGiftReuqest(RequestComm.gift_send, hostId, liveId, sendGiftInfo.getGiftId(), sendGiftInfo.getGiftCount());
        AsyncHttp.instance().postJson(req, new AsyncHttp.IHttpListener() {
            @Override
            public void onStart(int requestId) {

            }

            @Override
            public void onSuccess(int requestId, Response response) {
                if (response.status == RequestComm.SUCCESS) {
                    GiftWithUerInfo giftWithUerInfo = new GiftWithUerInfo();
                    giftWithUerInfo.setGiftInfo(sendGiftInfo);
                    giftWithUerInfo.setUserInfo(new LiveUserInfo(ACache.get(mBaseView.getContext()).getAsString("user_id"),
                            ACache.get(mBaseView.getContext()).getAsString("nickname"),
                            ACache.get(mBaseView.getContext()).getAsString("head_pic")));

                    mLiveGiftView.receiveGift(true, giftWithUerInfo);
                }
            }

            @Override
            public void onFailure(int requestId, int httpStatus, Throwable error) {
                mLiveGiftView.sendGiftFailed();
            }
        });
    }

    @Override
    public void coinCount(String userId) {
        CoinCountReuqest req = new CoinCountReuqest(RequestComm.coin_count, userId);
        AsyncHttp.instance().postJson(req, new AsyncHttp.IHttpListener() {
            @Override
            public void onStart(int requestId) {

            }

            @Override
            public void onSuccess(int requestId, Response response) {
                if (response.status == RequestComm.SUCCESS) {
                    CoinCount coinCount = (CoinCount) response.data;
                    if (coinCount != null) {
                        mBaseView.onCoinCount(coinCount.coinCount);
                    }
                }
            }

            @Override
            public void onFailure(int requestId, int httpStatus, Throwable error) {
                mBaseView.onCoinCount(-1);
            }
        });
    }

    private void showToast(final int stringId) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showShort(mLiveGiftView.getContext(), mLiveGiftView.getContext().getString(stringId));
            }
        });
    }

    /**
     * 去充值的dialog
     */
    public void showPayDialog() {
        if (mGotoPayDialog == null) {
            mGotoPayDialog = new CustomDialog(mLiveGiftView.getContext(), R.layout.live_dialog);
            Button btnCancel = (Button) mGotoPayDialog.findViewById(R.id.dialog_cancel);
            Button btnConfirm = (Button) mGotoPayDialog.findViewById(R.id.dialog_confirm);
            TextView title = (TextView) mGotoPayDialog.findViewById(R.id.dialog_message_info);
            title.setText(mLiveGiftView.getContext().getResources().getString(R.string.live_syb_gottopay_dialog));
            mGotoPayDialog.setCanceledOnTouchOutside(false);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mGotoPayDialog.dismiss();
                }
            });

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mGotoPayDialog.dismiss();
                    mLiveGiftView.gotoPay();
                }
            });
        }
        mGotoPayDialog.show();
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }
}
