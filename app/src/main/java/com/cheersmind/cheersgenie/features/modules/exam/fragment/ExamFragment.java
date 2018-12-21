package com.cheersmind.cheersgenie.features.modules.exam.fragment;

import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.event.LocationExamInListEvent;
import com.cheersmind.cheersgenie.features.event.RefreshTaskListEvent;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.ExamEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.module.login.UCManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * 测评主页面
 */
public class ExamFragment extends ExamDoingFragment {

    @Override
    public void onInitView(View contentView) {
        super.onInitView(contentView);

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_exam));
    }

    /**
     * 问题提交成功的通知事件的处理
     * @param dimension 量表对象
     */
    protected void onQuestionSubmit(DimensionInfoEntity dimension) {
        //孩子量表对象
        DimensionInfoChildEntity childDimension = dimension.getChildDimension();

        //量表
        if (childDimension == null) {
            //获取所属测评
            ExamEntity exam = getBelongToExam(dimension);
            if (exam != null) {
                //完成量表数+1
                exam.setCompleteDimensions(exam.getCompleteDimensions() + 1);
                //如果当前测评完成了，则发送任务列表（测评）刷新事件
                if (exam.getCompleteDimensions() == exam.getTotalDimensions()) {
                    //设置完成话题数
                    exam.setCompleteTopics(exam.getTotalTopics());
                    //设置完成状态
                    exam.setChildExamStatus(Dictionary.CHILD_EXAM_STATUS_COMPLETE);
                    EventBus.getDefault().post(new RefreshTaskListEvent());
                }
            }

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

            /*-------------------------------------------*/

            //是否在当前列表中（搜索功能可能使得当前量表不在当前列表中）
//            boolean existInList = false;
//            //局部刷新量表对应的视图项
//            List<MultiItemEntity> data = recyclerAdapter.getData();
//            if (ArrayListUtil.isNotEmpty(data)) {
//                //header模型位置
//                int headerPosition = 0;
//                TopicInfoEntity topicInfo = null;
//                DimensionInfoEntity t = null;
//
//                for (int i = 0; i < data.size(); i++) {
//                    MultiItemEntity item = data.get(i);
//                    if (item instanceof TopicInfoEntity) {
//                        topicInfo = (TopicInfoEntity) item;
//                        headerPosition = i;
//
//                    } else if (item instanceof DimensionInfoEntity) {
//                        t = (DimensionInfoEntity) item;
//                    }
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
//                                //处理话题是否为完成状态，如果已完成则刷新header模型对应的列表项
//                                if (handleTopicIsComplete(topicInfo)) {
//                                    refreshHeader(headerPosition);
//                                }
//                            }
//
//                            //标记当前量表存在当前列表中
//                            existInList = true;
//
//                            break;
//                        }
//
//                    }
//                }
//            }
//
//            //当前量表不存在当前列表中
//            if (!existInList) {
//
//                for (int i = 0; i < topicList.size(); i++) {
//                    TopicInfoEntity topicInfo = topicList.get(i);
//
//                    if (ArrayListUtil.isNotEmpty(topicInfo.getDimensions())) {
//                        for (DimensionInfoEntity t : topicInfo.getDimensions()) {
//                            //找出同一个量表，设置孩子量表，然后局部刷新列表项
//                            if (t.getTopicId().equals(dimension.getTopicId())
//                                    && t.getDimensionId().equals(dimension.getDimensionId())) {
//
//                                t.setChildDimension(dimension.getChildDimension());//重置孩子量表对象
//
//                                //判断当前话题是否有被锁的量表，有则判断是否满足解锁条件
//                                if (isMeetUnlockCondition(topicInfo)) {
//                                    //重置为第一页
//                                    resetPageInfo();
//                                    //刷新孩子话题
//                                    refreshChildTopList();
//
//                                } else {
//                                    //处理话题是否为完成状态
//                                    handleTopicIsComplete(topicInfo);
//                                }
//
//                                break;
//                            }
//                        }
//                    }
//
//                }
//
//            }

            /*-------------------------------------------*/

            //局部刷新量表对应的视图项
            List<MultiItemEntity> data = recyclerAdapter.getData();
            if (ArrayListUtil.isNotEmpty(data)) {
                //测评位置
                int examPosition = 0;
                //header模型位置
                int headerPosition = 0;
                ExamEntity exam = null;
                TopicInfoEntity topicInfo = null;
                DimensionInfoEntity t = null;

                for (int i = 0; i < data.size(); i++) {
                    MultiItemEntity item = data.get(i);

                    if (item instanceof ExamEntity) {
                        exam = (ExamEntity) item;
                        examPosition = i;

                    } else if (item instanceof TopicInfoEntity) {
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
                                //处理话题是否为完成状态，如果已完成则刷新header模型对应的列表项
                                if (handleTopicIsComplete(topicInfo)) {
                                    refreshHeader(headerPosition);
                                }

                                //处理测评项
                                if (exam != null) {
                                    //完成量表数+1
                                    exam.setCompleteDimensions(exam.getCompleteDimensions() + 1);
                                    //如果当前测评完成了，则发送任务列表（测评）刷新事件
                                    if (exam.getCompleteDimensions() == exam.getTotalDimensions()) {
                                        //设置完成话题数
                                        exam.setCompleteTopics(exam.getTotalTopics());
                                        //设置完成状态
                                        exam.setChildExamStatus(Dictionary.CHILD_EXAM_STATUS_COMPLETE);
                                        EventBus.getDefault().post(new RefreshTaskListEvent());
                                    }
                                    try {
                                        //刷新测评项
                                        refreshExamItem(examPosition);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //当前测评和粘性布局中是同一个才刷新粘性布局
                                    if (!TextUtils.isEmpty(exam.getExamName()) && exam.getExamName().equals(tvTitle.getText().toString())) {
                                        //刷新粘性布局
                                        refreshStickyHeaderView(exam);
                                    }
                                }
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
        DataRequestService.getInstance().loadChildExamList(defaultChildId, pageNum, PAGE_SIZE, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onRefreshChildTopListFailed(e);
            }

            @Override
            public void onResponse(Object obj) {
                onRefreshChildTopListSuccess(obj);
            }
        }, httpTag, getActivity());
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
        DataRequestService.getInstance().loadChildExamList(defaultChildId, pageNum, PAGE_SIZE, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onLoadMoreChildTopicListFailed(e);
            }

            @Override
            public void onResponse(Object obj) {
                onLoadMoreChildTopicListSuccess(obj);
            }
        }, httpTag, getActivity());
    }


}
