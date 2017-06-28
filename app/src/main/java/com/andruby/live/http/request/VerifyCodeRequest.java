package com.andruby.live.http.request;

import com.andruby.live.http.response.Response;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @description: 注册接口请求
 * @author: Andruby
 * @time: 2016/11/2 18:07
 */
public class VerifyCodeRequest extends IRequest {

	public VerifyCodeRequest(int requestId, String mobile) {
		mRequestId = requestId;
		mParams.put("action", "verifyCode");
		mParams.put("mobile", mobile);
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
