package com.andruby.live.presenter.ipresenter;

import android.widget.Toast;

import com.andruby.live.base.BasePresenter;
import com.andruby.live.base.BaseView;
import com.andruby.live.model.GiftInfo;
import com.andruby.live.model.GiftWithUerInfo;
import com.andruby.live.utils.ToastUtils;
import com.tencent.imcore.MemberInfo;

import java.util.ArrayList;

/**
 * author : qubian on 2016/12/26 11:08
 * description :
 */

public abstract class ILiveGiftPresenter implements BasePresenter {
    protected ILiveGiftView mBaseView;

    public ILiveGiftPresenter(ILiveGiftView baseView) {
        mBaseView = baseView;
    }


    /**
     * 礼物列表
     *
     * @param userId
     * @param liveId
     */
    public abstract void giftList(String userId, String liveId);

    /**
     * 发送礼物
     *
     * @param sendGiftInfo
     * @param hostId
     * @param liveId
     */
    public abstract void sendGift(GiftInfo sendGiftInfo, String hostId, String liveId);

    /**
     * 获取剩余金币
     *
     * @param userId
     */
    public abstract void coinCount(String userId);

    public interface ILiveGiftView extends BaseView {

        void receiveGift(boolean showGift, GiftWithUerInfo giftWithUerInfo);
        void sendGiftFailed();

        void gotoPay();

        void showSenderInfoCard(MemberInfo currentMember);

        void onCoinCount(int coinCount);

        void onGiftList(ArrayList<GiftInfo> giftList);

        void onGiftListFailed();

    }
}
