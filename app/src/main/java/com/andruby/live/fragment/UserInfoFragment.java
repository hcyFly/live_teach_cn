package com.andruby.live.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andruby.live.R;
import com.andruby.live.activity.EditUseInfoActivity;
import com.andruby.live.activity.LoginActivity;
import com.andruby.live.logic.IMLogin;
import com.andruby.live.model.UserInfoCache;
import com.andruby.live.utils.DeviceUtils;
import com.andruby.live.utils.DialogUtil;
import com.andruby.live.utils.ImageUtil;
import com.andruby.live.utils.OtherUtils;

/**
 * @description: 用户资料展示页面
 * @author: Andruby
 * @time: 2016/9/3 16:19
 */
public class UserInfoFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "UserInfoFragment";
    private ImageView mHeadPic;
    private TextView mNickName;

    public UserInfoFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_info;
    }

    @Override
    protected void initView(View view) {
        mHeadPic = obtainView(R.id.iv_ui_head);
        mNickName = obtainView(R.id.tv_ui_nickname);
    }

    @Override
    protected void initData() {
        mNickName.setText(UserInfoCache.getNickname(getContext()));
        ImageUtil.showRoundImage(getActivity(), mHeadPic, UserInfoCache.getHeadPic(getContext()), R.drawable.default_head);
    }

    @Override
    protected void setListener(View view) {
        obtainView(R.id.lcv_ui_set).setOnClickListener(this);
        obtainView(R.id.lcv_ui_logout).setOnClickListener(this);
        obtainView(R.id.lcv_ui_version).setOnClickListener(this);
        obtainView(R.id.fanceview).setOnClickListener(this);
        obtainView(R.id.followView).setOnClickListener(this);
        obtainView(R.id.review).setOnClickListener(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void enterEditUserInfo() {
        EditUseInfoActivity.invoke(getActivity());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lcv_ui_set: //设置用户信息
                enterEditUserInfo();
                break;
            case R.id.lcv_ui_logout: //注销APP
                showLogout();
                break;
            case R.id.lcv_ui_version:
                showAbout();
                break;
            case R.id.fanceview:
                break;
            case R.id.followView:
                break;
            case R.id.review:
                break;
        }
    }

    /**
     * 退出登录
     */
    private void showLogout() {
        DialogUtil.showComfirmDialog(getContext(), "你确定要退出当前账号吗？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                IMLogin.getInstance().logout();
                LoginActivity.invoke(getContext());
                getActivity().finish();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }


    /**
     * 显示关于信息
     */
    private void showAbout() {
        DialogUtil.showMsgDialog(getActivity(), getString(R.string.my_about_info, DeviceUtils.getAppVersion(getContext())), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            mNickName.setText(UserInfoCache.getNickname(getContext()));
            ImageUtil.showRoundImage(getActivity(), mHeadPic, UserInfoCache.getHeadPic(getContext()), R.drawable.default_head);
        }
    }
}
