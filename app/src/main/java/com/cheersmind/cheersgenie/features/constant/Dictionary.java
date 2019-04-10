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
    //报告数据比较的范围：8大区
    public static final int REPORT_COMPARE_AREA_8_DISTRICT = 1;
    //报告数据比较的范围：省
    public static final int REPORT_COMPARE_AREA_PROVINCE = 2;
    //报告数据比较的范围：市
    public static final int REPORT_COMPARE_AREA_CITY = 3;
    //报告数据比较的范围：区
    public static final int REPORT_COMPARE_AREA_DISTRICT = 4;
    //报告数据比较的范围：学校
    public static final int REPORT_COMPARE_AREA_SCHOOL = 5;
    //报告数据比较的范围：年级
    public static final int REPORT_COMPARE_AREA_GRADE = 6;
    //报告数据比较的范围：班级
    public static final int REPORT_COMPARE_AREA_CLASS = 7;

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
    public static final int ARTICLE_TYPE_SIMPLE = 1;
    //文章类型：视频
    public static final int ARTICLE_TYPE_VIDEO = 2;

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

    //用户的角色：自己
    public static final int PARENT_ROLE_MYSELF = 0;
    //用户的角色：父亲
    public static final int PARENT_ROLE_FATHER = 1;
    //用户的角色：母亲
    public static final int PARENT_ROLE_MOTHER = 2;
    //用户的角色：爷爷或外公
    public static final int PARENT_ROLE_GRANDPA = 3;
    //用户的角色：奶奶或外婆
    public static final int PARENT_ROLE_GRANDMA = 4;
    //用户的角色：其他
    public static final int PARENT_ROLE_OTHER = 99;

    //学段：幼儿园
    public static final int PERIOD_KINDERGARTEN = 1;
    //学段：小学
    public static final int PERIOD_PRIMARY_SCHOOL = 2;
    //学段：初中
    public static final int PERIOD_MIDDLE_SCHOOL = 3;
    //学段：高中
    public static final int PERIOD_HIGH_SCHOOL = 4;

    //头像header的key
    public static final String PROFILE_HEADER_KEY = "Referer";
    //头像header的value
    public static final String PROFILE_HEADER_VALUE = "http://www.cheersmind.com/";

    //测评列表布局类型：网格
    public static final int EXAM_LIST_LAYOUT_TYPE_GRID = 1;
    //测评列表布局类型：线性
    public static final int EXAM_LIST_LAYOUT_TYPE_LINEAR = 2;


    //图表类型
    //header
    public final static int CHART_HEADER = 100;
    //footer
    public final static int CHART_FOOTER = 200;
    //子标题
    public final static int CHART_SUB_TITLE = 500;
    //推荐的act分类
    public final static int CHART_RECOMMEND_ACT_TYPE = 600;
    //推荐内容标题
    public final static int CHART_RECOMMEND_CONTENT_TITLE = 700;
    //推荐内容项
    public final static int CHART_RECOMMEND_CONTENT_ITEM = 800;
    //子项
    public final static int CHART_SUB_ITEM = 300;
    //图表描述
    public final static int CHART_DESC = 400;
    //网状图
    public final static int CHART_RADAR = 1;
    //曲线图
    public final static int CHART_LINE = 2;
    //垂直柱状图
    public final static int CHART_BAR_V = 3;
    //水平条状图
    public final static int CHART_BAR_H = 4;
    //左右比例图
    public final static int CHART_LEFT_RIGHT_RATIO = 5;

    //RecyclerView最大滑动速度（dp）
    public final static int RECYCLER_VIEW_MAX_VELOCITY = 2000;

    //语音文本结束符
    public static final String VOICE_TEXT_END_SYMBOL = "。";

    //隐藏文章列表的加载更多视图的阈值数量
    public static final int HIDE_ARTICLE_LOAD_MORE_VIEW_COUNT = 5;
    //隐藏文章列表的加载更多视图的阈值数量
    public static final int HIDE_ARTICLE_LOAD_MORE_VIEW_COUNT_SMALL = 6;
    //隐藏历史测评列表的加载更多视图的阈值数量
    public static final int HIDE_HISTORY_EXAM_LOAD_MORE_VIEW_COUNT = 10;
    //隐藏历史专题列表的加载更多视图的阈值数量
    public static final int HIDE_HISTORY_SEMINAR_LOAD_MORE_VIEW_COUNT = 5;

    //测评状态：未激活
    public static final int EXAM_STATUS_INACTIVE = 0;
    //测评状态：进行中
    public static final int EXAM_STATUS_DOING = 1;
    //测评状态：已结束
    public static final int EXAM_STATUS_OVER = 2;

    //孩子测评状态：未完成
    public static final int CHILD_EXAM_STATUS_NO_COMPLETE = 0;
    //孩子测评状态：已完成
    public static final int CHILD_EXAM_STATUS_COMPLETE = 1;

    //从主页进入答题页
    public static final int FROM_ACTIVITY_TO_QUESTION_MAIN = 1;
    //从我的智评页进入答题页
    public static final int FROM_ACTIVITY_TO_QUESTION_MINE = 2;
    //从任务详情页进入答题页
    public static final int FROM_ACTIVITY_TO_TASK_DETAIL = 3;
    //从成长档案进入答题页
    public static final int FROM_ACTIVITY_TO_TRACK_RECORD = 4;
    //从系统推荐选科进入答题页
    public static final int FROM_ACTIVITY_TO_SYS_RMD_COURSE = 5;


    //报告图表比较ID：全国
    public static final String REPORT_CHART_COMPARE_ID_COUNTRY = "0";
    //报告图表比较ID：年级
    public static final String REPORT_CHART_COMPARE_ID_GRADE = "1";
    //报告图表比较ID：我
    public static final String REPORT_CHART_COMPARE_ID_MINE = "2";

    //任务状态：未完成
    public static final int TASK_STATUS_INCOMPLETE = 0;
    //任务状态：已完成
    public static final int TASK_STATUS_COMPLETED = 1;
    //任务状态：已结束未完成
    public static final int TASK_STATUS_OVER_INCOMPLETE = 2;

    //任务项类型：通用场景
    public static final int TASK_ITEM_TYPE_TOPIC_COMMON = 1;
    //任务项类型：量表
    public static final int TASK_ITEM_TYPE_DIMENSION = 2;
    //任务项类型：文章
    public static final int TASK_ITEM_TYPE_ARTICLE = 3;
    //任务项类型：视频
    public static final int TASK_ITEM_TYPE_VIDEO = 4;
    //任务项类型：音频
    public static final int TASK_ITEM_TYPE_AUDIO = 5;
    //任务项类型：实践
    public static final int TASK_ITEM_TYPE_PRACTICE = 6;
    //任务项类型：确认选课结果
    public static final int TASK_ITEM_TYPE_CHOOSE_COURSE = 7;
    //任务项类型：高考3+6选3
    public static final int TASK_ITEM_TYPE_TOPIC_363 = 21;
    //任务项类型：高考3+7选3
    public static final int TASK_ITEM_TYPE_TOPIC_373 = 22;
    //任务项类型：高考3+2选1+4选2
    public static final int TASK_ITEM_TYPE_TOPIC_321_42 = 23;

    //是否被锁：否
    public static final int IS_LOCKED_NO = 0;
    //是否被锁：是
    public static final int IS_LOCKED_YSE = 1;

    //RecyclerView层级0的布局类型
    public final static int RECYCLER_VIEW_LAYOUT_TYPE_LEVEL0 = 0;
    //RecyclerView层级1的布局类型
    public final static int RECYCLER_VIEW_LAYOUT_TYPE_LEVEL1 = 1;
    //RecyclerView层级2的布局类型
    public final static int RECYCLER_VIEW_LAYOUT_TYPE_LEVEL2 = 2;

    //任务来源类型：默认
    public static final int TASK_FROM_TYPE_DEFAULT = 0;
    //任务来源类型：校本课程
    public static final int TASK_FROM_TYPE_SCHOOL_COURSE = 1;

    //职业分类：ACT
    public static final int OCCUPATION_TYPE_ACT = 1;
    //职业分类：按行业分类
    public static final int OCCUPATION_TYPE_INDUSTRY = 2;

    //重点学科类型：国家重点
    public static final String KEY_SUBJECT_TYPE_COUNTRY = "national_key";
    //重点学科类型：一流学科
    public static final String KEY_SUBJECT_TYPE_FIRST_RATE = "double_first_class";

    //关注对象类型：院校
    public static final int ATTENTION_TYPE_COLLEGE = 0;
    //关注对象类型：专业
    public static final int ATTENTION_TYPE_MAJOR = 1;
    //关注对象类型：职业
    public static final int ATTENTION_TYPE_OCCUPATION = 2;

    //评论类型：文章
    public static final int COMMENT_TYPE_ARTICLE = 0;
    //评论类型：任务
    public static final int COMMENT_TYPE_TASK = 1;

}
