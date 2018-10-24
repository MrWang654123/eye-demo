package com.cheersmind.cheersgenie.features.modules.exam.fragment;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.ExamDimensionBaseRecyclerAdapter;
import com.cheersmind.cheersgenie.features.adapter.ExamDimensionLinearRecyclerAdapter;
import com.cheersmind.cheersgenie.features.adapter.ExamDimensionRecyclerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.RecyclerCommonSection;
import com.cheersmind.cheersgenie.features.event.QuestionSubmitSuccessEvent;
import com.cheersmind.cheersgenie.features.interfaces.RecyclerViewScrollListener;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.modules.exam.activity.DimensionDetailActivity;
import com.cheersmind.cheersgenie.features.modules.exam.activity.ReportActivity;
import com.cheersmind.cheersgenie.features.modules.exam.activity.TopicDetailActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.animation.SlideInBottomAnimation;
import com.cheersmind.cheersgenie.features.view.dialog.DimensionReportDialog;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.entity.TopicRootEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.module.login.UCManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 测评基础页面
 */
public class ExamBaseFragment extends LazyLoadFragment {

    //话题状态（默认获取完成的）
    protected int topicStatus = Dictionary.TOPIC_STATUS_COMPLETE;

    @BindView(R.id.recycleView)
    protected RecyclerView recycleView;
    protected Unbinder unbinder;

    //话题列表（话题基础数据、孩子话题的信息、量表）
    List<TopicInfoEntity> topicList;

    //适配器
    protected ExamDimensionBaseRecyclerAdapter recyclerAdapter;
    //适配器（网格式）
    protected ExamDimensionBaseRecyclerAdapter gridRecyclerAdapter;
    //适配器（线性式）
    protected ExamDimensionBaseRecyclerAdapter linearRecyclerAdapter;

