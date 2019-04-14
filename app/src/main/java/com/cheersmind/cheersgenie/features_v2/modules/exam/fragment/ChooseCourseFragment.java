package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.dto.ChildDto;
import com.cheersmind.cheersgenie.features.dto.SelectCourseDto;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.WarpLinearLayout;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.ChooseCourseRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.ConfirmSelectCourseDto;
import com.cheersmind.cheersgenie.features_v2.entity.ChooseCourseEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ChooseCourseRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.MajorRatio;
import com.cheersmind.cheersgenie.features_v2.entity.RecommendMajor;
import com.cheersmind.cheersgenie.features_v2.entity.RecommendMajorRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.SysRmdCourse;
import com.cheersmind.cheersgenie.features_v2.event.SelectCourseSuccessEvent;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 确认选课
 */
public class ChooseCourseFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //孩子测评ID
    private String childExamId;

    //系统推荐
    @BindView(R.id.wll_sys_recommend)
    WarpLinearLayout wllSysRecommend;
    @BindView(R.id.ll_sys_recommend)
    LinearLayout llSysRecommend;

    //观察专业推荐
    @BindView(R.id.wll_observe_major)
    WarpLinearLayout wllObserveMajor;
    @BindView(R.id.ll_observe_major)
    LinearLayout llObserveMajor;

    //上次选择学科
    @BindView(R.id.wll_last_select)
    WarpLinearLayout wllLastSelect;
    @BindView(R.id.ll_last_select)
    LinearLayout llLastSelect;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    @BindView(R.id.tv_can_select_major_ratio)
    TextView tvCanSelectMajorRatio;
    @BindView(R.id.tv_high_require_major_ratio)
    TextView tvHighRequireMajorRatio;
    @BindView(R.id.ll_relative_major_ratio)
    LinearLayout llRelativeMajorRatio;

    //确认按钮
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    //适配器
    ChooseCourseRecyclerAdapter recyclerAdapter;

    //限制数量
    int limitCount = 3;

    //recycler item点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            ChooseCourseEntity entity = recyclerAdapter.getData().get(position);
            //选科数量限制
            if (chooseCourseList.size() == limitCount && !entity.isSelected()) {
                //提示选中课程名称
                if (getActivity() != null) {
                    ToastUtil.showShort(getActivity().getApplication(), "请选" + limitCount + "个学科");
                }
                return;
            }

            //设置选中标志
            entity.setSelected(!entity.isSelected());
            //刷新视图
            View viewById = view.findViewById(R.id.iv_select);
            if (entity.isSelected()) {
                viewById.setVisibility(View.VISIBLE);
                //处理选中集合
                if (!chooseCourseList.contains(entity)) {
                    chooseCourseList.add(entity);
                }
            } else {
                viewById.setVisibility(View.GONE);
                //处理选中集合
                if (chooseCourseList.contains(entity)) {
                    chooseCourseList.remove(entity);
                }
            }

            //满足数量则加载相关专业比率
            if (chooseCourseList.size() == limitCount) {
                loadMajorRatioByCourseGroupWrap(confirmDto.getItems());
            }
        }
    };


    //页长度
    private static final int PAGE_SIZE = 10;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    SelectCourseDto dto;
    ConfirmSelectCourseDto confirmDto;

    //选中的课程
    List<ChooseCourseEntity> chooseCourseList = new ArrayList<>();


    @Override
    protected int setContentView() {
        return R.layout.fragment_choose_course;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //适配器
        recyclerAdapter = new ChooseCourseRecyclerAdapter(getContext(), R.layout.recycleritem_choose_course, null);
//        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
//        recyclerAdapter.openLoadAnimation(new SlideInBottomAnimation());
        //网格布局管理器
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recycleView.setLayoutManager(gridLayoutManager);
        recycleView.setAdapter(recyclerAdapter);
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_choose_course));
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                loadMoreData();
            }
        });

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            childExamId = bundle.getString(DtoKey.CHILD_EXAM_ID);
        }

