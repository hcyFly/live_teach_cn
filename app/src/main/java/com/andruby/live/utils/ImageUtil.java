package com.andruby.live.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.andruby.live.utils.GlideCircleTransform;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;

/**
 * Created by Andruby on 2017/5/6.
 */

public class ImageUtil {
    /**
     * 圆角显示图片
     *
     * @param context  一般为activtiy
     * @param view     图片显示类
     * @param url      图片url
     * @param defResId 默认图 id
     */
    public static void showRoundImage(Context context, ImageView view, String url, int defResId) {
        if (context == null || view == null) {
            return;
        }
        try {
            if (TextUtils.isEmpty(url)) {
                view.setImageResource(defResId);
            } else {
                Log.i("tag", "showPicWithUrl: " + url);
                if (url.startsWith("http")) {
                    Glide.with(context).load(url).placeholder(defResId).transform(new GlideCircleTransform(context)).into(view);
                } else {
                    Glide.with(context).load(new File(url)).placeholder(defResId).transform(new GlideCircleTransform(context)).into(view);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取网络类型
     */
    public static void blurBgPic(final Context context, final ImageView view, final String url, int defResId) {
        if (context == null || view == null) {
            return;
        }

        if (TextUtils.isEmpty(url)) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), defResId);
            view.setImageBitmap(blurBitmap(bitmap, context.getApplicationContext()));
        } else {
            Glide.with(context.getApplicationContext())
                    .load(url)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            if (resource == null) {
                                return;
                            }

                            final Bitmap bitmap = blurBitmap(resource, context.getApplicationContext());
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    view.setImageBitmap(bitmap);
                                }
                            });
                        }
                    });
        }
    }

    private static Bitmap blurBitmap(Bitmap resource, Context context) {
        Bitmap bitmap = Bitmap.createBitmap(resource.getWidth(), resource.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
        PorterDuffColorFilter filter =
                new PorterDuffColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
        paint.setColorFilter(filter);
        canvas.drawBitmap(resource, 0, 0, paint);

        RenderScript rs = RenderScript.create(context.getApplicationContext());
        Allocation input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        blur.setInput(input);
        blur.setRadius(10);
        blur.forEach(output);
        output.copyTo(bitmap);
        rs.destroy();

        return bitmap;
    }

    // 根据原图绘制圆形图片
    public static Bitmap createCircleImage(Bitmap source, int min) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (0 == min) {
            min = source.getHeight() > source.getWidth() ? source.getWidth() : source.getHeight();
        }
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        // 创建画布
        Canvas canvas = new Canvas(target);
        // 绘圆
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        // 设置交叉模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // 绘制图片
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }
}
