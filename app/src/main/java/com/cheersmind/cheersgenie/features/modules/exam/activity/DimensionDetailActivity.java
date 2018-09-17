package com.cheersmind.cheersgenie.features.modules.exam.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.dto.OpenDimensionDto;
import com.cheersmind.cheersgenie.features.event.LastHandleExamEvent;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.utils.StringUtil;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.module.login.UCManager;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 量表详细页面
 */
public class DimensionDetailActivity extends BaseActivity {

    public static final String TOPIC_INFO = "topic_info";
    public static final String DIMENSION_INFO = "dimension_info";

    //量表对象
    private DimensionInfoEntity dimensionInfoEntity;
    //话题对象
    private TopicInfoEntity topicInfoEntity;

    @BindView(R.id.iv_dimension)
    ImageView ivDimension;
    @BindView(R.id.tv_dimension_name)
    TextView tvDimensionName;
    @BindView(R.id.tv_suitable_user)
    TextView tvSuitableUser;
    @BindView(R.id.tv_question_count)
    TextView tvQuestionCount;
    @BindView(R.id.tv_used_count)
    TextView tvUsedCount;
    @BindView(R.id.tv_dimension_desc)
    TextView tvDimensionDesc;
//    @BindView(R.id.tv_dimension_def)
//    TextView tvDimensionDef;
    @BindView(R.id.btn_start_exam)
    Button btnStartExam;
    @BindView(R.id.ll_recommend_content)
    LinearLayout llRecommendContent;
    @BindView(R.id.ll_recommend)
    LinearLayout llRecommend;

