package com.andruby.live.http.request;

import com.andruby.live.http.response.Response;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @description: 直播喜欢请求
 * @author: Andruby
 * @time: 2016/11/2 18:07
 */
public class LiveLikeRequest extends IRequest {

    public LiveLikeRequest(int requestId, String userId, String liveId, String hostId, String groupId) {
        mRequestId = requestId;
        mParams.put("action", "liveLike");
        mParams.put("userId", userId);
        mParams.put("liveId", liveId);
        mParams.put("hostId", hostId);
        mParams.put("groupId", groupId);
    }

    @Override
    public String getUrl() {
        return getHost() + "Live";
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response>() {
        }.getType();
    }
}