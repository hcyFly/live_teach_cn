package com.andruby.live.http.request;

import com.andruby.live.model.UserInfo;
import com.andruby.live.http.response.Response;
import com.andruby.live.utils.CipherUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @description: 登陆请求
 * @author: Andruby
 * @time: 2016/11/2 18:07
 */
public class LoginRequest extends IRequest {

	public static  final int LOGIN_TYPE_ORDING =1;
	public static  final int LOGIN_TYPE_CNIAO =2;
	/**
	 *
	 * @param requestId
	 * @param userName
	 * @param password
	 * @param loginTpe  1 普通 2 菜鸟
	 */
	public LoginRequest(int requestId, String userName, String password,int loginTpe) {
		mRequestId = requestId;
		if(loginTpe==LOGIN_TYPE_ORDING){
			mParams.put("action", "login");//普通账号登录
		}else if(loginTpe==LOGIN_TYPE_CNIAO){
			mParams.put("action", "loginCniaow");//发起直播需要调用这个接口，使用菜鸟窝账号并且购买了直播课程
		}
		mParams.put("userName", userName);
		if (mParams.getUrlParams("action").equals("loginCniaow")) {
			mParams.put("password", CipherUtil.getAESInfo(password));
		} else {
			mParams.put("password", password);
		}
	}

	@Override
	public String getUrl() {
		return getHost() + "User";
	}

	@Override
	public Type getParserType() {
		return new TypeToken<Response<UserInfo>>() {
		}.getType();
	}

	@Override
	public boolean cleanCookie() {
		return true;
	}
}
