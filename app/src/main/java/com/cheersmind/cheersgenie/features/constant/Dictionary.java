package com.cheersmind.cheersgenie.features.constant;

/**
 * 词典
 */
public class Dictionary {

    //短信业务类型：注册用户
    public static final int SmsType_Register = 0;
    //短信业务类型：短信登录
    public static final int SmsType_SmsLogin = 1;
    //短信业务类型：绑定手机号
    public static final int SmsType_Bind_Phone_Num = 2;
    //短信业务类型：找回密码
    public static final int SmsType_Retrieve_Password = 3;

    //承租人：CHEERSMIND
    public static final String Tenant_CheersMind = "CHEERSMIND";

    //手机号区域码
    public static final String Area_Code_86 = "+86";

    //第三方登录平台名称：微信
    public static final String Plat_Source_Weixin = "weixin";
    //第三方登录平台名称：QQ
    public static final String Plat_Source_QQ = "qq";

    //缩进两格
    public static final String Text_Indent = "\u3000\u3000";

    //测评适用对象：学生
    public static final int Exam_Suitable_User_Student = 1;
    //测评适用对象：家长
    public static final int Exam_Suitable_User_Parent = 2;

    //报告类型：话题报告
    public static final String REPORT_TYPE_TOPIC = "topic";
    //报告类型：量表报告
    public static final String REPORT_TYPE_DIMENSION = "topic_dimension";

    //报告数据比较的范围：全国
    public static final int REPORT_COMPARE_AREA_COUNTRY = 0;

    //话题状态：未完成
    public static final int TOPIC_STATUS_INCOMPLETE = 0;
    //话题状态：完成
    public static final int TOPIC_STATUS_COMPLETE = 1;

    //量表是否被锁：否
    public static final int DIMENSION_LOCKED_STATUS_NO = 0;
    //量表是否被锁：是
    public static final int DIMENSION_LOCKED_STATUS_YSE = 1;

    //量表状态：未完成
    public static final int DIMENSION_STATUS_INCOMPLETE = 0;
    //量表状态：已完成
    public static final int DIMENSION_STATUS_COMPLETE = 1;

    //问题类型：只选
    public static final int QUESTION_TYPE_SELECT_ONLY = 1;
    //问题类型：手填
    public static final int QUESTION_TYPE_EDIT = 2;

    //文章是否关联测评：否
    public static final int ARTICLE_IS_REFERENCE_EXAM_NO = 0;
    //文章是否关联测评：是
    public static final int ARTICLE_IS_REFERENCE_EXAM_YES = 1;

    //文章类型：文章
    public static final int ARTICLE_TYPE_SIMPLE = 0;
    //文章类型：视频
    public static final int ARTICLE_TYPE_VIDEO = 1;

    //消息状态：未读
    public static final int MESSAGE_STATUS_UNREAD = 0;
    //消息状态：已读
    public static final int MESSAGE_STATUS_READ = 1;

    //创建会话：注册（手机）
    public static final int CREATE_SESSION_REGISTER = 0;
    //创建会话：登录(帐号、密码登录)
    public static final int CREATE_SESSION_ACCOUNT_LOGIN = 1;
    //创建会话：手机找回密码
    public static final int CREATE_SESSION_RETRIEVE_PASSWORD = 2;
    //创建会话：登录(短信登录)
    public static final int CREATE_SESSION_PHONE_MESSAGE_LOGIN = 3;
    //创建会话：下发短信验证码
    public static final int CREATE_SESSION_MESSAGE_CAPTCHA = 4;

    //http通信结果非json
//    public static final String HTTP_NO_JSON_RESULT = "HTTP_NO_JSON_RESULT";

}
