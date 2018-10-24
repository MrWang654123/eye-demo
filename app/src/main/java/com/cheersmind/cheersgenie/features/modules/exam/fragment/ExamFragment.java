package com.cheersmind.cheersgenie.features.modules.exam.fragment;

import android.support.v4.app.Fragment;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.features.adapter.ExamDimensionLinearRecyclerAdapter;
import com.cheersmind.cheersgenie.features.adapter.ExamDimensionRecyclerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.RecyclerCommonSection;
import com.cheersmind.cheersgenie.features.interfaces.ExamLayoutListener;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.entity.TopicRootEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.module.login.UCManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 测评主页面
 */
public class ExamFragment extends ExamDoingFragment {

    /**
     * 问题提交成功的通知事件的处理
     * @param dimension 量表对象
     */
    protected void onQuestionSubmit(DimensionInfoEntity dimension) {
        //量表
        if (dimension == null) {
            //重置为第一页
            resetPageInfo();
            //刷新孩子话题
            refreshChildTopList();

        } else {

//            //局部刷新量表对应的视图项
//            List<RecyclerCommonSection<DimensionInfoEntity>> data = recyclerAdapter.getData();
//            if (ArrayListUtil.isNotEmpty(data)) {
//                //header模型位置
//                int headerPosition = 0;
//
//                for (int i = 0; i < data.size(); i++) {
//                    RecyclerCommonSection<DimensionInfoEntity> item = data.get(i);
//                    TopicInfoEntity topicInfo = (TopicInfoEntity) item.getInfo();
//                    DimensionInfoEntity t = (DimensionInfoEntity) item.t;
//
//                    //t不为空
//                    if (t != null ) {
//                        //找出同一个量表，设置孩子量表，然后局部刷新列表项
//                        if (t.getTopicId().equals(dimension.getTopicId())
//                                && t.getDimensionId().equals(dimension.getDimensionId())) {
//                            //刷新对应量表的列表项
//                            t.setChildDimension(dimension.getChildDimension());//重置孩子量表对象
//                            int tempPosition = i + recyclerAdapter.getHeaderLayoutCount();
//                            recyclerAdapter.notifyItemChanged(tempPosition);//局部数显列表项，把header计算在内
//
//                            //判断当前话题是否有被锁的量表，有则判断是否满足解锁条件
//                            if (isMeetUnlockCondition(topicInfo)) {
//                                //重置为第一页
//                                resetPageInfo();
//                                //刷新孩子话题
//                                refreshChildTopList();
//
//                            } else {
//                                //判断话题（场景）是否完成，完成则刷新header模型对应的列表项
//                                refreshHeader(headerPosition, topicInfo);
//                            }
//                            break;
//                        }
//
//                    } else {
//                        //t为空则代表是header模型
//                        headerPosition = i;
//                    }
//
//                }
//            }


            //局部刷新量表对应的视图项
            List<MultiItemEntity> data = recyclerAdapter.getData();
            if (ArrayListUtil.isNotEmpty(data)) {
                //header模型位置
                int headerPosition = 0;
                TopicInfoEntity topicInfo = null;
                DimensionInfoEntity t = null;

                for (int i = 0; i < data.size(); i++) {
                    MultiItemEntity item = data.get(i);
                    if (item instanceof TopicInfoEntity) {
                        topicInfo = (TopicInfoEntity) item;
                        headerPosition = i;

                    } else if (item instanceof DimensionInfoEntity) {
                        t = (DimensionInfoEntity) item;
                    }

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

                    }
                }
            }

        }

    }


    /**
     * 刷新孩子话题列表
     */
    @Override
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

                //回调布局切换
                Fragment parentFragment = getParentFragment();
                if (parentFragment != null && parentFragment instanceof ExamLayoutListener) {
                    ((ExamLayoutListener)parentFragment).change(layoutType, false);
                }
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

                    //回调布局切换
                    Fragment parentFragment = getParentFragment();
                    if (parentFragment != null && parentFragment instanceof ExamLayoutListener && pageNum == 2) {
                        ((ExamLayoutListener)parentFragment).change(layoutType, true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //设置空布局：没有数据，可重载
                    emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                    //清空列表数据
                    recyclerAdapter.setNewData(null);

                    //回调布局切换
                    Fragment parentFragment = getParentFragment();
                    if (parentFragment != null && parentFragment instanceof ExamLayoutListener) {
                        ((ExamLayoutListener)parentFragment).change(layoutType, false);
                    }
                }

            }
        });
    }


    /**
     * 加载更多孩子话题列表
     */
    @Override
    protected void loadMoreChildTopicList() {
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

                    //回调布局切换
                    Fragment parentFragment = getParentFragment();
                    if (parentFragment != null && parentFragment instanceof ExamLayoutListener) {
                        ((ExamLayoutListener)parentFragment).change(layoutType, false);
                    }

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

                    //回调布局切换
                    Fragment parentFragment = getParentFragment();
                    if (parentFragment != null && parentFragment instanceof ExamLayoutListener && pageNum == 2) {
                        ((ExamLayoutListener)parentFragment).change(layoutType, true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (recyclerAdapter.getData().size() == 0) {
                        //设置空布局：没有数据，可重载
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);

                        //回调布局切换
                        Fragment parentFragment = getParentFragment();
                        if (parentFragment != null && parentFragment instanceof ExamLayoutListener) {
                            ((ExamLayoutListener)parentFragment).change(layoutType, false);
                        }

                    } else {
                        //加载失败处理
                        recyclerAdapter.loadMoreFail();
                    }
                }

            }
        });
    }


}
