package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.View;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabFragmentPagerAdapter;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 测评任务详情
 */
public class ExamTaskDetailFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //测评任务
    private ExamTaskEntity examTask;

    @BindView(R.id.iv_main)
    SimpleDraweeView ivMain;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.appbar_layout)
    AppBarLayout appBarLayout;

    @Override
    protected int setContentView() {
        return R.layout.fragment_exam_task_detail;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            examTask = (ExamTaskEntity) bundle.getSerializable(DtoKey.EXAM_TASK);
        }

        if (examTask != null) {
            //主图
            ivMain.setImageURI(examTask.getTask_icon());

            if (examTask.getChildTask() != null) {
                List<Pair<String, Fragment>> items = new ArrayList<>();

                ExamTaskItemFragment fragment1 = new ExamTaskItemFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putString(DtoKey.CHILD_TASK_ID, examTask.getChildTask().getChild_task_id());
                bundle1.putInt(DtoKey.TASK_STATUS, examTask.getChildTask().getStatus());
                bundle1.putString(DtoKey.TASK_DESC, examTask.getDescription());
                fragment1.setArguments(bundle1);

                ExamTaskCommentFragment fragment2 = new ExamTaskCommentFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putString(DtoKey.TASK_ID, examTask.getTask_id());
                fragment2.setArguments(bundle2);

                items.add(new Pair<String, Fragment>("内容", fragment1));
                items.add(new Pair<String, Fragment>("留言", fragment2));
                viewPager.setAdapter(new TabFragmentPagerAdapter(getChildFragmentManager(), items));
                //标签绑定viewpager
                tabs.setupWithViewPager(viewPager);
            }
        }

        //监听 AppBarLayout Offset 变化，动态设置 SwipeRefreshLayout 是否可用
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset >= 0) {
                    //发送停止Fling的事件
                    EventBus.getDefault().post(new StopFlingEvent());
                }

            }
        });
    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}

