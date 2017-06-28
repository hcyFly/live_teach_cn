package com.andruby.live.activity;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andruby.live.R;
import com.andruby.live.presenter.RegisterPresenter;
import com.andruby.live.presenter.ipresenter.IRegisterPresenter;
import com.andruby.live.utils.LogUtil;
import com.andruby.live.utils.OtherUtils;
import com.andruby.live.utils.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import java.lang.ref.WeakReference;


/**
 * @Description: 注册页面
 * @author: Andruby
 * @date: 2016年7月9日
 */
public class RegisterActivity extends BaseActivity implements IRegisterPresenter.IRegisterView {

	public static final String TAG = RegisterActivity.class.getSimpleName();

	private RegisterPresenter mIRegisterPresenter;
	private String mPassword;
	//共用控件
	private RelativeLayout relativeLayout;
	private ProgressBar progressBar;
	private EditText etPassword;
	private EditText etPasswordVerify;
	private EditText etRegister;
	private TextView tvPhoneRegister;
	private TextInputLayout tilRegister, tilPassword, tilPasswordVerify;
	private Button btnRegister;
	private TextView tvBackBtn;
	//手机验证注册控件
	private TextView tvVerifyCode;
	//动画
	private AlphaAnimation fadeInAnimation, fadeOutAnimation;

	public static void invoke(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, RegisterActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_register;
	}

	@Override
	protected void initView() {
		etRegister = obtainView(R.id.et_register);
		etPassword = obtainView(R.id.et_password);
		etPasswordVerify = obtainView(R.id.et_password_verify);
		tilPasswordVerify = obtainView(R.id.til_password_verify);
		tvPhoneRegister = obtainView(R.id.tv_phone_register);
		btnRegister = obtainView(R.id.btn_register);
		progressBar = obtainView(R.id.progressbar);
		tilRegister = obtainView(R.id.til_register);
		tilPassword = obtainView(R.id.til_password);
		tvVerifyCode = obtainView(R.id.btn_verify_code);
		tvBackBtn = obtainView(R.id.tv_back);
		relativeLayout = obtainView(R.id.rl_register_root);
	}

