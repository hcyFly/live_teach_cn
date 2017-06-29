package com.andruby.live.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.andruby.live.R;
import com.andruby.live.model.GiftInfo;

import java.util.ArrayList;

/**
 * @Description:  礼物列表适配器
 * @author: Andruby
 * @date: 2016年7月9日 下午5:46:44
 */
public class GiftPagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<ArrayList<GiftInfo>> mGiftPagerList;
    private GiftItemClickListener mGiftItemClickListener;

    public GiftPagerAdapter(Context context) {
        this.mContext = context;
    }


    public interface GiftItemClickListener {
        void onItemGiftClick(GiftInfo giftInfo);
    }

    public void setOnItemGiftClickListener(GiftItemClickListener onItemGiftClickListener) {
        mGiftItemClickListener = onItemGiftClickListener;
    }

    public void setGiftPagerList(ArrayList<ArrayList<GiftInfo>> giftPagerList) {
        mGiftPagerList = giftPagerList;
    }

    @Override
    public int getCount() {
        return mGiftPagerList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View bannerItem = LayoutInflater.from(mContext).inflate(R.layout.gift_pager_item_layout, null);

        GridView gvGift = (GridView) bannerItem.findViewById(R.id.gv_live_gift);
        final ArrayList<GiftInfo> giftInfos = mGiftPagerList.get(position);
        LiveGiftAdapter liveGiftMarketAdapter = new LiveGiftAdapter(mContext,giftInfos, mGiftItemClickListener);
        gvGift.setAdapter(liveGiftMarketAdapter);

        container.addView(bannerItem, 0, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return bannerItem;
    }
}
