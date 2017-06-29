package com.andruby.live.http.request;

import com.andruby.live.http.response.ResList;
import com.andruby.live.http.response.Response;
import com.andruby.live.model.ReviewInfo;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * author : Andruby on 2016/12/26 14:23
 * description :
 */

public class ReviewListRequest extends IRequest {

    public ReviewListRequest(int requestId, String userId, int pageIndex, int pageSize, int lastId) {
        mRequestId = requestId;
        mParams.put("action", "recordList");
        mParams.put("userId", userId);
        mParams.put("pageIndex", pageIndex);
        mParams.put("pageSize", pageSize);
        mParams.put("lastId", lastId);
    }


    @Override
    public String getUrl() {
        return getHost() + "Live";
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response<ResList<ReviewInfo>>>() {
        }.getType();
    }
}