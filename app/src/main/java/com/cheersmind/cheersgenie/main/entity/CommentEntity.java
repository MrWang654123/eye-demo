package com.cheersmind.cheersgenie.main.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 评论
 */
public class CommentEntity extends DataSupport implements Serializable {

    @InjectMap(name = "id")
    private String id;

    //评论内容
    @InjectMap(name = "comment_info")
    private String commentInfo;

    //评论时间
    @InjectMap(name = "create_time")
    private String createTime;

    //0：文章
    @InjectMap(name = "type")
    private int type;

    //用户（作者）
    @InjectMap(name = "user_data")
    private UserEntity userData;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommentInfo() {
        return commentInfo;
    }

    public void setCommentInfo(String commentInfo) {
        this.commentInfo = commentInfo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public UserEntity getUserData() {
        return userData;
    }

    public void setUserData(UserEntity userData) {
        this.userData = userData;
    }
}
