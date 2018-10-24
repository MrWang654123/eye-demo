package com.cheersmind.cheersgenie.features.modules.base.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.interfaces.ExamLayoutListener;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.ExamCompletedFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 报告主页面
 */
public class ReportFragment extends LazyLoadFragment implements ExamLayoutListener {

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


    @Override
    protected int setContentView() {
        return R.layout.fragment_report;
    }

    @Override
    protected void onInitView(View contentView) {
        unbinder = ButterKnife.bind(this, contentView);

        //标题
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText(R.string.title_report);
        }
        //隐藏回退按钮
        if (ivLeft != null) {
            ivLeft.setVisibility(View.GONE);
        }

        //初始隐藏布局切换按钮
        ivSwitchLayout.setVisibility(View.GONE);
    }

    @Override
    protected void lazyLoad() {
        FragmentManager childFragmentManager = getChildFragmentManager();
        String tag = ExamCompletedFragment.class.getSimpleName();
        Fragment fragmentByTag = childFragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //已完成的测评
            ExamCompletedFragment fragment = new ExamCompletedFragment();
            //添加已完成的测评fragment到容器中
            childFragmentManager.beginTransaction().add(R.id.fl_fragment, fragment, tag).commit();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //布局类型
    int layoutType = Dictionary.EXAM_LIST_LAYOUT_TYPE_GRID;

    @OnClick({R.id.iv_switch_layout})
    public void onViewClick(View view) {
        switch (view.getId()) {
            //切换列表布局
            case R.id.iv_switch_layout:{
                //当前是网格，切换成线性，显示网格图标
                if (layoutType == Dictionary.EXAM_LIST_LAYOUT_TYPE_GRID) {
                    ivSwitchLayout.setImageResource(R.drawable.ic_layout_list_black_30dp);
                    ivSwitchLayout.setTag(Dictionary.EXAM_LIST_LAYOUT_TYPE_GRID);
                    layoutType = Dictionary.EXAM_LIST_LAYOUT_TYPE_LINEAR;

                } else if (layoutType == Dictionary.EXAM_LIST_LAYOUT_TYPE_LINEAR) {//当前是线性，切换成网格，显示线性图标
                    ivSwitchLayout.setImageResource(R.drawable.ic_layout_grid_black_30dp);
                    ivSwitchLayout.setTag(Dictionary.EXAM_LIST_LAYOUT_TYPE_LINEAR);
                    layoutType = Dictionary.EXAM_LIST_LAYOUT_TYPE_GRID;
                }

                //切换布局
                FragmentManager childFragmentManager = getChildFragmentManager();
                String tag = ExamCompletedFragment.class.getSimpleName();
                Fragment fragmentByTag = childFragmentManager.findFragmentByTag(tag);
                //非空
                if (fragmentByTag != null) {
                    //调用切换布局
                    ((ExamCompletedFragment)fragmentByTag).switchLayout(layoutType);
                }
                break;
            }
        }
    }


    @Override
    public void change(int layoutType, boolean isShow) {
        if (isShow) {
            ivSwitchLayout.setVisibility(View.VISIBLE);
        } else {
            ivSwitchLayout.setVisibility(View.GONE);
        }

        this.layoutType = layoutType;

        if (layoutType == Dictionary.EXAM_LIST_LAYOUT_TYPE_GRID) {
            ivSwitchLayout.setImageResource(R.drawable.ic_layout_list_black_30dp);
            ivSwitchLayout.setTag(Dictionary.EXAM_LIST_LAYOUT_TYPE_GRID);

        } else if (layoutType == Dictionary.EXAM_LIST_LAYOUT_TYPE_LINEAR) {
            ivSwitchLayout.setImageResource(R.drawable.ic_layout_grid_black_30dp);
            ivSwitchLayout.setTag(Dictionary.EXAM_LIST_LAYOUT_TYPE_LINEAR);
        }

    }


}
