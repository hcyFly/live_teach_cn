package com.andruby.live.http.request;

import com.andruby.live.http.response.Response;
import com.andruby.live.http.response.UploadResp;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;


/**
 * @description: 图片上传请求
 *
 * @author: Andruby
 * @time: 2016/11/2 18:07
 */
public class UploadPicRequest extends IRequest {

    public final static int LIVE_COVER_TYPE = 1;
    public final static int USER_HEAD_TYPE = 2;


    public UploadPicRequest(int requestId, String userId, int type, File file) throws FileNotFoundException {
        mRequestId = requestId;
        mParams.put("userId",userId);
        mParams.put("type",type);
        mParams.put("file",file);
    }

    @Override
    public String getUrl() {
        return getHost() + "Image/upload";
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response<UploadResp>>() {}.getType();
    }
}