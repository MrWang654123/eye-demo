package com.cheersmind.cheersgenie.features.modules.mine.fragment;


import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.UserInfo;
import com.cheersmind.cheersgenie.features.event.ModifyProfileEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.TakePhotoFragment;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.ChildInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;

import org.devio.takephoto.model.TResult;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 个人资料
 */
public class UserInfoFragment extends TakePhotoFragment {

    Unbinder unbinder;

    //孩子信息提示文本
    @BindView(R.id.tv_childInfo_tip)
    TextView tvChildInfoTip;

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
    //头像
    @BindView(R.id.iv_profile)
    ImageView ivProfile;
    //修改头像的提示图
    @BindView(R.id.tv_modify_profile_tip)
    ImageView tvModifyProfileTip;

    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //默认Glide配置
    RequestOptions options;


    @Override
    protected int setContentView() {
        return R.layout.fragment_user_info;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        options = new RequestOptions()
                .circleCrop()//圆形
                .skipMemoryCache(true)//忽略内存
                .placeholder(R.drawable.ico_head)//占位图
                .dontAnimate()//Glide默认是渐变动画，设置dontAnimate()不要动画
                .diskCacheStrategy(DiskCacheStrategy.NONE);//磁盘缓存策略：不缓存

        //用户信息
        if (UCManager.getInstance().getUserInfo() == null) {
            //获取用户信息
            getUserInfo();

        } else {
            ChildInfoEntity defaultChild = UCManager.getInstance().getDefaultChild();
            UserInfo userInfo = UCManager.getInstance().getUserInfo();
            //刷新用户信息视图
            refreshUserInfoView(userInfo, defaultChild);
        }

        emptyLayout.setOnLayoutClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                getUserInfo();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    protected void lazyLoad() {

    }

    @OnClick({R.id.iv_profile, R.id.btn_modify_profile})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //头像
            case R.id.iv_profile: {
                //弹出选择修改头像方式的对话框
                popupModifyProfileWindows(getContext());

                break;
            }

            //提交头像
            case R.id.btn_modify_profile: {
                if (file != null) {
                    doPostModifyProfile(file);
                }
                break;
            }
        }
    }

    private File file;


    /**
     * 提交头像
     */
    private void doPostModifyProfile(File file) {
        //通信等待提示
        LoadingView.getInstance().show(getContext());
        DataRequestService.getInstance().postModifyProfile(file, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                //关闭通信等待提示
                LoadingView.getInstance().dismiss();

                try {
                    //解析数据
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    //头像url
                    String profileUrl = (String) dataMap.get("file_path");

                    //图片
                    Glide.with(getContext())
                            .load(profileUrl)
//                .thumbnail(0.5f)
                            .apply(options)
                            .into(ivProfile);

                    //发送修改头像的通知
                    EventBus.getDefault().post(new ModifyProfileEvent(profileUrl));

                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(new QSCustomException(getResources().getString(R.string.operate_fail)));
                }
            }
        });
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
                    //孩子信息
                    ChildInfoEntity defaultChild = UCManager.getInstance().getDefaultChild();
                    //刷新用户信息视图
                    refreshUserInfoView(userInfo, defaultChild);

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
     * @param userInfo 用户信息
     * @param childInfo 孩子信息
     */
    private void refreshUserInfoView(UserInfo userInfo, ChildInfoEntity childInfo) {

        if (userInfo != null) {
            //刷新头像
            refreshProfile(userInfo.getAvatar());
        }

        String userName = "";//用户名
        int gender = 1;//1：男，2：女

        //角色
        int parentRole = childInfo.getParentRole();
        //非自己
        if (parentRole != Dictionary.PARENT_ROLE_MYSELF) {
            //提示是孩子信息
            tvChildInfoTip.setVisibility(View.VISIBLE);
            userName = childInfo.getChildName();
            gender = childInfo.getSex();

        } else {//是自己
            //隐藏提示是孩子信息
            tvChildInfoTip.setVisibility(View.GONE);
            userName = userInfo.getUserName();
            gender = userInfo.getSex();
        }

        //姓名
        tvUserName.setText(userName);
        //性别
        tvGender.setText(gender == 1 ? "男": "女");

        //出生年月
        String dateStr = childInfo.getBirthDay();//ISO8601 时间字符串
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
        tvSchool.setText(childInfo.getSchoolName());
        //学段
        tvPeriod.setText(childInfo.getPeriod());
        //年级
        tvGrade.setText(childInfo.getGrade());
        //班级
        tvClass.setText(childInfo.getClass_name());

    }


    /**
     * 刷新头像
     * @param profileUrl
     */
    private void refreshProfile(String profileUrl) {
        if (TextUtils.isEmpty(profileUrl)) {
            return;
        }

        //图片
        Glide.with(this)
                .load(profileUrl)
//                .thumbnail(0.5f)
                .apply(options)
                .into(ivProfile);

        //隐藏修改头像提示图
        tvModifyProfileTip.setVisibility(View.GONE);
    }


    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);

//        file = new File(result.getImages().get(0).getCompressPath());
//        Glide.with(this).load(file).into(ivProfile);
        //上传头像
        doPostModifyProfile(new File(result.getImages().get(0).getCompressPath()));
    }


}
