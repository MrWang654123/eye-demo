package com.cheersmind.cheersgenie.main.constant;

import com.cheersmind.cheersgenie.module.login.EnvHostManager;

/**
 * Created by goodm on 2017/4/15.
 */
public class HttpConfig {

    //生产环境
//    public static final String UC_HOST  = "http://psytest-server.cheersmind.com";
//    public static final String API_HOST = "http://psytest-server.cheersmind.com";
//    public static final String WEB_HOST = "http://psytest-web.cheersmind.com";

    //开发环境
    public static String UC_HOST  = EnvHostManager.getInstance().getUcHost();
    public static String API_HOST = EnvHostManager.getInstance().getApiHost();
    public static String WEB_HOST = EnvHostManager.getInstance().getWebHost();

    /**-----------------------------------------------------------------------
     *---------------------------用户信息相关-----------------------------------
     ---------------------------------------------------------------------------*/
    //邀请码验证
    public static final String URL_CODE_INVATE = API_HOST + "/v1/api/registers/actions/check_invite_code";
    //邀请码注册
    public static final String URL_CODE_REGISTERS = API_HOST + "/v1/api/registers";
    //登入uc
    public static final String URL_LOGIN = UC_HOST+"/v1/oauth2/tokens/actions/sign_in";
    //获取用户详情
    public static final String URL_USER_INFO = UC_HOST+"/v1/api/users";
    //获取用户详情 V2
    public static final String URL_USER_INFO_V2 = UC_HOST+"/v2/api/users";
    //获取孩子列表
    public static final String URL_CHILD_LIST = API_HOST+"/v1/api/users/children";
    //获取孩子列表V2
    public static final String URL_CHILD_LIST_V2 = API_HOST+"/v2/api/users/children";

//    public static final String URL_CHILD_LIST = API_HOST+"/v1/api/children";
    public static final String URL_CHILD_INFO = API_HOST+"/v1/api/children/{child_id}";

    //uc第三方登入
    public static final String URL_UC_THIRD_LOGIN = UC_HOST + "/v1/oauth2/tokens/actions/third_sign_in";
    //uc第三方登入 V2
    public static final String URL_UC_THIRD_LOGIN_V2 = UC_HOST + "/v2/oauth2/tokens/actions/third_sign_in";
    //注册
    public static final String URL_UC_REGISTER = API_HOST + "/v1/api/users/third/sign_in";

    //获取用户详情
    public static final String URL_USER_DETAILS = API_HOST + "/v1/api/users";

    //获取系统时间
    public static final String URL_SERVER_TIME = API_HOST + "/v1/oauth2/server/time";

    //获取手机验证码
    public static final String URL_PHONE_CAPTCHA = API_HOST + "/v2/accounts/sms";

    //手机短信账号注册
    public static final String URL_PHONE_MESSAGE_REGISTER = API_HOST + "/v2/oauth2/mobile/sign_up";

    //获取班级信息
    public static final String URL_CLASS_INFO = API_HOST + "/v2/api/classes/groups/{group_no}";

    //注册完善用户信息
    public static final String URL_REGISTER_SUBMIT_USERINFO = API_HOST + "/v2/api/users/info";

    //绑定手机号
    public static final String URL_BIND_PHONE_NUM = API_HOST + "/v2/accounts/mobile/actions/bind";

    //手机短信登录
    public static final String URL_PHONE_NUM_LOGIN = API_HOST + "/v2/oauth2/sms/sign_in";

    //账号登录
    public static final String URL_ACCOUNT_LOGIN = API_HOST + "/v2/oauth2/tokens/actions/sign_in";

    //我的收藏
    public static final String URL_MINE_FAVORITE = API_HOST + "/v2/api/articles/{user_id}/favorites?page={page}&size={size}";

    //修改密码
    public static final String URL_MODIFY_PASSWORD = API_HOST + "/v2/accounts/users/password";

    //查询已绑定的第三方平台
    public static final String URL_THIRD_PLATPORM = API_HOST + "/v2/accounts/users/third_party?tenant={tenant}";

    //绑定第三方平台账号
    public static final String URL_BIND_THIRD_PLATPORM = API_HOST + "/v2/accounts/actions/third_bind";

    //解绑第三方平台账号
    public static final String URL_UNBIND_THIRD_PLATPORM = API_HOST + "/v2/accounts/actions/third_unbind";

    //查询当前签到状态
    public static final String URL_DAILY_SIGN_IN_STATUS = API_HOST + "/v2/api/missions/daily/sign_in";

