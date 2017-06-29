package com.andruby.live.presenter;

import android.Manifest;
import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.andruby.live.LiveApp;
import com.andruby.live.R;
import com.andruby.live.http.AsyncHttp;
import com.andruby.live.http.request.EditUserInfoRequest;
import com.andruby.live.http.request.RequestComm;
import com.andruby.live.http.response.Response;
import com.andruby.live.logic.UserInfoMgr;
import com.andruby.live.model.UserInfo;
import com.andruby.live.model.UserInfoCache;
import com.andruby.live.presenter.ipresenter.IEditUseInfoPresenter;
import com.andruby.live.utils.Constants;
import com.andruby.live.utils.OtherUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 编辑用户信息管理
 * @author: Andruby
 * @time: 2016/12/18 14:04
 */
public class EditUseInfoPresenter extends IEditUseInfoPresenter {
    private static final String TAG = EditUseInfoPresenter.class.getName();
    private IEditUseInfoView mEditUseInfoView;
    public static final int CROP_CHOOSE = 10;
    public static final int CAPTURE_IMAGE_CAMERA = 100;
    public static final int IMAGE_STORE = 200;

    public EditUseInfoPresenter(IEditUseInfoView baseView) {
        super(baseView);
        mEditUseInfoView = baseView;
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }


    /**
     * 检查裁剪图像相关的权限
     *
     * @return 权限不足返回false，否则返回true
     */
    @Override
    public boolean checkPublishPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(mEditUseInfoView.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(mEditUseInfoView.getContext(), Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(activity,
                        permissions.toArray(new String[0]),
                        Constants.WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }

        return true;
    }

    @Override
    public Uri startPhotoZoom(Uri uri) {
        Uri iconCrop = createCoverUri("_icon_crop");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 200);
        intent.putExtra("aspectY", 200);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, iconCrop);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        mEditUseInfoView.getActivity().startActivityForResult(intent, CROP_CHOOSE);
        return iconCrop;
    }

    /**
     * 创建封面图片的uri
     *
     * @param type 要创建的URI类型
     *             _icon ：通过相机拍摄图片
     *             _select_icon ： 从文件获取图片文件
     * @return 返回uri
     */
    private Uri createCoverUri(String type) {
        String filename = UserInfoCache.getUserId(LiveApp.getApplication()) + type + "_" + System.currentTimeMillis() + ".jpg";
        File outputImage = new File(OtherUtils.getRootDir(), filename);
        if (ContextCompat.checkSelfPermission(mEditUseInfoView.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mEditUseInfoView.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.WRITE_PERMISSION_REQ_CODE);
            return null;
        }
        if (outputImage.exists()) {
            outputImage.delete();
        }
        return Uri.fromFile(outputImage);
    }

    /**
     * 获取图片资源
     *
     * @param type 类型（本地IMAGE_STORE/拍照CAPTURE_IMAGE_CAMERA）
     */

    @Override
    public Uri getPicFrom(boolean mPermission, int type) {
        Uri iconUrl = null;
        if (!mPermission) {
            mEditUseInfoView.showMsg(R.string.tip_no_permission);
            return null;
        }
        switch (type) {
            case CAPTURE_IMAGE_CAMERA:
                Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                iconUrl = createCoverUri("_icon");
                intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, iconUrl);
                mEditUseInfoView.getActivity().startActivityForResult(intent_photo, CAPTURE_IMAGE_CAMERA);
                break;
            case IMAGE_STORE:
                iconUrl = createCoverUri("_select_icon");
                Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
                intent_album.setType("image/*");
                mEditUseInfoView.getActivity().startActivityForResult(intent_album, IMAGE_STORE);
                break;
        }
        return iconUrl;
    }

    @Override
    public void updateUserInfo(final String userId, String nickName, String sex, String headPic) {
        final EditUserInfoRequest request = new EditUserInfoRequest(RequestComm.edit_userinfo, userId, nickName, sex, headPic);
        AsyncHttp.instance().post(request, new AsyncHttp.IHttpListener() {
            @Override
            public void onStart(int requestId) {

            }

            @Override
            public void onSuccess(int requestId, Response response) {
                if (response != null && response.status == RequestComm.SUCCESS) {
                    //更新界面
                    //更新本地用户信息
                    //更新IM用户信息
                    UserInfo userInfo = (UserInfo) response.data;
                    if (userInfo != null) {
                        mEditUseInfoView.updateUserInfoSuccess(userInfo);
                        UserInfoCache.saveCache(mEditUseInfoView.getContext(), userInfo);
                        UserInfoMgr.getInstance().setUserInfo(userInfo);
                    }
                } else {
                    mEditUseInfoView.updateUserInfoFailed(null);
                }
            }

            @Override
            public void onFailure(int requestId, int httpStatus, Throwable error) {
                mEditUseInfoView.updateUserInfoFailed(null);
            }
        });
    }
}
