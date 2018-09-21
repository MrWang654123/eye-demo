package com.cheersmind.cheersgenie.features.modules.base.fragment;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.ExamDimensionRecyclerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.RecyclerCommonSection;
import com.cheersmind.cheersgenie.features.event.DimensionOpenSuccessEvent;
import com.cheersmind.cheersgenie.features.event.QuestionSubmitSuccessEvent;
import com.cheersmind.cheersgenie.features.modules.exam.activity.DimensionDetailActivity;
import com.cheersmind.cheersgenie.features.modules.exam.activity.ReportActivity;
import com.cheersmind.cheersgenie.features.modules.exam.activity.TopicDetailActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.RecyclerLoadMoreView;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.dialog.DimensionReportDialog;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
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
 * 测评主页面
 */
public class ExamFragment extends LazyLoadFragment {
    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    Unbinder unbinder;

    //话题列表（话题基础数据、孩子话题的信息、量表）
    List<TopicInfoEntity> topicList;
    //适配器的数据列表
//    List<RecyclerCommonSection<DimensionInfoEntity>> recyclerItem;
    //适配器
    ExamDimensionRecyclerAdapter recyclerAdapter;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //recycler子项的点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener =  new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            RecyclerCommonSection<DimensionInfoEntity> data = (RecyclerCommonSection<DimensionInfoEntity>) adapter.getData().get(position);
            //非Header模型
            if (!data.isHeader) {
                DimensionInfoEntity dimensionInfoEntity = (DimensionInfoEntity) data.t;
                TopicInfoEntity topicInfoEntity = (TopicInfoEntity) data.getInfo();
                //点击量表项的操作
                operateClickDimension(dimensionInfoEntity, topicInfoEntity);
            }
        }
    };

    //recycler子项的孩子视图点击监听
    BaseQuickAdapter.OnItemChildClickListener recyclerItemChildClickListener =  new BaseQuickAdapter.OnItemChildClickListener() {
        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            //话题详情
            if (view.getId() == R.id.iv_desc) {
                RecyclerCommonSection<DimensionInfoEntity> data = (RecyclerCommonSection<DimensionInfoEntity>) adapter.getData().get(position);
                DimensionInfoEntity dimensionInfoEntity = (DimensionInfoEntity) data.t;
                TopicInfoEntity topicInfoEntity = (TopicInfoEntity) data.getInfo();
                //跳转到话题详情页面
                TopicDetailActivity.startTopicDetailActivity(getContext(), topicInfoEntity.getTopicId());

            } else if (view.getId() == R.id.tv_nav_to_report) {
                RecyclerCommonSection<DimensionInfoEntity> data = (RecyclerCommonSection<DimensionInfoEntity>) adapter.getData().get(position);
                DimensionInfoEntity dimensionInfoEntity = (DimensionInfoEntity) data.t;
                TopicInfoEntity topicInfoEntity = (TopicInfoEntity) data.getInfo();
                //查看话题报告
                ReportActivity.startReportActivity(getContext(), topicInfoEntity);
            }
        }
    };


    //下拉刷新的监听
    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //刷新孩子话题
            refreshChildTopList();
        }
    };
    //上拉加载更多的监听
    BaseQuickAdapter.RequestLoadMoreListener loadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            //加载更多孩子话题
            loadMoreChildTopicList();
        }
    };

    //页长度
    private static final int PAGE_SIZE = 5;
    //页码
    private int pageNum = 1;
    //后台总记录数
    private int totalCount = 0;

    @Override
    protected int setContentView() {
        return R.layout.fragment_exam;
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

//        recyclerItem = new ArrayList<>();
        recyclerAdapter = new ExamDimensionRecyclerAdapter(ExamFragment.this, R.layout.recycleritem_axam, R.layout.recycleritem_axam_header, null);
        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
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
        recycleView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recycleView.setAdapter(recyclerAdapter);
        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        //设置样式刷新显示的位置
        swipeRefreshLayout.setProgressViewOffset(true, -20, 100);

        //空布局重载点击监听
        emptyLayout.setOnLayoutClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //加载更多孩子话题
                loadMoreChildTopicList();
            }
        });
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
     * 量表开启成功的通知事件
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDimensionOpenSuccessNotice(DimensionOpenSuccessEvent event) {
        //已经加载了数据
        if (hasLoaded) {
            //量表
            DimensionInfoEntity dimension = event.getDimension();
            if (dimension == null) {
                //重置为第一页
                resetPageInfo();
                //刷新孩子话题
                refreshChildTopList();

            } else {
                //局部刷新量表对应的视图项
                List<RecyclerCommonSection<DimensionInfoEntity>> data = recyclerAdapter.getData();
                if (ArrayListUtil.isNotEmpty(data)) {
                    //header模型位置
                    int headerPosition = 0;

                    for (int i = 0; i < data.size(); i++) {
                        RecyclerCommonSection<DimensionInfoEntity> item = data.get(i);
                        TopicInfoEntity topicInfo = (TopicInfoEntity) item.getInfo();
                        DimensionInfoEntity t = (DimensionInfoEntity) item.t;

                        //t不为空
                        if (t != null ) {
                            //找出同一个量表，设置孩子量表，然后局部刷新列表项
                            if (t.getTopicId().equals(dimension.getTopicId())
                                    && t.getDimensionId().equals(dimension.getDimensionId())) {
                                //刷新对应量表的列表项
                                t.setChildDimension(dimension.getChildDimension());//重置孩子量表对象
                                int tempPosition = i + recyclerAdapter.getHeaderLayoutCount();
                                recyclerAdapter.notifyItemChanged(tempPosition);//局部数显列表项，把header计算在内

                                break;
                            }

                        } else {
                            //t为空则代表是header模型
                            headerPosition = i;
                        }

                    }
                }
            }
        }
    }

    /**
     * 问题提交成功的通知事件
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQuestionSubmitNotice(QuestionSubmitSuccessEvent event) {
        //已经加载了数据
        if (hasLoaded) {
            //量表
            DimensionInfoEntity dimension = event.getDimension();
            if (dimension == null) {
                //重置为第一页
                resetPageInfo();
                //刷新孩子话题
                refreshChildTopList();

            } else {
                //局部刷新量表对应的视图项
                List<RecyclerCommonSection<DimensionInfoEntity>> data = recyclerAdapter.getData();
                if (ArrayListUtil.isNotEmpty(data)) {
                    //header模型位置
                    int headerPosition = 0;

                    for (int i = 0; i < data.size(); i++) {
                        RecyclerCommonSection<DimensionInfoEntity> item = data.get(i);
                        TopicInfoEntity topicInfo = (TopicInfoEntity) item.getInfo();
                        DimensionInfoEntity t = (DimensionInfoEntity) item.t;

                        //t不为空
                        if (t != null ) {
                            //找出同一个量表，设置孩子量表，然后局部刷新列表项
                            if (t.getTopicId().equals(dimension.getTopicId())
                                    && t.getDimensionId().equals(dimension.getDimensionId())) {
                                //刷新对应量表的列表项
                                t.setChildDimension(dimension.getChildDimension());//重置孩子量表对象
                                int tempPosition = i + recyclerAdapter.getHeaderLayoutCount();
                                recyclerAdapter.notifyItemChanged(tempPosition);//局部数显列表项，把header计算在内

                                //判断当前话题是否有被锁的量表，有则判断是否满足解锁条件
                                if (isMeetUnlockCondition(topicInfo)) {
                                    //重置为第一页
                                    resetPageInfo();
                                    //刷新孩子话题
                                    refreshChildTopList();

                                } else {
                                    //判断话题（场景）是否完成，完成则刷新header模型对应的列表项
                                    refreshHeader(headerPosition, topicInfo);
                                }
                                break;
                            }

                        } else {
                            //t为空则代表是header模型
                            headerPosition = i;
                        }

                    }
                }
            }
        }

    }

    /**
     * 是否满足解锁条件
     * @param topicInfo
     * @return
     */
    private boolean isMeetUnlockCondition(TopicInfoEntity topicInfo) {
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
                            //未做完的条件：1、孩子量表为空；2、孩子量表不为空，且状态为未完成
                            if (dimension.getChildDimension() == null ||
                                    dimension.getChildDimension().getStatus() == Dictionary.DIMENSION_STATUS_INCOMPLETE) {
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
    private void refreshHeader(int headerPosition, TopicInfoEntity topicInfo) {
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
    private void resetPageInfo() {
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
    private void refreshChildTopList() {
        //重置为第一页
        resetPageInfo();
        //确保显示了刷新动画
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        //关闭上拉加载功能
        recyclerAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().loadChildTopicList(defaultChildId, pageNum, PAGE_SIZE, new BaseService.ServiceCallback() {
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
                        emptyLayout.setErrorType(XEmptyLayout.NODATA);
                        return;
                    }

                    //话题列表转成用于适配recycler的分组数据模型，以维度（量表行）为基本单元
                    List<RecyclerCommonSection<DimensionInfoEntity>> recyclerItem = topicInfoEntityToRecyclerSection(dataList);

                    //下拉刷新
                    recyclerAdapter.setNewData(recyclerItem);
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

                } catch (Exception e) {
                    e.printStackTrace();
                    //设置空布局：没有数据，可重载
                    emptyLayout.setErrorType(XEmptyLayout.NODATA_ENABLE_CLICK);
                    //清空列表数据
                    recyclerAdapter.setNewData(null);
                }

            }
        });
    }

    /**
     * 加载更多孩子话题列表
     */
    private void loadMoreChildTopicList() {
        //关闭下拉刷新功能
        swipeRefreshLayout.setEnabled(false);//防止加载更多和下拉刷新冲突

        //设置空布局，当前列表还没有数据的情况，显示通信等待提示
        if (recyclerAdapter.getData().size() == 0) {
            emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
        }

        String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().loadChildTopicList(defaultChildId, pageNum, PAGE_SIZE, new BaseService.ServiceCallback() {
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
                        emptyLayout.setErrorType(XEmptyLayout.NODATA);
                        return;
                    }

                    //话题列表转成用于适配recycler的分组数据模型，以维度（量表行）为基本单元
                    List<RecyclerCommonSection<DimensionInfoEntity>> recyclerItem = topicInfoEntityToRecyclerSection(dataList);

                    //当前列表无数据
                    if (recyclerAdapter.getData().size() == 0) {
                        recyclerAdapter.setNewData(recyclerItem);

                    } else {
                        recyclerAdapter.addData(recyclerItem);
                    }

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

                } catch (Exception e) {
                    e.printStackTrace();
                    if (recyclerAdapter.getData().size() == 0) {
                        //设置空布局：没有数据，可重载
                        emptyLayout.setErrorType(XEmptyLayout.NODATA_ENABLE_CLICK);
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
    private List<RecyclerCommonSection<DimensionInfoEntity>> topicInfoEntityToRecyclerSection(List<TopicInfoEntity> topicList) {
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
     * 操作点击量表
     * @param dimensionInfoEntity
     */
    private void operateClickDimension(final DimensionInfoEntity dimensionInfoEntity, TopicInfoEntity topicInfoEntity) {
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
    private void showDimensionReport(DimensionInfoEntity dimensionInfo) {
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
    private void lockedDimensionTip(DimensionInfoEntity dimensionInfoEntity, TopicInfoEntity topicInfoEntity) {
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

}
