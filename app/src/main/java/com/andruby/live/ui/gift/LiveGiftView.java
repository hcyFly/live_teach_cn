package com.andruby.live.ui.gift;


import com.andruby.live.model.GiftInfo;
import com.tencent.imcore.MemberInfo;

/**
 * @description:  礼物的直播界面回调
 * @author: Andruby
 * @time: 2016/12/17 10:23
 */
public interface LiveGiftView  {

    void showSenderInfoCard(MemberInfo currentMember);

    void ShowGotoPay();

    void gotoPay();

    void sendGift(GiftInfo sendGiftInfo);

    void isGiftMarketShowing(boolean isShowing);

}
