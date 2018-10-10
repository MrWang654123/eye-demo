package com.cheersmind.cheersgenie.features.dto;

/**
 * 通信响应对象
 */
public class ResponseDto {

    int code;
    String bodyStr;
    byte[] data;

    public ResponseDto() {
    }

    public ResponseDto(int code, String bodyStr) {
        this.code = code;
        this.bodyStr = bodyStr;
    }

    public ResponseDto(int code, byte[] data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getBodyStr() {
        return bodyStr;
    }

    public void setBodyStr(String bodyStr) {
        this.bodyStr = bodyStr;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}

