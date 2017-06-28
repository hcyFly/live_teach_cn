package com.andruby.live.model;
/**
 * @description: 直播信息
 *
 * @author: Andruby
 * @time: 2016/11/4 14:12
 */
public class LiveInfo {
    public String userId;
    public String groupId;
    public String liveId;
    public int createTime;
    public int      type;
    public int viewCount;
    public int likeCount;
    public String   title;
    public String playUrl;
    public String fileId;
    public String liveCover;

    //TCLiveUserInfo
    public TCLiveUserInfo userInfo;


    public class TCLiveUserInfo {
        public String userId;
        public String nickname;
        public String headPic;
        public String frontcover;
        public String location;
    }
}
