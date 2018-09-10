package com.cheersmind.cheersgenie.features.modules.mine.activity;

import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.WXUserInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;
import com.cheersmind.cheersgenie.module.login.UserService;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import butterknife.BindView;

/**
 * 用户信息页面
 */
public class UserInfoActivity extends BaseActivity {

    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.tv_school)
    TextView tvSchool;
    @BindView(R.id.tv_level)
    TextView tvLevel;
    @BindView(R.id.tv_grade)
    TextView tvGrade;
    @BindView(R.id.tv_class)
    TextView tvClass;
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    @Override
    protected int setContentView() {
        return R.layout.activity_user_info;
    }

    @Override
    protected String settingTitle() {
        return "我的资料";
    }

    @Override
    protected void onInitView() {

    }

    @Override
    protected void onInitData() {
        getUserInfo();
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo () {
        //空布局提示：正在加载
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        DataRequestService.getInstance().getUserInfo(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
                //空布局提示：网络错误
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                //空布局提示：隐藏
                emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);
//                Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
//                UserInfoEntity childData = InjectionWrapperUtil.injectMap(dataMap, UserInfoEntity.class);
                try {
                    JSONObject objData = (JSONObject) obj;
                    try {
                        JSONObject obje = objData.getJSONObject("org_exinfo");
                        String realName = obje.getString("real_name");
                        UCManager.getInstance().setRealName(realName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    WXUserInfoEntity wxUserInfoEntity = new WXUserInfoEntity();
                    wxUserInfoEntity.setAccessToken(UCManager.getInstance().getAcccessToken());
                    wxUserInfoEntity.setUserId(UCManager.getInstance().getUserId());
                    wxUserInfoEntity.setMacKey(UCManager.getInstance().getMacKey());
                    wxUserInfoEntity.setRefreshToken(UCManager.getInstance().getRefreshToken());
                    wxUserInfoEntity.setSysTimeMill(System.currentTimeMillis());
                    DataSupport.deleteAll(WXUserInfoEntity.class);
                    wxUserInfoEntity.save();

                } catch (Exception e) {
                    e.printStackTrace();
                    //空布局提示：无数据，可重试
                    emptyLayout.setErrorType(XEmptyLayout.NODATA_ENABLE_CLICK);
                }
            }
        });
    }

}
