package com.andruby.live.http.request;

import android.util.Log;

import com.andruby.live.http.response.Response;
import com.andruby.live.model.UserInfo;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;


/**
 * @description: 更新用户信息
 * @author: Andruby
 * @time: 2016/11/2 18:07
 */
public class EditUserInfoRequest extends IRequest {

    public EditUserInfoRequest(int requestId, String userId, String nickname, String sex, String headPic) {
        mRequestId = requestId;
        mParams.put("action", "editUserInfo");
        mParams.put("userId", userId);
        mParams.put("nickname", nickname);
        mParams.put("sex", sex);
        File file = new File(headPic);
        Log.i("TAG", "EditUserInfoRequest: file exist= " + file.exists());
        if (file.exists()) {
            try {
                mParams.put("file", file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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
}
