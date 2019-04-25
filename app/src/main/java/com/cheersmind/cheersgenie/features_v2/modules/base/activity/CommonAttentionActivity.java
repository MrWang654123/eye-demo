package com.cheersmind.cheersgenie.features_v2.modules.base.activity;


import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.dto.AttentionDto;
import com.cheersmind.cheersgenie.features_v2.entity.AttentionEntity;
import com.cheersmind.cheersgenie.features_v2.interfaces.AttentionBtnCtrlListener;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 通用关注页面
 */
@SuppressLint("Registered")
public class CommonAttentionActivity extends BaseActivity implements AttentionBtnCtrlListener {

    @BindView(R.id.btn_attention)
    Button mBtnAttention;

    protected AttentionDto dto;
    //是否关注
    private boolean isAttention;

    @Override
    protected int setContentView() {
        return R.layout.activity_common_attention_titlebar;
    }

    @Override
    protected String settingTitle() {
        return "";
    }


    @Override
    protected void onInitView() {
        //修改状态栏颜色
//        setStatusBarBackgroundColor(CommonAttentionActivity.this, getResources().getColor(R.color.white));

        //初始隐藏关注按钮
        mBtnAttention.setVisibility(View.GONE);
    }

    @Override
    protected void onInitData() {
        dto = new AttentionDto();
    }

    @OnClick(R.id.btn_attention)
    public void onViewClicked() {
        if (isAttention) {
            dto.setFollow(false);
        } else {
            dto.setFollow(true);
        }

        doAttention();
    }

    /**
     * 关注操作
     */
    private void doAttention() {
        //通信加载等待
        LoadingView.getInstance().show(this, httpTag);
        DataRequestService.getInstance().postDoAttention(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //关闭通信加载等待
                    LoadingView.getInstance().dismiss();

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    AttentionEntity attentionEntity = InjectionWrapperUtil.injectMap(dataMap, AttentionEntity.class);

                    //刷新关注视图
                    refreshAttentionView(attentionEntity.isIs_follow());

                } catch (Exception e) {
                    onFailure(new QSCustomException(getString(R.string.operate_fail)));
                }


            }
        }, httpTag, CommonAttentionActivity.this);
    }

    @Override
    public void ctrlStatus(boolean hasAttention) {
        mBtnAttention.setVisibility(View.VISIBLE);
        //刷新关注视图
        refreshAttentionView(hasAttention);
    }

    /**
     * 刷新关注视图
     * @param hasAttention 是否关注
     */
    private void refreshAttentionView(boolean hasAttention) {
        if (hasAttention) {
            mBtnAttention.setText("已关注");
            mBtnAttention.setBackgroundResource(R.drawable.shape_white_attention);
            mBtnAttention.setTextColor(getResources().getColor(R.color.color_444444));
            isAttention = true;

        } else {
            mBtnAttention.setText("关注");
            mBtnAttention.setBackgroundResource(R.drawable.shape_accent_attention);
            mBtnAttention.setTextColor(getResources().getColor(R.color.white));
            isAttention = false;
        }
    }

}
