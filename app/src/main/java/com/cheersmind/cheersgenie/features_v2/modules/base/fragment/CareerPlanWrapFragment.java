package com.cheersmind.cheersgenie.features_v2.modules.base.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.CareerPlanFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 生涯规划包裹页面
 */
public class CareerPlanWrapFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //标题
    @BindView(R.id.tv_toolbar_title)
    protected @Nullable
    TextView tvToolbarTitle;
    //左侧按钮
    @BindView(R.id.iv_left)
    @Nullable
    ImageView ivLeft;

    //搜索图标
    @BindView(R.id.iv_search_tip)
    ImageView ivSearchTip;

    @Override
    protected int setContentView() {
        return R.layout.fragment_career_plan_wrap;
    }

    @Override
    protected void onInitView(View contentView) {
        unbinder = ButterKnife.bind(this, contentView);

        //标题
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText(R.string.title_exam);
        }
        //隐藏回退按钮
        if (ivLeft != null) {
            ivLeft.setVisibility(View.GONE);
        }

        //隐藏搜索按钮
        ivSearchTip.setVisibility(View.GONE);
    }

    @Override
    protected void lazyLoad() {
        FragmentManager childFragmentManager = getChildFragmentManager();
        String tag = CareerPlanFragment.class.getSimpleName();
        Fragment fragmentByTag = childFragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //生涯
            CareerPlanFragment fragment = new CareerPlanFragment();
            //添加已完成的测评fragment到容器中
            childFragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}

