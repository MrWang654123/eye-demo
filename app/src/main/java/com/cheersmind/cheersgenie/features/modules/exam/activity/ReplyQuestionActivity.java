package com.cheersmind.cheersgenie.features.modules.exam.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheersmind.cheersgenie.BuildConfig;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.dto.AnswerDto;
import com.cheersmind.cheersgenie.features.event.LastHandleExamEvent;
import com.cheersmind.cheersgenie.features.event.QuestionSubmitSuccessEvent;
import com.cheersmind.cheersgenie.features.event.WaitingLastHandleRefreshEvent;
import com.cheersmind.cheersgenie.features.interfaces.SoundPlayListener;
import com.cheersmind.cheersgenie.features.interfaces.VoiceButtonUISwitchListener;
import com.cheersmind.cheersgenie.features.interfaces.VoiceControlListener;
import com.cheersmind.cheersgenie.features.interfaces.baidu.MainHandlerConstant;
import com.cheersmind.cheersgenie.features.interfaces.baidu.UiMessageListener;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.base.activity.MasterTabActivity;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.BaseQuestionFragment;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.DefaultQuestionFragment;
import com.cheersmind.cheersgenie.features.modules.mine.activity.MineExamDetailActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.utils.PermissionUtil;
import com.cheersmind.cheersgenie.features.view.ReplyQuestionViewPager;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.dialog.DimensionReportDialog;
import com.cheersmind.cheersgenie.features.view.dialog.IntegralTipDialog;
import com.cheersmind.cheersgenie.features.view.dialog.QuestionCompleteXDialog;
import com.cheersmind.cheersgenie.features.view.dialog.QuestionQuitDialog;
import com.cheersmind.cheersgenie.features.view.dialog.TopicReportDialog;
import com.cheersmind.cheersgenie.features_v2.dto.ExamReportDto;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.ExamTaskDetailActivity;
import com.cheersmind.cheersgenie.features_v2.modules.exam.activity.SelectCourseAssistantActivity;
import com.cheersmind.cheersgenie.features_v2.modules.trackRecord.activity.TrackRecordActivity;
import com.cheersmind.cheersgenie.features_v2.view.dialog.ExamReportDialog;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.OptionsEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionRootEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.SoundPlayUtils;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 回答问题的页面
 */
public class ReplyQuestionActivity extends BaseActivity implements VoiceButtonUISwitchListener, SoundPlayListener {
    //定时器消息
    private static final int MSG_TIMER = 23;

    public static final String TOPIC_INFO = "topic_info";
    public static final String DIMENSION_INFO = "dimension_info";
    public static final String FROM_ACTIVITY_TO_QUESTION = "FROM_ACTIVITY_TO_QUESTION";//从哪个页面进入的答题页

    //话题对象
    private TopicInfoEntity topicInfoEntity;
    //量表对象
    private DimensionInfoEntity dimensionInfoEntity;
    //从哪个页面进入的答题页
    int fromActivityToQuestion;

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
    private TimerTask timerTask;
    //限制空闲计时器（60秒）
    private CountTimer countTimer;
    private static final int LIMIT_TIME = 60000;//限制作答时间

    //问题集合
    List<QuestionInfoEntity> questionList;
    //问题fragment集合
    List<BaseQuestionFragment> fragments = new ArrayList<>();
    //适配器
    MyFragAdapter adapter;
    //当前位置
    int curPosition;

    //保存上一次答过的题目，key：问题ID，value：问题对象
    private ArrayMap<String, QuestionInfoEntity> arrayMapBeforeHasReply = new ArrayMap<String, QuestionInfoEntity>();
    //保存本次答过的题目,key：问题ID（注意是问题ID），value：答案dto
    private ArrayMap<String, AnswerDto> arrayMapCurHasReply = new ArrayMap<String, AnswerDto>();

    //答题总耗时
    int costTime = 0;
    //是否提交成功
    boolean hasSubmitSuccess = false;


    //说明弹窗按钮
    @BindView(R.id.fabDesc)
    ImageView fabDesc;
    //语音播放悬浮按钮
    @BindView(R.id.fabVoicePlay)
    ImageView fabVoicePlay;

    //是否已经显示了报告弹窗
    private boolean hasShowReportDialog;


