package com.cheersmind.cheersgenie.features.modules.base.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.interfaces.ExamLayoutListener;
import com.cheersmind.cheersgenie.features.interfaces.SearchLayoutControlListener;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.ExamFragment;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 测评包裹页面
 */
public class ExamWrapFragment extends LazyLoadFragment implements ExamLayoutListener {

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

    }

    @Override
    protected void lazyLoad() {
        FragmentManager childFragmentManager = getChildFragmentManager();
        String tag = ExamFragment.class.getSimpleName();
        Fragment fragmentByTag = childFragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //测评
            ExamFragment fragment = new ExamFragment();
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

    @OnClick({R.id.iv_switch_layout,R.id.iv_search_tip,R.id.tv_cancel})
    public void onViewClick(View view) {
        FragmentManager childFragmentManager = getChildFragmentManager();
        String tag = ExamFragment.class.getSimpleName();
        Fragment fragmentByTag = childFragmentManager.findFragmentByTag(tag);

        switch (view.getId()) {
            //切换列表布局
            case R.id.iv_switch_layout:{
                //当前是网格，切换成线性，显示网格图标
                if (layoutType == Dictionary.EXAM_LIST_LAYOUT_TYPE_GRID) {
                    ivSwitchLayout.setImageResource(R.drawable.ic_layout_grid_black_30dp);
                    layoutType = Dictionary.EXAM_LIST_LAYOUT_TYPE_LINEAR;

                } else if (layoutType == Dictionary.EXAM_LIST_LAYOUT_TYPE_LINEAR) {//当前是线性，切换成网格，显示线性图标
                    ivSwitchLayout.setImageResource(R.drawable.ic_layout_list_black_30dp);
                    layoutType = Dictionary.EXAM_LIST_LAYOUT_TYPE_GRID;
                }

                //非空
                if (fragmentByTag != null) {
                    //调用切换布局
                    ((ExamFragment)fragmentByTag).switchLayout(layoutType);
                }
                break;
            }
            //显示搜索布局的提示图标
            case R.id.iv_search_tip: {
                showSearchLayout();
                //显示搜索布局
                if (fragmentByTag != null) {
                    if (fragmentByTag instanceof SearchLayoutControlListener) {
                        ((SearchLayoutControlListener) fragmentByTag).searchLayoutControl(true);
                    }
                }
                break;
            }
            //隐藏搜索布局的提示文字
            case R.id.tv_cancel:{
                hideSearchLayout();
                //隐藏搜索布局
                if (fragmentByTag != null) {
                    if (fragmentByTag instanceof SearchLayoutControlListener) {
                        ((SearchLayoutControlListener) fragmentByTag).searchLayoutControl(false);
                    }
                }
                break;
            }
        }
    }


    /**
     * 显示搜索布局
     */
    private void showSearchLayout() {
        ivSearchTip.setVisibility(View.GONE);
        tvCancel.setVisibility(View.VISIBLE);
    }


    /**
     * 隐藏搜索布局
     */
    private void hideSearchLayout() {
        ivSearchTip.setVisibility(View.VISIBLE);
        tvCancel.setVisibility(View.GONE);
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

        } else if (layoutType == Dictionary.EXAM_LIST_LAYOUT_TYPE_LINEAR) {
            ivSwitchLayout.setImageResource(R.drawable.ic_layout_grid_black_30dp);
        }

    }


    /**
     * 视图是否已经对用户可见，系统的方法
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (!isVisibleToUser && hasLoaded) {
            FragmentManager childFragmentManager = getChildFragmentManager();
            String tag = ExamFragment.class.getSimpleName();
            Fragment fragmentByTag = childFragmentManager.findFragmentByTag(tag);

            if (fragmentByTag instanceof SearchLayoutControlListener) {
                ((SearchLayoutControlListener) fragmentByTag).hideSoftInputAndOverlay();
            }
        }
    }

}

