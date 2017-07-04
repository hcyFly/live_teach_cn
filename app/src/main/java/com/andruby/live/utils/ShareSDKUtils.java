package com.andruby.live.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import com.andruby.live.R;
import com.andruby.live.model.ShareData;

import java.io.File;
import java.io.FileOutputStream;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * author : Andruby on 2017/5/4 11:09
 * description :
 */

public class ShareSDKUtils {

    public static final String DETAULT_SHARE_FILE = "default_share.jpg";

    public static void init(Context application){
        ShareSDK.initSDK(application);
        initDefaultShareFile(application);
    }

    /**
     * 存储一张默认分享图片
     * @param context
     */
    private static void initDefaultShareFile(Context context) {
        Drawable drawable = context.getResources().getDrawable(R.mipmap.ic_launcher);
        Bitmap bitmap = drawableToBitmap(drawable);
        File file = new File(context.getExternalCacheDir(), DETAULT_SHARE_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream);
                fileOutputStream.close();
                bitmap.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void showShare(Context context, final ShareData data) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle(data.getShareTitle());
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl(data.getShareUrl());
        // text是分享文本，所有平台都需要这个字段
        oks.setText(data.getShareDesc());
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl(data.getShareImageUrl());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(data.getLocalImage());//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(data.getShareUrl());
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(data.getShareTitle());
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(data.getShareUrl());
        oks.show(context);
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