    /**
     * 打开作答页面
     * @param context 上下文
     * @param dimensionInfoEntity 量表
     */
    public static void startReplyQuestionActivity(Context context,
                                                  DimensionInfoEntity dimensionInfoEntity,
                                                  TopicInfoEntity topicInfoEntity, int fromActivityToQuestion) {
        Intent intent = new Intent(context, ReplyQuestionActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(DIMENSION_INFO, dimensionInfoEntity);
        extras.putSerializable(TOPIC_INFO, topicInfoEntity);
        extras.putInt(FROM_ACTIVITY_TO_QUESTION, fromActivityToQuestion);
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
        xemptyLayout.setOnReloadListener(new View.OnClickListener() {

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
//                doPostSubmitQuestions();
                //显示提交对话框
                showQuestionCompleteDialog();
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
            ToastUtil.showShort(getApplication(), "数据传递有误");
            return;
        }

        topicInfoEntity = (TopicInfoEntity)getIntent().getExtras().getSerializable(TOPIC_INFO);
        dimensionInfoEntity = (DimensionInfoEntity) getIntent().getExtras().getSerializable(DIMENSION_INFO);
        fromActivityToQuestion = getIntent().getIntExtra(FROM_ACTIVITY_TO_QUESTION, Dictionary.FROM_ACTIVITY_TO_QUESTION_MAIN);
        if (dimensionInfoEntity == null
                || dimensionInfoEntity.getChildDimension() == null
                || TextUtils.isEmpty(dimensionInfoEntity.getChildDimension().getChildDimensionId())) {
            ToastUtil.showShort(getApplication(), "数据传递有误..");
            return;
        }

        //初始化说明弹窗按钮，没有说明则隐藏
        String definition = dimensionInfoEntity.getDefinition();
        if (TextUtils.isEmpty(definition)) {
            fabDesc.setVisibility(View.GONE);
        }

        //初始化开页动画，动画结束后加载问题列表
//        initViewGo();
        //加载问题
        loadChildQuestions(dimensionInfoEntity.getChildDimension().getChildDimensionId());

        //初始化计时时间值
        costTime = 0;

        // android 6.0以上动态权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtil.lacksPermissions(ReplyQuestionActivity.this, permissions)) {
                ActivityCompat.requestPermissions(this, permissions, WRITE_EXTERNAL_STORAGE);
            } else {
                //初始化百度音频
                getSynthesizerManager().initialTts();
            }
        } else {
            //初始化百度音频
            getSynthesizerManager().initialTts();
        }

        //初始化声音池
        initSoundPool();
    }


    SoundPool mSoundPool;
    int maxSteams = 10;
    boolean isInitSoundPool;
    boolean isOpenSound;
    int soundId;
    MediaPlayer mediaPlayer;
    /**
     * 初始化声音池
     */
    private void initSoundPool() {
        //本地开关
        isOpenSound = SoundPlayUtils.getInstance().getSoundStatus(getApplicationContext());

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            mSoundPool = new SoundPool.Builder()
//                    .setMaxStreams(maxSteams)
//                    .build();
//        } else {
//            mSoundPool = new SoundPool(maxSteams, AudioManager.STREAM_MUSIC, 5);
//        }
//
//        mSoundPool.load(getApplicationContext(),R.raw.question_click,1);//this是因为写在代码里的一段方法,R.raw.right是指/res/raw包下的相关资源
//        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//            @Override
//            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
//                //soundID       int: a soundID returned by the load() function
//                //leftVolume    float: left volume value (range = 0.0 to 1.0)
//                //rightVolume   float: right volume value (range = 0.0 to 1.0)
//                //priority      int: stream priority (0 = lowest priority)
//                //loop          int: loop mode (0 = no loop, -1 = loop forever)
//                //rate          float: playback rate (1.0 = normal playback, range 0.5 to 2.0)
////                soundPool.play(sampleId,1.0f,1.0f,1,0,1.0f);
//                isInitSoundPool = true;
//                soundId = sampleId;
//            }
//        });

//        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.question_click);
    }


