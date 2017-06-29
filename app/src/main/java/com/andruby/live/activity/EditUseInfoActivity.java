package com.andruby.live.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.andruby.live.R;
import com.andruby.live.model.UserInfo;
import com.andruby.live.model.UserInfoCache;
import com.andruby.live.presenter.EditUseInfoPresenter;
import com.andruby.live.presenter.ipresenter.IEditUseInfoPresenter;
import com.andruby.live.ui.customviews.ActivityTitle;
import com.andruby.live.ui.customviews.LineControllerView;
import com.andruby.live.ui.customviews.LineEditTextView;
import com.andruby.live.utils.AsimpleCache.ACache;
import com.andruby.live.utils.Constants;
import com.andruby.live.utils.DialogUtil;
import com.andruby.live.utils.ImageUtil;
import com.andruby.live.utils.OtherUtils;
import com.andruby.live.utils.ToastUtils;

import java.io.File;

/**
 * @Description: 编辑用户信息
 * @author: Andruby
 * @date: 2016年7月7日 下午4:46:44
 */
public class EditUseInfoActivity extends IMBaseActivity implements View.OnClickListener,
        IEditUseInfoPresenter.IEditUseInfoView {
    private String TAG = getClass().getName();

    private ImageView ivHead;
    private ActivityTitle atTitle;
    private LineEditTextView letvNickName;
    private LineControllerView lcvSelectSex;
    private boolean checkPermission = false;
    private Uri iconUrl, iconCrop;
    private Dialog pickDialog;
    private AlertDialog sexDialog;
    private EditUseInfoPresenter mEditUseInfoPresenter;

    //用户信息
    private boolean mModified = false;
    private String mHeadPic = null;
    private String mSex = null;

    public static void invoke(Activity activity) {
        Intent intent = new Intent(activity, EditUseInfoActivity.class);
        activity.startActivityForResult(intent, 1000);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_user_info;
    }

    @Override
    protected void initView() {
        atTitle = obtainView(R.id.at_eui_edit);
        ivHead = obtainView(R.id.iv_head);
        letvNickName = obtainView(R.id.letv_nickname);
        lcvSelectSex = obtainView(R.id.lcv_sex);
    }

    @Override
    protected void initData() {
        mEditUseInfoPresenter = new EditUseInfoPresenter(this);
        checkPermission = mEditUseInfoPresenter.checkPublishPermission(getActivity());

        letvNickName.setContent(UserInfoCache.getNickname(this));
        lcvSelectSex.setContent(OtherUtils.genderToString(UserInfoCache.getSex(this)));
        ImageUtil.showRoundImage(this, ivHead, UserInfoCache.getHeadPic(this), R.drawable.default_head);
        mHeadPic = UserInfoCache.getHeadPic(this);
        mSex = UserInfoCache.getSex(this);
    }

    @Override
    protected void setListener() {
        atTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_head:
                //相册、拍照
                showPhotoDialog();
                break;
            case R.id.letv_nickname:
                break;
            case R.id.lcv_sex:
                showSelectSexDialog();
                break;
            case R.id.chos_camera:
                pickDialog.dismiss();
                iconUrl = mEditUseInfoPresenter.getPicFrom(checkPermission, EditUseInfoPresenter.CAPTURE_IMAGE_CAMERA);
                break;
            case R.id.pic_lib:
                iconUrl = mEditUseInfoPresenter.getPicFrom(checkPermission, EditUseInfoPresenter.IMAGE_STORE);
                pickDialog.dismiss();
                break;
            case R.id.dialog_btn_cancel:
                pickDialog.dismiss();
                break;
            case R.id.btn_male:
                mModified = true;
                mSex = "1";
                lcvSelectSex.setContent(OtherUtils.genderToString(mSex));
                sexDialog.dismiss();
                break;
            case R.id.btn_female:
                mModified = true;
                mSex = "0";
                lcvSelectSex.setContent(OtherUtils.genderToString(mSex));
                sexDialog.dismiss();
                break;
            default:
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (letvNickName.getContent().length() == 0) {
            showMsg("昵称不能为空");
            return;
        }
        if (!letvNickName.getContent().equals(UserInfoCache.getNickname(getContext()))) {
            mModified = true;
        }
        if (mModified) {
            DialogUtil.showComfirmDialog(getContext(), getString(R.string.userinfo_modified_msg), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //上传用户信息
                    mEditUseInfoPresenter.updateUserInfo(UserInfoCache.getUserId(getContext()), letvNickName.getContent(), mSex, mHeadPic);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.WRITE_PERMISSION_REQ_CODE:
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                checkPermission = true;
                break;
            default:
                break;
        }
    }

    /**
     * 选择性别对话框
     */
    private void showSelectSexDialog() {
        sexDialog = new AlertDialog.Builder(this).create();
        View viewSex = getLayoutInflater().inflate(R.layout.view_select_sex, null);
        sexDialog.setView(viewSex, 0, 0, 0, 0);
        int width = getWindowManager().getDefaultDisplay().getWidth();
        WindowManager.LayoutParams params = sexDialog.getWindow().getAttributes();
        params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        sexDialog.getWindow().setAttributes(params);
        viewSex.findViewById(R.id.btn_male).setOnClickListener(this);
        viewSex.findViewById(R.id.btn_female).setOnClickListener(this);
        sexDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Log.e(TAG, "onActivityResult->failed for request: " + requestCode + "/" + resultCode);
            return;
        }
        switch (requestCode) {
            case EditUseInfoPresenter.CAPTURE_IMAGE_CAMERA:
                iconCrop = mEditUseInfoPresenter.startPhotoZoom(iconUrl);
                break;
            case EditUseInfoPresenter.IMAGE_STORE:
                String path = OtherUtils.getPath(this, data.getData());
                if (null != path) {
                    Log.d(TAG, "cropImage->path:" + path);
                    File file = new File(path);
                    iconCrop = mEditUseInfoPresenter.startPhotoZoom(Uri.fromFile(file));
                }
                break;
            case EditUseInfoPresenter.CROP_CHOOSE:
                mHeadPic = iconCrop.getPath();
                mModified = true;
                ImageUtil.showRoundImage(this, ivHead, mHeadPic, R.color.transparent);
                break;
        }
    }

    /**
     * 图片选择对话框
     */
    private void showPhotoDialog() {
        pickDialog = new Dialog(this, R.style.floag_dialog);
        pickDialog.setContentView(R.layout.dialog_pic_choose);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window dlgwin = pickDialog.getWindow();
        WindowManager.LayoutParams lp = dlgwin.getAttributes();
        dlgwin.setGravity(Gravity.BOTTOM);
        lp.width = (int) (display.getWidth()); //设置宽度
        pickDialog.getWindow().setAttributes(lp);
        pickDialog.findViewById(R.id.chos_camera).setOnClickListener(this);
        pickDialog.findViewById(R.id.pic_lib).setOnClickListener(this);
        pickDialog.findViewById(R.id.dialog_btn_cancel).setOnClickListener(this);
        pickDialog.show();
    }

    @Override
    public void onReceiveExitMsg() {
        super.onReceiveExitMsg();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void updateUserInfoSuccess(UserInfo userInfo) {
        showMsg("更新用户信息成功");
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void updateUserInfoFailed(String msg) {
        showMsg("更新用户信息失败");
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showMsg(String msg) {
        ToastUtils.showShort(this, msg);
    }

    @Override
    public void showMsg(int msg) {
        ToastUtils.showShort(this, getString(msg));
    }

    @Override
    public Context getContext() {
        return this;
    }

}
