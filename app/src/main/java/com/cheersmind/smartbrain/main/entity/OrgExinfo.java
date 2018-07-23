package com.cheersmind.smartbrain.main.entity;

import com.cheersmind.smartbrain.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/11/6.
 */

public class OrgExinfo extends DataSupport {
//    "org_name": "CHEERSMIND",
//            "org_id": 481036560705,
//            "org_user_code": "",
//            "org_full_name": "cheersmind",
//            "real_name_short": "",
//            "real_name_full": "",
//            "real_name": "",
//            "node_name": "",
//            "node_id": 0

    @InjectMap(name = "org_name")
    private String orgName;

    @InjectMap(name = "org_id")
    private String orgId;

    @InjectMap(name = "org_user_code")
    private String orgUserCode;

    @InjectMap(name = "org_full_name")
    private String orgFullName;

    @InjectMap(name = "real_name_short")
    private String realNameShort;

    @InjectMap(name = "real_name_full")
    private String realNameFull;

    @InjectMap(name = "real_name")
    private String realName;

    @InjectMap(name = "node_name")
    private String nodeName;

    @InjectMap(name = "node_id")
    private String nodeId;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgUserCode() {
        return orgUserCode;
    }

    public void setOrgUserCode(String orgUserCode) {
        this.orgUserCode = orgUserCode;
    }

    public String getOrgFullName() {
        return orgFullName;
    }

    public void setOrgFullName(String orgFullName) {
        this.orgFullName = orgFullName;
    }

    public String getRealNameShort() {
        return realNameShort;
    }

    public void setRealNameShort(String realNameShort) {
        this.realNameShort = realNameShort;
    }

    public String getRealNameFull() {
        return realNameFull;
    }

    public void setRealNameFull(String realNameFull) {
        this.realNameFull = realNameFull;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
