package com.cheersmind.cheersgenie.features.modules.mine.activity;

import android.view.View;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.entity.UserInfo;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.module.login.UCManager;

import java.util.Map;

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

        emptyLayout.setOnLayoutClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                getUserInfo();
            }
        });
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

        DataRequestService.getInstance().getUserInfoV2(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
//                onFailureDefault(e);
                //空布局提示：网络错误
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                //空布局提示：隐藏
                emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    UserInfo userInfo = InjectionWrapperUtil.injectMap(dataMap, UserInfo.class);
                    tvUserName.setText(userInfo.getUserName());
                    tvGender.setText(userInfo.getSex() == 1 ? "男": "女");
                    //设置用户信息
                    UCManager.getInstance().setUserInfo(userInfo);

                } catch (Exception e) {
                    e.printStackTrace();
                    //空布局提示：无数据，可重试
                    emptyLayout.setErrorType(XEmptyLayout.NODATA_ENABLE_CLICK);
                }
            }
        });
    }

}