    /**
     * 启动量表详细页面
     * @param context
     * @param dimensionInfo
     * @param topicInfoEntity
     */
    public static void startDimensionDetailActivity(Context context, DimensionInfoEntity dimensionInfo, TopicInfoEntity topicInfoEntity) {
        Intent intent = new Intent(context, DimensionDetailActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(DIMENSION_INFO, dimensionInfo);
        extras.putSerializable(TOPIC_INFO, topicInfoEntity);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_dimension_detail;
    }

    @Override
    protected String settingTitle() {
        return "量表详情";
    }

    @Override
    protected void onInitView() {

    }

    @Override
    protected void onInitData() {
        if (getIntent() == null || getIntent().getExtras() == null) {
            ToastUtil.showShort(DimensionDetailActivity.this, "数据传递有误");
            return;
        }
        topicInfoEntity = (TopicInfoEntity)getIntent().getExtras().getSerializable(TOPIC_INFO);
        dimensionInfoEntity = (DimensionInfoEntity) getIntent().getExtras().getSerializable(DIMENSION_INFO);

        //刷新量表视图
        refreshDimensionDetailView(dimensionInfoEntity);
    }


    /**
     * 更新页面上的量表详细信息
     *
     * @param dimensionInfoEntity
     */
    private void refreshDimensionDetailView(DimensionInfoEntity dimensionInfoEntity) {
        //图片
        Glide.with(DimensionDetailActivity.this)
                .load(dimensionInfoEntity.getIcon())
                .thumbnail(0.5f)
                .apply(QSApplication.getDefaultOptions())
                .into(ivDimension);
        //名称
        tvDimensionName.setText(dimensionInfoEntity.getDimensionName());
        //适合人群
        String suitableUser = "";
        //学生
        if (dimensionInfoEntity.getSuitableUser() == Dictionary.Exam_Suitable_User_Student) {
            suitableUser = getResources().getString(R.string.exam_topic_suitable_user, getResources().getString(R.string.student));

        } else if (dimensionInfoEntity.getSuitableUser() == Dictionary.Exam_Suitable_User_Parent) {
            //家长
            suitableUser = getResources().getString(R.string.exam_topic_suitable_user, getResources().getString(R.string.parent));
        } else {
            //默认 "--"
            suitableUser = getResources().getString(R.string.exam_topic_suitable_user, "--");
        }

        tvSuitableUser.setText(suitableUser);
        //问题数量
        String questionCount = getResources().getString(R.string.exam_question_count, dimensionInfoEntity.getQuestionCount()+"");
        tvQuestionCount.setText(questionCount);
        //使用人数
        String useCountStr = getResources().getString(R.string.exam_dimension_use_count, dimensionInfoEntity.getUseCount()+"");
        tvUsedCount.setText(useCountStr);

        //测评介绍（测评中话题、量表的说明统一用description字段，definition暂不使用）
        if (StringUtil.isNotBlank(dimensionInfoEntity.getDescription())) {
            tvDimensionDesc.setText("\u3000\u3000" + dimensionInfoEntity.getDescription());
        } else {
            tvDimensionDesc.setVisibility(View.GONE);
        }
        //测评须知
//        tvDimensionDef.setText(dimensionInfoEntity.getDefinition());
    }


    @OnClick(R.id.btn_start_exam)
    public void click(View view) {
        switch (view.getId()) {
            //开始测评
            case R.id.btn_start_exam:
                startDimension(dimensionInfoEntity, topicInfoEntity);
                break;
        }
    }

    /**
     * 响应开启测评按钮
     * @param dimensionInfoEntity 量表对象
     * @param topicInfoEntity 话题对象
     */
    private void startDimension(DimensionInfoEntity dimensionInfoEntity, TopicInfoEntity topicInfoEntity) {
        if (dimensionInfoEntity == null) {
            //开启量表失败的提示
            doOpenFailedTip();
            return;
        }

        //被锁定
        if(dimensionInfoEntity.getIsLocked() == Dictionary.DIMENSION_LOCKED_STATUS_YSE){
            //锁定提示
            lockedDimensionTip();

        } else {
            //孩子量表对象
            DimensionInfoChildEntity entity = dimensionInfoEntity.getChildDimension();
            //从未开启过的状态
            if (entity == null) {
                //请求开启量表
                doGetStartDimension(dimensionInfoEntity, topicInfoEntity);

            } else {//已经开启过的状态
                //未完成状态，继续作答
                if (entity.getStatus() == Dictionary.DIMENSION_STATUS_INCOMPLETE) {
                    //打开答题页面，传递分量表对象
                    ReplyQuestionActivity.startReplyQuestionActivity(DimensionDetailActivity.this, dimensionInfoEntity);

                } else {
                    //已完成状态，显示报告
                    showDimensionReport();
                }
            }
        }

    }


    /**
     * 显示量表报告
     */
    private void showDimensionReport() {
        ToastUtil.showShort(getApplicationContext(), "查看该量表报告");
    }

    /**
     * 量表被锁定的提示
     */
    private void lockedDimensionTip() {
        ToastUtil.showShort(getApplicationContext(), "带测评被锁定，请先完成其他测评");
    }

    /**
     * 请求开启量表
     * @param dimensionInfoEntity
     */
    private void doGetStartDimension(final DimensionInfoEntity dimensionInfoEntity, TopicInfoEntity topicInfoEntity) {
        //开启量表的dto
        OpenDimensionDto openDimensionDto = new OpenDimensionDto();
        try {
            //孩子话题ID，测试用
//            TopicInfoChildEntity childTopic = topicInfoEntity.getChildTopic();
            //孩子ID
            openDimensionDto.setChildId(UCManager.getInstance().getDefaultChild().getChildId());
            //测评ID
            openDimensionDto.setExamId(topicInfoEntity.getExamId());
            //话题ID
            openDimensionDto.setTopic(topicInfoEntity.getTopicId());
            //量表ID
            openDimensionDto.setDimensionId(dimensionInfoEntity.getDimensionId());

        } catch (Exception e) {
            e.printStackTrace();
            //开启量表失败的提示
            doOpenFailedTip();
            return;
        }

        //通信等待提示
        LoadingView.getInstance().show(DimensionDetailActivity.this);

        DataRequestService.getInstance().startChildDimensionV2(openDimensionDto, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
                //开启量表失败的提示
//                doOpenFailedTip();
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    DimensionInfoChildEntity startEntity = InjectionWrapperUtil.injectMap(dataMap, DimensionInfoChildEntity.class);
                    if (startEntity != null) {
                        //设置孩子量表对象
                        dimensionInfoEntity.setChildDimension(startEntity);
                        //打开答题页面，传递分量表对象
                        ReplyQuestionActivity.startReplyQuestionActivity(DimensionDetailActivity.this, dimensionInfoEntity);

                        //发送最新操作测评通知：更新操作
                        EventBus.getDefault().post(new LastHandleExamEvent(LastHandleExamEvent.HANDLE_TYPE_UPDATE));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //开启量表失败的提示
                    doOpenFailedTip();
                }
            }
        });
    }

    /**
     * 开启量表失败的提示
     */
    private void doOpenFailedTip() {
        ToastUtil.showShort(getApplicationContext(),"开启测评失败");
    }

}