    /**
     * 释放声音池
     */
    private void releaseSoundPool() {
        try {
//            if (mSoundPool != null) {
//                mSoundPool.unload(soundId);
//                mSoundPool.setOnLoadCompleteListener(null);
//                mSoundPool.release();
//                mSoundPool = null;
//            }

//            if (mediaPlayer != null) {
//                mediaPlayer.release();
//                mediaPlayer = null;
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void clickQuestionOption() {
//        if (isInitSoundPool && isOpenSound) {
////            SoundPlayUtils.play(mSoundPool, soundId);
//            mSoundPool.play(soundId, 1, 1, 0, 0, 1);
//        }

        if (isOpenSound) {
//            mediaPlayer.start();
            SoundPlayUtils.getInstance().play();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        //已经加载了题目且当前未显示弹窗，则开启计时
        if (ArrayListUtil.isNotEmpty(questionList)
                && (quitDialog == null || !quitDialog.isShowing())
                && (completeDialog == null || !completeDialog.isShowing())
                && !hasShowReportDialog) {
            startAllTimer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        //释放所有计时器
        releaseAllTimer();
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();

    }*/

    @Override
    public void onDestroy() {
        //释放所有计时器
        releaseAllTimer();
        countTimer = null;

        try {
            //注销事件
            EventBus.getDefault().removeStickyEvent(WaitingLastHandleRefreshEvent.class);
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //停止任何语音
        getSynthesizerManager().stop();
        //清空设置监听
        getSynthesizerManager().setSpeechSynthesizerListener(null);

        //释放pagerView监听器
        vpQuestion.clearOnPageChangeListeners();
        vpQuestion.setTooFastClickListener(null);
//        try {
//            //释放适配器
//            vpQuestion.clearDisappearingChildren();
//            vpQuestion.clearFocus();
//            vpQuestion.setAdapter(null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //释放监听事件
        xemptyLayout.setOnReloadListener(null);
        btnSubmit.setOnClickListener(null);
        btnPre.setOnClickListener(null);
        ivStop.setOnClickListener(null);

        //释放声音
        releaseSoundPool();

        try {
            //释放完成对话框
            if (completeDialog != null) {
                completeDialog.clearListener();
                completeDialog.dismiss();
                completeDialog.cancel();
                completeDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //释放积分对话框
            if (integralTipDialog != null) {
                integralTipDialog.clearListener();
                integralTipDialog.dismiss();
                integralTipDialog.cancel();
                integralTipDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //释放话题报告对话框
            if (topicReportDialog != null) {
                topicReportDialog.clearListener();
                topicReportDialog.dismiss();
                topicReportDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //释放量表报告对话框
            if (dimensionReportDialog != null) {
                dimensionReportDialog.clearListener();
                dimensionReportDialog.dismiss();
                dimensionReportDialog.cancel();
                dimensionReportDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //释放描述对话框
            if (descDialog != null) {
                descDialog.dismiss();
                descDialog.cancel();
                descDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //释放暂停对话框
            if (quitDialog != null) {
                quitDialog.dismiss();
                quitDialog.cancel();
                quitDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //释放通用报告对话框
            if (examReportDto != null) {
                examReportDto.clearListener();
                examReportDto.dismiss();
                examReportDto = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //释放数据集合
            questionList.clear();
            questionList = null;
            try {
                if (fragments != null) {
                    for (BaseQuestionFragment fragment : fragments) {
                        fragment.clearData();
                    }

                    fragments.clear();
                    fragments = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            arrayMapBeforeHasReply.clear();
            arrayMapBeforeHasReply = null;
            arrayMapCurHasReply.clear();
            arrayMapCurHasReply = null;
            topicInfoEntity = null;
            dimensionInfoEntity = null;

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }


    /**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    public void speak(String text) {
        // android 6.0以上动态权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtil.lacksPermissions(ReplyQuestionActivity.this, permissions)) {
                ActivityCompat.requestPermissions(this, permissions, WRITE_EXTERNAL_STORAGE);
                return;
            }
        }

        getSynthesizerManager().speak(text);
    }


    /**
     * 批量播放
     */
    public void batchSpeak(List<Pair<String, String>> texts) {
//        List<Pair<String, String>> texts = new ArrayList<Pair<String, String>>();
//        texts.add(new Pair<String, String>("开始批量播放，", "a0"));
//        texts.add(new Pair<String, String>("123456，", "a1"));
//        texts.add(new Pair<String, String>("欢迎使用百度语音，，，", "a2"));
//        texts.add(new Pair<String, String>("重(chong2)量这个是多音字示例", "a3"));
        // android 6.0以上动态权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtil.lacksPermissions(ReplyQuestionActivity.this, permissions)) {
                ActivityCompat.requestPermissions(this, permissions, WRITE_EXTERNAL_STORAGE);
                return;
            }
        }

        getSynthesizerManager().batchSpeak(texts);
    }


    /**
     * 暂停播放。仅调用speak后生效
     */
    public void pause() {
        getSynthesizerManager().pause();
    }

    /**
     * 继续播放。仅调用speak后生效，调用pause生效
     */
    public void resume() {
        getSynthesizerManager().resume();
    }


    /*
     * 停止合成引擎。即停止播放，合成，清空内部合成队列。
     */
    private void stop() {
        getSynthesizerManager().stop();
    }


    @Override
    public void onHandleMessage(Message msg) {
        super.onHandleMessage(msg);
        switch (msg.what) {
            //语音播放结束
            case MainHandlerConstant.SPEECH_START: {
                Fragment fragment = fragments.get(curPosition);
                if (fragment instanceof VoiceControlListener) {
                    ((VoiceControlListener) fragment).speechStart((String)msg.obj);
                }
                if (BuildConfig.DEBUG) {
                    System.out.println("语音开始播放");
                }
                break;
            }
            //语音播放结束
            case MainHandlerConstant.SPEECH_FINISH: {
                Fragment fragment = fragments.get(curPosition);
                if (fragment instanceof VoiceControlListener) {
                    ((VoiceControlListener) fragment).speechFinish((String)msg.obj);
                }
                if (BuildConfig.DEBUG) {
                    System.out.println("语音播放结束");
                }
                break;
            }
            //合成或者播放错误
            case MainHandlerConstant.ERROR: {
                Fragment fragment = fragments.get(curPosition);
                if (fragment instanceof VoiceControlListener) {
                    ((VoiceControlListener) fragment).speechFinish(null);
                }
                if (BuildConfig.DEBUG) {
                    System.out.println("语音合成或者错误");
                }
                break;
            }
            //定时器修改定时数值
            case MSG_TIMER: {
                costTime++;
                tvTime.setText(String.valueOf(costTime));
                break;
            }
        }
    }


    /**
     * 等待最新操作测评在服务端刷新的消息
     * @param event 事件
     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
    @Subscribe(sticky = true)
    public void onWaitingLastHandleRefreshNotice(WaitingLastHandleRefreshEvent event) {
        //发送最新操作测评通知：更新操作
        EventBus.getDefault().post(new LastHandleExamEvent(LastHandleExamEvent.HANDLE_TYPE_UPDATE));
    }

    /**
     * 加载问题集合
     * @param childDimensionId 孩子量表ID
     */
    private void loadChildQuestions(String childDimensionId){
        //正在加载提示
        xemptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        DataRequestService.getInstance().getChildQuestionsV2(childDimensionId, 0, 1000, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                xemptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                xemptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(),Map.class);
                    QuestionRootEntity rootData = InjectionWrapperUtil.injectMap(dataMap,QuestionRootEntity.class);
                    questionList = rootData.getItems();

                    //排序问题集合，已答的在前面
                    sortForQuestionList(questionList);
                    //初始化问题卡片（fragment）
                    initQuestionFragment(questionList);
                    //开启计时（必须在跳转到第一题之前：限时定时器）
                    startAllTimer();
                    //跳转到第一个显示的题目（默认：未答过的第一题）
                    gotoFirstShowQuestion();

                    //注册粘性事件（等待最新操作量表在服务端刷新的事件）接收器
                    EventBus.getDefault().register(ReplyQuestionActivity.this);

                } catch (Exception e) {
                    e.printStackTrace();
                    //视为找不到文章数据
                    xemptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                }
            }
        }, httpTag, ReplyQuestionActivity.this);

    }


    /**
     * 跳转到第一个显示的题目（默认：未答过的第一题）
     */
    private void gotoFirstShowQuestion() {
        //已答过的题目数量
        int hasAnswer = arrayMapBeforeHasReply.size();

        //当前pageView索引为未答过的第一题，如何已经全部答完，则显示最后一题
        int curPageIndex = (hasAnswer == questionList.size() ? questionList.size() -1 : hasAnswer);
        //跳转
        vpQuestion.setCurrentItem(curPageIndex);
        //初始化时索引如果为0，不会触发onPageSelected，所以手动更新视图，后续切换则由onPageSelected中自动调用更新视图
        if (curPageIndex == 0) {
            //更新视图信息
            updateLastNextText(curPageIndex);
        }

        //已经全部答完，但是未提交（必须在setCurrentItem之后：限时器）
        if (hasAnswer == questionList.size()) {
            //显示自动提交的按钮
            showAutoSubmitButton();
            //显示提交对话框
            showQuestionCompleteDialog();
        }

        //设置最后能显示的页索引
        setLastCanShowPageIndex(curPageIndex);
    }


    /**
     * 排序问题集合，已答的在前面
     * @param questionList 问题列表
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
     * @param questionInfoEntity 问题对象
     */
    private void saveBeforeHasedReply(QuestionInfoEntity questionInfoEntity) {
        if (questionInfoEntity == null) return;
        questionInfoEntity.setHasAnswer(true);
        arrayMapBeforeHasReply.put(questionInfoEntity.getQuestionId(), questionInfoEntity);
    }

    /**
     * 添加当前题目的答案到集合中
     * @param optionsEntity 选项对象
     */
    public void saveCurHasedReply(String childFactorId, OptionsEntity optionsEntity, String optionText) {
        if (optionsEntity == null) return;

        //当前题目如果为上一次进入作答页面时作答过的，则检测答案选项是否改变，有改变的情况才添加到当前作答的题目答案集合中
        String questionId = optionsEntity.getQuestionId();//问题ID
        //上一次已答过
        if (arrayMapBeforeHasReply.containsKey(questionId)) {
            QuestionInfoEntity questionInfoEntity = arrayMapBeforeHasReply.get(questionId);
            QuestionInfoChildEntity questionInfoChildEntity = questionInfoEntity.getChildQuestion();

            if (questionInfoChildEntity != null) {
                String optionId = questionInfoChildEntity.getOptionId();
                //答案没改变则忽略
                if (!TextUtils.isEmpty(optionId) && optionId.equals(optionsEntity.getOptionId())) {
                    //之前修改过一次，第N次修改后，答案又和初始时一样，则把该题答案从当前题目答案集合中移除
                    if (arrayMapCurHasReply.containsKey(questionId)) {
                        arrayMapCurHasReply.remove(questionId);
                    }
                    return;
                }
            }
        }

        //添加到当前题目答案的集合：key为问题ID
        AnswerDto answerDto = assembleAnswerDto(childFactorId, optionsEntity, optionText);
        arrayMapCurHasReply.put(questionId, answerDto);
//        LogUtils.w("当前题目答案的集合长度：" + arrayMapCurHasedReply.size());
    }

    /**
     * 选项转成答案dto
     * @param childFactorId 孩子因子ID
     * @param optionsEntity 选项对象
     * @return 答案dto
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
//        vpQuestion.setOffscreenPageLimit(3);
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

//        //适配器
//        adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
//            @Override
//            public Fragment getItem(int position) {
//                return fragments.get(position);
//            }
//
//            @Override
//            public int getCount() {
//                return fragments.size();
//            }
//        };
        adapter = new MyFragAdapter(getSupportFragmentManager());

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

                //清空语音
                stop();
                //调用离开前的fragment清空处理播放相关视图
                Fragment fragment = fragments.get(curPosition);
                if (fragment instanceof VoiceControlListener) {
                    ((VoiceControlListener)fragment).stop();
                }

                //记录当前位置
                curPosition = position;

                //重启限时计时器
                startLimitCountTimer();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //点击太快监听
        vpQuestion.setTooFastClickListener(new ReplyQuestionViewPager.TooFastClickListener() {
            @Override
            public void onTooFastClick() {
                ToastUtil.showLong(getApplication(), getResources().getString(R.string.click_question_to_fast));
            }
        });

    }


    /**
     * viewpager fragment适配器
     */
    class MyFragAdapter extends FragmentStatePagerAdapter {

        MyFragAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @OnClick({R.id.btn_pre, R.id.fabDesc, R.id.fabVoicePlay, R.id.iv_clock})
    public void onViewClick(View view) {
        switch (view.getId()) {
            //上一题
            case R.id.btn_pre: {
                toPreQuestion();
                break;
            }
            //说明
            case R.id.fabDesc: {
                popupDescWindows();
                break;
            }
            //语音播放
            case R.id.fabVoicePlay: {
                //语音播放
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Fragment fragment = fragments.get(curPosition);
//                        if (fragment instanceof VoiceControlListener) {
//                            ((VoiceControlListener)fragment).play();
//                        }
//                    }
//                }).start();

                //初始设置百度语音监听
                if (!getSynthesizerManager().isHasSetSpeechSynthesizerListener()) {
                    getSynthesizerManager().setSpeechSynthesizerListener(new UiMessageListener(getHandler()));
                }

                Fragment fragment = fragments.get(curPosition);
                if (fragment instanceof VoiceControlListener) {
                    ((VoiceControlListener)fragment).play();
                }
                break;
            }
            //时钟图标（用于测试时快速答题）
            case R.id.iv_clock: {
                String hostType = BuildConfig.HOST_TYPE;
                //调试模式或者非生产环境
                if(BuildConfig.DEBUG || !"product".equals(hostType)){
                    fastAnswerClickLimitCount++;
                    if (fastAnswerClickLimitCount == 3) {
                        fastAnswer();
                    }
                }
                break;
            }
        }
    }

    //快速答题点击次数
    private int fastAnswerClickLimitCount;

    /**
     * 快速答题
     */
    private void fastAnswer() {
        //处理答题信息
        if (arrayMapBeforeHasReply.size() < questionList.size()) {
            for (int i=arrayMapBeforeHasReply.size(); i<questionList.size(); i++) {
                QuestionInfoEntity item = questionList.get(i);
                List<OptionsEntity> options = item.getOptions();
                int selectIndex = new Random().nextInt(options.size());
                saveCurHasedReply(item.getChildFactorId(), options.get(selectIndex), "测试答题");
            }

            //显示自动提交的按钮
            showAutoSubmitButton();
            //显示提交对话框
            showQuestionCompleteDialog();
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
        //当前页是否是能显示的最后一页（默认：未答的第一题的索引）
        boolean curPageIsCanShowLastPage = curPageIndex == lastCanShowPageIndex;

        //不是所有题目的最后一题
        if (curPageIndex < questionList.size() -1) {
            curPageIndex++;

            final int tempCurPageIndex = curPageIndex;
            getHandler().postDelayed(new Runnable() {
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
            //显示自动提交的按钮
            showAutoSubmitButton();
            //显示提交对话框
            showQuestionCompleteDialog();
        }
    }

    IntegralTipDialog integralTipDialog;

//    /**
//     * 提交所有问题答案
//     */
//    private void doPostSubmitQuestions() {
//        LoadingView.getInstance().show(ReplyQuestionActivity.this, httpTag);
//        DataRequestService.getInstance().postQuestionAnswersSubmit(
//                dimensionInfoEntity.getChildDimension().getChildDimensionId(),
//                costTime,
//                arrayMapCurHasedReply.values(),
//                new BaseService.ServiceCallback() {
//                    @Override
//                    public void onFailure(QSCustomException e) {
//                        onFailureDefault(e);
//                    }
//
//                    @Override
//                    public void onResponse(Object obj) {
//                        LoadingView.getInstance().dismiss();
////                        ToastUtil.showShort(getApplicationContext(), "提交问题成功，继续下一步？？");
//                        //标记问题提交成功
//                        hasSubmitSuccess = true;
//                        //释放所有计时器
//                        releaseAllTimer();
//
//                        try {
//                            //提交分量表返回添加了topic_id和topic_dimension_id
//                            Map map = JsonUtil.fromJson(obj.toString(), Map.class);
//                            final DimensionInfoChildEntity dimensionChild = InjectionWrapperUtil.injectMap(map, DimensionInfoChildEntity.class);
//
//                            //设置孩子量表对象
//                            dimensionInfoEntity.setChildDimension(dimensionChild);
//                            //设置话题量表ID
//                            if (!TextUtils.isEmpty(dimensionChild.getTopicDimensionId())) {
//                                dimensionInfoEntity.setTopicDimensionId(dimensionChild.getTopicDimensionId());
//                            }
//
//                            //设置话题ID
//                            if (topicInfoEntity == null) {
//                                topicInfoEntity = new TopicInfoEntity();
//                            }
//                            //话题对象的ID为空，才进行设置
//                            if (TextUtils.isEmpty(topicInfoEntity.getTopicId())
//                                    && !TextUtils.isEmpty(dimensionChild.getTopicId())) {
//                                topicInfoEntity.setTopicId(dimensionChild.getTopicId());
//                            }
//
//                            //如果孩子话题为空，量表对象传null，让测评页面刷新数据（测评页面显示“查看报告”按钮的前提就是要有孩子话题对象）
//                            //孩子话题不为空，但是孩子话题对象的ID为空，说明这个孩子话题对象是本地创建的，必须让测评页面刷新数据（刷新后孩子话题对象中才会有孩子测评ID，用于查看话题报告）
//                            if (topicInfoEntity.getChildTopic() == null
//                                    || TextUtils.isEmpty(topicInfoEntity.getChildTopic().getChildTopicId())) {
//                                //置空孩子量表对象
//                                dimensionInfoEntity.setChildDimension(null);
//                                //发送问题提交成功的事件通知，附带量表对象
//                                EventBus.getDefault().post(new QuestionSubmitSuccessEvent(dimensionInfoEntity));
//                            } else {
//                                //发送问题提交成功的事件通知，附带量表对象
//                                EventBus.getDefault().post(new QuestionSubmitSuccessEvent(dimensionInfoEntity));
//                            }
//
//                            //设置孩子量表对象
//                            dimensionInfoEntity.setChildDimension(dimensionChild);
//                            //发送最新操作测评通知：完成操作
//                            EventBus.getDefault().post(new LastHandleExamEvent(LastHandleExamEvent.HANDLE_TYPE_COMPLETE));
//
//                            //积分提示
//                            integralTipDialog = IntegralUtil.buildIntegralTipDialog(ReplyQuestionActivity.this, obj, new IntegralTipDialog.OnOperationListener() {
//                                @Override
//                                public void onAnimationEnd() {
//                                    try {
//                                        //判断话题是否已完成
//                                        if (dimensionChild.isTopicComplete()) {
//                                            //确保话题对象有孩子话题对象，且孩子测评ID不为空
//                                            if (topicInfoEntity.getChildTopic() == null) {
//                                                TopicInfoChildEntity topicInfoChild = new TopicInfoChildEntity();
//                                                topicInfoEntity.setChildTopic(topicInfoChild);
//                                            }
//                                            //设置孩子测评ID
//                                            topicInfoEntity.getChildTopic().setChildExamId(dimensionChild.getChildExamId());
//
//                                            //请求话题报告（需要TopicId和ChildExamId）
//                                            queryTopicReport(topicInfoEntity);
//
//                                        } else {
//                                            //请求量表报告
//                                            queryDimensionReport(dimensionInfoEntity);
//                                        }
//
//                                        //标记已经显示了报告弹窗
//                                        hasShowReportDialog = true;
//
//                                    } catch (Exception e) {
//                                        onFailure(new QSCustomException("获取报告失败"));
//                                    }
//                                }
//
//                            });
//
//                            if (integralTipDialog != null) {
//                                integralTipDialog.show();
//                            }
//
//                        } catch (Exception e) {
//                            onFailure(new QSCustomException("获取报告失败"));
//                        }
//                    }
//                }, httpTag, ReplyQuestionActivity.this);
//    }


    /**
     * 提交所有问题答案V2
     */
    private void doPostSubmitQuestions() {
        LoadingView.getInstance().show(ReplyQuestionActivity.this, httpTag);
        DataRequestService.getInstance().postQuestionAnswersSubmit(
                dimensionInfoEntity.getChildDimension().getChildDimensionId(),
                costTime,
                arrayMapCurHasReply.values(),
                new BaseService.ServiceCallback() {
                    @Override
                    public void onFailure(QSCustomException e) {
                        onFailureDefault(e);
                    }

                    @Override
                    public void onResponse(Object obj) {
                        LoadingView.getInstance().dismiss();
                        //标记问题提交成功
                        hasSubmitSuccess = true;
                        //释放所有计时器
                        releaseAllTimer();

                        try {
                            //提交分量表返回添加了topic_id和topic_dimension_id
                            Map map = JsonUtil.fromJson(obj.toString(), Map.class);
                            final DimensionInfoChildEntity dimensionChild = InjectionWrapperUtil.injectMap(map, DimensionInfoChildEntity.class);

                            //设置孩子量表对象
                            dimensionInfoEntity.setChildDimension(dimensionChild);
                            //设置话题量表ID
                            if (!TextUtils.isEmpty(dimensionChild.getTopicDimensionId())) {
                                dimensionInfoEntity.setTopicDimensionId(dimensionChild.getTopicDimensionId());
                            }

                            //设置话题ID
                            if (topicInfoEntity == null) {
                                topicInfoEntity = new TopicInfoEntity();
                            }
                            //话题对象的ID为空，才进行设置
                            if (TextUtils.isEmpty(topicInfoEntity.getTopicId())
                                    && !TextUtils.isEmpty(dimensionChild.getTopicId())) {
                                topicInfoEntity.setTopicId(dimensionChild.getTopicId());
                            }

                            //如果孩子话题为空，量表对象传null，让测评页面刷新数据（测评页面显示“查看报告”按钮的前提就是要有孩子话题对象）
                            //孩子话题不为空，但是孩子话题对象的ID为空，说明这个孩子话题对象是本地创建的，必须让测评页面刷新数据（刷新后孩子话题对象中才会有孩子测评ID，用于查看话题报告）
                            if (topicInfoEntity.getChildTopic() == null
                                    || TextUtils.isEmpty(topicInfoEntity.getChildTopic().getChildTopicId())) {
                                //置空孩子量表对象
                                dimensionInfoEntity.setChildDimension(null);
                                //发送问题提交成功的事件通知，附带量表对象
                                EventBus.getDefault().post(new QuestionSubmitSuccessEvent(dimensionInfoEntity));
                            } else {
                                //发送问题提交成功的事件通知，附带量表对象
                                EventBus.getDefault().post(new QuestionSubmitSuccessEvent(dimensionInfoEntity));
                            }

                            //设置孩子量表对象
                            dimensionInfoEntity.setChildDimension(dimensionChild);
                            //发送最新操作测评通知：完成操作
                            EventBus.getDefault().post(new LastHandleExamEvent(LastHandleExamEvent.HANDLE_TYPE_COMPLETE));

                            try {
                                //显示报告弹窗
                                ExamReportDto dto = new ExamReportDto();
                                dto.setChildExamId(dimensionChild.getChildExamId());//孩子测评ID
                                dto.setCompareId(Dictionary.REPORT_COMPARE_AREA_COUNTRY);//对比样本全国
                                //判断显示话题报告还是量表报告
                                if (dimensionChild.isTopicComplete()) {
                                    //只有一个量表则显示量表报告（或者话题对象中的量表集合为空）
                                    if ((ArrayListUtil.isNotEmpty(topicInfoEntity.getDimensions())
                                            && topicInfoEntity.getDimensions().size() == 1)
                                            || ArrayListUtil.isEmpty(topicInfoEntity.getDimensions())) {
                                        dto.setRelationId(dimensionInfoEntity.getTopicDimensionId());
                                        dto.setRelationType(Dictionary.REPORT_TYPE_DIMENSION);
//                                        dto.setDimensionId(dimensionInfoEntity.getDimensionId());//量表ID（目前用于报告推荐内容）

                                    } else {
                                        dto.setRelationId(dimensionInfoEntity.getTopicId());
                                        dto.setRelationType(Dictionary.REPORT_TYPE_TOPIC);
                                    }

                                } else {
                                    dto.setRelationId(dimensionInfoEntity.getTopicDimensionId());
                                    dto.setRelationType(Dictionary.REPORT_TYPE_DIMENSION);
//                                    dto.setDimensionId(dimensionInfoEntity.getDimensionId());//量表ID（目前用于报告推荐内容）
                                }
                                //显示弹窗
                                showExamReportDialog(dto);

                                //标记已经显示了报告弹窗
                                hasShowReportDialog = true;

                            } catch (Exception e) {
//                                onFailure(new QSCustomException("获取报告失败"));
                                //跳转到下一个页面
                                gotoNextActivity();
                            }

                        } catch (Exception e) {
//                            onFailure(new QSCustomException("获取报告失败"));
                            //跳转到下一个页面
                            gotoNextActivity();
                        }
                    }
                }, httpTag, ReplyQuestionActivity.this);
    }


    TopicReportDialog topicReportDialog;
    /**
     * 请求话题报告
     * @param topicInfo 话题对象
     */
    private void queryTopicReport(TopicInfoEntity topicInfo) {
        try {
            if (topicReportDialog == null) {
                topicReportDialog = new TopicReportDialog().setTopicInfo(topicInfo).setListener(new TopicReportDialog.OnOperationListener() {
                    @Override
                    public void onExit() {
                        //跳转到下一个页面
                        gotoNextActivity();
                    }
                });
            }
            if (topicReportDialog != null) {
                topicReportDialog.show(getSupportFragmentManager(), "报告");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showShort(getApplication(), getResources().getString(R.string.operate_fail));
        }
    }

    ExamReportDialog examReportDto;

    /**
     * 显示测评报告弹窗
     * @param dto 报告dto
     */
    private void showExamReportDialog(ExamReportDto dto) throws QSCustomException {
        if (examReportDto == null) {
            examReportDto = new ExamReportDialog().setReportDto(dto).setListener(new ExamReportDialog.OnOperationListener() {
                @Override
                public void onExit() {
                    //跳转到下一个页面
                    gotoNextActivity();
                }
            });
        }
        if (examReportDto != null) {
            examReportDto.show(getSupportFragmentManager(), "报告");
        }
    }

    /**
     * 跳转到下一个页面
     */
    private void gotoNextActivity() {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Class toActivity;
                switch (fromActivityToQuestion) {
                    case Dictionary.FROM_ACTIVITY_TO_QUESTION_MAIN: {
                        //主页面
                        toActivity = MasterTabActivity.class;
                        break;
                    }
                    case Dictionary.FROM_ACTIVITY_TO_QUESTION_MINE: {
                        //我的智评明细
                        toActivity = MineExamDetailActivity.class;
                        break;
                    }
                    case Dictionary.FROM_ACTIVITY_TO_TASK_DETAIL: {
                        //任务详情页
                        toActivity = ExamTaskDetailActivity.class;
                        break;
                    }
                    case Dictionary.FROM_ACTIVITY_TO_TRACK_RECORD: {
                        //成长档案
                        toActivity = TrackRecordActivity.class;
                        break;
                    }
                    case Dictionary.FROM_ACTIVITY_TO_SYS_RMD_COURSE: {
                        //系统推荐选科
                        toActivity = SelectCourseAssistantActivity.class;
                        break;
                    }
                    default: {
                        //默认主页面
                        toActivity = MasterTabActivity.class;
                    }
                }

                //跳转页面
                Intent intent = new Intent(ReplyQuestionActivity.this, toActivity);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        }, 200);
    }

    /**
     * 返回主页面
     */
    private void gotoMainPage() {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //返回主页面
                Intent intent = new Intent(ReplyQuestionActivity.this, MasterTabActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        }, 200);
    }

    DimensionReportDialog dimensionReportDialog;
    /**
     * 请求量表报告
     * @param dimensionInfo 分量表对象，一定得带孩子量表对象
     */
    private void queryDimensionReport(DimensionInfoEntity dimensionInfo) {
        try {
            if (dimensionReportDialog == null) {
                dimensionReportDialog = new DimensionReportDialog(ReplyQuestionActivity.this, dimensionInfo, new DimensionReportDialog.OnOperationListener() {
                    @Override
                    public void onExit() {
                        //跳转到下一个页面
                        gotoNextActivity();
                    }
                });
            }

            dimensionReportDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showShort(getApplication(), getResources().getString(R.string.operate_fail));
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
        if (arrayMapCurHasReply.size() == 0) {
            return;
        }

        DataRequestService.getInstance().postQuestionAnswersSave(
                dimensionInfoEntity.getChildDimension().getChildDimensionId(),
                costTime,
                arrayMapCurHasReply.values(),
                new BaseService.ServiceCallback() {
                    @Override
                    public void onFailure(QSCustomException e) {
//                        onFailureDefault(e);
                        ToastUtil.showShort(getApplication(), "网络错误，保存答案失败");
                    }

                    @Override
                    public void onResponse(Object obj) {
//                        ToastUtil.showShort(getApplicationContext(), "保存答案成功");
                        //发送最新操作测评通知：更新操作
                        EventBus.getDefault().post(new LastHandleExamEvent(LastHandleExamEvent.HANDLE_TYPE_UPDATE));
                    }
                }, httpTag, ReplyQuestionActivity.this);
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
//                            SoundPlayUtils.play(ReplyQuestionActivity.this, 2);
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
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    /**
     * 开启计时
     */
    private void startTimeTask(){
        releaseTimeTask();
        if (timer == null) {
            timer = new Timer();
            getHandler();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    getHandler().sendEmptyMessage(MSG_TIMER);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            costTime++;
//                            tvTime.setText(String.valueOf(costTime));
//                        }
//                    });

                }
            };
            timer.schedule(timerTask, 1000, 1000);
        }
    }


    /**
     * 开启限时计时
     */
    private void startLimitCountTimer(){
        releaseLimitCountTimer();//不需要
        //初始化计时器
        if (countTimer == null) {
            countTimer = new CountTimer(LIMIT_TIME, 1000);
        }
        countTimer.start();
    }

    /**
     * 释放限时计时器
     */
    private void releaseLimitCountTimer() {
        //释放计时器
        if (countTimer != null) {
            countTimer.cancel();
//            countTimer = null;//onDestroy中置空
        }
    }


    /**
     * 开启所有计时器
     */
    private void startAllTimer() {
        //开启计时器
        startTimeTask();
        //开启限时计时器
        startLimitCountTimer();
    }

    /**
     * 释放所有计时器
     */
    private void releaseAllTimer() {
        //释放计时器
        releaseTimeTask();
        //释放限时计时器
        releaseLimitCountTimer();
    }


    QuestionQuitDialog quitDialog;
    /**
     * 显示暂停对话框
     * @param type
     */
    public void showQuestionQuitDialog(int type){
        //释放计时器
        releaseAllTimer();

        try {
            if (quitDialog == null) {
                quitDialog = new QuestionQuitDialog(ReplyQuestionActivity.this,
                        type, new QuestionQuitDialog.OnOperationListener() {
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
                        startAllTimer();
                    }
                });

                if (quitDialog.getWindow() != null) {
                    quitDialog.getWindow().setWindowAnimations(R.style.WUI_Animation_Dialog);
                }
            }

            quitDialog.show();
            quitDialog.setType(type);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    QuestionCompleteXDialog completeDialog = null;
    /**
     * 显示提交对话框
     */
    public void showQuestionCompleteDialog(){
        //释放计时器
        releaseAllTimer();

        try {
            if (completeDialog == null) {
                completeDialog = new QuestionCompleteXDialog(ReplyQuestionActivity.this, new QuestionCompleteXDialog.OnOperationListener() {
                    @Override
                    public void onCancel() {
                        //开启计时器
                        startAllTimer();
                    }

                    @Override
                    public void onConfirm() {
                        //提交所有答案
                        doPostSubmitQuestions();
                    }
                });

                if (completeDialog.getWindow() != null) {
                    completeDialog.getWindow().setWindowAnimations(R.style.WUI_Animation_Dialog);
                }
            }

            completeDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    AlertDialog descDialog;
    /**
     * 弹出须知弹窗
     */
    void popupDescWindows() {
        try {
            if (descDialog == null) {
                String definition = dimensionInfoEntity.getDefinition();
                if (TextUtils.isEmpty(definition)) {
                    definition = getResources().getString(R.string.empty_tip_question_definition);
                }
                //内容
                View view = View.inflate(ReplyQuestionActivity.this, R.layout.text_view_question_definition, null);
                TextView textViewContent = view.findViewById(R.id.tv_content);
                textViewContent.setText(Html.fromHtml(definition));

                descDialog = new AlertDialog.Builder(this)
                        .setTitle("须知")
//                .setMessage(desc)
                        .setView(textViewContent)
//                .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
                        .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //退出操作
                            }
                        })
                        .create();
                if (descDialog.getWindow() != null) {
                    descDialog.getWindow().setWindowAnimations(R.style.WUI_Animation_Dialog);
                }
            }

            descDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void switchToPlayIcon() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fabVoicePlay.setImageResource(R.drawable.voice_play_white);
            }
        });
    }

    @Override
    public void switchToPauseIcon() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fabVoicePlay.setImageResource(R.drawable.voice_pause);
            }
        });
    }


    /**
     * 计时器
     */
    class CountTimer extends CountDownTimer {

        CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            if (BuildConfig.DEBUG) {
                //加上100是为了防止秒数丢失：9秒时显示成8999 /1000 = 8秒
                long second = (l + 100) / 1000;
                System.out.println("限时计时器：" + second);
            }
        }

        @Override
        public void onFinish() {
            try {
                showQuestionQuitDialog(QuestionQuitDialog.QUIT_TYPE_TIMEOUT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

