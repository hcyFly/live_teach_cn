package com.andruby.live.model;

import java.io.Serializable;

/**
 * author : qubian on 2016/12/26 15:07
 * description :
 */

public class ReviewInfo implements Serializable{

    private String title;
    private String liveId;
    private String videoUrl;
    private String liveCover;
    private String thumb;
    private String viewCount;
    private String createTime;
    private String duration;
    private SimpleUserInfo userInfo;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getLiveCover() {
        return liveCover;
    }

    public void setLiveCover(String liveCover) {
        this.liveCover = liveCover;
    }

    public String getLiveId() {
        return liveId;
    }

    public void setLiveId(String liveId) {
        this.liveId = liveId;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SimpleUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(SimpleUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