    //签到
    public static final String URL_DAILY_SIGN_IN = API_HOST + "/v2/api/missions/daily/sign_in";

    //获取总积分
    public static final String URL_INTEGRAL_TOTAL_SCORE = API_HOST + "/v2/api/integrals/total_score";

    //获取积分列表
    public static final String URL_INTEGRALS = API_HOST + "/v2/api/integrals?page={page}&size={size}";

    //重置密码
    public static final String URL_RESET_PASSWORD = API_HOST + "/v2/accounts/password/actions/reset";

    //获取用户消息列表
    //?page={page}&size={size}
    public static final String URL_MESSAGE = API_HOST + "/v2/api/messages?page={page}&size={size}";

    //获取新消息条数
    //?page={page}&size={size}
    public static final String URL_NEW_MESSAGE_COUNT = API_HOST + "/v2/api/messages/unread_message/count";

    //标记消息为已读
    public static final String URL_MARK_READ = API_HOST + "/v2/api/messages/{message_id}/read";

    //创建会话
    public static final String URL_CREATE_SESSION = API_HOST + "/v2/accounts/sessions";

    //获取图形验证码
    public static final String URL_IMAGE_CAPTCHA = API_HOST + "/v2/accounts/sessions/{session_id}/verification_code";

    //获取用户的手机号
    public static final String URL_USER_PHONE_NUM = UC_HOST+"/v2/accounts/mobile";

    //修改用户头像（图片支持格式为.png .gif .jpg格式，文件大小不能超过1M）
    public static final String URL_MODIFY_PROFILE = UC_HOST+"/v2/api/users/avatar/upload";

    //修改昵称（用户信息）
    public static final String URL_MODIFY_USER_INFO = API_HOST + "/v2/api/users";

    //获取系统所有勋章列表
    public static final String URL_MEDALS = API_HOST + "/v2/api/medals";


    /**-----------------------------------------------------------------------
     *---------------------------业务相关-----------------------------------
     ---------------------------------------------------------------------------*/

    //获取项目列表，带当前测评（exam_id）
    public static final String URL_PROJECTS_LIST = API_HOST + "/v1/api/children/{child_id}/projects";

    //获取首页推荐量表列表
    public static final String URL_RECOMMEND_MAIN = API_HOST + "/v1/api/children/{child_id}/exams/{exam_id}/recommend_dimensions?topic_id={topic_id}&page={page}&size={size}";

    //获取主题列表
    public static final String URL_TOPIC_LIST = API_HOST + "/v1/api/topics?page={page}&size={size}";
    //获取主题详情
    public static final String URL_TOPIC_INFO = API_HOST + "/v1/api/topics/{topic_id}";
    //获取主题详情V2
    public static final String URL_TOPIC_INFO_V2 = API_HOST + "/v2/api/topics/{topic_id}";

    //获取量表列表
    public static final String URL_DIMENSION_LIST = API_HOST + "/v1/api/dimensions?topic_id={topic_id}&page={page}&size={size}";
    //获取量表详情
    public static final String URL_DIMENSION_INFO = API_HOST + "/v1/api/dimensions/{dimension_id}";

    //热门分量表列表
    public static final String URL_DIMENSION_HOTE_LIST = API_HOST + "/v1/api/children/{child_id}/exams/{exam_id}/dimensions/hot";

    //获取孩子最近一次使用量表
    public static final String URL_DIMENSION_LATEST = API_HOST + "/v1/api/children/{child_id}/latest_dimensions";
    //获取孩子最近一次使用量表 V2
    public static final String URL_DIMENSION_LATEST_V2 = API_HOST + "/v2/api/children/{child_id}/latest_dimensions";

    //获取因子列表
    public static final String URL_FACTOR_LIST = API_HOST +"/v1/api/factors?dimension_id={dimension_id}&page={page}&size={size}";
    //获取因子列表
    public static final String URL_FACTOR_INFO = API_HOST +"/v1/api/factors?dimension_id={dimension_id}&page={page}&size={size}";

    //获取题目列表
    public static final String URL_QUESTION_LIST = API_HOST + "/v1/api/questions?factor_id={factor_id}&page={page}&size={size}";
    //获取问题详情
    public static final String URL_QUESTION_INFO = API_HOST + "/v1/api/questions/{question_id}";

    //关注孩子主题(开始主题)
    public static final String URL_CHILD_TOPIC_FOLLOW = API_HOST + "/v1/api/children/{child_id}/exams/{exam_id}/topics/{topic_id}/follow";

