package com.andruby.live.http.request;

import com.andruby.live.http.response.ResList;
import com.andruby.live.http.response.Response;
import com.google.gson.reflect.TypeToken;
import com.andruby.live.model.LiveInfo;

import java.lang.reflect.Type;

/**
 * @description: 直播列表请求
 *
 * @author: Andruby
 * @time: 2016/11/2 18:07
 */
public class LiveListRequest extends IRequest {

	public LiveListRequest(int requestId,String userId ,int pageIndex, int pageSize) {
		mRequestId = requestId;
		mParams.put("action","liveList");
		mParams.put("userId",userId);
		mParams.put("pageIndex", pageIndex);
		mParams.put("pageSize", pageSize);
	}

	@Override
	public String getUrl() {
		return getHost() + "Live";
	}

	@Override
	public Type getParserType() {
		return new TypeToken<Response<ResList<LiveInfo>>>() {}.getType();
	}
}
