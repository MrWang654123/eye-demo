package com.cheersmind.cheersgenie.features.manager;

import android.content.Context;
import android.util.Pair;

import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.cheersmind.cheersgenie.BuildConfig;
import com.cheersmind.cheersgenie.features.interfaces.baidu.UiMessageListener;
import com.cheersmind.cheersgenie.features.interfaces.baidu.control.InitConfig;
import com.cheersmind.cheersgenie.features.interfaces.baidu.control.MySyntherizer;
import com.cheersmind.cheersgenie.features.utils.baidu.OfflineResource;
import com.cheersmind.cheersgenie.main.constant.Constant;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 百度语音管理器
 */
public class SynthesizerManager {

    private Context context;
    // 主控制类，所有合成控制方法从这个类开始
    private MySyntherizer synthesizer;
    //是否已经设置了监听器
    private boolean hasSetSpeechSynthesizerListener;

    //监听器
    SpeechSynthesizerListener speechSynthesizerListener;


    public SynthesizerManager(Context context) {
        this.context = context;
    }

    /**
     * 是否已经初始化了语音
     * @return true 已初始化
     */
    public boolean isInitVoice() {
        return synthesizer != null;
    }

    /**
     * 初始化引擎，需要的参数均在InitConfig类里
     * <p>
     * DEMO中提供了3个SpeechSynthesizerListener的实现
     * MessageListener 仅仅用log.i记录日志，在logcat中可以看见
     * UiMessageListener 在MessageListener的基础上，对handler发送消息，实现UI的文字更新
     * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
     */
    public void initialTts() {
        if (synthesizer == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
//            LoggerProxy.printable(true); // 日志打印在logcat中
                        // 设置初始化参数
                        // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
//                    SpeechSynthesizerListener listener = new UiMessageListener(mHandler);

                        Map<String, String> params = getParams();

                        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
                        InitConfig initConfig = new InitConfig(Constant.BAIDU_APP_ID, Constant.BAIDU_APP_KEY, Constant.BAIDU_SECRET_KEY, Constant.ttsMode, params, null);

                        // 如果您集成中出错，请将下面一段代码放在和demo中相同的位置，并复制InitConfig 和 AutoCheck到您的项目中
                        // 上线时请删除AutoCheck的调用
                        //非生产环境
//            String hostType = BuildConfig.HOST_TYPE;
//            if(!"product".equals(hostType)) {
//                AutoCheck.getInstance(getApplicationContext()).check(initConfig, new Handler() {
//                    @Override
//                    public void handleMessage(Message msg) {
//                        if (msg.what == 100) {
//                            AutoCheck autoCheck = (AutoCheck) msg.obj;
//                            synchronized (autoCheck) {
//                                String message = autoCheck.obtainDebugMessage();
//                                toPrint(message); // 可以用下面一行替代，在logcat中查看代码
//                                // Log.w("AutoCheckMessage", message);
//                            }
//                        }
//                    }
//
//                });
//            }
//                    synthesizer = new NonBlockSyntherizer(ReplyQuestionActivity.this, initConfig, mHandler); // 此处可以改为MySyntherizer 了解调用过程
                        synthesizer = new MySyntherizer(); // 此处可以改为MySyntherizer 了解调用过程
                        synthesizer.init(context, initConfig);
                        setSpeechSynthesizerListener(speechSynthesizerListener);//设置监听

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }


    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return 配置Map
     */
    private Map<String, String> getParams() {
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

    /**
     * 创建离线资源
     * @param voiceType 声音类型
     * @return 离线资源
     */
    private OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(context, voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
        }
        return offlineResource;
    }


    /**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    public void speak(String text) {
        // android 6.0以上动态权限申请
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (PermissionUtil.lacksPermissions(ReplyQuestionActivity.this, permissions)) {
//                ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE);
//                return;
//            }
//        }
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
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (PermissionUtil.lacksPermissions(ReplyQuestionActivity.this, permissions)) {
//                ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE);
//                return;
//            }
//        }
        //先停止
        stop();

        int result = synthesizer.batchSpeak(texts);
        if (BuildConfig.DEBUG) {
            System.out.println("播放语音batchSpeak结果码：" + result);
        }
    }


    /**
     * 暂停播放。仅调用speak后生效
     */
    public void pause() {
        if (synthesizer != null) {
            int result = synthesizer.pause();
            if (BuildConfig.DEBUG) {
                System.out.println("播放语音pause结果码：" + result);
            }
        }
    }

    /**
     * 继续播放。仅调用speak后生效，调用pause生效
     */
    public void resume() {
        if (synthesizer != null) {
            int result = synthesizer.resume();
            if (BuildConfig.DEBUG) {
                System.out.println("播放语音resume结果码：" + result);
            }
        }
    }


    /*
     * 停止合成引擎。即停止播放，合成，清空内部合成队列。
     */
    public void stop() {
        if (synthesizer != null) {
            int result = synthesizer.stop();
            if (BuildConfig.DEBUG) {
                System.out.println("播放语音stop结果码：" + result);
            }
        }
    }

    /**
     * 释放资源
     */
    public void release() {
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


    /**
     * 设置监听器
     * @param speechSynthesizerListener 监听器
     */
    public void setSpeechSynthesizerListener(SpeechSynthesizerListener speechSynthesizerListener) {
        this.speechSynthesizerListener = speechSynthesizerListener;
        if (synthesizer != null) {
            hasSetSpeechSynthesizerListener = true;
            synthesizer.setSpeechSynthesizerListener(speechSynthesizerListener);
        } else {
            hasSetSpeechSynthesizerListener = false;
        }
    }

    /**
     * 是否初始化了
     * @return true 已经初始化了
     */
    public boolean isInit() {
        return synthesizer != null;
    }

    /**
     * 是否已经设置了监听器
     * @return true：是
     */
    public boolean isHasSetSpeechSynthesizerListener() {
        return hasSetSpeechSynthesizerListener;
    }

}
