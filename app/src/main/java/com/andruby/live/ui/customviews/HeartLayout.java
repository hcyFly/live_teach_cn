/*
 * Copyright (C) 2015 tyrantgit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.andruby.live.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.andruby.live.R;
import com.andruby.live.anim.AbstractPathAnimator;
import com.andruby.live.anim.PathAnimator;

import java.util.Random;

/**
 * @description: * 飘心动画界面布局类
 *                  通过动画控制每个心形界面的显示
 *                  PathAnimator 控制显示路径
 *                  HeartView 单个心形界面
 * @author: Andruby
 * @time: 2016/12/17 10:23
 */
public class HeartLayout extends RelativeLayout {

    private AbstractPathAnimator mAnimator;
    private int defStyleAttr = 0;

    private int textHight;
    private int dHeight;
    private int dWidth;
    private int initX;
    private int pointx;

    public HeartLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        findViewById(context);
        initHeartDrawable();
        init(attrs, defStyleAttr);
    }

    private void findViewById(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_periscope, this);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_like_png);
        dHeight = bitmap.getWidth();
        dWidth = bitmap.getHeight();
        textHight = sp2px(getContext(), 20) + dHeight / 2;

        pointx = dWidth;//随机上浮方向的x坐标

        bitmap.recycle();
    }

    private static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    private void init(AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.HeartLayout, defStyleAttr, 0);

        //todo:获取确切值
        initX = 30;
        if (pointx <= initX && pointx >= 0) {
            pointx -= 10;
        } else if (pointx >= -initX && pointx <= 0) {
            pointx += 10;
        } else pointx = initX;

        mAnimator = new PathAnimator(
                AbstractPathAnimator.Config.fromTypeArray(a, initX, textHight, pointx, dWidth, dHeight));
        a.recycle();
    }

    public void clearAnimation() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).clearAnimation();
        }
        removeAllViews();
    }
    public void resourceLoad(){
        mHearts = new Bitmap[drawableIds.length];
        mHeartsDrawable = new BitmapDrawable[drawableIds.length];
        for (int i =0;i < drawableIds.length;i++){
            mHearts[i] = BitmapFactory.decodeResource(getResources(), drawableIds[i]);
            mHeartsDrawable[i] = new BitmapDrawable(getResources(), mHearts[i]);
        }
    }
    private static int[] drawableIds = new int[]{R.drawable.praise_heart_1, R.drawable.praise_heart_2, R.drawable.praise_heart_3, R.drawable.praise_heart_4, R.drawable.praise_heart_5, R.drawable.praise_heart_6, R.drawable.praise_heart_7, R.drawable.praise_heart_8, R.drawable.praise_heart_9,};
    private Random mRandom = new Random();
    private static Drawable[] sDrawables;
    private Bitmap[] mHearts;
    private BitmapDrawable[] mHeartsDrawable;
    private void initHeartDrawable() {
        int size = drawableIds.length;
        sDrawables = new Drawable[size];
        for (int i = 0 ; i < size; i++) {
            sDrawables[i] = getResources().getDrawable(drawableIds[i]);
        }
        resourceLoad();
    }

    public void addFavor() {
        HeartView heartView = new HeartView(getContext());
        heartView.setDrawable(mHeartsDrawable[mRandom.nextInt(8)]);
//        heartView.setImageDrawable(sDrawables[random.nextInt(8)]);
//        init(attrs, defStyleAttr);
        mAnimator.start(heartView, this);
    }

}
