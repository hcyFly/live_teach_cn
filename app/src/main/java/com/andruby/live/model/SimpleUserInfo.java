package com.andruby.live.model;


/**
 * @description: 用户基本信息封装 id、nickname、faceurl
 *
 * @author: Andruby
 * @time: 2016/11/4 14:12
 */
public class SimpleUserInfo {

    public String userId;
    public String nickname;
    public String headPic;

    public SimpleUserInfo(String userId, String nickname, String headpic) {
        this.userId = userId;
        this.nickname = nickname;
        this.headPic = headpic;
    }
}
