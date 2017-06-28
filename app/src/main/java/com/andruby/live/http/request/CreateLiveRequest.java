package com.andruby.live.http.request;

import com.andruby.live.http.response.CreateLiveResp;
import com.andruby.live.http.response.Response;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


/**
 * @description: 创建直播接口请求
 * @author: Andruby
 * @time: 2016/11/2 18:07
 */
public class CreateLiveRequest extends IRequest {

    public CreateLiveRequest(int requestId, String userId, String groupId, String title, String liveCover, String location, int isRecord) {
        mRequestId = requestId;
        mParams.put("action", "createLive");
        mParams.put("userId", userId);
        mParams.put("groupId", groupId);
        mParams.put("title", title);
        mParams.put("liveCover", liveCover);
        mParams.put("isRecord", isRecord);
        mParams.put("location", location);
    }

    @Override
    public String getUrl() {
        return getHost() + "Live";
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response<CreateLiveResp>>() {
        }.getType();
    }
}
