package com.andruby.live.http.request;

import com.andruby.live.http.response.ResList;
import com.andruby.live.http.response.Response;
import com.andruby.live.model.GiftInfo;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @description: 礼物列表请求
 *
 * @author: Andruby
 * @time: 2016/11/2 18:07
 */
public class GiftListReuqest extends IRequest {

    public GiftListReuqest(int requestId, String userId , String liveId) {
        mRequestId = requestId;
        mParams.put("action","giftList");
        mParams.put("userId",userId);
        mParams.put("liveId",liveId);
    }

    @Override
    public String getUrl() {
        return getHost() + "Live";
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response<ResList<GiftInfo>>>() {}.getType();
    }
}