    //获取孩子关注的主题列表
    public static final String URL_CHILD_TOPIC_LIST = API_HOST + "/v2/api/children/{child_id}/topics?page={page}&size={size}";
    //根据状态（未完成、已完成）获取孩子关注主题列表
    public static final String URL_CHILD_TOPIC_LIST_BY_STATUS = API_HOST + "/v2/api/children/{child_id}/child_exams?status={status}&page={page}&size={size}";
    //获取孩子的测评列表
    public static final String URL_CHILD_EXAM_LIST = API_HOST + "/v2/api/children/{child_id}/exam_with_topics?page={page}&size={size}";
    //获取孩子的历史测评列表
    public static final String URL_CHILD_HISTORY_EXAM_LIST = API_HOST + "/v2/api/children/{child_id}/exams/history?page={page}&size={size}";
    //获取孩子的历史测评明细
    public static final String URL_CHILD_HISTORY_EXAM_DETAIL = API_HOST + "/v2/api/children/{child_id}/exams/{exam_id}/topics?page={page}&size={size}";
    //获取孩子的历史专题列表
    public static final String URL_CHILD_HISTORY_SEMINAR_LIST = API_HOST + "/v2/api/children/{child_id}/seminars/history?page={page}&size={size}";


    //获取孩子关注的主题列表（报告表头使用）
    public static final String URL_CHILD_TOPIC_LIST_REPORT = API_HOST + "/v2/api/children/{child_id}/latest_exam_topics";

    //获取孩子的主题详情
    public static final String URL_CHILD_TOPIC_INFO = API_HOST + "/v1/api/children/{child_id}/topics/{topic_id}";

    //获取孩子主题下量表
    public static final String URL_CHILD_DIMENSION_LIST = API_HOST + "/v1/api/children/{child_id}/exams/{exam_id}/dimensions?topic_id={topic_id}&page={page}&size={size}";
    //获取孩子所有主题下量表
    public static final String URL_CHILD_DIMENSION_LIST_ALL = API_HOST + "/v1/api/children/{child_id}/dimensions?&page={page}&size={size}";

    //获取孩子量表详情
    public static final String URL_CHILD_DIMENSION_INFO = API_HOST + "/v1/api/children/{child_id}/dimensions/{dimension_id}";
    //开始某个孩子量表
    public static final String URL_CHILD_FDIMENSION_START = API_HOST + "/v1/api/children/{child_id}/exams/{exam_id}/topics/{topic_id}/dimensions/{dimension_id}";
    //开始某个孩子量表 V2
    public static final String URL_CHILD_DIMENSION_START_V2 = API_HOST + "/v2/api/children/{child_id}/exams/{exam_id}/topics/{topic_id}/dimensions/{dimension_id}";

    //获取孩子的因子列表
    public static final String URL_CHILD_FACTOR_LIST = API_HOST + "/v1/api/children/{child_id}/dimensions/{dimension_id}/factors?child_dimension_id={child_dimension_id}&page={page}&size={size}";
    //获取孩子因子详情
    public static final String URL_CHILD_FACTOR_INFO = API_HOST + "/v1/api/children/{child_id}/factors/{factor_id}";
    //开始孩子某个因子测评
    public static final String URL_CHILD_FACTOR_START = API_HOST + "/v1/api/children/{child_id}/exams/{exam_id}/topics/{topic_id}/dimensions/{dimension_id}/factors/{factor_id}";

    //提交某个孩子因子测评
    public static final String URL_CHILD_FACTOR_COMMIT = API_HOST + "/v1/api/children/{child_id}/factors/{child_factor_id}/submit";
    //获取孩子的因子排行数据
    public static final String URL_CHILD_FACTOR_RANKING = API_HOST + "/v1/api/children/{child_id}/topics/{topic_id}/dimensions/{dimension_id}/factors/{factor_id}/rank";
    //获取孩子的因子统计报表数据
    public static final String URL_CHILD_FACTOR_RANK_REPORT = API_HOST + "/v1/api/children/{child_id}/child_factors/{child_factor_id}/rank_report";

    //获取孩子某个因子下的题目列表
    public static final String URL_CHILD_FACTOR_QUESTION = API_HOST + "/v1/api/children/{child_id}/factors/{factor_id}/questions?child_factor_id={child_factor_id}&page={page}&size={size}";
    //获取孩子某个量表下的题目列表 V2
    public static final String URL_CHILD_DIMENSION_QUESTION_V2 = API_HOST + "/v2/api/child_dimensions/{child_dimension_id}/questions?page={page}&size={size}";
    //保存单题答案
    public static final String URL_QUESTION_SAVE_SINGLE = API_HOST + "/v1/api/children/{child_id}/questions/{question_id}/save";