    @BindView(R.id.swipeRefreshLayout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    //空布局
    @BindView(R.id.emptyLayout)
    protected XEmptyLayout emptyLayout;

    //置顶按钮
    @BindView(R.id.fabGotoTop)
    protected FloatingActionButton fabGotoTop;

    //默认网格
    protected int layoutType = Dictionary.EXAM_LIST_LAYOUT_TYPE_GRID;

    //recycler子项的点击监听
    protected BaseQuickAdapter.OnItemClickListener recyclerItemClickListener =  new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//            //网格
//            RecyclerCommonSection<DimensionInfoEntity> data = (RecyclerCommonSection<DimensionInfoEntity>) adapter.getData().get(position);
//            //非Header模型
//            if (!data.isHeader) {
//                DimensionInfoEntity dimensionInfoEntity = (DimensionInfoEntity) data.t;
//                TopicInfoEntity topicInfoEntity = (TopicInfoEntity) data.getInfo();
//                //点击量表项的操作
//                operateClickDimension(dimensionInfoEntity, topicInfoEntity);
//            }

            Object item = ((ExamDimensionBaseRecyclerAdapter) adapter).getItem(position);
            if (item instanceof DimensionInfoEntity) {
                DimensionInfoEntity dimensionInfoEntity = (DimensionInfoEntity) item;
                int parentPosition = adapter.getParentPosition(dimensionInfoEntity);
                TopicInfoEntity topicInfoEntity = (TopicInfoEntity) ((ExamDimensionBaseRecyclerAdapter) adapter).getItem(parentPosition);
                //点击量表项的操作
                operateClickDimension(dimensionInfoEntity, topicInfoEntity);
            }
        }
    };

    //recycler子项的孩子视图点击监听
    protected BaseQuickAdapter.OnItemChildClickListener recyclerItemChildClickListener =  new BaseQuickAdapter.OnItemChildClickListener() {
        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//            //话题详情
//            if (view.getId() == R.id.iv_desc) {
//                RecyclerCommonSection<DimensionInfoEntity> data = (RecyclerCommonSection<DimensionInfoEntity>) adapter.getData().get(position);
//                DimensionInfoEntity dimensionInfoEntity = (DimensionInfoEntity) data.t;
//                TopicInfoEntity topicInfoEntity = (TopicInfoEntity) data.getInfo();
//                //跳转到话题详情页面
//                TopicDetailActivity.startTopicDetailActivity(getContext(), topicInfoEntity.getTopicId(), topicInfoEntity);
//
//            } else if (view.getId() == R.id.tv_nav_to_report) {//查看报告
//                RecyclerCommonSection<DimensionInfoEntity> data = (RecyclerCommonSection<DimensionInfoEntity>) adapter.getData().get(position);
//                DimensionInfoEntity dimensionInfoEntity = (DimensionInfoEntity) data.t;
//                TopicInfoEntity topicInfoEntity = (TopicInfoEntity) data.getInfo();
//                //查看话题报告
//                ReportActivity.startReportActivity(getContext(), topicInfoEntity);
//            }

            Object item = ((ExamDimensionBaseRecyclerAdapter) adapter).getItem(position);
            TopicInfoEntity topicInfoEntity = (TopicInfoEntity) item;
            //话题详情
            if (view.getId() == R.id.iv_desc) {
                //跳转到话题详情页面
                TopicDetailActivity.startTopicDetailActivity(getContext(), topicInfoEntity.getTopicId(), topicInfoEntity);

            } else if (view.getId() == R.id.tv_nav_to_report) {//查看报告
                //查看话题报告
                ReportActivity.startReportActivity(getContext(), topicInfoEntity);
            }

        }
    };


    //下拉刷新的监听
    protected SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //刷新孩子话题
            refreshChildTopList();
        }
    };
    //上拉加载更多的监听
    protected BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            //加载更多孩子话题
            loadMoreChildTopicList();
        }
    };

    //页长度
    protected static final int PAGE_SIZE = 5;
    //页码
    protected int pageNum = 1;
    //后台总记录数
    protected int totalCount = 0;

    @Override
    protected int setContentView() {
        return R.layout.fragment_report_completed;
    }

    @Override
    protected void lazyLoad() {
        //加载更多孩子话题
        loadMoreChildTopicList();
    }

    /**
     * 初始化视图
     */
    @Override
    public void onInitView(View contentView) {
        //绑定ButterKnife
        unbinder = ButterKnife.bind(this, contentView);
        //注册事件
        EventBus.getDefault().register(this);

        try {
            gridRecyclerAdapter = new ExamDimensionRecyclerAdapter(this, null);
            linearRecyclerAdapter = new ExamDimensionLinearRecyclerAdapter(this,  null);
        } catch (QSCustomException e) {
            e.printStackTrace();
        }
        //设置适配器属性
        settingAdapterProperty(gridRecyclerAdapter);
        settingAdapterProperty(linearRecyclerAdapter);

        //默认设置网格布局
        if (layoutType == Dictionary.EXAM_LIST_LAYOUT_TYPE_GRID) {
            recyclerAdapter = gridRecyclerAdapter;
        } else {
            recyclerAdapter = linearRecyclerAdapter;
        }

        //设置加载更多视图
        recyclerAdapter.setLoadMoreView(new RecyclerLoadMoreView());

        recycleView.setAdapter(recyclerAdapter);

        //线性布局
        if (recyclerAdapter instanceof ExamDimensionLinearRecyclerAdapter) {
            recycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        } else if (recyclerAdapter instanceof ExamDimensionRecyclerAdapter) {//网格布局
            //网格布局管理器
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return recyclerAdapter.getItemViewType(position) == ExamDimensionBaseRecyclerAdapter.LAYOUT_TYPE_BODY ? 1 : gridLayoutManager.getSpanCount();
                }
            });
            //必须在setAdapter之后
            recycleView.setLayoutManager(gridLayoutManager);

        } else {
            recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        //滑动监听
        try {
            recycleView.addOnScrollListener(new RecyclerViewScrollListener(getContext(), fabGotoTop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        //设置样式刷新显示的位置
        swipeRefreshLayout.setProgressViewOffset(true, -20, 100);

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_report));
        //空布局重载点击监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //加载更多孩子话题
                loadMoreChildTopicList();
            }
        });

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);
    }


    /**
     * 设置适配器属性
     * @param recyclerAdapter 适配器
     */
    protected void settingAdapterProperty(BaseQuickAdapter recyclerAdapter) {
        //        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        recyclerAdapter.openLoadAnimation(new SlideInBottomAnimation());
        //设置上拉加载更多的监听
        recyclerAdapter.setOnLoadMoreListener(loadMoreListener, recycleView);
        //禁用未满页自动触发上拉加载
        recyclerAdapter.disableLoadMoreIfNotFullPage();
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //设置子项的孩子视图点击监听
        recyclerAdapter.setOnItemChildClickListener(recyclerItemChildClickListener);
        //设置加载更多视图
        recyclerAdapter.setLoadMoreView(new RecyclerLoadMoreView());
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * 视图是否已经对用户可见，系统的方法
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //解绑ButterKnife
        unbinder.unbind();

        //注销事件
        EventBus.getDefault().unregister(this);
    }


    /**
     * 问题提交成功的通知事件
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQuestionSubmitNotice(QuestionSubmitSuccessEvent event) {
        //已经加载了数据
        if (hasLoaded) {
            DimensionInfoEntity dimension = event.getDimension();
            onQuestionSubmit(dimension);
        }

    }

    /**
     * 问题提交成功的通知事件的处理
     * @param dimension 量表
     */
    protected void onQuestionSubmit(DimensionInfoEntity dimension) {
        //重置为第一页
        resetPageInfo();
        //刷新孩子话题
        refreshChildTopList();
    }

    /**
     * 是否满足解锁条件
     * @param topicInfo 话题
     * @return true 满足
     */
    protected boolean isMeetUnlockCondition(TopicInfoEntity topicInfo) {
        //是否满足解锁条件
        boolean isMeetUnlockCondition = false;

        if (topicInfo != null) {
            List<DimensionInfoEntity> dimensions = topicInfo.getDimensions();
            if (ArrayListUtil.isNotEmpty(dimensions)) {
                //是否有被锁的量表
                boolean hasLockedDimension = false;
                //被锁的量表
                DimensionInfoEntity lockedDimension = null;

                for (DimensionInfoEntity dimension : dimensions) {
                    //被锁状态
                    if (dimension.getIsLocked() == Dictionary.DIMENSION_LOCKED_STATUS_YSE) {
                        lockedDimension = dimension;
                        hasLockedDimension = true;
                        break;
                    }
                }

                //存在被锁的量表则判断是否满足解锁条件
                if (hasLockedDimension) {

                    //解锁必须先做完的量表id集合
                    String [] dimensionIds = lockedDimension.getPreDimensions().split(",");
                    Set<String> dimensionSet = new HashSet<>();
                    for (String dimensionId : dimensionIds) {
                        dimensionSet.add(dimensionId);
                    }

                    isMeetUnlockCondition = true;
                    //存在一个未做完的量表，则视为不满足解锁条件
                    for (DimensionInfoEntity dimension : dimensions) {
                        if (dimensionSet.contains(dimension.getDimensionId())) {
                            if (dimension.getChildDimension() != null
                                    && dimension.getChildDimension().getStatus() == Dictionary.DIMENSION_STATUS_INCOMPLETE) {
                                isMeetUnlockCondition = false;
                            }
                        }
                    }
                }
            }
        }

        return isMeetUnlockCondition;
    }

    /**
     * 判断话题（场景）是否完成，完成则刷新header模型对应的列表项
     * @param headerPosition 列表索引
     * @param topicInfo 话题对象
     */
    protected void refreshHeader(int headerPosition, TopicInfoEntity topicInfo) {
        if (topicInfo != null) {
            List<DimensionInfoEntity> dimensions = topicInfo.getDimensions();
            //量表非空
            if (ArrayListUtil.isNotEmpty(dimensions)) {
                //话题是否完成
                boolean isTopicComplete = true;
                for (DimensionInfoEntity tempDimension : dimensions) {
                    //孩子量表对象为空或者状态为0，视为未完成
                    if (tempDimension.getChildDimension() == null
                            || tempDimension.getChildDimension().getStatus() == Dictionary.DIMENSION_STATUS_INCOMPLETE) {
                        isTopicComplete = false;
                        break;
                    }
                }

                //话题（场景）完成，则刷新header模型对应的列表项
                if (isTopicComplete) {
                    //孩子话题非空
                    if (topicInfo.getChildTopic() != null) {
                        topicInfo.getChildTopic().setStatus(Dictionary.TOPIC_STATUS_COMPLETE);
                        int tempHeaderPosition = headerPosition + recyclerAdapter.getHeaderLayoutCount();
                        recyclerAdapter.notifyItemChanged(tempHeaderPosition);//局部数显列表项，把header计算在内
                    }
                }
            }
        }
    }

    /**
     * 重置为第一页
     */
    protected void resetPageInfo() {
        //页码
        pageNum = 1;
        //话题集合
        topicList = null;
        //总数量
        totalCount = 0;
    }


    /**
     * 刷新孩子话题列表
     */
    protected void refreshChildTopList() {
        //重置为第一页
        resetPageInfo();
        //确保显示了刷新动画
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        //关闭上拉加载功能
        recyclerAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().loadChildTopicListByStatus(defaultChildId,topicStatus,
                pageNum, PAGE_SIZE, new BaseService.ServiceCallback() {
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
                    TopicRootEntity topicData = InjectionWrapperUtil.injectMap(dataMap, TopicRootEntity.class);

                    //后台总记录数
                    totalCount = topicData.getTotal();
                    List<TopicInfoEntity> dataList = topicData.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    //转成列表的数据项
                    List recyclerItem = topicInfoEntityToRecyclerMulti(dataList);

                    //下拉刷新
                    recyclerAdapter.setNewData(recyclerItem);

                    //初始展开
                    recyclerAdapter.expandAll();

                    //判断是否全部加载结束
                    if (recyclerAdapter.getData().size() >= totalCount) {
                        //全部加载结束
                        recyclerAdapter.loadMoreEnd();
                    } else {
                        //本次加载完成
                        recyclerAdapter.loadMoreComplete();
                    }

                    //页码+1
                    pageNum++;
                    topicList = dataList;

                } catch (Exception e) {
                    e.printStackTrace();
                    //设置空布局：没有数据，可重载
                    emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                    //清空列表数据
                    recyclerAdapter.setNewData(null);
                }

            }
        });
    }

    /**
     * 加载更多孩子话题列表
     */
    protected void loadMoreChildTopicList() {
        //关闭下拉刷新功能
        swipeRefreshLayout.setEnabled(false);//防止加载更多和下拉刷新冲突

        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
        if (recyclerAdapter.getData().size() == 0) {
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }

        String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().loadChildTopicListByStatus(defaultChildId,topicStatus,
                pageNum, PAGE_SIZE, new BaseService.ServiceCallback() {
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
                    TopicRootEntity topicData = InjectionWrapperUtil.injectMap(dataMap, TopicRootEntity.class);

                    //后台总记录数
                    totalCount = topicData.getTotal();
                    List<TopicInfoEntity> dataList = topicData.getItems();

                    //空数据处理
                    if (ArrayListUtil.isEmpty(dataList)) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    //转成列表的数据项
                    List recyclerItem = topicInfoEntityToRecyclerMulti(dataList);

                    //当前列表无数据
                    if (recyclerAdapter.getData().size() == 0) {
                        recyclerAdapter.setNewData(recyclerItem);

                    } else {
                        recyclerAdapter.addData(recyclerItem);
                    }

                    //初始展开
                    int totalItem = getTotalItem(dataList);
                    recyclerAdapter.expandAll(totalItem);

                    //判断是否全部加载结束
                    if (recyclerAdapter.getData().size() >= totalCount) {
                        //全部加载结束
                        recyclerAdapter.loadMoreEnd();
                    } else {
                        //本次加载完成
                        recyclerAdapter.loadMoreComplete();
                    }

                    //页码+1
                    pageNum++;
                    if (topicList == null) {
                        topicList = new ArrayList<>();
                    }
                    topicList.addAll(dataList);

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
        });
    }


    /**
     * 话题列表转成用于适配recycler的分组数据模型，以维度（量表行）为基本单元
     *
     * @param topicList 话题集合
     * @return 适配recycler的分组数据模型
     */
    protected List<RecyclerCommonSection<DimensionInfoEntity>> topicInfoEntityToRecyclerSection(List<TopicInfoEntity> topicList) {
        List<RecyclerCommonSection<DimensionInfoEntity>> resList = null;

        if (ArrayListUtil.isNotEmpty(topicList)) {
            resList = new ArrayList<RecyclerCommonSection<DimensionInfoEntity>>();
            //遍历话题列表
            for (TopicInfoEntity topicInfo : topicList) {
                List<DimensionInfoEntity> dimensionInfoList = topicInfo.getDimensions();
                if (ArrayListUtil.isNotEmpty(dimensionInfoList)) {
                    //添加适配器的header模型
                    resList.add(new RecyclerCommonSection<DimensionInfoEntity>(true, topicInfo.getTopicName(), topicInfo));
                    //遍历维度列表（量表）
                    for (DimensionInfoEntity dimensionInfo : dimensionInfoList) {
                        //添加适配器的普通模型
                        resList.add(new RecyclerCommonSection<DimensionInfoEntity>(dimensionInfo, topicInfo));
                    }
                }
            }
        }

        return resList;
    }


    /**
     * 话题列表转成用于适配recycler的分组数据模型，以维度（量表行）为基本单元
     *
     * @param topicList 话题集合
     * @return 适配recycler的分组数据模型
     */
    protected List<MultiItemEntity> topicInfoEntityToRecyclerMulti(List<TopicInfoEntity> topicList) {
        List<MultiItemEntity> resList = null;

        if (ArrayListUtil.isNotEmpty(topicList)) {
            resList = new ArrayList<MultiItemEntity>();
            //遍历话题列表
            for (TopicInfoEntity topicInfo : topicList) {
                //已经添加过了就清空
                if (topicInfo.hasSubItem()) {
                    topicInfo.getSubItems().clear();
                }
                //默认设置为不展开
                topicInfo.setExpanded(false);

                List<DimensionInfoEntity> dimensionInfoList = topicInfo.getDimensions();
                if (ArrayListUtil.isNotEmpty(dimensionInfoList)) {
                    //添加适配器的1级模型
                    resList.add(topicInfo);
                    //遍历维度列表（量表）
                    for (DimensionInfoEntity dimensionInfo : dimensionInfoList) {
                        //添加适配器的2级模型
                        topicInfo.addSubItem(dimensionInfo);
                    }
                }
            }
        }

        return resList;
    }


    /**
     * 获取共总有多少项（话题加量表）
     *
     * @param topicList 话题集合
     * @return 适配recycler的分组数据模型
     */
    protected int getTotalItem(List<TopicInfoEntity> topicList) {
        int total = 0;

        if (ArrayListUtil.isNotEmpty(topicList)) {
            //话题数量
            total = topicList.size();
            //遍历话题列表
            for (TopicInfoEntity topicInfo : topicList) {
                List<DimensionInfoEntity> dimensionInfoList = topicInfo.getDimensions();
                if (ArrayListUtil.isNotEmpty(dimensionInfoList)) {
                    total += dimensionInfoList.size();
                }
            }
        }

        return total;
    }


    /**
     * 操作点击量表
     * @param dimensionInfoEntity
     */
    protected void operateClickDimension(final DimensionInfoEntity dimensionInfoEntity, TopicInfoEntity topicInfoEntity) {
        if( dimensionInfoEntity == null ){
            ToastUtil.showShort(getContext(), "打开测评失败，请稍后再试");
            return;
        }

        //被锁定
        if(dimensionInfoEntity.getIsLocked() == 1){
            //锁定提示
            lockedDimensionTip(dimensionInfoEntity, topicInfoEntity);

        } else {
            //孩子量表对象
            DimensionInfoChildEntity entity = dimensionInfoEntity.getChildDimension();
            //从未开启过的状态
            if (entity == null) {
                //跳转到量表详细页面，传递量表对象和话题对象
                DimensionDetailActivity.startDimensionDetailActivity(getContext(), dimensionInfoEntity, topicInfoEntity);

            } else {//已经开启过的状态
                //未完成状态
                if(entity.getStatus() == 0){
                    //跳转到量表详细页面，传递量表对象和话题对象
                    DimensionDetailActivity.startDimensionDetailActivity(getContext(), dimensionInfoEntity, topicInfoEntity);

                } else {
                    //已完成状态，显示报告
                    showDimensionReport(dimensionInfoEntity);
                }
            }
        }

    }

    /**
     * 显示量表报告
     */
    protected void showDimensionReport(DimensionInfoEntity dimensionInfo) {
//        ToastUtil.showShort(getContext(), "查看该量表报告");
        try {
            new DimensionReportDialog(getContext(), dimensionInfo, null).show();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showShort(getContext(), e.getMessage());
        }
    }

    /**
     * 量表被锁定的提示
     */
    protected void lockedDimensionTip(DimensionInfoEntity dimensionInfoEntity, TopicInfoEntity topicInfoEntity) {
//        ToastUtil.showShort(getContext(), "被锁定");

        if(!TextUtils.isEmpty(dimensionInfoEntity.getPreDimensions())){
            String [] dimensionIds = dimensionInfoEntity.getPreDimensions().split(",");
            List<DimensionInfoEntity> dimensions = topicInfoEntity.getDimensions();
            if(dimensionIds.length>0 && dimensions.size()>0){
                StringBuffer stringBuffer = new StringBuffer("");
                for(int i=0;i<dimensions.size();i++){
                    for(int j=0;j<dimensionIds.length;j++){
                        if(dimensionIds[j].equals(dimensions.get(i).getDimensionId())){
                            stringBuffer.append(dimensions.get(i).getDimensionName());
                            if(j!=dimensionIds.length-1){
                                stringBuffer.append("、");
                            }
                        }
                    }
                }
                if(!TextUtils.isEmpty(stringBuffer.toString())){
                    String str = getActivity().getResources().getString(R.string.lock_tip,stringBuffer.toString());
                    ToastUtil.showLong(getActivity(),str);
                }
            }
        }
    }


    /**
     * 切换布局
     */
    public void switchLayout() {
        //改变布局标记值
        if (layoutType == Dictionary.EXAM_LIST_LAYOUT_TYPE_GRID) {
            layoutType = Dictionary.EXAM_LIST_LAYOUT_TYPE_LINEAR;

        } else if (layoutType == Dictionary.EXAM_LIST_LAYOUT_TYPE_LINEAR) {
            layoutType = Dictionary.EXAM_LIST_LAYOUT_TYPE_GRID;
        }

        //改变列表布局
        changeRecyclerViewLayout(layoutType);
    }


    /**
     * 改变列表布局
     * @param layoutType
     */
    protected void changeRecyclerViewLayout(int layoutType) {
        //线性
        if (layoutType == Dictionary.EXAM_LIST_LAYOUT_TYPE_LINEAR) {
            List<MultiItemEntity> newData = topicInfoEntityToRecyclerMulti(topicList);
            linearRecyclerAdapter.setNewData(newData);
            recyclerAdapter = linearRecyclerAdapter;
            recycleView.setAdapter(recyclerAdapter);
            recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerAdapter.expandAll();

        } else if (layoutType == Dictionary.EXAM_LIST_LAYOUT_TYPE_GRID) {//网格
//            List<RecyclerCommonSection<DimensionInfoEntity>> newData = topicInfoEntityToRecyclerSection(topicList);
//            gridRecyclerAdapter.setNewData(newData);
//            recyclerAdapter = gridRecyclerAdapter;
//            //setAdapter之前调用
//            recycleView.setLayoutManager(new GridLayoutManager(getContext(), 2));
//            recycleView.setAdapter(recyclerAdapter);

            List<MultiItemEntity> newData = topicInfoEntityToRecyclerMulti(topicList);
            gridRecyclerAdapter.setNewData(newData);
            recyclerAdapter = gridRecyclerAdapter;
            recycleView.setAdapter(recyclerAdapter);
            //网格布局管理器（切换后受到了影响，得重新设置对象）
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return recyclerAdapter.getItemViewType(position) == ExamDimensionBaseRecyclerAdapter.LAYOUT_TYPE_BODY ? 1 : gridLayoutManager.getSpanCount();
                }
            });
            //必须在setAdapter之后调用
            recycleView.setLayoutManager(gridLayoutManager);
            recyclerAdapter.expandAll();
        }

        //判断是否全部加载结束
        if (recyclerAdapter.getData().size() >= totalCount) {
            //全部加载结束
            recyclerAdapter.loadMoreEnd();
        } else {
            //本次加载完成
            recyclerAdapter.loadMoreComplete();
        }
//        recycleView.setAdapter(recyclerAdapter);
    }


}

