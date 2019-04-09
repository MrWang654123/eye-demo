package com.cheersmind.cheersgenie.features_v2.modules.mine.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.animation.SlideInBottomAnimation;
import com.cheersmind.cheersgenie.features_v2.adapter.AttentionRecyclerAdapter;
import com.cheersmind.cheersgenie.features_v2.dto.AttentionDto;
import com.cheersmind.cheersgenie.features_v2.dto.AttentionListDto;
import com.cheersmind.cheersgenie.features_v2.entity.AttentionEntity;
import com.cheersmind.cheersgenie.features_v2.entity.AttentionListRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEntity;
import com.cheersmind.cheersgenie.features_v2.entity.MajorItem;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationItem;
import com.cheersmind.cheersgenie.features_v2.modules.college.activity.CollegeDetailActivity;
import com.cheersmind.cheersgenie.features_v2.modules.major.activity.MajorDetailActivity;
import com.cheersmind.cheersgenie.features_v2.modules.occupation.activity.OccupationDetailActivity;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 我的关注
 */
public class MineAttentionFragment extends LazyLoadFragment {

    Unbinder unbinder;
    //关注类型
    private int attentionType;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //置顶按钮
    @BindView(R.id.fabGotoTop)
    FloatingActionButton fabGotoTop;

    //适配器的数据列表
    AttentionRecyclerAdapter recyclerAdapter;

    //下拉刷新的监听
    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //加载数据
            refreshData();
        }
    };

    //上拉加载更多的监听
    BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            //加载更多数据
            loadMoreData();
        }
    };

    //recycler item点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            AttentionEntity entity = recyclerAdapter.getData().get(position);
            switch (attentionType) {
                //院校
                case Dictionary.ATTENTION_TYPE_COLLEGE: {
                    try {
                        CollegeEntity collegeEntity = new CollegeEntity();
                        collegeEntity.setId(entity.getEntity_id());
                        collegeEntity.setCn_name(entity.getTag());
                        CollegeDetailActivity.startCollegeDetailActivity(getContext(), collegeEntity);
                    } catch (Exception e) {
                        if (getActivity() != null) {
                            ToastUtil.showShort(getActivity().getApplication(), getString(R.string.operate_fail));
                        }
                    }
                    break;
                }
                //专业
                case Dictionary.ATTENTION_TYPE_MAJOR: {
                    try {
                        MajorItem majorItem = new MajorItem();
                        majorItem.setMajor_code(entity.getEntity_id());
                        majorItem.setMajor_name(entity.getTag());
                        MajorDetailActivity.startMajorDetailActivity(getContext(), majorItem);
                    } catch (Exception e) {
                        if (getActivity() != null) {
                            ToastUtil.showShort(getActivity().getApplication(), getString(R.string.operate_fail));
                        }
                    }
                    break;
                }
                //职业
                case Dictionary.ATTENTION_TYPE_OCCUPATION: {
                    try {
                        OccupationItem occupationItem = new OccupationItem();
                        occupationItem.setOccupation_id(Long.valueOf(entity.getEntity_id()));
                        occupationItem.setOccupation_name(entity.getTag());
                        OccupationDetailActivity.startOccupationDetailActivity(getContext(), occupationItem);
                    } catch (Exception e) {
                        if (getActivity() != null) {
                            ToastUtil.showShort(getActivity().getApplication(), getString(R.string.operate_fail));
                        }
                    }
                    break;
                }
            }
        }
    };

    //recycler子项的孩子的点击监听
    BaseQuickAdapter.OnItemChildClickListener recyclerItemChildClickListener = new BaseQuickAdapter.OnItemChildClickListener() {

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            AttentionEntity entity = recyclerAdapter.getData().get(position);

            switch (view.getId()) {
                //取消关注
                case R.id.tv_cancel_attention: {
                    attentionDto.setEntity_id(entity.getEntity_id());
                    attentionDto.setTag(entity.getTag());
                    doAttention(attentionDto, position);
                    break;
                }
            }
        }
    };

    //页长度
    private static final int PAGE_SIZE = 20;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    //关注列表Dto
    AttentionListDto dto;
    //关注dto
    AttentionDto attentionDto;

    @Override
    protected int setContentView() {
        return R.layout.fragment_common_white_refresh_recycler_gotop;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            attentionType = bundle.getInt(DtoKey.ATTENTION_TYPE);
        }

        //适配器
        recyclerAdapter = new AttentionRecyclerAdapter(getContext(), R.layout.recycleritem_mine_attention, null);
