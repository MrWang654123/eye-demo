package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features_v2.event.AddObserveMajorEvent;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ChooseCourseActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ObserveMajorActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.SystemRecommendCourseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 选科助手
 */
public class SelectCourseAssistantFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //孩子测评ID
    private String childExamId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_select_course_assistant;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            childExamId = bundle.getString(DtoKey.CHILD_EXAM_ID);
        }

    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销事件
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.cl_system_recommend, R.id.cl_observe_major, R.id.cl_select_course})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //系统推荐
            case R.id.cl_system_recommend: {
                SystemRecommendCourseActivity.startSystemRecommendCourseActivity(getContext(), childExamId);
                break;
            }
            //观察专业
            case R.id.cl_observe_major: {
                ObserveMajorActivity.startObserveMajorActivity(getContext(), childExamId);
                break;
            }
            //确认选科
            case R.id.cl_select_course: {
                ChooseCourseActivity.startChooseCourseActivity(getContext(), childExamId);
                break;
            }
        }
    }

    /**
     * 添加观察专业的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddObserveMajorNotice(AddObserveMajorEvent event) {

    }

}

