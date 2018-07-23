package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

/**
 * Created by Administrator on 2017/12/9.
 */

public class DialogDataEntity {

//    "type": 1,
//            "title": "提示",
//            "message": "该对比报告还未生成，请耐心等待。",
//            "cancelButton": "",
//            "okButton": "确定"

    @InjectMap(name = "type")
    private int type;

    @InjectMap(name = "title")
    private String title;

    @InjectMap(name = "message")
    private String message;

    @InjectMap(name = "cancelButton")
    private String cancelButton;

    @InjectMap(name = "okButton")
    private String okButton;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getCancelButton() {
        return cancelButton;
    }

    public void setCancelButton(String cancelButton) {
        this.cancelButton = cancelButton;
    }

    public String getOkButton() {
        return okButton;
    }

    public void setOkButton(String okButton) {
        this.okButton = okButton;
    }
}
