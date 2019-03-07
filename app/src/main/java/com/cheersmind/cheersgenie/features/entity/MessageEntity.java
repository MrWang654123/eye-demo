package com.cheersmind.cheersgenie.features.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.main.ioc.InjectMap;

/**
 * 消息
 */
public class MessageEntity  implements MultiItemEntity {

    //未读
    public static final int UNREAD = 0;
    //已读
    public static final int READ = 1;


    //默认是body项
    private int itemType;

    public MessageEntity() {
        //默认未读
        itemType = UNREAD;
    }

    public MessageEntity(int itemType) {
        this.itemType = itemType;
    }

    //消息id
    @InjectMap(name = "id")
    private long id;

    //消息标题
    @InjectMap(name = "title")
    private String title;

    //消息内容
    @InjectMap(name = "message")
    private String message;

    //消息发送时间
    @InjectMap(name = "send_time")
    private String sendTime;

    //消息状态 状态：0未读，1已读
    @InjectMap(name = "status")
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int getItemType() {
        itemType = status;
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

}
