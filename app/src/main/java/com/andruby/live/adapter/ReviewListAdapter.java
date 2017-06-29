package com.andruby.live.adapter;

import android.content.Context;
import android.widget.TextView;

import com.andruby.live.R;
import com.andruby.live.base.BaseAdapter;
import com.andruby.live.model.ReviewInfo;
import com.andruby.live.utils.TimeUtils;

import java.util.List;

/**
 * @Description: 达人列表的Adapter
 * @author: Andruby
 * @date: 2016年7月9日
 */
public class ReviewListAdapter extends BaseAdapter<ReviewInfo> {

    public ReviewListAdapter(Context context, List<ReviewInfo> dataList) {
        super(context, dataList);
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.item_review;
    }

    @Override
    protected void initData(ViewHolder viewHolder, ReviewInfo data, int position) {
        TextView live_title = viewHolder.getView(R.id.live_title);
        TextView live_viewer = viewHolder.getView(R.id.live_viewer);
        TextView live_time = viewHolder.getView(R.id.live_time);
        live_title.setText(data.getTitle());
        live_viewer.setText(data.getViewCount());
        live_time.setText(TimeUtils.formattedTime(Long.parseLong(data.getDuration())));
    }



}
