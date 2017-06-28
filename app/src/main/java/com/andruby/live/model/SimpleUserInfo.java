package com.andruby.live.model;


/**
 * @description: 用户基本信息封装 id、nickname、faceurl
 *
 * @author: Andruby
 * @time: 2016/11/4 14:12
 */
public class SimpleUserInfo {

    public String userid;
    public String nickname;
    public String headpic;

    public SimpleUserInfo(String userId, String nickname, String headpic) {
        this.userid = userId;
        this.nickname = nickname;
        this.headpic = headpic;
    }
}
