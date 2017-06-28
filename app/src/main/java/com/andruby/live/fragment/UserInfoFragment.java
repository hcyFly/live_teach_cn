package com.andruby.live.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andruby.live.R;
import com.andruby.live.activity.LoginActivity;
import com.andruby.live.logic.IMLogin;
import com.andruby.live.logic.IUserInfoMgrListener;
import com.andruby.live.logic.UserInfoMgr;
import com.andruby.live.utils.DeviceUtils;
import com.andruby.live.utils.OtherUtils;
import com.tencent.rtmp.TXRtmpApi;

/**
 * @description: 用户资料展示页面
 * @author: Andruby
 * @time: 2016/9/3 16:19
 */
public class UserInfoFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "UserInfoFragment";
    private ImageView mHeadPic;
    private TextView mNickName;
    private TextView mUserId;

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
        mUserId = obtainView(R.id.tv_ui_user_id);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener(View view) {
        obtainView(R.id.lcv_ui_set).setOnClickListener(this);
        obtainView(R.id.lcv_ui_logout).setOnClickListener(this);
        obtainView(R.id.lcv_ui_version).setOnClickListener(this);
        obtainView(R.id.fanceview).setOnClickListener(this);
        obtainView(R.id.followView).setOnClickListener(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        //页面展示之前，更新一下用户信息
        UserInfoMgr.getInstance().queryUserInfo(new IUserInfoMgrListener() {
            @Override
            public void OnQueryUserInfo(int error, String errorMsg) {
                if (0 == error) {
                    mNickName.setText(UserInfoMgr.getInstance().getNickname());
                    mUserId.setText("");
                    OtherUtils.showPicWithUrl(getActivity(), mHeadPic, UserInfoMgr.getInstance().getHeadPic(), R.drawable.default_head);
                }
            }

            @Override
            public void OnSetUserInfo(int error, String errorMsg) {

            }
        });
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
//        EditUseInfoActivity.invoke(getContext());
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
            case R.id.lcv_ui_version: //显示 APP SDK 的版本信息
                showSDKVersion();
                break;
            case R.id.fanceview:
//                FanceActivity.invoke(mContext);
                break;
            case R.id.followView:
//                FollowActivity.invoke(mContext);
                break;
        }
    }

    /**
     * 退出登录
     */
    private void showLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(true);
        builder.setTitle("您确定要退出？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                IMLogin.getInstance().logout();
                LoginActivity.invoke(mContext);
                getActivity().finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /**
     * 显示 APP SDK 的版本信息
     */
    private void showSDKVersion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        int[] sdkver = TXRtmpApi.getSDKVersion();
        builder.setMessage(getString(R.string.app_name) + DeviceUtils.getAppVersion(mContext) + "\r\n"
        );
        builder.show();
    }

}