    //孩子的量表报告
    public static final String URL_CHILD_DIMENSION_REPORT = API_HOST + "/v1/api/children/{child_id}/dimension_reports/{dimension_report_id}";

    //获取孩子鲜花消费
    public static final String URL_FLOWER_CONSUME_RECORD = API_HOST + "/v1/api/flowers?page={page}&size={size}";


    //报告地址
    public static String DIMENSION_REPORT_URL = WEB_HOST + "/report.html?access_token={access_token}&refresh_token={refresh_token}&mac_key={mac_key}&user_id={user_id}&child_id={child_id}&exam_id={exam_id}&type={type}&id={id}";
    //报告拦截地址
    public static String DIMENSION_REPORT_INTERCEPT_URL = WEB_HOST + "/report.html?{intercept_url}";

    //用户许可协议
    public static String USER_LICENSE = "http://cheersmind.com/about/user.html";
    //关于我们
    public static String URL_ABOUT_US = "http://cheersmind.com/about/about.html";
    //积分说明
    public static String URL_INTEGRAL_DESC = "http://cheersmind.com/about/description/score.html";
    //app访问地址
    public static String URL_APP_MARKET = "http://zhushou.360.cn/detail/index/soft_id/3950691?recrefer=SE_D_%E5%A4%A9%E5%A4%A9%E6%99%BA%E5%AD%A6#nogo";
//    public static String URL_APP_MARKET = "http://sj.qq.com/myapp/detail.htm?apkName=com.cheersmind.smartapp";

    //主题下报告（包括量表报告）
    public static String URL_TOPIC_REPORT = API_HOST + "/v1/api/exams/{exam_id}/children/{child_id}/reports?topic_id={topic_id}&sample_id={sample_id}";

    public static String URL_TOPIC_REPORT_V2 = API_HOST + "/v2/api/exams/{exam_id}/children/{child_id}/reports?relation_id={relation_id}&relation_type={relation_type}&sample_id={sample_id}";
    //获取量表
    public static String URL_DIMENSION_REPORT = API_HOST + "/v1/api/exams/{exam_id}/dimensions/{dimension_id}/children/{child_id}/reports?sample_id={sample_id}";

    public static final String URL_APP_ID = API_HOST + "/v1/api/apps/{app_id}";
    public static final String URL_VERSION_UPDATE = API_HOST + "/v1/api/apps/{app_id}/versions/actions/get_latest_version";

    //提交问题答案（分量表下的所有题目）
    public static String URL_QUESTIONS_ANSWERS_SUBMIT = API_HOST + "/v2/api/child_dimensions/{child_dimension_id}/submit";

    //保存问题答案（分量表下的题目）
    public static String URL_QUESTIONS_ANSWERS_SAVE = API_HOST + "/v2/api/child_dimensions/{child_dimension_id}/save";

    //微信登入
    public static final String URL_WX_GET_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid={appid}&secret={secret}&code={code}&grant_type=authorization_code";

    //公告更新通知
    public static final String URL_UPDATE_NOTIFICATION = "http://img.cheersmind.com/notice/fireeyes.json";

    //获取报告
//    public static String URL_REPORT_V2 = API_HOST + "/v2/api/exams/reports?child_exam_id={child_exam_id}&relation_id={relation_id}&relation_type={relation_type}&compare_id={compare_id}";
    public static String URL_REPORT_V2 = API_HOST + "/v2/api/reports/students/{child_exam_id}?relation_id={relation_id}&relation_type={relation_type}&sample_id={sample_id}";

    //获取报告新
    public static String URL_REPORT_V2_NEW = API_HOST + "/v2/api/exams/personal/reports";

    //获取报告推荐文章
    public static String URL_REPORT_RECOMMEND_ARTICLE = API_HOST + "/v2/api/exams/reports/actions/get_recommend_articles?child_exam_id={child_exam_id}&relation_id={relation_id}&relation_type={relation_type}&compare_id={compare_id}";

    //获取话题的历史报告
    public static String URL_HISTORY_REPORT = API_HOST + "/v2/exams/{topic_id}/history?child_id={child_id}";

    //获取孩子的量表对象（某个话题下的量表对象，其中嵌套孩子量表）
    public static String URL_CHILD_DIMENSION = API_HOST + "/v2/api/children/{children}/topics/{topics}/dimensions/{dimensions}";

    //获取任务列表（专题海报）
    public static String URL_TASK_LIST = API_HOST + "/v2/api/children/{child_id}/seminars";


