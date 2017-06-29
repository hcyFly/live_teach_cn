package com.andruby.live.presenter;

import android.content.Context;

import com.andruby.live.model.SimpleUserInfo;
import com.andruby.live.ui.dialog.UserInfoDialog;

/**
 * Created by Andruby on 2017/5/9.
 */

public abstract class ILivePresenter {
    private UserInfoDialog mUserInfoDialog;

    public void showUserInfo(Context context, SimpleUserInfo userInfo) {
        if (mUserInfoDialog == null) {
            mUserInfoDialog = new UserInfoDialog(context, userInfo);
        }
        mUserInfoDialog.show();
    }
}
