package com.cheersmind.cheersgenie.features_v2.modules.base.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features_v2.modules.exam.fragment.ExamModuleFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 测评包裹页面
 */
public class ExamWrapFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //标题
    @BindView(R.id.tv_toolbar_title)
    protected @Nullable
    TextView tvToolbarTitle;
    //左侧按钮
    @BindView(R.id.iv_left)
    @Nullable
    ImageView ivLeft;

    //切换列表布局按钮
    @BindView(R.id.iv_switch_layout)
    ImageView ivSwitchLayout;

    //显示搜索布局的提示图标
    @BindView(R.id.iv_search_tip)
    ImageView ivSearchTip;
    //隐藏搜索布局的提示文字
    @BindView(R.id.tv_cancel)
    TextView tvCancel;


    @Override
    protected int setContentView() {
        return R.layout.fragment_exam_wrap;
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

        //初始隐藏布局切换按钮
        ivSwitchLayout.setVisibility(View.GONE);

        //隐藏搜索按钮
        ivSearchTip.setVisibility(View.GONE);
        tvCancel.setVisibility(View.GONE);
    }

    @Override
    protected void lazyLoad() {
        FragmentManager childFragmentManager = getChildFragmentManager();
        String tag = ExamModuleFragment.class.getSimpleName();
        Fragment fragmentByTag = childFragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //测评
            ExamModuleFragment fragment = new ExamModuleFragment();
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

