package com.andruby.live.http.request;

import com.andruby.live.http.response.Response;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * author : qubian on 2016/12/26 14:16
 * description :
 */

public class DoFollowRequest extends IRequest {

    public DoFollowRequest(int requestId, String userId , String followTo) {
        mRequestId = requestId;
        mParams.put("action","follow");
        mParams.put("userId",userId);
        mParams.put("followTo",followTo);
    }

    @Override
    public String getUrl() {
        return getHost() + "Live";
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response>() {}.getType();
    }
}