package com.andruby.live.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.andruby.live.R;
import com.andruby.live.base.BaseAdapter;
import com.andruby.live.model.GiftInfo;
import com.andruby.live.ui.gift.LiveGiftItemView;

import java.util.ArrayList;


/**
 * @Description: 礼物适配器
 * GridView adapter
 * @author: Andruby
 * @date: 2016年7月15日
 */
public class LiveGiftAdapter extends BaseAdapter<GiftInfo> {
    private String TAG = "LiveGiftAdapter";
    private ArrayList<LiveGiftItemView> mFreeGiftView = new ArrayList<>();
    public static final String FREE_GIFT_SEND = "FREE_GIFT_SEND";

    private GiftPagerAdapter.GiftItemClickListener mOnItemGiftClickListener;

    public LiveGiftAdapter(Context context, ArrayList<GiftInfo> dataList, GiftPagerAdapter.GiftItemClickListener itemGiftClickListener) {
        super(context, dataList);
        mOnItemGiftClickListener = itemGiftClickListener;
    }

    public interface GiftViewClickListener {
        void onGiftViewClick(GiftInfo giftInfo);
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.live_gift_item;
    }

    @Override
    protected void initData(ViewHolder viewHolder, GiftInfo data, int position) {
        LiveGiftItemView liveGiftItemView = viewHolder.getView(R.id.live_gift_item);
        liveGiftItemView.setGiftViewClickListener(new GiftViewClickListener() {
            @Override
            public void onGiftViewClick(GiftInfo giftInfo) {
                //礼物item的逻辑 免费礼物点击数量

                mOnItemGiftClickListener.onItemGiftClick(giftInfo);
            }
        });
        if (position >= getCount()) {
            liveGiftItemView.setData(null);
        } else {
            GiftInfo giftInfo = getItem(position);
            liveGiftItemView.setData(giftInfo);
            if (giftInfo.getGiftPrice() == 0) {
                mFreeGiftView.add(liveGiftItemView);
            }
        }
    }
}
