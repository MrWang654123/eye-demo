package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.WarpLinearLayout;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.CourseGroupRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.ConfirmSelectCourseDto;
import com.cheersmind.cheersgenie.features_v2.dto.CourseGroupDto;
import com.cheersmind.cheersgenie.features_v2.entity.CourseGroup;
import com.cheersmind.cheersgenie.features_v2.entity.CourseGroupRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.SysRmdCourse;
import com.cheersmind.cheersgenie.features_v2.event.SelectCourseSuccessEvent;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ChooseCourseActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ChooseCourseResultActivity;
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
import java.util.List;
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

    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //确认按钮
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    //适配器
    CourseGroupRecyclerAdapter recyclerAdapter;

    //限制数量
    int limitCount = 3;

    //上拉加载更多的监听
    BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            loadCanSelectCourseGroup();
        }
    };

    //recycler item点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            CourseGroup entity = (CourseGroup) recyclerAdapter.getData().get(position);
            //选科数量限制
            if (chooseCourseList.size() == limitCount && !entity.isSelected()) {
                //提示选中课程名称
                if (getActivity() != null) {
                    ToastUtil.showShort(getActivity().getApplication(), "最多选" + limitCount + "种组合");
                }
                return;
            }

            //设置选中标志
            entity.setSelected(!entity.isSelected());
            //刷新视图
            ImageView ivSelect = view.findViewById(R.id.iv_select);
            if (entity.isSelected()) {
                ivSelect.setImageResource(R.drawable.check_box_outline);
                //处理选中集合
                if (!chooseCourseList.contains(entity)) {
                    chooseCourseList.add(entity);
                }
            } else {
                ivSelect.setImageResource(R.drawable.check_box_outline_bl);
                //处理选中集合
                if (chooseCourseList.contains(entity)) {
                    chooseCourseList.remove(entity);
                }
            }

        }
    };


    //页长度
    private static final int PAGE_SIZE = 50;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    ConfirmSelectCourseDto confirmDto;
    CourseGroupDto courseGroupDto;

    //选中的学科组合
    List<CourseGroup> chooseCourseList = new ArrayList<>();


    @Override
    protected int setContentView() {
        return R.layout.fragment_choose_course;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //适配器
        recyclerAdapter = new CourseGroupRecyclerAdapter(getContext(), null);
        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        //设置上拉加载更多的监听
        recyclerAdapter.setOnLoadMoreListener(loadMoreListener, recycleView);
        //禁用未满页自动触发上拉加载
        recyclerAdapter.disableLoadMoreIfNotFullPage();
        //设置加载更多视图
        recyclerAdapter.setLoadMoreView(new RecyclerLoadMoreView());
        //预加载，当列表滑动到倒数第N个Item的时候(默认是1)回调onLoadMoreRequested方法
        recyclerAdapter.setPreLoadNumber(4);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setAdapter(recyclerAdapter);
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_choose_course));
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
//                loadCanSelectCourseGroup();
                //加载系统推荐
                loadSysRecommend(childExamId);
                //加载用户选科组合
                loadUserSelectCourseGroup();
            }
        });

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            childExamId = bundle.getString(DtoKey.CHILD_EXAM_ID);
        }

        confirmDto = new ConfirmSelectCourseDto();
        confirmDto.setChildExamId(childExamId);
        confirmDto.setChildId(UCManager.getInstance().getDefaultChild().getChildId());
        confirmDto.setItems(chooseCourseList);

        courseGroupDto = new CourseGroupDto(pageNum, PAGE_SIZE);
        courseGroupDto.setChild_id(UCManager.getInstance().getDefaultChild().getChildId());
        courseGroupDto.setChild_exam_id(childExamId);

        //设置recyclerView不影响嵌套滚动
        recycleView.setNestedScrollingEnabled(false);
        //使其失去焦点。
        recycleView.setFocusable(false);

        String childId = UCManager.getInstance().getDefaultChild().getChildId();
        System.out.println(childId);
    }

    @Override
    protected void lazyLoad() {
//        //加载可选学科
//        loadMoreData();
        //加载系统推荐
        loadSysRecommend(childExamId);
//        //加载专业观察表
//        loadObserveMajor();
        //加载用户选科组合
        loadUserSelectCourseGroup();
//        //加载可选学科组合
//        loadCanSelectCourseGroup();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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

                    //跳转到选科结果页
                    Intent intent = new Intent(getContext(), ChooseCourseResultActivity.class);
                    intent.putExtra(DtoKey.CHILD_EXAM_ID, childExamId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent);

                    getActivity().finish();

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

                    if (wllSysRecommend.getChildCount() > 0) {
                        wllSysRecommend.removeAllViews();
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

    /**
     * 加载可选学科组合
     */
    private void loadCanSelectCourseGroup() {

        //设置页
        courseGroupDto.setPage(pageNum);

        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
        if (recyclerAdapter.getData().size() == 0) {
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }

        DataRequestService.getInstance().getCanSelectCourseGroup(courseGroupDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {

                if (ArrayListUtil.isEmpty(recyclerAdapter.getData())) {
                    //设置空布局：网络错误
                    emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                } else {
                    //加载失败处理
                    recyclerAdapter.loadMoreFail();
                }
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //设置空布局：隐藏
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    CourseGroupRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, CourseGroupRootEntity.class);

                    totalCount = rootEntity.getTotal();
                    List<CourseGroup> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    List<MultiItemEntity> multiItemEntities = courseGroupToMultiItem(dataList);

                    //当前列表无数据
                    if (recyclerAdapter.getData().size() == 0) {
                        recyclerAdapter.setNewData(multiItemEntities);

                    } else {
                        recyclerAdapter.addData(multiItemEntities);
                    }

                    //判断是否全部加载结束
                    if (recyclerAdapter.getData().size() >= totalCount) {
                        //全部加载结束
                        recyclerAdapter.loadMoreEnd(true);
                    } else {
                        //本次加载完成
                        recyclerAdapter.loadMoreComplete();
                    }

                    //页码+1
                    pageNum++;

                } catch (Exception e) {
                    e.printStackTrace();
                    if (ArrayListUtil.isEmpty(recyclerAdapter.getData())) {
                        //设置空布局：没有数据，可重载
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                    } else {
                        //加载失败处理
                        recyclerAdapter.loadMoreFail();
                    }
                }

            }
        }, httpTag, getActivity());
    }

    /**
     * CourseGroup转成List<MultiItemEntity>
     * @param dataList CourseGroup集合
     * @return List<MultiItemEntity>
     */
    private List<MultiItemEntity> courseGroupToMultiItem(List<CourseGroup> dataList) {
        List<MultiItemEntity> resList = null;

        if (ArrayListUtil.isNotEmpty(dataList)) {
            resList = new ArrayList<>();
            for (CourseGroup item : dataList) {
                item.setItemType(CourseGroupRecyclerAdapter.LAYOUT_SELECT_CAN_SELECT);
                resList.add(item);
            }

            //设置上次已选组合
            if (ArrayListUtil.isNotEmpty(lastSelectCourses)) {
                for (CourseGroup selectItem : lastSelectCourses) {
                    for (CourseGroup item : dataList) {
                        if (!TextUtils.isEmpty(selectItem.getSubjectGroup())
                                && selectItem.getSubjectGroup().equals(item.getSubjectGroup())) {
//                            item.setSelected(true);
                            item.setLastSelect(true);//上次选中
//                            chooseCourseList.add(item);
                        }
                    }
                }
            }
        }

        return resList;
    }

    //上次选科组合
    List<CourseGroup> lastSelectCourses;

    /**
     * 加载用户已选的学科组合
     */
    private void loadUserSelectCourseGroup() {

        CourseGroupDto courseGroupDto = new CourseGroupDto(1, 50);
        courseGroupDto.setChild_id(UCManager.getInstance().getDefaultChild().getChildId());
        courseGroupDto.setChild_exam_id(childExamId);

        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
        if (recyclerAdapter.getData().size() == 0) {
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }

        DataRequestService.getInstance().getUserSelectCourseGroup(courseGroupDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //设置空布局：网络错误
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    CourseGroupRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, CourseGroupRootEntity.class);

                    lastSelectCourses = rootEntity.getItems();

                    //加载可选学科组合
                    loadCanSelectCourseGroup();

                } catch (Exception e) {
                    e.printStackTrace();
                    //设置空布局：没有数据，可重载
                    emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                }

            }
        }, httpTag, getActivity());
    }


}

