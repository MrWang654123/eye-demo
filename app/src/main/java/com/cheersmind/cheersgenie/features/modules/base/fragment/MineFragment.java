package com.cheersmind.cheersgenie.features.modules.base.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.UserInfo;
import com.cheersmind.cheersgenie.features.event.MessageReadEvent;
import com.cheersmind.cheersgenie.features.event.ModifyProfileEvent;
import com.cheersmind.cheersgenie.features.modules.base.activity.MasterTabActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.MineExamActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.MineFavoriteActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.MineIntegralActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.MineMessageActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.UserInfoActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.XSettingActivity;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.entity.ChildInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * “我的”页面（个人资料等）
 */
public class MineFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //签到
    @BindView(R.id.btn_sign_in)
    Button btnSignIn;

    //新消息数量
    @BindView(R.id.tv_new_message_count)
    TextView tvNewMessageCount;
    //用户名
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    //性别
    @BindView(R.id.tv_gender)
    TextView tvGender;
    //头像
    @BindView(R.id.iv_profile)
    ImageView ivProfile;
    //修改头像的提示图
    @BindView(R.id.tv_modify_profile_tip)
    ImageView tvModifyProfileTip;

    //默认Glide配置
    RequestOptions options;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);

        options = new RequestOptions()
                .circleCrop()//圆形
                .skipMemoryCache(true)//忽略内存
                .placeholder(R.drawable.ico_head)//占位图
                .dontAnimate()//Glide默认是渐变动画，设置dontAnimate()不要动画
                .diskCacheStrategy(DiskCacheStrategy.NONE);//磁盘缓存策略：不缓存
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_mine_x;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //初始隐藏个人信息
        tvUserName.setVisibility(View.GONE);
        tvGender.setVisibility(View.GONE);
        //初始隐藏最新消息条数
        tvNewMessageCount.setVisibility(View.GONE);

        //查询当前签到状态
        queryDailySignInStatus();

        //获取最新消息条数
        queryNewMessageCount();

        //获取用户个人资料
        if (UCManager.getInstance().getUserInfo() != null) {
            //刷新个人资料视图
            refreshPersonalInfo(UCManager.getInstance().getUserInfo());
        } else {
            //请求个人资料
            getUserInfo();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销事件
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void lazyLoad() {
        //查询当前签到状态
//        queryDailySignInStatus();

        //如果最新消息数量为隐藏，则再获取一次
        if (tvNewMessageCount.getVisibility() == View.VISIBLE) {
            //获取最新消息条数
            queryNewMessageCount();
        }

        //获取用户个人资料
        if (UCManager.getInstance().getUserInfo() == null) {
            //请求个人资料
            getUserInfo();
        }
    }


    /**
     * 消息被置为已读的通知
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
//    @Subscribe
    public void onMessageReadNotice(MessageReadEvent event) {
        if (event == null) {
            return;
        }

        //最新消息数量
        int newMessageCount = Integer.parseInt(tvNewMessageCount.getText().toString());
        if (newMessageCount > 0) {
            newMessageCount = newMessageCount - 1;
        }
        tvNewMessageCount.setText(String.valueOf(newMessageCount));
        //数量为0就隐藏
        if (newMessageCount == 0) {
            tvNewMessageCount.setVisibility(View.GONE);
        }

    }


    /**
     *
     * 修改头像的通知
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
//    @Subscribe
    public void onModifyProfileNotice(ModifyProfileEvent event) {
        if (event == null) {
            return;
        }

        //非空
        String profileUrl = event.getProfileUrl();
        if (!TextUtils.isEmpty(profileUrl)) {
            //重置本地缓存中的头像url
            if (UCManager.getInstance().getUserInfo() != null) {
                UCManager.getInstance().getUserInfo().setAvatar(profileUrl);
            }

            //刷新头像
            refreshProfile(profileUrl);
        }

    }


    /**
     * 获取最新消息条数
     */
    private void queryNewMessageCount() {
        DataRequestService.getInstance().getNewMessageCount(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    int messageCount = ((JSONObject) obj).getInt("total");
                    if (messageCount > 0) {
                        tvNewMessageCount.setVisibility(View.VISIBLE);
                        tvNewMessageCount.setText(String.valueOf(messageCount));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 查询签到状态
     */
    private void queryDailySignInStatus() {
//        LoadingView.getInstance().show(getActivity());

        DataRequestService.getInstance().getDailySignInStatus(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();

                try {
                    boolean isSignIn = ((JSONObject) obj).getBoolean("is_sign_in");
                    //设置签到状态
                    settingSignInStatus(isSignIn);

                } catch (Exception e) {
                    e.printStackTrace();
                    //设置签到状态：未签
                    settingSignInStatus(false);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.ll_mine_integral, R.id.ll_mine_message, R.id.ll_mine_collect,
            R.id.ll_mine_report, R.id.ll_feedback, R.id.ll_setting, R.id.tv_user_info, R.id.btn_sign_in, R.id.tv_user_name, R.id.iv_profile})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //我的资料
            case R.id.iv_profile:
            case R.id.tv_user_name:
            case R.id.tv_user_info: {
                Intent intent = new Intent(getContext(), UserInfoActivity.class);
                startActivity(intent);

                break;
            }
            //签到
            case R.id.btn_sign_in: {
                //签到
                doSignIn();
                break;
            }
            //我的积分
            case R.id.ll_mine_integral: {
                Intent intent = new Intent(getContext(), MineIntegralActivity.class);
                startActivity(intent);

                break;
            }
            //我的消息
            case R.id.ll_mine_message: {
                Intent intent = new Intent(getContext(), MineMessageActivity.class);
                startActivity(intent);
                break;
            }
            //我的收藏
            case R.id.ll_mine_collect: {
                Intent intent = new Intent(getContext(), MineFavoriteActivity.class);
                startActivity(intent);
                break;
            }
            //我的智评（目前就是报告页面）
            case R.id.ll_mine_report: {
                //切换到报告页面
//                ((MasterTabActivity) getActivity()).switchToReportPage();
                Intent intent = new Intent(getContext(), MineExamActivity.class);
                startActivity(intent);
                break;
            }
            //反馈
            case R.id.ll_feedback: {
                FeedbackAPI.openFeedbackActivity();
                break;
            }
            //我的设置
            case R.id.ll_setting: {
                startActivity(new Intent(getActivity(), XSettingActivity.class));
                break;
            }
        }
    }

    /**
     * 签到
     */
    private void doSignIn() {
        LoadingView.getInstance().show(getActivity());

        DataRequestService.getInstance().postDailySignIn(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();
                //设置签到状态：已签
                settingSignInStatus(true);
            }
        });
    }

    /**
     * 设置签到状态
     * @param has
     */
    private void settingSignInStatus(boolean has) {
        if (has) {
            btnSignIn.setText("已签到");
            btnSignIn.setEnabled(false);
            btnSignIn.setTextColor(Color.parseColor("#ffffff"));
        } else {
            btnSignIn.setText("签到");
            btnSignIn.setEnabled(true);
        }
    }


    /**
     * 获取用户信息
     */
    private void getUserInfo () {
        LoadingView.getInstance().show(getActivity());

        DataRequestService.getInstance().getUserInfoV2(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    UserInfo userInfo = InjectionWrapperUtil.injectMap(dataMap, UserInfo.class);
                    //设置用户信息
                    UCManager.getInstance().setUserInfo(userInfo);

                    //刷新个人资料视图
                    refreshPersonalInfo(userInfo);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 刷新个人资料视图
     * @param userInfo 用户信息
     */
    private void refreshPersonalInfo(UserInfo userInfo) {
        tvUserName.setVisibility(View.VISIBLE);
//        tvGender.setVisibility(View.VISIBLE);
        //姓名
        tvUserName.setText(userInfo.getUserName());
        //性别
        tvGender.setText(userInfo.getSex() == 1 ? "男": "女");

        //刷新头像
        refreshProfile(userInfo.getAvatar());
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

}


