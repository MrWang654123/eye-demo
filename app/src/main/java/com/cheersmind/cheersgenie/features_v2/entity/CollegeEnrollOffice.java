package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;

/**
 * 招生办信息
 */
public class CollegeEnrollOffice implements Serializable {

    //招生办联系电话
    @InjectMap(name = "contact_info")
    private String contact_info;

    //招生办地址
    @InjectMap(name = "address")
    private String address;

    //招生办官网
    @InjectMap(name = "website")
    private String website;

    public String getContact_info() {
        return contact_info;
    }

    public void setContact_info(String contact_info) {
        this.contact_info = contact_info;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