//        //系统推荐选课视图
//        sysRmdCourseView = getLayoutInflater().inflate(R.layout.recycler_header_choose_course, null);
//        //推荐选课名称
//        tvSysRmdCourse = sysRmdCourseView.findViewById(R.id.tv_system_recommend_course);
//        //添加为recyclerView的header
//        recyclerAdapter.addHeaderView(sysRmdCourseView);

        dto = new SelectCourseDto(pageNum, PAGE_SIZE);
        dto.setChildId(UCManager.getInstance().getDefaultChild().getChildId());

        confirmDto = new ConfirmSelectCourseDto();
        confirmDto.setChildExamId(childExamId);
        confirmDto.setChildId(UCManager.getInstance().getDefaultChild().getChildId());
        confirmDto.setItems(chooseCourseList);

        courseNameMap.put("0000", "无限制");
        courseNameMap.put("1001", "语文");
        courseNameMap.put("1002", "数学");
        courseNameMap.put("1003", "英语");
        courseNameMap.put("1004", "物理");
        courseNameMap.put("1005", "化学");
        courseNameMap.put("1006", "生物");
        courseNameMap.put("1007", "历史");
        courseNameMap.put("1008", "地理");
        courseNameMap.put("1009", "政治");
        courseNameMap.put("1010", "信息");

        //初始隐藏相关专业比率模块
        llRelativeMajorRatio.setVisibility(View.GONE);
        tvCanSelectMajorRatio.setVisibility(View.GONE);
        tvHighRequireMajorRatio.setVisibility(View.GONE);

        //初始隐藏上次选科
        llLastSelect.setVisibility(View.GONE);

        //设置recyclerView不影响嵌套滚动
        recycleView.setNestedScrollingEnabled(false);
        //使其失去焦点。
        recycleView.setFocusable(false);
    }

    @Override
    protected void lazyLoad() {
        //加载可选学科
//        loadMoreData();
        //加载系统推荐
        loadSysRecommend(childExamId);
        //加载专业观察表
        loadObserveMajor();
        //加载用户选科组合
        loadUserSelectCourseGroup();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    /**
     * 加载更多数据
     */
    private void loadMoreData() {

        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        DataRequestService.getInstance().getSelectCourse(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //设置空布局：网络错误
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //设置空布局：隐藏
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ChooseCourseRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, ChooseCourseRootEntity.class);

                    totalCount = rootEntity.getTotal();
                    List<ChooseCourseEntity> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    //上次选科不为空，则做标记
                    if (ArrayListUtil.isNotEmpty(lastSelectCourses)) {
                        for (ChooseCourseEntity course : dataList) {
                            for (ChooseCourseEntity lastCourse : lastSelectCourses) {
                                if (course.getSubject_code() == lastCourse.getSubject_code()
                                        && lastCourse.getSubject_code() > 0) {
                                    //添加到当前选择
                                    chooseCourseList.add(lastCourse);
                                    //标记为选择状态
                                    course.setSelected(true);
                                }
                            }
                        }
                    }

                    //当前列表无数据
                    if (recyclerAdapter.getData().size() == 0) {
                        recyclerAdapter.setNewData(dataList);

                    } else {
                        recyclerAdapter.addData(dataList);
                    }

                    //判断是否全部加载结束
                    if (recyclerAdapter.getData().size() >= totalCount) {
                        //全部加载结束
                        recyclerAdapter.loadMoreEnd(true);
                    } else {
                        //本次加载完成
                        recyclerAdapter.loadMoreComplete();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //设置空布局：没有数据，可重载
                    emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                }

            }
        }, httpTag, getActivity());
    }


    @OnClick({R.id.btn_confirm})
    public void onViewClick(View view) {
        switch (view.getId()) {
            //确定
            case R.id.btn_confirm: {
                doConfirm();
                break;
            }
        }
    }

    /**
     * 确定操作
     */
    private void doConfirm() {
        if (chooseCourseList.size() == 0) {
            if (getActivity() != null) {
                ToastUtil.showShort(getActivity().getApplication(), "请选择学科");
            }
        } else {
            //请求确认选科
            doConfirmSelectCourse();
        }

    }


    /**
     * 请求确认选科
     */
    private void doConfirmSelectCourse() {
        //通信加载等待
        LoadingView.getInstance().show(getContext(), httpTag);

        DataRequestService.getInstance().postConfirmSelectCourse(confirmDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //关闭加载等待
                    LoadingView.getInstance().dismiss();

                    //提示选中课程名称
                    if (getActivity() != null) {
                        ToastUtil.showShort(getActivity().getApplication(), "选科成功");
                    }

//                    //选科成功后，隐藏确认按钮
//                    btnConfirm.setVisibility(View.GONE);

                    //发送选科成功事件
                    EventBus.getDefault().post(new SelectCourseSuccessEvent());

                } catch (Exception e) {
                    e.printStackTrace();
                    onFailureDefault(new QSCustomException(getResources().getString(R.string.operate_fail)));
                }

            }
        }, httpTag, getActivity());
    }


    /**
     * 加载系统推荐
     */
    private void loadSysRecommend(String childExamId) {
        DataRequestService.getInstance().getSysRmdCourse(childExamId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Object obj) {
                try {

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    SysRmdCourse entity = InjectionWrapperUtil.injectMap(dataMap, SysRmdCourse.class);

                    if (entity == null || ArrayListUtil.isEmpty(entity.getItems())) {
                        return;
                    }

                    for (String str : entity.getRecommend_subjects()) {
                        TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.record_result_item_unclickable, null);
                        tv.setText(str);
                        wllSysRecommend.addView(tv);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, httpTag, getActivity());
    }

    //课程编码-累计值
    private HashMap<String, Integer> courseMap = new HashMap<>();
    //课程编码-名称
    private HashMap<String, String> courseNameMap = new HashMap<>();

    /**
     * 加载观察专业
     */
    private void loadObserveMajor() {
        ChildDto dto = new ChildDto(1, 100);
        dto.setChildId(UCManager.getInstance().getDefaultChild().getChildId());

        DataRequestService.getInstance().getSaveObserveMajor(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    RecommendMajorRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, RecommendMajorRootEntity.class);

                    List<RecommendMajor> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        return;
                    }

                    List<String> courseList = generateMaxCountCourse(dataList);
                    for (String str : courseList) {
                        TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.record_result_item_unclickable, null);
                        tv.setText(str);
                        wllObserveMajor.addView(tv);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, httpTag, getActivity());
    }


    /**
     * 次数最多的三个学科
     */
    private List<String> generateMaxCountCourse(List<RecommendMajor> all) {
        List<String> resList = new ArrayList<>();

        if (ArrayListUtil.isNotEmpty(all)) {
            for (RecommendMajor major : all) {
                String require_subjects = major.getRequire_subjects();
                //非空
                if (!TextUtils.isEmpty(require_subjects)) {
                    String[] group = require_subjects.split("\\|\\|");
                    if (group.length > 0) {
                        for (String groupItem : group) {
                            String[] items = groupItem.split(",");
                            for (String item : items) {
                                String[] codes = item.split("\\|");
                                for (String code : codes) {
                                    //累加
                                    if (courseMap.containsKey(code)) {
                                        courseMap.put(code, courseMap.get(code) + 1);

                                    } else {
                                        courseMap.put(code, 1);
                                    }
                                }
                            }
                        }
                    }
                } /*else {//空表示无限制
                    //累加
                    if (courseMap.containsKey("0000")) {
                        courseMap.put("0000", courseMap.get("0000") + 1);

                    } else {
                        courseMap.put("0000", 1);
                    }
                }*/
            }

            //选出最多的三个（目前先随便选三个）
            int count = 1;
            for (Map.Entry<String, Integer> entry : courseMap.entrySet()) {
                resList.add(courseNameMap.get(entry.getKey()));
                if (count == 3) {
                    break;
                }
                count++;
            }
        }

        return resList;
    }

    /**
     * 根据选科组合获取专业覆盖率包裹
     */
    private void loadMajorRatioByCourseGroupWrap(List<ChooseCourseEntity> items) {
        int[] courses = null;
        if (ArrayListUtil.isNotEmpty(items)) {
            courses = new int[items.size()];
            for (int i = 0; i < items.size(); i++) {
                ChooseCourseEntity course = items.get(i);
                courses[i] = course.getSubject_code();
            }

            Arrays.sort(courses);//使用java.util.Arrays对象的sort方法
        }

        if (courses != null) {
            StringBuilder builder = new StringBuilder();
            for (int course : courses) {
                builder.append(course);
            }

            loadMajorRatioByCourseGroup(builder.toString());
        }
    }

    /**
     * 根据选科组合获取专业覆盖率
     */
    private void loadMajorRatioByCourseGroup(String courseGroup) {
        try {
            String childId = UCManager.getInstance().getDefaultChild().getChildId();
            DataRequestService.getInstance().getMajorRatioByCourseGroup(childId, courseGroup, new BaseService.ServiceCallback() {
                @Override
                public void onFailure(QSCustomException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Object obj) {
                    try {
                        Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                        MajorRatio data = InjectionWrapperUtil.injectMap(dataMap, MajorRatio.class);

                        if (data == null) {
                            return;
                        }

                        //相关专业比率
                        if ((data.getRate() == null || data.getRate() < 0.000001)
                                && (data.getRequire_rate() == null || data.getRequire_rate() < 0.000001)) {
                            llRelativeMajorRatio.setVisibility(View.GONE);

                        } else {
                            llRelativeMajorRatio.setVisibility(View.VISIBLE);

                            //可报考专业百分比
                            if (data.getRate() != null && data.getRate() > 0.000001) {
                                tvCanSelectMajorRatio.setVisibility(View.VISIBLE);
                                tvCanSelectMajorRatio.setText(
                                        Html.fromHtml("-您可报考的专业占所有大学专业的<b><font color='" +
                                                getResources().getColor(R.color.colorAccent) + "'>" +
                                                String.format(Locale.CHINA, "%.1f", data.getRate() * 100) + "%</font></b>"));
                            } else {
                                tvCanSelectMajorRatio.setVisibility(View.GONE);
                            }

                            //要求较高专业百分比
                            if (data.getRequire_rate() != null && data.getRequire_rate() > 0.000001) {
                                tvHighRequireMajorRatio.setVisibility(View.VISIBLE);
                                tvHighRequireMajorRatio.setText(
                                        Html.fromHtml("-要求较高的专业占所有大学专业的<b><font color='" +
                                                getResources().getColor(R.color.colorAccent) + "'>" +
                                                String.format(Locale.CHINA, "%.1f", data.getRequire_rate() * 100) + "%</font></b>"));
                            } else {
                                tvHighRequireMajorRatio.setVisibility(View.GONE);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, httpTag, getActivity());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //上次选科
    List<ChooseCourseEntity> lastSelectCourses;

    /**
     * 加载用户选科组合
     */
    private void loadUserSelectCourseGroup() {

        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        String childId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().getUserSelectCourseGroup(childId, childExamId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                e.printStackTrace();
                //加载可选学科
                loadMoreData();
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    Double total = (Double) dataMap.get("total");
                    List<List<Map<String,Object>>> items = (List<List<Map<String,Object>>>) dataMap.get("items");

                    //空表示未选科，显示确认选科按钮
                    if (ArrayListUtil.isEmpty(items) || ArrayListUtil.isEmpty(items.get(0))) {
                        //加载可选学科
                        loadMoreData();
                        return;
                    }

                    //上次的选科
                    lastSelectCourses = InjectionWrapperUtil.injectMaps(items.get(0), ChooseCourseEntity.class);
                    //非空
                    if (ArrayListUtil.isEmpty(lastSelectCourses)) {
                        //加载可选学科
                        loadMoreData();
                        return;
                    }

                    //显示上次选科布局
//                    llLastSelect.setVisibility(View.VISIBLE);
                    //按钮文字修改
                    btnConfirm.setText("修改我的选科选择");

//                    for (ChooseCourseEntity course : lastSelectCourses) {
//                        TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.record_result_item_unclickable, null);
//                        tv.setText(course.getSubject_name());
//                        wllLastSelect.addView(tv);
//                    }

                    //加载可选学科
                    loadMoreData();
                    //根据选科组合获取专业覆盖率包裹
                    loadMajorRatioByCourseGroupWrap(lastSelectCourses);

                } catch (Exception e) {
                    onFailure(new QSCustomException(e.getMessage()));
                }

            }
        }, httpTag, getActivity());
    }


}

