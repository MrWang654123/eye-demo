package com.cheersmind.cheersgenie.features.modules.mine.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.UserInfo;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.ChildInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.module.login.UCManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;

/**
 * 用户信息页面
 */
public class UserInfoActivity extends BaseActivity {

    @BindView(R.id.ll_childInfo_tip)
    LinearLayout llChildInfoTip;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;
    @BindView(R.id.tv_school)
    TextView tvSchool;
    @BindView(R.id.tv_period)
    TextView tvPeriod;
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
        ChildInfoEntity defaultChild = UCManager.getInstance().getDefaultChild();
        refreshUserInfoView(defaultChild);

        //获取用户信息
//        getUserInfo();
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
                    //设置用户信息
                    UCManager.getInstance().setUserInfo(userInfo);
                    //姓名
                    tvUserName.setText(userInfo.getUserName());
                    //性别
                    tvGender.setText(userInfo.getSex() == 1 ? "男": "女");

                    ChildInfoEntity defaultChild = UCManager.getInstance().getDefaultChild();
                    //出生年月
                    String dateStr = defaultChild.getBirthDay();//ISO8601 时间字符串
                    SimpleDateFormat formatIso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                    try {
                        Date date = formatIso8601.parse(dateStr);
                        SimpleDateFormat formatNormal = new SimpleDateFormat("yyyy-MM-dd");
                        String normalDateStr = formatNormal.format(date);
                        tvBirthday.setText(normalDateStr);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //学校
                    tvSchool.setText(defaultChild.getSchoolName());
                    //学段
                    tvPeriod.setText(defaultChild.getPeriod());
                    //年级
                    tvGrade.setText(defaultChild.getGrade());
                    //班级
                    tvClass.setText(defaultChild.getClassName());

                    //角色
                    int parentRole = defaultChild.getParentRole();
                    //非自己
                    if (parentRole != Dictionary.PARENT_ROLE_MYSELF) {
                        llChildInfoTip.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //空布局提示：无数据，可重试
                    emptyLayout.setErrorType(XEmptyLayout.NODATA_ENABLE_CLICK);
                }
            }
        });
    }

    /**
     * 刷新用户信息视图
     * @param childInfo
     */
    private void refreshUserInfoView(ChildInfoEntity childInfo) {
        //姓名
        tvUserName.setText(childInfo.getChildName());
        //性别
        tvGender.setText(childInfo.getSex() == 1 ? "男": "女");

        ChildInfoEntity defaultChild = UCManager.getInstance().getDefaultChild();
        //出生年月
        String dateStr = defaultChild.getBirthDay();//ISO8601 时间字符串
        SimpleDateFormat formatIso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            Date date = formatIso8601.parse(dateStr);
            SimpleDateFormat formatNormal = new SimpleDateFormat("yyyy-MM-dd");
            String normalDateStr = formatNormal.format(date);
            tvBirthday.setText(normalDateStr);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //学校
        tvSchool.setText(defaultChild.getSchoolName());
        //学段
        tvPeriod.setText(defaultChild.getPeriod());
        //年级
        tvGrade.setText(defaultChild.getGrade());
        //班级
        tvClass.setText(defaultChild.getClassName());

        //角色
        int parentRole = defaultChild.getParentRole();
        //非自己
        if (parentRole != Dictionary.PARENT_ROLE_MYSELF) {
            llChildInfoTip.setVisibility(View.VISIBLE);
        }
    }

}
