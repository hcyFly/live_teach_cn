package com.andruby.live.model;

import com.andruby.live.http.IDontObfuscate;

/**
 * @description: 用户信息
 * @author: Andruby
 * @time: 2016/10/31 18:07
 */
public class UserInfo extends IDontObfuscate {

	public String userId;
	public String nickname;
	public String headPic;
	public String sigId;
	public String sdkAppId;
	public String sdkAccountType;
	public int sex;
	public String token;

	public UserInfo() {
	}

	public UserInfo(String userId, String nickname, String headPic, int sex) {
		this.userId = userId;
		this.nickname = nickname;
		this.headPic = headPic;
		this.sex = sex;
	}

}