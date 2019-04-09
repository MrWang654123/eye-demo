package com.cheersmind.cheersgenie.features_v2.modules.college.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.dto.AttentionEntity;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEntity;
import com.cheersmind.cheersgenie.features_v2.interfaces.AttentionBtnCtrlListener;
import com.cheersmind.cheersgenie.features_v2.modules.college.fragment.CollegeDetailFragment;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 院校详情
 */
public class CollegeDetailActivity extends BaseActivity implements AttentionBtnCtrlListener {

    @BindView(R.id.btn_attention)
    Button mBtnAttention;

    private AttentionEntity dto;
    private boolean IsAttention = false;

    /**
     * 启动院校详情页面
     *
     * @param context 上下文
     * @param college 院校
     */
    public static void startCollegeDetailActivity(Context context, CollegeEntity college) {
        Intent intent = new Intent(context, CollegeDetailActivity.class);
        intent.putExtra(DtoKey.COLLEGE, college);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_common_attention_titlebar;
    }

    @Override
    protected String settingTitle() {
        return null;
    }


    @Override
    protected void onInitView() {
        //修改状态栏颜色
        setStatusBarBackgroundColor(CollegeDetailActivity.this, getResources().getColor(R.color.white));
        dto = new AttentionEntity();
        boolean is_follow = dto.isIs_follow();
        if (is_follow) {
            mBtnAttention.setEnabled(false);
            mBtnAttention.setText("已关注");
        } else {
            mBtnAttention.setEnabled(true);
            mBtnAttention.setText("关注");
        }
    }

    @Override
    protected void onInitData() {
        //院校
        CollegeEntity college = (CollegeEntity) getIntent().getSerializableExtra(DtoKey.COLLEGE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = CollegeDetailFragment.class.getSimpleName();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //院校详情
            CollegeDetailFragment fragment = new CollegeDetailFragment();
            //添加初始数据
            Bundle bundle = new Bundle();
            bundle.putSerializable(DtoKey.COLLEGE, college);
            fragment.setArguments(bundle);
            //添加fragment到容器中
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }
    }

    @OnClick(R.id.btn_attention)
    public void onClick() {
        if (IsAttention) {
            mBtnAttention.setEnabled(true);
            mBtnAttention.setText("关注");
            IsAttention = false;
        } else {
            mBtnAttention.setEnabled(false);
            mBtnAttention.setText("已关注");
            IsAttention = true;
        }
        doAttention();
    }

    /**
     * 关注操作
     */
    private void doAttention() {

        DataRequestService.getInstance().postDoAttention("i7eIjUJKKUMG",
                0, "0", IsAttention, new BaseService.ServiceCallback() {
                    @Override
                    public void onFailure(QSCustomException e) {
                        onFailureDefault(e);
                    }

                    @Override
                    public void onResponse(Object obj) {
                        try {
                            Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                            AttentionEntity attentionEntity = InjectionWrapperUtil.injectMap(dataMap, AttentionEntity.class);
                            ToastUtil.showShort(getApplication(), "关注成功");

                        } catch (Exception e) {
                            ToastUtil.showShort(getApplication(), "关注失败");
                        }


                    }
                }, httpTag, CollegeDetailActivity.this);
    }


    @Override
    public void ctrlStatus(boolean hasAttention) {
        mBtnAttention.setVisibility(View.VISIBLE);

        if (hasAttention) {
            mBtnAttention.setEnabled(false);
            mBtnAttention.setText("已关注");
            IsAttention = true;

        } else {
            mBtnAttention.setEnabled(true);
            mBtnAttention.setText("关注");
            IsAttention = false;
        }
    }
}