	@Override
	protected void initData() {
		if (null != relativeLayout) {
			ViewTarget<RelativeLayout, GlideDrawable> viewTarget = new ViewTarget<RelativeLayout, GlideDrawable>(relativeLayout) {
				@Override
				public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
					this.view.setBackgroundDrawable(resource.getCurrent());
				}
			};
			Glide.with(getApplicationContext()) // safer!
					.load(R.drawable.bg_login)
					.diskCacheStrategy(DiskCacheStrategy.SOURCE)
					.into(viewTarget);
		}
		mIRegisterPresenter = new RegisterPresenter(this);
		fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
		fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
		fadeInAnimation.setDuration(250);
		fadeOutAnimation.setDuration(250);
		LayoutTransition layoutTransition = new LayoutTransition();
		relativeLayout.setLayoutTransition(layoutTransition);
	}

	@Override
	protected void setListener() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		userNameRegisterViewInit();
	}

	public void showOnLoading(boolean active) {
		if (active) {
			progressBar.setVisibility(View.VISIBLE);
			btnRegister.setVisibility(View.INVISIBLE);
			etPassword.setEnabled(false);
			etPasswordVerify.setEnabled(false);
			etRegister.setEnabled(false);
			tvPhoneRegister.setClickable(false);
			tvPhoneRegister.setTextColor(getResources().getColor(R.color.colorLightTransparentGray));
			btnRegister.setEnabled(false);
		} else {
			progressBar.setVisibility(View.GONE);
			btnRegister.setVisibility(View.VISIBLE);
			etPassword.setEnabled(true);
			etPasswordVerify.setEnabled(true);
			etRegister.setEnabled(true);
			tvPhoneRegister.setClickable(true);
			tvPhoneRegister.setTextColor(getResources().getColor(R.color.colorTransparentGray));
			btnRegister.setEnabled(true);
		}

	}

	private void phoneRegistViewinit() {

		etRegister.setText("");
		etRegister.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		etPassword.setText("");
		etPasswordVerify.setText("");
		tvPhoneRegister.setText(getString(R.string.activity_register_normal_register));
		tilRegister.setHint(getString(R.string.activity_login_phone_num));
		tilPassword.setHint(getString(R.string.activity_register_verify_code));
		tilPasswordVerify.setVisibility(View.GONE);
		tvVerifyCode.setVisibility(View.VISIBLE);
		tvVerifyCode.bringToFront();
		tvVerifyCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mIRegisterPresenter.sendVerifyCode(etRegister.getText().toString());
			}
		});
		tvPhoneRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//转换为用户名注册界面
				userNameRegisterViewInit();
			}
		});
		btnRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//手机注册逻辑
				mPassword = null;
				mIRegisterPresenter.phoneRegister(etRegister.getText().toString(), etPassword.getText().toString());
			}
		});
	}

	private void userNameRegisterViewInit() {

		etRegister.setText("");
		etRegister.setInputType(EditorInfo.TYPE_CLASS_TEXT);
		etPassword.setText("");
		etPasswordVerify.setText("");
		tvVerifyCode.setVisibility(View.GONE);
		tilPasswordVerify.setVisibility(View.VISIBLE);
		tvPhoneRegister.setText(getString(R.string.activity_register_phone_register));
		tilRegister.setHint(getString(R.string.activity_register_username));
		tilPassword.setHint(getString(R.string.activity_register_password));
		tilPasswordVerify.setHint(getString(R.string.activity_register_password_verify));
		tvPhoneRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//转换为手机注册界面
				phoneRegistViewinit();
			}
		});

		btnRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//调用normal注册逻辑
				mPassword = etPassword.getText().toString();
				mIRegisterPresenter.normalRegister(etRegister.getText().toString(),
						mPassword, etPasswordVerify.getText().toString());
			}
		});

		tvBackBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}

	@Override
	public void showRegistError(String errorString) {
		etRegister.setError(errorString);
	}

	@Override
	public void verifyCodeError(String errorMsg) {
		showMsg("验证码错误");
	}

	@Override
	public void verifyCodeFailed(String errorMsg) {
		showMsg("获取验证码失败");
	}

	@Override
	public void showPasswordError(String errorString) {
		etPassword.setError(errorString);
		showOnLoading(false);
	}

	@Override
	public void showPhoneError(String errorMsg) {
		etRegister.setError(errorMsg);
	}

	private void jumpToHomeActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		finish();
	}

	/**
	 * 注册成功
	 * 成功后直接登录
	 *
	 * @param identifier id
	 */
	@Override
	public void onSuccess(final String identifier) {
		Toast.makeText(getApplicationContext(), "成功注册", Toast.LENGTH_SHORT).show();
		finish();
	}

	/**
	 * 注册失败
	 *
	 * @param code 错误码
	 * @param msg  错误信息
	 */
	@Override
	public void onFailure(int code, String msg) {
		LogUtil.d(TAG, "regist fail, code:" + code + " msg:" + msg);
		showMsg(msg);
	}


	@Override
	public void verifyCodeSuccess(int reaskDuration, int expireDuration) {
		LogUtil.d(TAG, "OnSmsRegReaskCodeSuccess");
		showMsg("注册短信下发,验证码" + expireDuration / 60 + "分钟内有效");
		OtherUtils.startTimer(new WeakReference<>(tvVerifyCode), "验证码", reaskDuration, 1);
	}


	@Override
	public void showLoading() {
		showOnLoading(true);
	}

	@Override
	public void dismissLoading() {
		showOnLoading(false);
	}

	@Override
	public void showMsg(String msg) {
		ToastUtils.showShort(this, msg);
	}

	@Override
	public void showMsg(int msg) {
		ToastUtils.showShort(this, msg);
	}

	@Override
	public Context getContext() {
		return this;
	}
}
