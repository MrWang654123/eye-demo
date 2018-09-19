package com.cheersmind.cheersgenie.features.modules.exam.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.dto.AnswerDto;
import com.cheersmind.cheersgenie.features.event.LastHandleExamEvent;
import com.cheersmind.cheersgenie.features.event.QuestionSubmitSuccessEvent;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.base.activity.MasterTabActivity;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.DefaultQuestionFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.ReplyQuestionViewPager;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.dialog.DimensionReportDialog;
import com.cheersmind.cheersgenie.features.view.dialog.QuestionCompleteXDialog;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.OptionsEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionRootEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.LogUtils;
import com.cheersmind.cheersgenie.main.util.SoundPlayUtils;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;
import com.cheersmind.cheersgenie.features.view.dialog.QuestionQuitDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 回答问题的页面
 */
public class ReplyQuestionActivity extends BaseActivity {

    public static final String DIMENSION_INFO = "dimension_info";
    //量表对象
    private DimensionInfoEntity dimensionInfoEntity;

    private String testQuestionStr = "{\"total\":4,\"items\":[{\"factor_id\":\"ebaa15c9-b234-d935-3d07-0dcbed75bbcb\",\"child_question\":{\"id\":\"107e3310-f5f8-47a9-809e-f9def6862d18\",\"exam_id\":\"6\",\"update_time\":\"2018-08-03T20:52:27.000+0800\",\"child_id\":\"10123\",\"create_time\":\"2018-08-03T20:29:15.000+0800\",\"score\":2,\"child_factor_id\":\"21fa62b2-6ea2-436f-b1b8-dedcef77e410\",\"user_id\":2200133574,\"option_id\":\"b11ee725-50dd-9823-3968-204c4d8ec05b\",\"flowers\":0,\"question_id\":\"7ba91a4c-37c0-89f6-8b40-ac23a9026017\",\"option_text\":\"\"},\"orderby\":1,\"stem\":\"复习时我准备充分，但真正开始考试时我大脑变得空白。\",\"type\":1,\"question_id\":\"7ba91a4c-37c0-89f6-8b40-ac23a9026017\",\"show_type\":11,\"options\":[{\"content\":\"完全不符合\",\"score\":1,\"orderby\":1,\"option_id\":\"e8c6b2ac-3fd6-5a32-a23c-06d6aef1f7a9\",\"type\":1,\"question_id\":\"7ba91a4c-37c0-89f6-8b40-ac23a9026017\",\"show_value\":0},{\"content\":\"比较不符合\",\"score\":2,\"orderby\":2,\"option_id\":\"21599dd4-3b37-159d-6033-68e5259a8378\",\"type\":1,\"question_id\":\"7ba91a4c-37c0-89f6-8b40-ac23a9026017\",\"show_value\":25},{\"content\":\"部分符合\",\"score\":3,\"orderby\":3,\"option_id\":\"b11ee725-50dd-9823-3968-204c4d8ec05b\",\"type\":1,\"question_id\":\"7ba91a4c-37c0-89f6-8b40-ac23a9026017\",\"show_value\":50},{\"content\":\"大部分符合\",\"score\":4,\"orderby\":4,\"option_id\":\"6147a64a-cb5f-a108-00b1-8ad868f58c3d\",\"type\":1,\"question_id\":\"7ba91a4c-37c0-89f6-8b40-ac23a9026017\",\"show_value\":75},{\"content\":\"完全符合\",\"score\":5,\"orderby\":5,\"option_id\":\"b5ef3f9c-6812-90d4-1ede-f1bf55c068b8\",\"type\":1,\"question_id\":\"7ba91a4c-37c0-89f6-8b40-ac23a9026017\",\"show_value\":100}]},{\"factor_id\":\"ebaa15c9-b234-d935-3d07-0dcbed75bbcb\",\"child_question\":null,\"orderby\":2,\"stem\":\"我感觉自己对考试准备得不充分。\",\"type\":1,\"question_id\":\"ac8f0ded-6e17-c026-da44-7661019b442d\",\"show_type\":11,\"options\":[{\"content\":\"完全不符合\",\"score\":1,\"orderby\":1,\"option_id\":\"a75482f4-0f95-ef6a-2e15-4d6b9f7443f1\",\"type\":1,\"question_id\":\"ac8f0ded-6e17-c026-da44-7661019b442d\",\"show_value\":0},{\"content\":\"比较不符合\",\"score\":2,\"orderby\":2,\"option_id\":\"ef7dedf5-04bd-255d-fdd2-54ab408bdaff\",\"type\":1,\"question_id\":\"ac8f0ded-6e17-c026-da44-7661019b442d\",\"show_value\":25},{\"content\":\"部分符合\",\"score\":3,\"orderby\":3,\"option_id\":\"3798273a-a7a4-2145-bf13-b64de72b1008\",\"type\":1,\"question_id\":\"ac8f0ded-6e17-c026-da44-7661019b442d\",\"show_value\":50},{\"content\":\"大部分符合\",\"score\":4,\"orderby\":4,\"option_id\":\"e204f91f-81a1-7add-5ee6-d66d5de50a67\",\"type\":1,\"question_id\":\"ac8f0ded-6e17-c026-da44-7661019b442d\",\"show_value\":75},{\"content\":\"完全符合\",\"score\":5,\"orderby\":5,\"option_id\":\"ef9fec44-f811-b206-3ff7-d0aee53a2001\",\"type\":1,\"question_id\":\"ac8f0ded-6e17-c026-da44-7661019b442d\",\"show_value\":100}]},{\"factor_id\":\"ebaa15c9-b234-d935-3d07-0dcbed75bbcb\",\"child_question\":null,\"orderby\":16,\"stem\":\"即使我觉得考试内容很熟悉，我还是出错很多。\",\"type\":1,\"question_id\":\"68119618-b06c-2e24-3ac9-035c373b1ff0\",\"show_type\":11,\"options\":[{\"content\":\"完全不符合\",\"score\":1,\"orderby\":1,\"option_id\":\"091fb9c5-5a78-2efa-2d24-ece8ba1d7751\",\"type\":1,\"question_id\":\"68119618-b06c-2e24-3ac9-035c373b1ff0\",\"show_value\":0},{\"content\":\"比较不符合\",\"score\":2,\"orderby\":2,\"option_id\":\"bfc8f9c4-5f17-5b8c-3053-c2748eaf1544\",\"type\":1,\"question_id\":\"68119618-b06c-2e24-3ac9-035c373b1ff0\",\"show_value\":25},{\"content\":\"部分符合\",\"score\":3,\"orderby\":3,\"option_id\":\"6d0cc932-eaff-c947-2a41-e5f86719482f\",\"type\":1,\"question_id\":\"68119618-b06c-2e24-3ac9-035c373b1ff0\",\"show_value\":50},{\"content\":\"大部分符合\",\"score\":4,\"orderby\":4,\"option_id\":\"e6444ed4-69e3-c36c-05b8-4871228eb65e\",\"type\":1,\"question_id\":\"68119618-b06c-2e24-3ac9-035c373b1ff0\",\"show_value\":75},{\"content\":\"完全符合\",\"score\":5,\"orderby\":5,\"option_id\":\"ec18d2f8-9106-656c-fe07-e7eede29d728\",\"type\":1,\"question_id\":\"68119618-b06c-2e24-3ac9-035c373b1ff0\",\"show_value\":100}]},{\"factor_id\":\"ebaa15c9-b234-d935-3d07-0dcbed75bbcb\",\"child_question\":null,\"orderby\":20,\"stem\":\"在快要考试之前，匆忙、胡乱地学习。\",\"type\":1,\"question_id\":\"a82465f6-d3f5-7737-e48f-8ad4b9d469d5\",\"show_type\":11,\"options\":[{\"content\":\"完全不符合\",\"score\":1,\"orderby\":1,\"option_id\":\"3d1cda7d-cbc3-da80-23e6-36ed20c6c6ea\",\"type\":1,\"question_id\":\"a82465f6-d3f5-7737-e48f-8ad4b9d469d5\",\"show_value\":0},{\"content\":\"比较不符合\",\"score\":2,\"orderby\":2,\"option_id\":\"160b8beb-390a-8d38-19a6-633746a76aa8\",\"type\":1,\"question_id\":\"a82465f6-d3f5-7737-e48f-8ad4b9d469d5\",\"show_value\":25},{\"content\":\"部分符合\",\"score\":3,\"orderby\":3,\"option_id\":\"d22874b4-9af7-a4f4-44f9-514dea5aa14e\",\"type\":1,\"question_id\":\"a82465f6-d3f5-7737-e48f-8ad4b9d469d5\",\"show_value\":50},{\"content\":\"大部分符合\",\"score\":4,\"orderby\":4,\"option_id\":\"b1ae845d-2d62-4bfe-74e8-725764f05bf3\",\"type\":1,\"question_id\":\"a82465f6-d3f5-7737-e48f-8ad4b9d469d5\",\"show_value\":75},{\"content\":\"完全符合\",\"score\":5,\"orderby\":5,\"option_id\":\"6ac69a5c-87c8-3653-4dc9-5ca477c6f633\",\"type\":1,\"question_id\":\"a82465f6-d3f5-7737-e48f-8ad4b9d469d5\",\"show_value\":100}]}]}";

