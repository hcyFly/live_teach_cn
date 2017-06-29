package com.andruby.live.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.andruby.live.R;
import com.andruby.live.http.AsyncHttp;
import com.andruby.live.http.request.DoFollowRequest;
import com.andruby.live.http.request.RequestComm;
import com.andruby.live.http.response.Response;
import com.andruby.live.model.SimpleUserInfo;
import com.andruby.live.model.UserInfoCache;
import com.andruby.live.utils.DeviceUtils;
import com.andruby.live.utils.ImageUtil;
import com.andruby.live.utils.ToastUtils;

/**
 * author : Andruby on 2017/5/7 11:09
 * description :
 */

public class UserInfoDialog extends Dialog {


    public UserInfoDialog(final Context context, final SimpleUserInfo userInfo) {
        super(context, R.style.live_dialog_style);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutView = inflater.inflate(R.layout.userinfo_dialog, null);
        FrameLayout.LayoutParams paramsF = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        paramsF.setMargins(DeviceUtils.dip2px(context, 20), 0, DeviceUtils.dip2px(context, 20), 0);
        setContentView(layoutView, paramsF);
        TextView nicknameTv = (TextView) layoutView.findViewById(R.id.tv_member_nickname);
        nicknameTv.setText(userInfo.nickname);
        ImageView headIv = (ImageView) layoutView.findViewById(R.id.iv_head_icon);
        ImageUtil.showRoundImage(context, headIv, userInfo.headPic, R.drawable.default_head);
        layoutView.findViewById(R.id.dialog_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //不能关注自己
                if (UserInfoCache.getUserId(context).equals(userInfo.userId)) {
                    ToastUtils.showShort(context, "不能关注自己");
                }
                //发送关注请求
                DoFollowRequest followRequest = new DoFollowRequest(RequestComm.do_follow, UserInfoCache.getUserId(context), userInfo.userId);
                AsyncHttp.instance().postJson(followRequest, new AsyncHttp.IHttpListener() {
                    @Override
                    public void onStart(int requestId) {

                    }

                    @Override
                    public void onSuccess(int requestId, Response response) {
                        if (response != null && response.status == RequestComm.SUCCESS) {
                            ToastUtils.showShort(context, "关注成功");
                        } else {
                            ToastUtils.showShort(context, "关注失败");
                        }
                        dismiss();
                    }

                    @Override
                    public void onFailure(int requestId, int httpStatus, Throwable error) {
                        ToastUtils.showShort(context, "关注失败");
                        dismiss();
                    }
                });
            }
        });
    }

}
