/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.handmark.pulltorefresh.library.R;

@SuppressLint("ViewConstructor")
public class TweenAnimLoadingLayout extends LoadingLayout {

	private AnimationDrawable animationDrawable;
	Mode mode;
	Drawable drawable;

	public TweenAnimLoadingLayout(Context context, Mode mode,
								  Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);
		this.mode = mode;
		// 初始化
		switch (mode) {

			case PULL_FROM_START:
				mHeaderImage.setImageResource(R.drawable.loding_refresh);

				break;

			case PULL_FROM_END:
				mHeaderImage.setImageResource(R.drawable.loding_refresh2);

				break;
			default:
				mHeaderImage.setImageResource(R.drawable.loding_refresh);

				break;
		}

		animationDrawable = (AnimationDrawable) mHeaderImage.getDrawable();
	}

	// 默认图片
	@Override
	protected int getDefaultDrawableResId() {

		return R.drawable.loding_1;

	}

	@Override
	protected void onLoadingDrawableSet(Drawable imageDrawable) {
		// NO-OP
	}

	@Override
	protected void onPullImpl(float scaleOfLayout) {
		// NO-OP
	}

	// 下拉以刷新
	@Override
	protected void pullToRefreshImpl() {
		// NO-OP
		//animationDrawable.start();
	}

	// 正在刷新时回调
	@Override
	protected void refreshingImpl() {
		animationDrawable.start();

	}

	// 释放以刷新
	@Override
	protected void releaseToRefreshImpl() {
		// NO-OP
	}

	// 重新设置
	@Override
	protected void resetImpl() {
		mHeaderImage.setVisibility(View.VISIBLE);
		mHeaderImage.clearAnimation();
	}


}
