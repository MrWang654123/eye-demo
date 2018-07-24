package com.cheersmind.smartbrain.main.constant;

/**
 * Created by goodm on 2017/4/15.
 */
public class HttpConfig {

    //生产环境
//    public static final String API_HOST = "http://psytest-server.cheersmind.com";
//    public static final String WEB_HOST = "http://psytest-web.cheersmind.com";
//    public static final String UC_HOST  = "http://psytest-server.cheersmind.com";

    //开发环境
    public static String UC_HOST  = "http://psytest-server.test.cheersmind.qst";
    public static String API_HOST = "http://psytest-server.test.cheersmind.qst";
    public static String WEB_HOST = "http://psytest-web.test.101qisi.com";


    /**-----------------------------------------------------------------------
     *---------------------------用户信息相关-----------------------------------
     ---------------------------------------------------------------------------*/
    //邀请码验证
    public static final String URL_CODE_INVATE = API_HOST + "/v1/api/registers/actions/check_invite_code";
    //邀请码注册
    public static final String URL_CODE_REGISTERS = API_HOST + "/v1/api/registers";
    //登入uc
    public static final String URL_LOGIN = UC_HOST+"/v1/oauth2/tokens/actions/sign_in";
    public static final String URL_USER_INFO = UC_HOST+"/v1/oauth2/users";
    public static final String URL_CHILD_LIST = API_HOST+"/v1/api/users/children";
//    public static final String URL_CHILD_LIST = API_HOST+"/v1/api/children";
    public static final String URL_CHILD_INFO = API_HOST+"/v1/api/children/{child_id}";

    //uc第三方登入
    public static final String URL_UC_THIRD_LOGIN = UC_HOST + "/v1/oauth2/tokens/actions/third_sign_in";
    //注册
    public static final String URL_UC_REGISTER = API_HOST + "/v1/api/users/third/sign_in";

    //获取用户详情
    public static final String URL_USER_DETAILS = API_HOST + "/v1/api/users";

    //获取项目列表，带当前测评（exam_id）
    public static final String URL_PROJECTS_LIST = API_HOST + "/v1/api/children/{child_id}/projects";

    //获取首页推荐量表列表
    public static final String URL_RECOMMEND_MAIN = API_HOST + "/v1/api/children/{child_id}/exams/{exam_id}/recommend_dimensions?topic_id={topic_id}&page={page}&size={size}";

    //获取主题列表
    public static final String URL_TOPIC_LIST = API_HOST + "/v1/api/topics?page={page}&size={size}";
    //获取主题详情
    public static final String URL_TOPIC_INFO = API_HOST + "/v1/api/topics/{topic_id}";

    //获取量表列表
    public static final String URL_DIMENSION_LIST = API_HOST + "/v1/api/dimensions?topic_id={topic_id}&page={page}&size={size}";
    //获取量表详情
    public static final String URL_DIMENSION_INFO = API_HOST + "/v1/api/dimensions/{dimension_id}";

    //热门分量表列表
    public static final String URL_DIMENSION_HOTE_LIST = API_HOST + "/v1/api/children/{child_id}/exams/{exam_id}/dimensions/hot";

    //获取孩子最近一次使用量表
    public static final String URL_DIMENSION_LATEST = API_HOST + "/v1/api/children/{child_id}/latest_dimensions";

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
    public static final String URL_CHILD_TOPIC_LIST = API_HOST + "/v2/api/children/{child_id}/topics";

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


    //微信登入
    public static final String URL_WX_GET_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid={appid}&secret={secret}&code={code}&grant_type=authorization_code";

}
