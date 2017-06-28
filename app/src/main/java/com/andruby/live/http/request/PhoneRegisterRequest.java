package com.andruby.live.http.request;

import com.andruby.live.http.response.Response;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @description: 注册接口请求
 * @author: Andruby
 * @time: 2016/11/2 18:07
 */
public class PhoneRegisterRequest extends IRequest {

	public PhoneRegisterRequest(int requestId, String mobile, String verifyCode) {
		mRequestId = requestId;
		mParams.put("action", "phoneRegister");
		mParams.put("mobile", mobile);
		mParams.put("verifyCode", verifyCode);
	}

	@Override
	public String getUrl() {
		return getHost() + "User";
	}

	@Override
	public Type getParserType() {
		return new TypeToken<Response>() {
		}.getType();
	}
}
