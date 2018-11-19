package com.cheersmind.cheersgenie.features.modules.exam.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.cheersmind.cheersgenie.BuildConfig;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.dto.AnswerDto;
import com.cheersmind.cheersgenie.features.event.LastHandleExamEvent;
import com.cheersmind.cheersgenie.features.event.QuestionSubmitSuccessEvent;
import com.cheersmind.cheersgenie.features.event.WaitingLastHandleRefreshEvent;
import com.cheersmind.cheersgenie.features.interfaces.VoiceControlListener;
import com.cheersmind.cheersgenie.features.interfaces.baidu.MainHandlerConstant;
import com.cheersmind.cheersgenie.features.interfaces.baidu.UiMessageListener;
import com.cheersmind.cheersgenie.features.interfaces.baidu.control.InitConfig;
import com.cheersmind.cheersgenie.features.interfaces.baidu.control.MySyntherizer;
import com.cheersmind.cheersgenie.features.interfaces.baidu.control.NonBlockSyntherizer;
import com.cheersmind.cheersgenie.features.modules.base.activity.BaseActivity;
import com.cheersmind.cheersgenie.features.modules.base.activity.MasterTabActivity;
import com.cheersmind.cheersgenie.features.modules.exam.fragment.DefaultQuestionFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.utils.IntegralUtil;
import com.cheersmind.cheersgenie.features.utils.PermissionUtil;
import com.cheersmind.cheersgenie.features.utils.baidu.AutoCheck;
import com.cheersmind.cheersgenie.features.utils.baidu.OfflineResource;
import com.cheersmind.cheersgenie.features.view.ReplyQuestionViewPager;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.dialog.DimensionReportDialog;
import com.cheersmind.cheersgenie.features.view.dialog.IntegralTipDialog;
import com.cheersmind.cheersgenie.features.view.dialog.QuestionCompleteXDialog;
import com.cheersmind.cheersgenie.features.view.dialog.TopicReportDialog;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.constant.Constant;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.OptionsEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionRootEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
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
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

    //请求读取外部文件
    public static final int READ_EXTERNAL_STORAGE = 950;

    public static final String TOPIC_INFO = "topic_info";
    public static final String DIMENSION_INFO = "dimension_info";
    //话题对象
    private TopicInfoEntity topicInfoEntity;
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
    //当前位置
    int curPosition;

    //保存上一次答过的题目，key：问题ID，value：问题对象
    private ArrayMap<String, QuestionInfoEntity> arrayMapBeforeHasedReply = new ArrayMap();
    //保存本次答过的题目,key：问题ID（注意是问题ID），value：答案dto
    private ArrayMap<String, AnswerDto> arrayMapCurHasedReply = new ArrayMap();

    //答题总耗时
    int costTime = 0;
    //是否提交成功
    boolean hasSubmitSuccess = false;


    // 主控制类，所有合成控制方法从这个类开始
    protected MySyntherizer synthesizer;
    //权限
    String[] permissions = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    //描述悬浮按钮
    @BindView(R.id.fabDesc)
    FloatingActionButton fabDesc;


    /**
     * 打开作答页面
     * @param context
     * @param dimensionInfoEntity
     */
    public static void startReplyQuestionActivity(Context context, DimensionInfoEntity dimensionInfoEntity, TopicInfoEntity topicInfoEntity) {
        Intent intent = new Intent(context, ReplyQuestionActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(DIMENSION_INFO, dimensionInfoEntity);
        extras.putSerializable(TOPIC_INFO, topicInfoEntity);
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
            ToastUtil.showShort(ReplyQuestionActivity.this, "数据传递有误");
            return;
        }

        topicInfoEntity = (TopicInfoEntity)getIntent().getExtras().getSerializable(TOPIC_INFO);
        dimensionInfoEntity = (DimensionInfoEntity) getIntent().getExtras().getSerializable(DIMENSION_INFO);
        if (dimensionInfoEntity == null
                || dimensionInfoEntity.getChildDimension() == null
                || TextUtils.isEmpty(dimensionInfoEntity.getChildDimension().getChildDimensionId())) {
            ToastUtil.showShort(ReplyQuestionActivity.this, "数据传递有误..");
            return;
        }

        //初始化开页动画，动画结束后加载问题列表
//        initViewGo();
        //加载问题
        loadChildQuestions(dimensionInfoEntity.getChildDimension().getChildDimensionId());

        //初始化计时时间值
//        costTime = dimensionInfoEntity.getChildDimension().getCostTime();
        costTime = 0;

        // android 6.0以上动态权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtil.lacksPermissions(ReplyQuestionActivity.this, permissions)) {
                ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE);
            } else {
                initialTts(); // 初始化TTS引擎
            }
        } else {
            initialTts(); // 初始化TTS引擎
        }

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

    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放计时器
        releaseTimeTask();

        try {
            //注销事件
            EventBus.getDefault().removeStickyEvent(WaitingLastHandleRefreshEvent.class);
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //释放音频资源
            if (synthesizer != null) {
                synthesizer.release();
                synthesizer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //请求读取外部文件
        if (requestCode == READ_EXTERNAL_STORAGE){
            //授权成功
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //初始化百度语音
                initialTts();

            } else {//授权失败
                //友好的提示
            }
        }
    }


    /**
     * 初始化引擎，需要的参数均在InitConfig类里
     * <p>
     * DEMO中提供了3个SpeechSynthesizerListener的实现
     * MessageListener 仅仅用log.i记录日志，在logcat中可以看见
     * UiMessageListener 在MessageListener的基础上，对handler发送消息，实现UI的文字更新
     * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
     */
    protected void initialTts() {
        try {
            LoggerProxy.printable(true); // 日志打印在logcat中
            // 设置初始化参数
            // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
            SpeechSynthesizerListener listener = new UiMessageListener(mHandler);

            Map<String, String> params = getParams();

            // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
            InitConfig initConfig = new InitConfig(Constant.BAIDU_APP_ID, Constant.BAIDU_APP_KEY, Constant.BAIDU_SECRET_KEY, Constant.ttsMode, params, listener);

            // 如果您集成中出错，请将下面一段代码放在和demo中相同的位置，并复制InitConfig 和 AutoCheck到您的项目中
            // 上线时请删除AutoCheck的调用
            //非生产环境
            String hostType = BuildConfig.HOST_TYPE;
            if(!"product".equals(hostType)) {
                AutoCheck.getInstance(getApplicationContext()).check(initConfig, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {
                            AutoCheck autoCheck = (AutoCheck) msg.obj;
                            synchronized (autoCheck) {
                                String message = autoCheck.obtainDebugMessage();
                                toPrint(message); // 可以用下面一行替代，在logcat中查看代码
                                // Log.w("AutoCheckMessage", message);
                            }
                        }
                    }

                });
            }
            SpeechSynthesizer instance = SpeechSynthesizer.getInstance();
            synthesizer = new NonBlockSyntherizer(this, initConfig, mHandler); // 此处可以改为MySyntherizer 了解调用过程

        } catch (Exception e) {
            e.printStackTrace();
        }

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
                ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE);
                return;
            }
        }
        //先停止
        stop();

        int result = synthesizer.speak(text);
        System.out.println("播放语音speak结果码："+ result);
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
                ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE);
                return;
            }
        }
        //先停止
        stop();

        int result = synthesizer.batchSpeak(texts);
        System.out.println("播放语音batchSpeak结果码："+ result);
    }


    /**
     * 暂停播放。仅调用speak后生效
     */
    private void pause() {
        if (synthesizer != null) {
            int result = synthesizer.pause();
            System.out.println("播放语音pause结果码：" + result);
        }
    }

    /**
     * 继续播放。仅调用speak后生效，调用pause生效
     */
    private void resume() {
        if (synthesizer != null) {
            int result = synthesizer.resume();
            System.out.println("播放语音resume结果码：" + result);
        }
    }


    /*
     * 停止合成引擎。即停止播放，合成，清空内部合成队列。
     */
    private void stop() {
        if (synthesizer != null) {
            int result = synthesizer.stop();
            System.out.println("播放语音stop结果码：" + result);
        }
    }


    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "4");
        // 设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");

        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
        OfflineResource offlineResource = createOfflineResource(Constant.offlineVoice);
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                offlineResource.getModelFilename());
        return params;
    }


    protected OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(this, voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
            toPrint("【error】:copy files from assets failed." + e.getMessage());
        }
        return offlineResource;
    }


    protected void toPrint(String str) {
        Message msg = Message.obtain();
        msg.obj = str;
        mHandler.sendMessage(msg);
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
                System.out.println("语音开始播放");
                break;
            }
            //语音播放结束
            case MainHandlerConstant.SPEECH_FINISH: {
                Fragment fragment = fragments.get(curPosition);
                if (fragment instanceof VoiceControlListener) {
                    ((VoiceControlListener) fragment).speechFinish((String)msg.obj);
                }
                System.out.println("语音播放结束");
                break;
            }
            //合成或者播放错误
            case MainHandlerConstant.ERROR: {
                Fragment fragment = fragments.get(curPosition);
                if (fragment instanceof VoiceControlListener) {
                    ((VoiceControlListener) fragment).speechFinish(null);
                }
                System.out.println("语音合成或者错误");
                break;
            }
        }
    }


    /**
     * 等待最新操作测评在服务端刷新的消息
     * @param event
     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
    @Subscribe(sticky = true)
    public void onWaitingLastHandleRefreshNotice(WaitingLastHandleRefreshEvent event) {
        //发送最新操作测评通知：更新操作
        EventBus.getDefault().post(new LastHandleExamEvent(LastHandleExamEvent.HANDLE_TYPE_UPDATE));
    }

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

                    //注册粘性事件（等待最新操作量表在服务端刷新的事件）接收器
                    EventBus.getDefault().register(ReplyQuestionActivity.this);

                } catch (Exception e) {
//                    onFailure(new QSCustomException("加载问题失败"));
                    e.printStackTrace();
                    //视为找不到文章数据
                    xemptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
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
        arrayMapBeforeHasedReply.put(questionInfoEntity.getQuestionId(), questionInfoEntity);
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

                //清空语音
                stop();
                //调用离开前的fragment清空处理播放相关视图
                Fragment fragment = fragments.get(curPosition);
                if (fragment instanceof VoiceControlListener) {
                    ((VoiceControlListener)fragment).stop();
                }

                //记录当前位置
                curPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        startTimeTask();
    }


    @OnClick({R.id.btn_pre, R.id.fabVoicePlay, R.id.fabDesc})
    public void onViewClick(View view) {
        switch (view.getId()) {
            //上一题
            case R.id.btn_pre: {
                toPreQuestion();
                break;
            }
            //语音播放
            case R.id.fabVoicePlay: {
                Fragment fragment = fragments.get(curPosition);
                if (fragment instanceof VoiceControlListener) {
                    ((VoiceControlListener)fragment).play();
                }

                break;
            }
            //说明
            case R.id.fabDesc: {
                popupDescWindows();
                break;
            }
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
                                //发送问题提交成功的事件通知，附带量表对象
                                EventBus.getDefault().post(new QuestionSubmitSuccessEvent(null));
                            } else {
                                //发送问题提交成功的事件通知，附带量表对象
                                EventBus.getDefault().post(new QuestionSubmitSuccessEvent(dimensionInfoEntity));
                            }
                            //发送最新操作测评通知：完成操作
                            EventBus.getDefault().post(new LastHandleExamEvent(LastHandleExamEvent.HANDLE_TYPE_COMPLETE));

                            //积分提示
                            IntegralUtil.showIntegralTipDialog(ReplyQuestionActivity.this, obj, new IntegralTipDialog.OnOperationListener() {
                                @Override
                                public void onAnimationEnd() {
                                    try {
                                        //判断话题是否已完成
                                        if (dimensionChild.isTopicComplete()) {
                                            //确保话题对象有孩子话题对象，且孩子测评ID不为空
                                            if (topicInfoEntity.getChildTopic() == null) {
                                                TopicInfoChildEntity topicInfoChild = new TopicInfoChildEntity();
                                                topicInfoEntity.setChildTopic(topicInfoChild);
                                            }
                                            //设置孩子测评ID
                                            topicInfoEntity.getChildTopic().setChildExamId(dimensionChild.getChildExamId());

                                            //请求话题报告（需要TopicId和ChildExamId）
                                            queryTopicReport(topicInfoEntity);

                                        } else {
                                            //请求量表报告
                                            queryDimensionReport(dimensionInfoEntity);
                                        }

                                    } catch (Exception e) {
                                        onFailure(new QSCustomException("获取报告失败"));
                                    }
                                }

                            });

                        } catch (Exception e) {
                            onFailure(new QSCustomException("获取报告失败"));
                        }
                    }
                });
    }

    /**
     * 请求话题报告
     * @param topicInfo 话题对象
     */
    private void queryTopicReport(TopicInfoEntity topicInfo) {
        try {
            new TopicReportDialog().setTopicInfo(topicInfo).setListener(new TopicReportDialog.OnOperationListener() {
                @Override
                public void onExit() {
                    //返回主页面
                    gotoMainPage();
                }
            }).show(getSupportFragmentManager(), "报告");
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showShort(ReplyQuestionActivity.this, e.getMessage());
        }
    }

    /**
     * 返回主页面
     */
    private void gotoMainPage() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //返回主页面
                Intent intent = new Intent(ReplyQuestionActivity.this, MasterTabActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        }, 200);
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
                    gotoMainPage();
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

        QuestionQuitDialog dialog = new QuestionQuitDialog(ReplyQuestionActivity.this,
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

        dialog.getWindow().setWindowAnimations(R.style.WUI_Animation_Dialog);
        dialog.show();
    }

    /**
     * 显示提交对话框
     */
    public void showQuestionCompleteDialog(){
        //释放计时器
        releaseTimeTask();

        QuestionCompleteXDialog dialog = new QuestionCompleteXDialog(ReplyQuestionActivity.this, new QuestionCompleteXDialog.OnOperationListener() {
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
        });

        dialog.getWindow().setWindowAnimations(R.style.WUI_Animation_Dialog);
        dialog.show();
    }


    /**
     * 弹出描述框
     */
    void popupDescWindows() {
        String desc = dimensionInfoEntity.getDescription();
        if (TextUtils.isEmpty(desc)) {
            desc = getResources().getString(R.string.empty_tip_question_desc);
        }
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("须知")
                .setMessage(desc)
                .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        //退出操作
//                    }
//                })
                .create();
        dialog.getWindow().setWindowAnimations(R.style.WUI_Animation_Dialog);
        dialog.show();
    }

}