    //暂停按钮
    @BindView(R.id.iv_stop)
    ImageView ivStop;
    //时间
    @BindView(R.id.tv_time)
    TextView tvTime;
    //当前问题序号
    @BindView(R.id.tv_question_cur)
    TextView tvQuestionCur;
    //问题总数
    @BindView(R.id.tv_question_count)
    TextView tvQuestionCount;
    //题目
    @BindView(R.id.tv_question_title)
    TextView tvQuestionTitle;
    //题目ViewPager
    @BindView(R.id.vp_question)
    ReplyQuestionViewPager vpQuestion;
    //上一题
    @BindView(R.id.btn_pre)
    Button btnPre;
    // 提交
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    //空布局模块
    @BindView(R.id.emptyLayout)
    XEmptyLayout xemptyLayout;

    //GO相关view
    private View layoutCountDown;
    private TextView tvTimeGo;
    private Timer timerGo;
    private int timeCout = 1;

    //作答计时器
    private Timer timer;

    //问题集合
    List<QuestionInfoEntity> questionList;
    //问题fragment集合
    List<Fragment> fragments = new ArrayList<>();
    //适配器
    FragmentPagerAdapter adapter;

    //保存上一次答过的题目，key：问题ID，value：问题对象
    private ArrayMap<String, QuestionInfoEntity> arrayMapBeforeHasedReply = new ArrayMap();
    //保存本次答过的题目,key：问题ID（注意是问题ID），value：答案dto
    private ArrayMap<String, AnswerDto> arrayMapCurHasedReply = new ArrayMap();

