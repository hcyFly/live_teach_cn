package com.andruby.live.presenter.ipresenter;

import android.app.Activity;
import android.net.Uri;

import com.andruby.live.base.BasePresenter;
import com.andruby.live.base.BaseView;
import com.andruby.live.model.UserInfo;


/**
 * @description: 用户信息管理
 * @author: Andruby
 * @time: 2016/12/15 11:54
 */
public abstract class IEditUseInfoPresenter implements BasePresenter {

    protected BaseView mBaseView;

    public IEditUseInfoPresenter(BaseView baseView) {
        mBaseView = baseView;
    }
    //头像上传逻辑,从相册、拍照，同发起直播选择封面
    //昵称
    //性别
    //信息上传

    /**
     * 检查裁剪图像相关的权限
     *
     * @return 权限不足返回false，否则返回true
     */
    public abstract boolean checkPublishPermission(Activity activity);

    /**
     * 截图
     *
     * @param uri
     * @return
     */
    public abstract Uri startPhotoZoom(Uri uri);

    /**
     * 获取图片位置
     *
     * @param mPermission
     * @param type
     * @return
     */
    public abstract Uri getPicFrom(boolean mPermission, int type);



    /**
     * 修改用户信息
     *
     * @param userId
     * @param nickName
     * @param sex
     * @param headPic
     */
    public abstract void updateUserInfo(String userId, String nickName, String sex, String headPic);

    public interface IEditUseInfoView extends BaseView {

        Activity getActivity();

        /**
         * 更新用户信息成功
         *
         * @param userInfo
         */
        void updateUserInfoSuccess(UserInfo userInfo);

        /**
         * 更新用户信息失败
         * @param msg
         */
        void updateUserInfoFailed(String msg);
    }
}
