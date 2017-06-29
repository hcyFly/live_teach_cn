package com.andruby.live.ui.gift;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andruby.live.R;
import com.andruby.live.adapter.LiveGiftAdapter;
import com.andruby.live.model.GiftInfo;
import com.bumptech.glide.Glide;

/**
 * @description:  礼物的ItemView
 * @author: Andruby
 * @time: 2016/12/17 10:23
 */
public class LiveGiftItemView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = LiveGiftItemView.class.getSimpleName();
    private Context mContext;
    private ImageView mGiftImage;
    private TextView mGiftName;
    private TextView mGiftPrice;
    private GiftInfo mGiftInfo;

    private LiveGiftAdapter.GiftViewClickListener mOnGiftViewClickListener;

    public LiveGiftItemView(Context context) {
        this(context, null);
    }

    public LiveGiftItemView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LiveGiftItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.live_gift_item_view, this);
        initView();
    }

    public void setGiftViewClickListener(LiveGiftAdapter.GiftViewClickListener
                                                   onGiftViewClickListener) {
        mOnGiftViewClickListener = onGiftViewClickListener;
    }

    private void initView() {
        mGiftImage = (ImageView) findViewById(R.id.item_gift_icon);
        mGiftName = (TextView) findViewById(R.id.tv_item_gift_name);
        mGiftPrice = (TextView) findViewById(R.id.tv_item_gift_cost);
    }

    public void setData(GiftInfo data) {
        mGiftInfo = data;
        if (data != null) {
            setViewsVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(data.getImageUrl())
                    .placeholder(R.drawable.live_head_placeholder)
                    .centerCrop()
                    .into(mGiftImage);
            mGiftName.setText(data.getGiftName());
            if (data.getGiftPrice() == 0) {
                mGiftPrice.setText(mContext.getString(R.string.gift_market_no_syb));
            } else {
                mGiftPrice.setText(((int) data.getGiftPrice()) + mContext.getString(R.string.gift_market_text_syb));
            }
            mGiftImage.setOnClickListener(this);
        } else {
            setViewsVisibility(View.GONE);
        }
    }

    public GiftInfo getGiftInfo() {
        return mGiftInfo;
    }

    public void setProgress(int CurrentTime) {
        mGiftInfo.setCurrentTime(CurrentTime);
    }

    private void setViewsVisibility(int visible) {
        mGiftImage.setVisibility(visible);
        mGiftName.setVisibility(visible);
        mGiftPrice.setVisibility(visible);
    }

    @Override
    public void onClick(View v) {
        if (mGiftInfo.getGiftPrice() == 0) {
            int currentCount = mGiftInfo.getCurrentCount();
            currentCount--;
            if (mOnGiftViewClickListener != null && currentCount > -1) {
                mGiftInfo.setCurrentCount(currentCount);
                mOnGiftViewClickListener.onGiftViewClick(mGiftInfo);
            }
        } else {
            mOnGiftViewClickListener.onGiftViewClick(mGiftInfo);
        }
    }
}