    //答题总耗时
    int costTime = 0;
    //是否提交成功
    boolean hasSubmitSuccess = false;

    /**
     * 打开作答页面
     * @param context
     * @param dimensionInfoEntity
     */
    public static void startReplyQuestionActivity(Context context, DimensionInfoEntity dimensionInfoEntity) {
        Intent intent = new Intent(context, ReplyQuestionActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(DIMENSION_INFO, dimensionInfoEntity);
        intent.putExtras(extras);
        context.startActivity(intent);
    }


    @Override
    protected int setContentView() {
        return R.layout.activity_reply_question;
    }

    @Override
    protected String settingTitle() {
        return null;
    }

    @Override
    protected void onInitView() {
        //重载点击监听
        xemptyLayout.setOnLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //加载问题
                loadChildQuestions(dimensionInfoEntity.getChildDimension().getChildDimensionId());
            }
        });

        //提交按钮点击监听
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //手动提交答案（默认会自动提交，失败的时候才显示手动提交按钮）
                doPostSubmitQuestions();
            }
        });

        //暂停按钮
        ivStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuestionQuitDialog(QuestionQuitDialog.QUIT_TYPE_STOP);
            }
        });
    }

    @Override
    protected void onInitData() {
        if (getIntent() == null || getIntent().getExtras() == null) {
            ToastUtil.showShort(ReplyQuestionActivity.this, "数据传递有误");
            return;
        }

        dimensionInfoEntity = (DimensionInfoEntity) getIntent().getExtras().getSerializable(DIMENSION_INFO);
        if (dimensionInfoEntity == null
                || dimensionInfoEntity.getChildDimension() == null
                || TextUtils.isEmpty(dimensionInfoEntity.getChildDimension().getChildDimensionId())) {
            ToastUtil.showShort(ReplyQuestionActivity.this, "数据传递有误..");
            return;
        }

        //初始化开页动画，动画结束后加载问题列表
        initViewGo();
        //加载问题
        loadChildQuestions(dimensionInfoEntity.getChildDimension().getChildDimensionId());

        //初始化计时时间值
        costTime = dimensionInfoEntity.getChildDimension().getCostTime();
    }


    @Override
    protected void onResume() {
        super.onResume();

        //已经加载了题目，则开启计时
        if (ArrayListUtil.isNotEmpty(questionList)) {
            startTimeTask();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //释放计时器
        releaseTimeTask();
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        //释放计时器
        releaseTimeTask();
    }*/

    /**
     * 加载问题集合
     * @param childDimensionId
     */
    private void loadChildQuestions(String childDimensionId){
        //正在加载提示
        xemptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
//        LoadingView.getInstance().show(this);

        DataRequestService.getInstance().getChildQuestionsV2(childDimensionId, 0, 1000, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                xemptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
//                onFailureDefault(e);
                /*LoadingView.getInstance().dismiss();

                Map dataMap = JsonUtil.fromJson(testQuestionStr,Map.class);
                QuestionRootEntity rootData = InjectionWrapperUtil.injectMap(dataMap,QuestionRootEntity.class);
                questionList = rootData.getItems();
                //排序问题集合，已答的在前面
                sortForQuestionList(questionList);
                //初始化问题卡片（fragment）
                initQuestionFragment(questionList);
                //跳转到第一个显示的题目（默认：未答过的第一题）
                gotoFirstShowQuestion();*/
            }

            @Override
            public void onResponse(Object obj) {
                xemptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);
//                LoadingView.getInstance().dismiss();
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(),Map.class);
                    QuestionRootEntity rootData = InjectionWrapperUtil.injectMap(dataMap,QuestionRootEntity.class);
                    questionList = rootData.getItems();

                    //排序问题集合，已答的在前面
                    sortForQuestionList(questionList);
                    //初始化问题卡片（fragment）
                    initQuestionFragment(questionList);
                    //跳转到第一个显示的题目（默认：未答过的第一题）
                    gotoFirstShowQuestion();
                    //开启计时
                    startTimeTask();

                } catch (Exception e) {
//                    onFailure(new QSCustomException("加载问题失败"));
                    //视为找不到文章数据
                    xemptyLayout.setErrorType(XEmptyLayout.NODATA_ENABLE_CLICK);
                }
            }
        });

    }


    /**
     * 跳转到第一个显示的题目（默认：未答过的第一题）
     */
    private void gotoFirstShowQuestion() {
        //已答过的题目数量
        int hasAnswer = arrayMapBeforeHasedReply.size();

        //已经全部答完，但是未提交
        if (hasAnswer == questionList.size()) {
            //显示自动提交的按钮
            showAutoSubmitButton();

            //显示提交对话框
            showQuestionCompleteDialog();
        }

        //当前pageview索引为未答过的第一题，如何已经全部答完，则显示最后一题
        int curPageIndex = (hasAnswer == questionList.size() ? questionList.size() -1 : hasAnswer);
        //跳转
        vpQuestion.setCurrentItem(curPageIndex);
        //初始化时索引如果为0，不会触发onPageSelected，所以手动更新视图，后续切换则由onPageSelected中自动调用更新视图
        if (curPageIndex == 0) {
            //更新视图信息
            updateLastNextText(curPageIndex);
        }

        //设置最后能显示的页索引
        setLastCanShowPageIndex(curPageIndex);
    }


    /**
     * 排序问题集合，已答的在前面
     * @param questionList
     */
    private void sortForQuestionList(List<QuestionInfoEntity> questionList) {
        if (ArrayListUtil.isEmpty(questionList)) return;
        //已回答
        List<QuestionInfoEntity> hasedReply = new ArrayList<>();
        //未回答
        List<QuestionInfoEntity> noReply = new ArrayList<>();

        for (QuestionInfoEntity questionInfoEntity : questionList) {
            if (questionInfoEntity.getChildQuestion() != null) {
                hasedReply.add(questionInfoEntity);
                //添加之前答过的题目到集合中
                saveBeforeHasedReply(questionInfoEntity);
            } else {
                noReply.add(questionInfoEntity);
            }
        }

        //清空后再添加
        questionList.clear();
        questionList.addAll(hasedReply);
        questionList.addAll(noReply);
    }

    /**
     * 添加之前答过的题目到集合中
     * @param questionInfoEntity
     */
    private void saveBeforeHasedReply(QuestionInfoEntity questionInfoEntity) {
        if (questionInfoEntity == null) return;
        questionInfoEntity.setHasAnswer(true);
        arrayMapBeforeHasedReply.put(questionInfoEntity.getQuestionId(), questionInfoEntity);
    }

    /**
     * 添加当前题目的答案到集合中
     * @param optionsEntity
     */
    public void saveCurHasedReply(String childFactorId, OptionsEntity optionsEntity, String optionText) {
        if (optionsEntity == null) return;

        //当前题目如果为上一次进入作答页面时作答过的，则检测答案选项是否改变，有改变的情况才添加到当前作答的题目答案集合中
        String questionId = optionsEntity.getQuestionId();//问题ID
        //上一次已答过
        if (arrayMapBeforeHasedReply.containsKey(questionId)) {
            QuestionInfoEntity questionInfoEntity = arrayMapBeforeHasedReply.get(questionId);
            QuestionInfoChildEntity questionInfoChildEntity = questionInfoEntity.getChildQuestion();

            if (questionInfoChildEntity != null) {
                String optionId = questionInfoChildEntity.getOptionId();
                //答案没改变则忽略
                if (!TextUtils.isEmpty(optionId) && optionId.equals(optionsEntity.getOptionId())) {
                    //之前修改过一次，第N次修改后，答案又和初始时一样，则把该题答案从当前题目答案集合中移除
                    if (arrayMapCurHasedReply.containsKey(questionId)) {
                        arrayMapCurHasedReply.remove(questionId);
                    }
                    return;
                }
            }
        }

        //添加到当前题目答案的集合：key为问题ID
        AnswerDto answerDto = assembleAnswerDto(childFactorId, optionsEntity, optionText);
        arrayMapCurHasedReply.put(questionId, answerDto);
        LogUtils.w("当前题目答案的集合长度：" + arrayMapCurHasedReply.size());
    }

    /**
     * 选项转成答案dto
     * @param childFactorId
     * @param optionsEntity
     * @return
     */
    private AnswerDto assembleAnswerDto(String childFactorId, OptionsEntity optionsEntity, String optionText) {
        AnswerDto answerDto = new AnswerDto();
        //问题ID
        answerDto.setQuestion_id(optionsEntity.getQuestionId());
        //孩子因子ID
        answerDto.setChild_factor_id(childFactorId);
        //选项ID
        answerDto.setOption_id(optionsEntity.getOptionId());
        //填写的答案
        answerDto.setOption_text(optionText);

        return answerDto;
    }


    /**
     * 初始化问题卡片（fragment）
     */
    private void initQuestionFragment(List<QuestionInfoEntity> questionList) {
        //设置viewpage缓存界面数
//        vpQuestion.setOffscreenPageLimit(questionList.size());
//        vpQuestion.setOffscreenPageLimit(5);
        for (int i = 0; i < questionList.size(); i++) {
            QuestionInfoEntity entity = questionList.get(i);

            Bundle bundle = new Bundle();
            bundle.putSerializable("question_content", entity);
//            bundle.putString("child_factor_id", factorInfoEntity.getChildFactor().getChildFactorId());

//            if (entity.getShowType() == 11) {
//                QsAccordJudgeFragment fg = new QsAccordJudgeFragment();
//                fg.setArguments(bundle);
//                fragments.add(fg);
//            } else if (entity.getShowType() == 12 || entity.getShowType() == 14 || entity.getShowType() == 16) {
//                QsTickedCorrectFragment fg = new QsTickedCorrectFragment();
//                fg.setArguments(bundle);
//                fragments.add(fg);
//            } else if (entity.getShowType() == 15 || entity.getShowType() == 17 || entity.getShowType() == 18) {
//                QsBalanceSelectFragment fg = new QsBalanceSelectFragment();
//                fg.setArguments(bundle);
//                fragments.add(fg);
//            } else if (entity.getShowType() == 13) {
//                QsFrequencySelectFragment fg = new QsFrequencySelectFragment();
//                fg.setArguments(bundle);
//                fragments.add(fg);
//            } else {
//                QsDefaultQuestionFragment fg = new QsDefaultQuestionFragment();
//                fg.setArguments(bundle);
//                fragments.add(fg);
//            }

            DefaultQuestionFragment fg = new DefaultQuestionFragment();
            fg.setArguments(bundle);
            fragments.add(fg);
        }

        //适配器
        adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };

        vpQuestion.setAdapter(adapter);
        //换页监听
        vpQuestion.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //更新视图信息
                updateLastNextText(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        startTimeTask();
    }


    @OnClick(R.id.btn_pre)
    public void click(View view) {
        switch (view.getId()) {
            //上一题
            case R.id.btn_pre:
                toPreQuestion();
                break;
        }
    }

    /**
     * 跳到上一题
     */
    public void toPreQuestion() {
        int curPageIndex = vpQuestion.getCurrentItem();
        //不是第一题
        if (curPageIndex > 0) {
            curPageIndex--;
            vpQuestion.setCurrentItem(curPageIndex,true);
        }
    }

    /**
     * 跳到下一题
     */
    public void toNextQuestion() {
        //当前索引
        int curPageIndex = vpQuestion.getCurrentItem();
        //最后能显示的索引
        int lastCanShowPageIndex = vpQuestion.getLastCanShowPageIndex();
        //当前页是否是能显示的最后一页（默认是：未答的第一题的索引）
        boolean curPageIsCanShowLastPage = curPageIndex == lastCanShowPageIndex;

        //不是所有题目的最后一题
        if (curPageIndex < questionList.size() -1) {
            curPageIndex++;

            final int tempCurPageIndex = curPageIndex;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    vpQuestion.setCurrentItem(tempCurPageIndex, true);
                }
            }, 200);

            //curPageIndex加1之前就是能显示的最后一页了，所以加1后就得重置最后能显示的页索引
            if (curPageIsCanShowLastPage) {
                //设置最后能显示的页索引
                setLastCanShowPageIndex(curPageIndex);
            }

        } else if (curPageIndex == questionList.size() -1) {//最后一题
            //提交所有答案
//            ToastUtil.showShort(ReplyQuestionActivity.this, "提交答案中，请稍等……");
//            doPostSubmitQuestions();

            //显示自动提交的按钮
            showAutoSubmitButton();

            //显示提交对话框
            showQuestionCompleteDialog();
        }
    }

    /**
     * 提交所有问题答案
     */
    private void doPostSubmitQuestions() {
        LoadingView.getInstance().show(ReplyQuestionActivity.this);
        DataRequestService.getInstance().postQuestionAnswersSubmit(
                dimensionInfoEntity.getChildDimension().getChildDimensionId(),
                costTime,
                arrayMapCurHasedReply.values(),
                new BaseService.ServiceCallback() {
                    @Override
                    public void onFailure(QSCustomException e) {
                        onFailureDefault(e);
                    }

                    @Override
                    public void onResponse(Object obj) {
                        LoadingView.getInstance().dismiss();
//                        ToastUtil.showShort(getApplicationContext(), "提交问题成功，继续下一步？？");
                        //标记问题提交成功
                        hasSubmitSuccess = true;
                        //释放计时器
                        releaseTimeTask();

                        try {
                            Map map = JsonUtil.fromJson(obj.toString(), Map.class);
                            DimensionInfoChildEntity dimensionChild = InjectionWrapperUtil.injectMap(map, DimensionInfoChildEntity.class);

                            //请求量表报告
                            dimensionInfoEntity.setChildDimension(dimensionChild);
                            queryDimensionReport(dimensionInfoEntity);

                            //发送问题提交成功的事件通知，附带量表对象
                            EventBus.getDefault().post(new QuestionSubmitSuccessEvent(dimensionInfoEntity));
                            //发送最新操作测评通知：完成操作
                            EventBus.getDefault().post(new LastHandleExamEvent(LastHandleExamEvent.HANDLE_TYPE_COMPLETE));

                        } catch (Exception e) {
                            onFailure(new QSCustomException("获取报告失败"));
                        }
                    }
                });
    }

    /**
     * 请求量表报告
     * @param dimensionInfo 分量表对象，一定得带孩子量表对象
     */
    private void queryDimensionReport(DimensionInfoEntity dimensionInfo) {
        try {
            new DimensionReportDialog(ReplyQuestionActivity.this, dimensionInfo, new DimensionReportDialog.OnOperationListener() {
                @Override
                public void onExit() {
                    //返回主页面
                    Intent intent = new Intent(ReplyQuestionActivity.this, MasterTabActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }).show();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showShort(getApplicationContext(), e.getMessage());
        }
    }


    /**
     * 显示提交的按钮
     */
    private void showAutoSubmitButton() {
        btnSubmit.setVisibility(View.VISIBLE);
    }


    /**
     * 保存当前问题答案到服务器
     */
    private void doPostSaveQuestions() {
        //问题已经提交成功了，则直接return
        if (hasSubmitSuccess) return;
        //当前有作答
        if (arrayMapCurHasedReply.size() == 0) {
            return;
        }

        DataRequestService.getInstance().postQuestionAnswersSave(
                dimensionInfoEntity.getChildDimension().getChildDimensionId(),
                costTime,
                arrayMapCurHasedReply.values(),
                new BaseService.ServiceCallback() {
                    @Override
                    public void onFailure(QSCustomException e) {
//                        onFailureDefault(e);
                        ToastUtil.showShort(getApplicationContext(), "网络错误，保存答案失败");
                    }

                    @Override
                    public void onResponse(Object obj) {
//                        ToastUtil.showShort(getApplicationContext(), "保存答案成功");
                        //发送最新操作测评通知：更新操作
                        EventBus.getDefault().post(new LastHandleExamEvent(LastHandleExamEvent.HANDLE_TYPE_UPDATE));
                    }
                });
    }

    /**
     * 设置最后能显示的页索引
     * @param lastCanShowPageIndex
     */
    private void setLastCanShowPageIndex(int lastCanShowPageIndex) {
        vpQuestion.setLastCanShowPageIndex(lastCanShowPageIndex);
    }

    /**
     * 更新视图信息
     */
    private void updateLastNextText(int pageIndex) {
        QuestionInfoEntity questionInfoEntity = questionList.get(pageIndex);
        //题目
        tvQuestionTitle.setText(questionInfoEntity.getStem());
        //当前题目序号
        tvQuestionCur.setText(String.valueOf(pageIndex+1));
        //题目总数
        tvQuestionCount.setText(String.valueOf(questionList.size()));
        //上一题按钮
        if (pageIndex == 0) {
            //第一题时隐藏
            btnPre.setVisibility(View.GONE);
        } else {
            btnPre.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 回退事件
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //保存当前问题答案到服务器
        doPostSaveQuestions();
    }

    /**
     * 动画
     */
    private void initViewGo(){
        layoutCountDown = (View)findViewById(R.id.layout_count_down);
        tvTimeGo = (TextView)layoutCountDown.findViewById(R.id.tv_time_go);
        startGo();
    };

    private void startGo(){
        layoutCountDown.setVisibility(View.VISIBLE);
        tvTimeGo.setVisibility(View.GONE);
        timeCout = 1;

        timerGo = new Timer();
        timerGo.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(tvTimeGo.getVisibility() == View.GONE){
                            tvTimeGo.setVisibility(View.VISIBLE);
                        }
                        if(timeCout==1){
                            tvTimeGo.setText("GO");
                            SoundPlayUtils.play(2);
                            showTimeAnim();
                            timeCout --;
                        }else{
                            timerGo.cancel();
                        }
                    }
                });
            }
        },1000,1000);
    }

    private void showTimeAnim(){
        Animation showAnim = AnimationUtils.loadAnimation(
                ReplyQuestionActivity.this, R.anim.anim_bottom_in);
        showAnim.setDuration(200);
        tvTimeGo.startAnimation(showAnim);
        showAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layoutCountDown.setVisibility(View.GONE);
                        //加载问题
//                        loadChildQuestions(dimensionInfoEntity.getChildDimension().getChildDimensionId());
                    }
                },1000);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    /**
     * 释放计时器
     */
    private void releaseTimeTask() {
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 开启计时
     */
    private void startTimeTask(){
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            costTime++;
                            tvTime.setText(String.valueOf(costTime));
                        }
                    });

                }
            }, 1000, 1000);
        }
    }

    /**
     * 显示暂停对话框
     * @param type
     */
    public void showQuestionQuitDialog(int type){
        //释放计时器
        releaseTimeTask();

        QuestionQuitDialog questionQuitDialog = new QuestionQuitDialog(ReplyQuestionActivity.this,
                type, new QuestionQuitDialog.QuestionQuitDialogCallback() {
            @Override
            public void onQuesExit() {
                //保存当前问题答案到服务器
                doPostSaveQuestions();
                //退出
                finish();
            }

            @Override
            public void onQuesContinue() {
                //继续答题，开启计时器
                startTimeTask();
            }
        });
        if(ReplyQuestionActivity.this!=null){
            questionQuitDialog.show();
        }
    }

    /**
     * 显示提交对话框
     */
    public void showQuestionCompleteDialog(){
        //释放计时器
        releaseTimeTask();

        new QuestionCompleteXDialog(ReplyQuestionActivity.this, new QuestionCompleteXDialog.OnOperationListener() {
            @Override
            public void onCancel() {
                //继续答题，开启计时器
                startTimeTask();
            }

            @Override
            public void onConfirm() {
                //提交所有答案
                doPostSubmitQuestions();
            }
        }).show();

    }

}