package com.cheersmind.cheersgenie.features_v2.modules.major.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.TabFragmentPagerAdapter;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.dto.ArticleDto;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.adapter.ExamTaskRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.adapter.MajorRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.MajorEntity;
import com.cheersmind.cheersgenie.features_v2.entity.MajorRootEntity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ExamTaskDetailActivity;
import com.cheersmind.cheersgenie.features_v2.modules.major.activity.MajorDetailActivity;
import com.cheersmind.cheersgenie.features_v2.modules.trackRecord.fragment.CareerPlanReportFragment;
import com.cheersmind.cheersgenie.features_v2.modules.trackRecord.fragment.TrackRecordDetailFragment;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 专业列表
 */
public class MajorListFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //本科专业
    public static final int TYPE_UNDERGRADUATE_MAJOR = 1;
    //专科专业
    public static final int TYPE_JUNIOR_MAJOR = 2;

    //类型
    private int type = TYPE_UNDERGRADUATE_MAJOR;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //适配器的数据列表
    MajorRecyclerAdapter recyclerAdapter;

    //上拉加载更多的监听
    BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            //加载数据
            loadData();
        }
    };

    //recycler item点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            MajorEntity major = (MajorEntity) recyclerAdapter.getData().get(position);
            //展开
            if (major.getLevel() == 0 || major.getLevel() == 1) {
                int realPosition = position + adapter.getHeaderLayoutCount();
                if (major.isExpanded()) {
                    adapter.collapse(realPosition);
                } else {
                    adapter.expand(position);
                    //防止展开后第一时间看不到子项的滚动逻辑
                    int size = recyclerAdapter.getData().size();
                    if ((position + 1 + 2) <= size) {
                        recycleView.scrollToPosition(realPosition + 2);
                    }
                }

            } else {
                //跳转到详情
                MajorDetailActivity.startMajorActivity(getContext(), major);
            }
        }
    };


    //页长度
    private static final int PAGE_SIZE = 20;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    ArticleDto dto;

    @Override
    protected int setContentView() {
        return R.layout.fragment_major_list;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //适配器
        recyclerAdapter = new MajorRecyclerAdapter( null);
//        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        //设置上拉加载更多的监听
        recyclerAdapter.setOnLoadMoreListener(loadMoreListener, recycleView);
        //禁用未满页自动触发上拉加载
        recyclerAdapter.disableLoadMoreIfNotFullPage();
        //设置加载更多视图
        recyclerAdapter.setLoadMoreView(new RecyclerLoadMoreView());
        //预加载，当列表滑动到倒数第N个Item的时候(默认是1)回调onLoadMoreRequested方法
        recyclerAdapter.setPreLoadNumber(4);
        //添加一个空HeaderView，用于显示顶部分割线
//        recyclerAdapter.addHeaderView(new View(getContext()));
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setAdapter(recyclerAdapter);
        //添加自定义分割线
//        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
//        divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycler_divider_custom));
//        recycleView.addItemDecoration(divider);
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_major_list));
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //设置为加载状态
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
                loadData();
            }
        });
        //空布局背景色
        if (getContext() != null) {
            emptyLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        }
        //初始设置为加载状态
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        //获取数据
        Bundle bundle = getArguments();
        if(bundle!=null) {
            type = bundle.getInt(DtoKey.MAJOR_TYPE);
        }

        dto = new ArticleDto(pageNum, PAGE_SIZE);
    }

    @Override
    protected void lazyLoad() {
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 加载数据
     */
    private void loadData() {

        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
//        if (recyclerAdapter.getData().size() == 0) {
//            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
//        }

        dto.setPage(pageNum);
        DataRequestService.getInstance().getArticles(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                if (recyclerAdapter.getData().size() == 0) {
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
                    MajorRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, MajorRootEntity.class);

                    totalCount = rootEntity.getTotal();
                    List<MajorEntity> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    List<MultiItemEntity> multiItemEntities = majorToRecyclerMulti(dataList);
                    //目前每次都是重置列表数据
                    recyclerAdapter.setNewData(multiItemEntities);
                    //初始展开
//                    recyclerAdapter.expandAll();

                    //全部加载结束
                    recyclerAdapter.loadMoreEnd(true);

                    //页码+1
                    pageNum++;

                } catch (Exception e) {
                    e.printStackTrace();
                    if (recyclerAdapter.getData().size() == 0) {
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
     * List<MajorEntity>转成转成用于适配recycler的分组数据模型List<MultiItemEntity>
     * @param majorList 专业集合
     * @return List<MultiItemEntity>
     */
    protected List<MultiItemEntity> majorToRecyclerMulti(List<MajorEntity> majorList) {
        List<MultiItemEntity> resList = null;

        if (ArrayListUtil.isNotEmpty(majorList)) {
            resList = new ArrayList<>();
            //一级
            resList.add(majorList.get(0));
            resList.add(majorList.get(1));
            resList.add(majorList.get(2));

            //二级
            majorList.get(3).setItemType(MajorRecyclerAdapter.LAYOUT_TYPE_LEVEL1);
            majorList.get(4).setItemType(MajorRecyclerAdapter.LAYOUT_TYPE_LEVEL1);
            majorList.get(5).setItemType(MajorRecyclerAdapter.LAYOUT_TYPE_LEVEL1);
            majorList.get(6).setItemType(MajorRecyclerAdapter.LAYOUT_TYPE_LEVEL1);
            majorList.get(7).setItemType(MajorRecyclerAdapter.LAYOUT_TYPE_LEVEL1);
            majorList.get(8).setItemType(MajorRecyclerAdapter.LAYOUT_TYPE_LEVEL1);
            majorList.get(3).setLevel(1);
            majorList.get(4).setLevel(1);
            majorList.get(5).setLevel(1);
            majorList.get(6).setLevel(1);
            majorList.get(7).setLevel(1);
            majorList.get(8).setLevel(1);
            majorList.get(0).addSubItem(majorList.get(3));
            majorList.get(0).addSubItem(majorList.get(4));
            majorList.get(1).addSubItem(majorList.get(5));
            majorList.get(1).addSubItem(majorList.get(6));
            majorList.get(2).addSubItem(majorList.get(7));
            majorList.get(2).addSubItem(majorList.get(8));

            //三级
            majorList.get(9).setItemType(MajorRecyclerAdapter.LAYOUT_TYPE_LEVEL2);
            majorList.get(10).setItemType(MajorRecyclerAdapter.LAYOUT_TYPE_LEVEL2);
            majorList.get(11).setItemType(MajorRecyclerAdapter.LAYOUT_TYPE_LEVEL2);
            majorList.get(12).setItemType(MajorRecyclerAdapter.LAYOUT_TYPE_LEVEL2);
            majorList.get(13).setItemType(MajorRecyclerAdapter.LAYOUT_TYPE_LEVEL2);
            majorList.get(14).setItemType(MajorRecyclerAdapter.LAYOUT_TYPE_LEVEL2);
            majorList.get(15).setItemType(MajorRecyclerAdapter.LAYOUT_TYPE_LEVEL2);
            majorList.get(16).setItemType(MajorRecyclerAdapter.LAYOUT_TYPE_LEVEL2);
            majorList.get(17).setItemType(MajorRecyclerAdapter.LAYOUT_TYPE_LEVEL2);
            majorList.get(18).setItemType(MajorRecyclerAdapter.LAYOUT_TYPE_LEVEL2);
            majorList.get(19).setItemType(MajorRecyclerAdapter.LAYOUT_TYPE_LEVEL2);

            majorList.get(9).setLevel(2);
            majorList.get(10).setLevel(2);
            majorList.get(11).setLevel(2);
            majorList.get(12).setLevel(2);
            majorList.get(13).setLevel(2);
            majorList.get(14).setLevel(2);
            majorList.get(15).setLevel(2);
            majorList.get(16).setLevel(2);
            majorList.get(17).setLevel(2);
            majorList.get(18).setLevel(2);
            majorList.get(19).setLevel(2);

            majorList.get(3).addSubItem(majorList.get(9));
            majorList.get(3).addSubItem(majorList.get(10));
            majorList.get(10).setLastInMaxLevel(true);
            majorList.get(4).addSubItem(majorList.get(11));
            majorList.get(4).addSubItem(majorList.get(12));
            majorList.get(12).setLastInMaxLevel(true);
            majorList.get(5).addSubItem(majorList.get(13));
            majorList.get(5).addSubItem(majorList.get(14));
            majorList.get(14).setLastInMaxLevel(true);
            majorList.get(6).addSubItem(majorList.get(15));
            majorList.get(6).addSubItem(majorList.get(16));
            majorList.get(16).setLastInMaxLevel(true);
            majorList.get(7).addSubItem(majorList.get(17));
            majorList.get(7).addSubItem(majorList.get(18));
            majorList.get(18).setLastInMaxLevel(true);
            majorList.get(8).addSubItem(majorList.get(19));
            majorList.get(19).setLastInMaxLevel(true);
//            majorList.get(8).addSubItem(majorList.get(20));
        }

        return resList;
    }

}