    /*----------------文章相关------------------*/

    //获取文章列表
    //?page=1&size=15&filter=&category_id=
    public static final String URL_ARTICLES = API_HOST + "/v2/api/articles";

    //热门文章
    public static final String URL_HOT_ARTICLES = API_HOST + "/v2/api/articles/top?page={page}&size={size}";

    //文章详情
    public static final String URL_ARTICLE_DETAIL = API_HOST + "/v2/api/articles/{articleId}";

    //文章评论列表
    public static final String URL_ARTICLE_COMMENT = API_HOST + "/v2/api/ref_comments/{id}/comments/{type}?page={page}&size={size}";

    //收藏
    public static final String URL_FAVORITE = API_HOST + "/v2/api/articles/{articleId}/actions/favorite";

    //点赞
    public static final String URL_LIKE = API_HOST + "/v2/api/articles/{articleId}/actions/like";

    //评论
    public static final String URL_DO_COMMENT = API_HOST + "/v2/api/ref_comments/{articleId}/actions/comment";

    //获取分类
    public static final String URL_CATEGORIES = API_HOST + "/v2/api/categories";

    //获取视频真实地址
    public static final String URL_VIDEO_REAL_URL = API_HOST + "/v2/api/videos/{video_id}/actions/get_display_url?sign={sign}&t={t}";


    /*----------------模块任务相关------------------*/

    //获取模块
    public static final String URL_MODULES = API_HOST + "/v2/api/children/{child_id}/child_modules";
    //获取测评任务列表（child_module_id）
    public static final String URL_EXAM_TASKS = API_HOST + "/v2/api/children/{child_id}/child_tasks";
    //获取测评任务详情子项列表（child_task_id）
    public static final String URL_EXAM_TASK_DETAIL_ITEMS = API_HOST + "/v2/api/children/{child_id}/child_task_items";
    //获取话题下的量表列表（child_exam_id、topic_id）
    public static final String URL_DIMENSIONS_IN_TOPIC = API_HOST + "/v2/api/children/{child_id}/child_dimensions";
    //获取任务状态
    public static final String URL_TASK_STATUS = API_HOST + "/v2/api/children/{child_id}/child_task_status?child_task_id={child_task_id}";
    //动作完成
    public static final String URL_ACTION_COMPLETE = API_HOST + "/v2/api/children/{child_id}/complete_item";


    /*----------------院校库相关------------------*/

    //获取院校的省份
    public static final String URL_COLLEGE_PROVINCE = API_HOST + "/v2/api/sy/regions";
    //获取院校的学历层级
    public static final String URL_COLLEGE_EDUCATION_LEVEL = API_HOST + "/v2/api/sy/degrees";
    //获取院校的院校类型
    public static final String URL_COLLEGE_CATEGORY = API_HOST + "/v2/api/sy/basic_categories";
    //获取院校排名列表
    public static final String URL_COLLEGE_RANK_LIST = API_HOST + "/v2/api/sy/universities";
    //获取院校的详情信息
    public static final String URL_COLLEGE_DETAIL_INFO = API_HOST + "/v2/api/sy/universities/{university_id}/general_situation";
    //获取院校的招生基本信息
    public static final String URL_COLLEGE_ENROLL_BASE_INFO = API_HOST + "/v2/api/sy/universities/{universities_id}/enrollment_infos";
    //获取专业树（edu_level、major_name）
    public static final String URL_MAJOR_TREE = API_HOST + "/v2/api/careers/majors";
    //专业详情
    public static final String URL_MAJOR_DETAIL = API_HOST + "/v2/api/careers/majors/{major_code}";
    //获取行业树（occupation_name）
    public static final String URL_OCCUPATION_TREE = API_HOST + "/v2/api/careers/occupations";
    //行业详情
    public static final String URL_OCCUPATION_DETAIL = API_HOST + "/v2/api/careers/occupations/{occupation_id}";
    //获取录取分数的文理科
    public static final String URL_COLLEGE_ENROLL_KIND = API_HOST + "/v2/api/sy/gaokao/provinces/{province}/kinds";
    //获取院校历年录取数据
    public static final String URL_COLLEGE_ENROLL = API_HOST + "/v2/api/sy/gaokao/score_line_university";
    //获取专业历年录取数据
    public static final String URL_MAJOR_ENROLL = API_HOST + "/v2/api/sy/gaokao/score_line_majors";
    //获取院校的毕业信息
    public static final String URL_COLLEGE_GRADUATION_INFO = API_HOST + "/v2/api/sy/universities/{university_id}/graduation_info";


}
