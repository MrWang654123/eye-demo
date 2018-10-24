package com.cheersmind.cheersgenie.features.modules.exam.fragment;

import android.view.View;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.features.adapter.ExamDimensionRecyclerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.RecyclerCommonSection;
import com.cheersmind.cheersgenie.features.event.DimensionOpenSuccessEvent;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * 正在进行的测评
 */
public class ExamDoingFragment extends ExamBaseFragment {

    @Override
    public void onInitView(View contentView) {
        //未完成状态
        topicStatus = Dictionary.TOPIC_STATUS_INCOMPLETE;

        super.onInitView(contentView);
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
//                //局部刷新量表对应的视图项
//                List<RecyclerCommonSection<DimensionInfoEntity>> data = recyclerAdapter.getData();
//                if (ArrayListUtil.isNotEmpty(data)) {
//                    //header模型位置
//                    int headerPosition = 0;
//
//                    for (int i = 0; i < data.size(); i++) {
//                        RecyclerCommonSection<DimensionInfoEntity> item = data.get(i);
//                        TopicInfoEntity topicInfo = (TopicInfoEntity) item.getInfo();
//                        DimensionInfoEntity t = (DimensionInfoEntity) item.t;
//
//                        //t不为空
//                        if (t != null) {
//                            //找出同一个量表，设置孩子量表，然后局部刷新列表项
//                            if (t.getTopicId().equals(dimension.getTopicId())
//                                    && t.getDimensionId().equals(dimension.getDimensionId())) {
//                                //孩子话题如果为空，则创建孩子话题对象，并设置为未完成状态
//                                TopicInfoChildEntity childTopic = topicInfo.getChildTopic();
//                                if (childTopic == null) {
//                                    childTopic = new TopicInfoChildEntity();
//                                    childTopic.setStatus(Dictionary.TOPIC_STATUS_INCOMPLETE);
//                                    topicInfo.setChildTopic(childTopic);
//                                }
//
//                                //刷新对应量表的列表项
//                                t.setChildDimension(dimension.getChildDimension());//重置孩子量表对象
//                                int tempPosition = i + recyclerAdapter.getHeaderLayoutCount();
//                                recyclerAdapter.notifyItemChanged(tempPosition);//局部数显列表项，把header计算在内
//
//                                break;
//                            }
//
//                        } else {
//                            //t为空则代表是header模型
//                            headerPosition = i;
//                        }
//
//                    }
//                }


                //局部刷新量表对应的视图项
                List<MultiItemEntity> data = recyclerAdapter.getData();
                if (ArrayListUtil.isNotEmpty(data)) {
                    //header模型位置
//                    int headerPosition = 0;
                    TopicInfoEntity topicInfo = null;
                    DimensionInfoEntity t = null;

                    for (int i = 0; i < data.size(); i++) {
                        MultiItemEntity item = data.get(i);
                        if (item instanceof TopicInfoEntity) {
                            topicInfo = (TopicInfoEntity) item;
//                            headerPosition = i;

                        } else if (item instanceof DimensionInfoEntity) {
                            t = (DimensionInfoEntity) item;
                        }

                        //t不为空
                        if (t != null) {
                            //找出同一个量表，设置孩子量表，然后局部刷新列表项
                            if (t.getTopicId().equals(dimension.getTopicId())
                                    && t.getDimensionId().equals(dimension.getDimensionId())) {
                                //孩子话题如果为空，则创建孩子话题对象，并设置为未完成状态
                                //（目前用于开始答题了，但未做答案完成的提交就进入了话题详情页面，判断话题是否开始做过）
                                TopicInfoChildEntity childTopic = topicInfo.getChildTopic();
                                if (childTopic == null) {
                                    childTopic = new TopicInfoChildEntity();
                                    childTopic.setStatus(Dictionary.TOPIC_STATUS_INCOMPLETE);
                                    topicInfo.setChildTopic(childTopic);
                                }

                                //刷新对应量表的列表项
                                t.setChildDimension(dimension.getChildDimension());//重置孩子量表对象
                                int tempPosition = i + recyclerAdapter.getHeaderLayoutCount();
                                recyclerAdapter.notifyItemChanged(tempPosition);//局部数显列表项，把header计算在内

                                break;
                            }

                        }

                    }
                }

            }
        }
    }


}

