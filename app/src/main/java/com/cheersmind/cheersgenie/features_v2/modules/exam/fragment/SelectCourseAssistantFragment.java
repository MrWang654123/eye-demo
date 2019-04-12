package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.dto.ChildDto;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features_v2.entity.RecommendMajor;
import com.cheersmind.cheersgenie.features_v2.entity.RecommendMajorRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.SysRmdCourse;
import com.cheersmind.cheersgenie.features_v2.event.AddObserveMajorSuccessEvent;
import com.cheersmind.cheersgenie.features_v2.event.SysRecommendCompleteEvent;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ChooseCourseActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ObserveMajorActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.SystemRecommendCourseActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.module.login.UCManager;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 选科助手
 */
public class SelectCourseAssistantFragment extends LazyLoadFragment {

    Unbinder unbinder;

    @BindView(R.id.avl_observe_major)
    AVLoadingIndicatorView avlObserveMajor;
    @BindView(R.id.rl_observe_major_mask)
    RelativeLayout rlObserveMajorMask;

    @BindView(R.id.avl_select_course)
    AVLoadingIndicatorView avlSelectCourse;
    @BindView(R.id.rl_select_course_mask)
    RelativeLayout rlSelectCourseMask;

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

        rlObserveMajorMask.setVisibility(View.VISIBLE);
        rlSelectCourseMask.setVisibility(View.VISIBLE);
    }

    @Override
    protected void lazyLoad() {
        //加载系统推荐选科
        loadSysRecommendCourse(childExamId);
        //加载观察专业
        loadObserveMajor();
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

    @OnClick({R.id.sdv_system_recommend, R.id.sdv_observe_major,
            R.id.sdv_select_course, R.id.rl_observe_major_mask, R.id.rl_select_course_mask})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //系统推荐
            case R.id.sdv_system_recommend: {
                SystemRecommendCourseActivity.startSystemRecommendCourseActivity(getContext(), childExamId);
                break;
            }
            //观察专业
            case R.id.sdv_observe_major: {
                ObserveMajorActivity.startObserveMajorActivity(getContext(), childExamId);
                break;
            }
            //确认选科
            case R.id.sdv_select_course: {
                ChooseCourseActivity.startChooseCourseActivity(getContext(), childExamId);
                break;
            }
        }
    }

    //观察专业是否解锁
//    private boolean observeMajorUnlock;
    //确认选科是否解锁
//    private boolean selectCourseUnlock;

    /**
     * 系统推荐完成的通知事件
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSysRecommendCompleteNotice(SysRecommendCompleteEvent event) {
        //解锁观察专业
        rlObserveMajorMask.setVisibility(View.GONE);
        avlObserveMajor.setVisibility(View.GONE);
    }

    /**
     * 添加观察专业成功的通知事件
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddObserveMajorSuccessNotice(AddObserveMajorSuccessEvent event) {
        if (event.getCount() > 0) {
            //解锁确认选科
            rlSelectCourseMask.setVisibility(View.GONE);
            avlSelectCourse.setVisibility(View.GONE);
        } else {
            //关闭确认选科
            rlSelectCourseMask.setVisibility(View.VISIBLE);
            avlSelectCourse.setVisibility(View.GONE);
        }
    }

    /**
     * 加载系统推荐选科
     */
    private void loadSysRecommendCourse(String childExamId) {
        avlObserveMajor.setVisibility(View.VISIBLE);

        DataRequestService.getInstance().getSysRmdCourse(childExamId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                avlObserveMajor.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    avlObserveMajor.setVisibility(View.GONE);

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    SysRmdCourse entity = InjectionWrapperUtil.injectMap(dataMap, SysRmdCourse.class);

                    if (entity == null || ArrayListUtil.isEmpty(entity.getRecommend_subjects())) {
                        return;
                    }

                    rlObserveMajorMask.setVisibility(View.GONE);

                } catch (Exception e) {
                    onFailure(new QSCustomException(e.getMessage()));
                }
            }
        }, httpTag, getActivity());
    }

    /**
     * 加载观察专业
     */
    private void loadObserveMajor() {
        avlSelectCourse.setVisibility(View.VISIBLE);

        ChildDto dto = new ChildDto(1, 100);
        dto.setChildId(UCManager.getInstance().getDefaultChild().getChildId());

        DataRequestService.getInstance().getSaveObserveMajor(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                avlSelectCourse.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    avlSelectCourse.setVisibility(View.GONE);

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    RecommendMajorRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, RecommendMajorRootEntity.class);

                    List<RecommendMajor> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        return;
                    }

                    rlSelectCourseMask.setVisibility(View.GONE);

                } catch (Exception e) {
                    onFailure(new QSCustomException(e.getMessage()));
                }

            }
        }, httpTag, getActivity());
    }


}

