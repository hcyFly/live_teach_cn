package com.andruby.live.model;

import java.io.Serializable;

/**
 * @description: 直播信息
 * @author: Andruby
 * @time: 2016/11/4 14:12
 */
public class LiveInfo implements Serializable {
    public String userId;
    public String groupId;
    public String liveId;
    public int createTime;
    public int type;
    public int viewCount;
    public int likeCount;
    public String title;
    public String playUrl;
    public String fileId;
    public String liveCover;

    //TCLiveUserInfo
    public TCLiveUserInfo userInfo;


    public class TCLiveUserInfo implements Serializable {
        public String userId;
        public String nickname;
        public String headPic;
        public String location;
    }
}
