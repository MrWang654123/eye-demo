package com.cheersmind.cheersgenie.features.modules.base.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.event.MessageReadEvent;
import com.cheersmind.cheersgenie.features.modules.base.activity.MasterTabActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.MineFavoriteActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.MineIntegralActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.MineMessageActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.UserInfoActivity;
import com.cheersmind.cheersgenie.features.modules.mine.activity.XSettingActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.mine.SettingActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

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

    @BindView(R.id.tv_new_message_count)
    TextView tvNewMessageCount;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_mine_x;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //初始隐藏最新消息条数
        tvNewMessageCount.setVisibility(View.GONE);

        //获取最新消息条数
        queryNewMessageCount();
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
        queryDailySignInStatus();

        //如果最新消息数量为隐藏，则再获取一次
        if (tvNewMessageCount.getVisibility() == View.VISIBLE) {
            //获取最新消息条数
            queryNewMessageCount();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
//    @Subscribe
    public void onLastExamNotice(MessageReadEvent event) {
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
        LoadingView.getInstance().show(getActivity());

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
                    if (isSignIn) {
                        btnSignIn.setText("已签到");
                        btnSignIn.setEnabled(false);
                    } else {
                        btnSignIn.setText("签到");
                        btnSignIn.setEnabled(true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    btnSignIn.setText("签到");
                    btnSignIn.setEnabled(true);
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
            R.id.ll_mine_report, R.id.ll_feedback, R.id.ll_setting, R.id.tv_user_info,R.id.btn_sign_in})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //我的资料
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
                ((MasterTabActivity)getActivity()).switchToReportPage();
                break;
            }
            //反馈
            case R.id. ll_feedback: {
                FeedbackAPI.openFeedbackActivity();
                break;
            }
            //我的设置
            case R.id. ll_setting: {
                startActivity(new Intent(getActivity(),XSettingActivity.class));
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

                btnSignIn.setText("已签到");
                btnSignIn.setEnabled(false);
            }
        });
    }
}


