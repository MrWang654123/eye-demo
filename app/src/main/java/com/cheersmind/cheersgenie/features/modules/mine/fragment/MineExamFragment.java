package com.cheersmind.cheersgenie.features.modules.mine.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.HistoryExamFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 我的测评
 */
public class MineExamFragment extends LazyLoadFragment {

    //ButterKnife解绑对象
    Unbinder unbinder;


    @Override
    protected int setContentView() {
        return R.layout.fragment_mine_exam;
    }

    @Override
    protected void onInitView(View contentView) {
        //ButterKnife绑定
        unbinder = ButterKnife.bind(this, contentView);

    }

    @Override
    protected void lazyLoad() {
        FragmentManager childFragmentManager = getChildFragmentManager();
        String tag = HistoryExamFragment.class.getSimpleName();
        Fragment fragmentByTag = childFragmentManager.findFragmentByTag(tag);
        //空则添加
        if (fragmentByTag == null) {
            //已完成的测评
            HistoryExamFragment fragment = new HistoryExamFragment();
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
