package com.andruby.live.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andruby.live.R;
import com.andruby.live.logic.LocationMgr;
import com.andruby.live.presenter.PublishSettingPresenter;
import com.andruby.live.presenter.ipresenter.IPublishSettingPresenter;
import com.andruby.live.ui.customviews.BallSpinLoadingView;
import com.andruby.live.utils.CameraUtil;
import com.andruby.live.utils.Constants;
import com.andruby.live.utils.ToastUtils;

import java.io.IOException;
import java.util.List;

/**
 * 直播发布类
 */
public class PublishLiveActivity extends BaseActivity implements View.OnClickListener,
         SurfaceHolder.Callback ,IPublishSettingPresenter.IPublishSettingView {

    private static final String TAG = PublishLiveActivity.class.getSimpleName();
    private View loadView; // 加载的视图
    private TextView mOpenLoactionTv;
    private TextView tvTitle;
    private TextView tvCamera;
    private TextView tvLocation;
    private int isOPenLocation;// 位置是否打开

    private FrameLayout mRootView;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private RelativeLayout mPreviewLayout;

    private double cameraPosition;//0后置，1前置

    public static final int OPEN_CAMERA = 1;
    private PublishSettingPresenter mPublishSettingPresenter;

    public static void invoke(Context context ) {
        Intent intent = new Intent(context, PublishLiveActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_publish;
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case OPEN_CAMERA:
                    openCamera();
                    break;
            }
            return false;
        }
    });

    protected void initView() {
        //title
        tvTitle = (TextView) findViewById(R.id.live_title);
        mRootView = (FrameLayout) findViewById(R.id.live_publish_root);
        mOpenLoactionTv = (TextView) findViewById(R.id.tv_publish_live_location);

        tvCamera = (TextView) findViewById(R.id.tv_toggle_camera);
        tvCamera.setSelected(true);
        tvCamera.setText(mContext.getString(R.string.live_publish_camera_back));
        tvCamera.setTextColor(getResources().getColor(R.color.colorTextWhiteTransparent));

        //设置背景点击收起软键盘
        findViewById(R.id.traceroute_rootview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        loadView = new BallSpinLoadingView(mContext);   //圆形进度条
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout
                .LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        loadView.setLayoutParams(layoutParams);
        mRootView.addView(loadView);
        showLoading(false);
        tvLocation = (TextView) findViewById(R.id.location);
        mPreviewLayout = (RelativeLayout) findViewById(R.id.preview_layout);
        initListener();
    }

    @Override
    protected void initData() {
        initSurfaceView();
        mPublishSettingPresenter = new PublishSettingPresenter(this);
        mPublishSettingPresenter.checkPublishPermission(this);
    }

    @Override
    protected void setListener() {

    }

    private void initSurfaceView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * 释放掉camera的引用
     */
    private void releaseCamera() {
        if (mCamera != null) {
            this.mCamera.setPreviewCallback(null);
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
        }
    }

    private void openCamera() {
        try {
            if(cameraPosition == 1) {
                int cameraCount = 0;
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
                for (int j = 0; j < cameraCount; j++) {
                    Camera.getCameraInfo(j, cameraInfo);//得到每一个摄像头的信息
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        mCamera = Camera.open(j);//打开当前选中的摄像头
                        try {
                            mCamera.setPreviewDisplay(mSurfaceHolder);//通过surfaceview显示取景画面
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else{
                mCamera = Camera.open();//打开当前选中的摄像头
                mCamera.setPreviewDisplay(mSurfaceHolder);//通过surfaceview显示取景画面
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessageDelayed(OPEN_CAMERA,1000);
        }
        if(mCamera != null){
            initCameraRatio();
            mCamera.startPreview();//开始预览
            mCamera.setDisplayOrientation(90);
        }
    }

    private void initCameraRatio() {

        Camera.Parameters cp = mCamera.getParameters();
        List<Camera.Size> sizes = cp.getSupportedPreviewSizes();
        Camera.Size size = CameraUtil.getOptimalPreviewSize(this, sizes);
        cp.setPreviewSize(size.width, size.height);
        cameraInited(size.width, size.height);
        mCamera.setParameters(cp);
    }

    public void cameraInited(int width, int height) {
        int optimalPreviewWidth = Math.max(width, height);
        int optimalPreviewHeight = Math.min(width, height);
        updatePreviewSize(optimalPreviewHeight, optimalPreviewWidth);
    }

    public void updatePreviewSize(int width, int height) {
        if (mPreviewLayout != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mPreviewLayout.getLayoutParams();
            Point point = CameraUtil.getDefaultDisplaySize(this, new Point());
            int screenHeight = Math.max(point.x, point.y);
            double ratio = (double) screenHeight / height;
            layoutParams.width = (int) ( width * ratio);
            layoutParams.height = screenHeight;
            mPreviewLayout.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {}

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(mCamera == null){
            mHandler.sendEmptyMessage(OPEN_CAMERA);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    private void showLoading(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void initListener() {
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_publish).setOnClickListener(this);
        mOpenLoactionTv.setOnClickListener(this);
        tvCamera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cancel) {
            finish();
        } else if (i == R.id.btn_publish) {
            String locationStr = tvLocation.getText().toString();

            String location =locationStr.equals(getString(R.string.text_live_lbs_fail)) ||
                    locationStr.equals(getString(R.string.text_live_location)) || TextUtils.isEmpty(locationStr)?
                    getString(R.string.text_live_close_lbs) : locationStr;
            if (!isFastDoubleClick()) {
                mPublishSettingPresenter.doPublish(tvTitle.getText().toString().trim(),
                        Constants.RECORD_TYPE_CAMERA,location,
                        Constants.BITRATE_NORMAL, false);
            }
        } else if (i == R.id.tv_publish_live_location) {
            if (mOpenLoactionTv.isSelected()) {
                mOpenLoactionTv.setSelected(false);
                isOPenLocation = 0;
                mOpenLoactionTv.setText(mContext.getString(R.string.live_publish_location_close));
                mOpenLoactionTv.setTextColor(getResources().getColor(R.color
                        .colorTextWhiteTransparent));
                tvLocation.setText("");
            } else {
                mOpenLoactionTv.setSelected(true);
                isOPenLocation = 1;
                mOpenLoactionTv.setText(mContext.getString(R.string.live_publish_location_open));
                mOpenLoactionTv.setTextColor(getResources().getColor(R.color.colorTextWhite));
                mPublishSettingPresenter.doLocation();
            }
        }else if (i == R.id.tv_toggle_camera) {
            if(mCamera == null){
                return;
            }
            Log.e("现在摄像头",cameraPosition+"");
            //切换前后摄像头
            int cameraCount = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数

            for(int j = 0; j< cameraCount; j++   ) {
                Camera.getCameraInfo(j, cameraInfo);//得到每一个摄像头的信息
                if(cameraPosition == 1) {
                    //现在是前置，变更为后置
                    if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                        mCamera.stopPreview();//停掉原来摄像头的预览
                        mCamera.release();//释放资源
                        mCamera = null;//取消原来摄像头
                        mCamera = Camera.open(j);//打开当前选中的摄像头
                        try {
                            mCamera.setPreviewDisplay(mSurfaceHolder);//通过surfaceview显示取景画面
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        initCameraRatio();
                        mCamera.setDisplayOrientation(90);
                        mCamera.startPreview();//开始预览
                        cameraPosition = 0;
                        tvCamera.setSelected(true);
                        tvCamera.setText(mContext.getString(R.string.live_publish_camera_back));
                        tvCamera.setTextColor(getResources().getColor(R.color.colorTextWhiteTransparent));
                        Log.e("摄像头",cameraPosition+"");
                        break;
                    }
                } else {
                    //现在是后置， 变更为前置
                    if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                        mCamera.stopPreview();//停掉原来摄像头的预览
                        mCamera.release();//释放资源
                        mCamera = null;//取消原来摄像头
                        mCamera = Camera.open(j);//打开当前选中的摄像头
                        try {
                            mCamera.setPreviewDisplay(mSurfaceHolder);//通过surfaceview显示取景画面
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        initCameraRatio();
                        mCamera.setDisplayOrientation(90);
                        mCamera.startPreview();//开始预览
                        cameraPosition = 1;
                        tvCamera.setSelected(false);
                        tvCamera.setText(mContext.getString(R.string.live_publish_camera_front));
                        tvCamera.setTextColor(getResources().getColor(R.color
                                .colorTextWhite));
                        Log.e("摄像头",cameraPosition+"");
                        break;
                    }
                }

            }
        }
    }


    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 2000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }



    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.LOCATION_PERMISSION_REQ_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!LocationMgr.getMyLocation(this, mPublishSettingPresenter.getLocationListener())) {
                        tvLocation.setText(getString(R.string.text_live_lbs_fail));
                    }
                }
                break;
            case Constants.WRITE_PERMISSION_REQ_CODE:
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void doLocationSuccess(String location) {
        tvLocation.setText(location);
    }

    @Override
    public void doLocationFailed() {
        tvLocation.setText("暂无定位");
    }

    @Override
    public void doUploadSuceess(String url) {

    }

    @Override
    public void doUploadFailed() {

    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void showLoading() {
        showLoading(true);
    }

    @Override
    public void dismissLoading() {
        showLoading(false);
    }

    @Override
    public void showMsg(String msg) {
        ToastUtils.showShort(mContext,msg);
    }

    @Override
    public void showMsg(int msg) {
        ToastUtils.showShort(mContext,msg);
    }

    @Override
    public Context getContext() {
        return mContext;
    }
}