//        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        recyclerAdapter.openLoadAnimation(new SlideInBottomAnimation());
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
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //子项孩子的点击监听
        recyclerAdapter.setOnItemChildClickListener(recyclerItemChildClickListener);
        //添加自定义分割线
//        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
//        divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycler_divider_custom));
//        recycleView.addItemDecoration(divider);
        //滑动监听
        try {
            recycleView.addOnScrollListener(new RecyclerViewScrollListener(getContext(), fabGotoTop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_attention_list));
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                loadMoreData();
            }
        });

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);

        dto = new AttentionListDto(pageNum, PAGE_SIZE);
        dto.setType(attentionType);

        attentionDto = new AttentionDto();
        attentionDto.setType(attentionType);
        attentionDto.setFollow(false);
    }

    @Override
    protected void lazyLoad() {
        loadMoreData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        //下拉刷新
        pageNum = 1;
        //确保显示了刷新动画
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        //关闭上拉加载功能
        recyclerAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        dto.setPage(pageNum);
        DataRequestService.getInstance().getMineAttentionList(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //开启上拉加载功能
                recyclerAdapter.setEnableLoadMore(true);
                //结束下拉刷新动画
                swipeRefreshLayout.setRefreshing(false);
                //设置空布局：网络错误
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                //清空列表数据
                recyclerAdapter.setNewData(null);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //开启上拉加载功能
                    recyclerAdapter.setEnableLoadMore(true);
                    //结束下拉刷新动画
                    swipeRefreshLayout.setRefreshing(false);
                    //设置空布局：隐藏
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    AttentionListRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, AttentionListRootEntity.class);

                    totalCount = rootEntity.getTotal();
                    List<AttentionEntity> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    //下拉刷新
                    recyclerAdapter.setNewData(dataList);
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
                    //设置空布局：没有数据，可重载
                    emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                    //清空列表数据
                    recyclerAdapter.setNewData(null);
                }
            }
        }, httpTag, getActivity());
    }


    /**
     * 加载更多数据
     */
    private void loadMoreData() {
        //关闭下拉刷新功能
        swipeRefreshLayout.setEnabled(false);//防止加载更多和下拉刷新冲突

        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
        if (recyclerAdapter.getData().size() == 0) {
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }

        dto.setPage(pageNum);
        DataRequestService.getInstance().getMineAttentionList(dto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //开启下拉刷新功能
                swipeRefreshLayout.setEnabled(true);//防止加载更多和下拉刷新冲突

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
                    //开启下拉刷新功能
                    swipeRefreshLayout.setEnabled(true);//防止加载更多和下拉刷新冲突
                    //设置空布局：隐藏
                    emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    AttentionListRootEntity rootEntity = InjectionWrapperUtil.injectMap(dataMap, AttentionListRootEntity.class);

                    totalCount = rootEntity.getTotal();
                    List<AttentionEntity> dataList = rootEntity.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
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
     * 关注操作
     */
    private void doAttention(AttentionDto attentionDto, final int position) {
        //通信加载等待
        LoadingView.getInstance().show(getContext(), httpTag);
        DataRequestService.getInstance().postDoAttention(attentionDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    //关闭通信加载等待
                    LoadingView.getInstance().dismiss();

                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    AttentionEntity attentionEntity = InjectionWrapperUtil.injectMap(dataMap, AttentionEntity.class);

                    recyclerAdapter.getData().remove(position);
                    int tempPosition = position + recyclerAdapter.getHeaderLayoutCount();
//                    //把header计算在内
//                    recyclerAdapter.notifyItemRemoved(tempPosition);

                    //无数据处理
                    if (recyclerAdapter.getData().size() == 0) {
                        //空布局设置：没有数据
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        //重置数据为空
                        recyclerAdapter.setNewData(null);
                    } else {
                        //把header计算在内
                        recyclerAdapter.notifyItemRemoved(tempPosition);
                    }

                } catch (Exception e) {
                    onFailure(new QSCustomException(getString(R.string.operate_fail)));
                }


            }
        }, httpTag, getContext());
    }

}

