package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.dto.ChildDto;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features_v2.entity.ChooseCourseEntity;
import com.cheersmind.cheersgenie.features_v2.entity.RecommendMajor;
import com.cheersmind.cheersgenie.features_v2.entity.RecommendMajorRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.SimpleDimensionResult;
import com.cheersmind.cheersgenie.features_v2.entity.SysRmdCourse;
import com.cheersmind.cheersgenie.features_v2.entity.SysRmdCourseItem;
import com.cheersmind.cheersgenie.features_v2.event.AddObserveMajorSuccessEvent;
import com.cheersmind.cheersgenie.features_v2.event.SelectCourseSuccessEvent;
import com.cheersmind.cheersgenie.features_v2.event.SysRecommendCompleteEvent;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ChooseCourseActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ObserveMajorActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.SystemRecommendCourseActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.module.login.UCManager;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
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

    @BindView(R.id.iv_step_one_status)
    ImageView ivStepOneStatus;
    @BindView(R.id.iv_step_two_status)
    ImageView ivStepTwoStatus;
    @BindView(R.id.iv_step_three_status)
    ImageView ivStepThreeStatus;

    //孩子测评ID
    private String childExamId;

    //观察专业
    @BindView(R.id.avl_observe_major)
    AVLoadingIndicatorView avlObserveMajor;
    @BindView(R.id.rl_observe_major_mask)
    RelativeLayout rlObserveMajorMask;

    //确认选科
    @BindView(R.id.avl_select_course)
    AVLoadingIndicatorView avlSelectCourse;
    @BindView(R.id.rl_select_course_mask)
    RelativeLayout rlSelectCourseMask;
    @BindView(R.id.tv_select_course)
    TextView tvSelectCourse;
    @BindView(R.id.tv_last_select_course)
    TextView tvLastSelectCourse;

    //课程编码-名称
    private HashMap<String, String> courseNameMap = new HashMap<>();

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

        //初始显示观察专业和确认选科遮罩
        rlObserveMajorMask.setVisibility(View.VISIBLE);
        rlSelectCourseMask.setVisibility(View.VISIBLE);
        //初始隐藏上次选科
        tvLastSelectCourse.setVisibility(View.GONE);

        courseNameMap.put("0000", "无限制");
        courseNameMap.put("1001", "语");
        courseNameMap.put("1002", "数");
        courseNameMap.put("1003", "英");
        courseNameMap.put("1004", "物");
        courseNameMap.put("1005", "化");
        courseNameMap.put("1006", "生");
        courseNameMap.put("1007", "历");
        courseNameMap.put("1008", "地");
        courseNameMap.put("1009", "政");
        courseNameMap.put("1010", "技");

        //初始隐藏状态图标
        ivStepOneStatus.setVisibility(View.GONE);
        ivStepTwoStatus.setVisibility(View.GONE);
        ivStepThreeStatus.setVisibility(View.GONE);
    }

    @Override
    protected void lazyLoad() {
        //加载系统推荐选科
        loadSysRecommendCourse(childExamId);
        //加载观察专业
        loadObserveMajor();
        //加载用户选科组合
        loadUserSelectCourseGroup();
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

    @OnClick({R.id.cl_system_recommend, R.id.cl_observe_major,
            R.id.cl_select_course, R.id.rl_observe_major_mask, R.id.rl_select_course_mask})
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
            //观察专业遮罩层
            case R.id.rl_observe_major_mask: {
                if (getActivity() != null) {
                    ToastUtil.showShort(getActivity().getApplication(), "请先完成第1步");
                }
                break;
            }
            //确认选科遮罩层
            case R.id.rl_select_course_mask: {
                if (getActivity() != null) {
                    ToastUtil.showShort(getActivity().getApplication(), "请先完成第2步");
                }
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
     * 选科成功的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectCourseSuccessNotice(SelectCourseSuccessEvent event) {
        //已经加载了数据
        if (hasLoaded) {
            //加载用户选科组合
            loadUserSelectCourseGroup();
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

                    if (entity == null || ArrayListUtil.isEmpty(entity.getItems())) {
                        return;
                    }

                    //是否完成所有
                    boolean complete = true;
                    List<SysRmdCourseItem> items = entity.getItems();
                    for (SysRmdCourseItem item : items) {
                        //如果有量表列表则判断所有量表的状态
                        List<SimpleDimensionResult> dimensions = item.getDimensions();
                        if (ArrayListUtil.isNotEmpty(dimensions)) {
                            for (SimpleDimensionResult dimension : dimensions) {
                                if (!dimension.isFinish()) {
                                    complete = false;
                                    break;
                                }
                            }

                            if (!complete) {
                                break;
                            }

                        } else {
                            if (!item.isFinish()) {
                                complete = false;
                                break;
                            }
                        }
                    }

                    //完成状态
                    if (complete) {
                        ivStepOneStatus.setVisibility(View.VISIBLE);
                        rlObserveMajorMask.setVisibility(View.GONE);
                    }

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

                    ivStepTwoStatus.setVisibility(View.VISIBLE);
                    rlSelectCourseMask.setVisibility(View.GONE);

                } catch (Exception e) {
                    onFailure(new QSCustomException(e.getMessage()));
                }

            }
        }, httpTag, getActivity());
    }

    /**
     * 加载用户选科组合
     */
    private void loadUserSelectCourseGroup() {

        String childId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().getUserSelectCourseGroup(childId, childExamId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    Double total = (Double) dataMap.get("total");
                    List<List<Map<String, Object>>> items = (List<List<Map<String, Object>>>) dataMap.get("items");

                    //空表示未选科，显示确认选科按钮
                    if (ArrayListUtil.isEmpty(items) || ArrayListUtil.isEmpty(items.get(0))) {
                        return;
                    }

                    //上次的选科
                    List<ChooseCourseEntity> lastSelectCourses = InjectionWrapperUtil.injectMaps(items.get(0), ChooseCourseEntity.class);
                    //非空
                    if (ArrayListUtil.isEmpty(lastSelectCourses)) {
                        return;
                    }

//                    //修改提示文字
//                    tvSelectCourse.setText("可修改你的选科");
//                    StringBuilder stringBuilder = new StringBuilder();
//                    for (ChooseCourseEntity course : lastSelectCourses) {
//                        stringBuilder.append(courseNameMap.get(String.valueOf(course.getSubject_code())));
//                        stringBuilder.append("、");
//                    }
//                    //上次选科
//                    tvLastSelectCourse.setVisibility(View.VISIBLE);
//                    tvLastSelectCourse.setText(stringBuilder.subSequence(0, stringBuilder.length() - 1));

                    ivStepTwoStatus.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    onFailure(new QSCustomException(e.getMessage()));
                }

            }
        }, httpTag, getActivity());
    }

}

