package com.andruby.live.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.andruby.live.R;
import com.andruby.live.base.BaseAdapter;
import com.andruby.live.model.UserInfo;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * @Description: 达人列表的Adapter
 * @author: Andruby
 * @date: 2016年7月9日
 */
public class UserListAdapter extends BaseAdapter<UserInfo> {

    public UserListAdapter(Context context, List<UserInfo> list) {
        super(context, list);
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.item_userinfo;
    }

    @Override
    protected void initData(BaseAdapter.ViewHolder viewHolder, UserInfo data, int position) {
        ImageView ivHead = viewHolder.getView(R.id.iv_head);
        TextView tvNickname = viewHolder.getView(R.id.tv_nickname);
        Glide.with(getContext())
                .load(data.headPic)
                .placeholder(R.drawable.live_login_bg)
                .into(ivHead);
        tvNickname.setText(data.nickname);
    }


}
