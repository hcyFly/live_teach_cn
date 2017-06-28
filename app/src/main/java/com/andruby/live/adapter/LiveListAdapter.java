package com.andruby.live.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.andruby.live.R;
import com.andruby.live.utils.OtherUtils;
import com.andruby.live.model.LiveInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;


/**
 * @Description: 直播列表的Adapter
 * 列表项布局格式: R.layout.listview_video_item
 * 列表项数据格式: LiveInfo
 * @author: Andruby
 * @date: 2016年7月15日
 */
public class LiveListAdapter extends ArrayAdapter<LiveInfo> {
	private int resourceId;
	private Activity mActivity;

	private class ViewHolder {
		TextView tvTitle;
		TextView tvHost;
		TextView tvMembers;
		TextView tvAdmires;
		TextView tvLbs;
		ImageView ivCover;
		ImageView ivAvatar;
		ImageView ivLogo;
	}

	public LiveListAdapter(Activity activity, ArrayList<LiveInfo> objects) {
		super(activity, R.layout.live_item_view, objects);
		resourceId = R.layout.live_item_view;
		mActivity = activity;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);

			holder = new ViewHolder();
			holder.ivCover = (ImageView) convertView.findViewById(R.id.cover);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.live_title);
			holder.tvHost = (TextView) convertView.findViewById(R.id.host_name);
			holder.tvMembers = (TextView) convertView.findViewById(R.id.live_members);
			holder.tvAdmires = (TextView) convertView.findViewById(R.id.praises);
			holder.tvLbs = (TextView) convertView.findViewById(R.id.live_lbs);
			holder.ivAvatar = (ImageView) convertView.findViewById(R.id.avatar);
			holder.ivLogo = (ImageView) convertView.findViewById(R.id.live_logo);

			convertView.setTag(holder);
		}

		LiveInfo data = getItem(position);

		//直播封面
		String cover = data.userInfo.frontcover;
		if (TextUtils.isEmpty(cover)) {
			holder.ivCover.setImageResource(R.drawable.bg);
		} else {
			RequestManager req = Glide.with(mActivity);
			req.load(cover).placeholder(R.drawable.bg).into(holder.ivCover);
		}

		//主播头像
		OtherUtils.showPicWithUrl(mActivity, holder.ivAvatar, data.userInfo.headPic, R.drawable.default_head);
		//主播昵称
		if (TextUtils.isEmpty(data.userInfo.nickname)) {
			holder.tvHost.setText("@" + OtherUtils.getLimitString(data.userId, 10));
		} else {
			holder.tvHost.setText("@" + OtherUtils.getLimitString(data.userInfo.nickname, 10));
		}
		//主播地址
		if (TextUtils.isEmpty(data.userInfo.location)) {
			holder.tvLbs.setText(getContext().getString(R.string.live_unknown));
		} else {
			holder.tvLbs.setText(OtherUtils.getLimitString(data.userInfo.location, 9));
		}

		//直播标题
		holder.tvTitle.setText(OtherUtils.getLimitString(data.title, 10));
		//直播观看人数
		holder.tvMembers.setText("" + data.viewCount);
		//直播点赞数
		holder.tvAdmires.setText("" + data.likeCount);
		//视频类型，直播或者回放
		if (data.type == 0) {
			holder.ivLogo.setImageResource(R.drawable.icon_live);
		} else {
			holder.ivLogo.setImageResource(R.drawable.icon_video);
		}
		return convertView;
	}

}
