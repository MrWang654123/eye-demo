package com.cheersmind.cheersgenie.features_v2.modules.exam.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.CourseGroupRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.CourseGroupDto;
import com.cheersmind.cheersgenie.features_v2.entity.ChooseCourseEntity;
import com.cheersmind.cheersgenie.features_v2.entity.CourseGroup;
import com.cheersmind.cheersgenie.features_v2.entity.CourseGroupRootEntity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ChooseCourseActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.module.login.UCManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 选科结果页
 */
public class ChooseCourseResultFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //孩子测评ID
    private String childExamId;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //适配器
    CourseGroupRecyclerAdapter recyclerAdapter;

    //上拉加载更多的监听
    BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            loadUserSelectCourseGroup();
        }
    };

    //页长度
    private static final int PAGE_SIZE = 50;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    CourseGroupDto courseGroupDto;


    @Override
    protected int setContentView() {
        return R.layout.fragment_choose_course_result;
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

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_choose_course));
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                loadUserSelectCourseGroup();
            }
        });

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            childExamId = bundle.getString(DtoKey.CHILD_EXAM_ID);
        }

        courseGroupDto = new CourseGroupDto(pageNum, PAGE_SIZE);
        courseGroupDto.setChild_id(UCManager.getInstance().getDefaultChild().getChildId());
        courseGroupDto.setChild_exam_id(childExamId);
    }

    @Override
    protected void lazyLoad() {
        //加载用户已选学科组合
        loadUserSelectCourseGroup();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.btn_modify})
    public void onViewClick(View view) {
        switch (view.getId()) {
            //修改选科
            case R.id.btn_modify: {
                Intent intent = new Intent(getContext(), ChooseCourseActivity.class);
                intent.putExtra(DtoKey.CHILD_EXAM_ID, childExamId);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent);
                break;
            }
        }
    }

    /**
     * 加载用户已选的学科组合
     */
    private void loadUserSelectCourseGroup() {

        //设置页
        courseGroupDto.setPage(pageNum);

        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
        if (recyclerAdapter.getData().size() == 0) {
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }

        DataRequestService.getInstance().getUserSelectCourseGroup(courseGroupDto, new BaseService.ServiceCallback() {
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
            for (int i=0; i<dataList.size(); i++) {
                CourseGroup item = dataList.get(i);
                item.setItemType(CourseGroupRecyclerAdapter.LAYOUT_SELECT_USER_HAS_SELECT);
                item.setIndex(i);
                //标记最后一个
                if (i == dataList.size() - 1) {
                    item.setLast(true);
                }
                resList.add(item);
            }
        }

        return resList;
    }

}

