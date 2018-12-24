package com.cheersmind.cheersgenie.features.modules.exam.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.interfaces.SoundPlayListener;
import com.cheersmind.cheersgenie.features.interfaces.VoiceButtonUISwitchListener;
import com.cheersmind.cheersgenie.features.interfaces.VoiceControlListener;
import com.cheersmind.cheersgenie.features.modules.exam.activity.ReplyQuestionActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.main.entity.OptionsEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionInfoEntity;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.SoundPlayUtils;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifTextView;

/**
 * 默认的问题Fragment
 */
public class DefaultQuestionFragment extends Fragment implements VoiceControlListener, SoundPlayListener {

    private View contentView;
    //题目文本
    private TextView tvQuestionTitle;
    //问题选项列表
    private ListView lvQuestion;

    //当前选中选项，默认没选中
    private int curSelect = -1;
    //填写的答案
    String optionText;
    //问题对象
    QuestionInfoEntity questionInfoEntity;
    //选项集合
    List<OptionsEntity> optionsList;

    //时间间隔
    private long INTERVAL_TIME = 220;

    //题目
    String mStem;
    //题目音频播放gft提示
    GifTextView gftVoicePlay;
    //播放的文字集合Pair<String, String>：1、文字；2、ID
    List<Pair<String, String>> texts;
    //播放完整结束
    boolean isPlayFullEnd = true;
    //是否暂停
    boolean isPause = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.fragment_question_default, container,
                    false);
        }

        initView();
        initData();
        return contentView;
    }

    @Override
    public void onDestroyView() {
        //记录当前选中项
        Bundle arguments = getArguments();
        if (arguments != null) {
            arguments.putInt("curSelect", curSelect);
            arguments.putString("optionText", optionText);
        }
        super.onDestroyView();
    }


    private void initView(){
        tvQuestionTitle = contentView.findViewById(R.id.tv_question_title);
        lvQuestion = contentView.findViewById(R.id.lv_question);
        lvQuestion.setAdapter(adapter);
        gftVoicePlay = contentView.findViewById(R.id.gif_voice_play);
    }

    private void initData(){
        //初始化已经选中的项
        Bundle bundle = getArguments();
        if(bundle!=null){
//            childFactorId = bundle.getString("child_factor_id");
            int curSelect = bundle.getInt("curSelect", -1);
            String optionText = bundle.getString("optionText", "");
            if (curSelect != -1) {
                //非第一次初始化
                if (optionsList != null) {
                    this.curSelect = curSelect;
                    this.optionText = optionText;
                    adapter.notifyDataSetChanged();
                }

            } else {
                //第一次初始化
                questionInfoEntity = (QuestionInfoEntity) bundle.getSerializable("question_content");
                if (questionInfoEntity != null) {
                    optionsList = questionInfoEntity.getOptions();
                    //如果之前回答过本题,更新对应选项选中状态
                    QuestionInfoChildEntity childQuestion = questionInfoEntity.getChildQuestion();
                    if (childQuestion != null) {
                        for (int i = 0; i < optionsList.size(); i++) {
                            if (childQuestion.getOptionId().equals(optionsList.get(i).getOptionId())) {
                                //初始化当前选中项
                                this.curSelect = i;
                            }
                        }

                        //填写的答案
                        this.optionText = childQuestion.getOptionText();
                    }

                    if (optionsList != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }

        //初始化题目
        if (questionInfoEntity != null) {
            tvQuestionTitle.setText(questionInfoEntity.getStem());
        }

        //题目
        if (questionInfoEntity != null) {
            mStem = questionInfoEntity.getStem();
        }

        //初始隐藏题目播放提示
        gftVoicePlay.setVisibility(View.INVISIBLE);

    }


    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return optionsList==null?0:optionsList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder viewHolder ;
            if(convertView == null){
                convertView = View.inflate(getActivity(),R.layout.item_evaluate_question,null);
                viewHolder = new ViewHolder();
                viewHolder.initView(convertView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            final OptionsEntity entity = optionsList.get(position);

            //隐藏填写答案文本
            viewHolder.tvOptionText.setVisibility(View.GONE);

            //选项标题
            viewHolder.tvOptionTitle.setText(entity.getContent());
            //选中索引
            if (curSelect == position) {
                //选中图标
//                viewHolder.ivIcon.setBackgroundResource(R.mipmap.option_choice_select);
                viewHolder.ivIcon.setVisibility(View.VISIBLE);
                //选中背景
//                viewHolder.llOption.setBackgroundResource(R.drawable.shape_corner_f5a400_12dp);
                //选中选项标题
//                viewHolder.tvOptionTitle.setTextColor(Color.parseColor("#ffffff"));
                //问题类型：手填
                if (entity.getType()== Dictionary.QUESTION_TYPE_EDIT) {
                    //显示填写答案文本
                    viewHolder.tvOptionText.setVisibility(View.VISIBLE);
                    //设置填写的答案
                    viewHolder.tvOptionText.setText(optionText);
                    //选中的填写答案
                    viewHolder.tvOptionText.setTextColor(Color.parseColor("#444444"));
                }

            } else {
                //未选中图标
//                viewHolder.ivIcon.setBackgroundResource(R.mipmap.option_choice_nor);
                viewHolder.ivIcon.setVisibility(View.INVISIBLE);
                //未选中背景
//                viewHolder.llOption.setBackgroundResource(R.drawable.shape_corner_f4f3ee_12dp);
                //未选中选项标题
//                viewHolder.tvOptionTitle.setTextColor(Color.parseColor("#7b7b7b"));
                //问题类型：手填
                if (entity.getType()== Dictionary.QUESTION_TYPE_EDIT) {
                    //选中的填写答案
                    viewHolder.tvOptionText.setTextColor(Color.parseColor("#898989"));
                }
            }

            //选项点击监听
            viewHolder.llOption.setOnClickListener(new OnMultiClickListener() {

                @Override
                public void onMultiClick(View view) {
                    int preSelect = curSelect;
                    curSelect = position;
                    //问题类型：只选
                    if (entity.getType()== Dictionary.QUESTION_TYPE_SELECT_ONLY) {
                        notifyDataSetChanged();
//                        SoundPlayUtils.play(getContext(),3);
                        clickQuestionOption();
                        //处理答题数据，并跳转到下一题
                        saveReplyInfo(questionInfoEntity.getChildFactorId(), entity, "");
//                        hiddenSoft();
                        SoftInputUtil.closeSoftInput(getActivity());

                    } else if (entity.getType()== Dictionary.QUESTION_TYPE_EDIT) {//问题类型：填写
                        //弹出填写答案的对话框
                        String initContent = preSelect == curSelect ? optionText : "";
                        popupAnswerEditWindows(optionsList.get(curSelect).getContent(), initContent);
                    }
                }

            });

            //语音播放提示
            if (entity.isVoicePlayingTipShow()) {
                viewHolder.gifTextView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.gifTextView.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

    };


    @Override
    public void clickQuestionOption() {
        if (getActivity() != null && getActivity() instanceof SoundPlayListener) {
            ((SoundPlayListener) getActivity()).clickQuestionOption();
        }
    }


    class ViewHolder{
        //布局容器
        private View llOption;
        //选中图标
        private ImageView ivIcon;
        //选项标题
        private TextView tvOptionTitle;
        //填写的答案
        private TextView tvOptionText;
        //语音播放提示
        private GifTextView gifTextView;

        void initView(View view){
            llOption = view.findViewById(R.id.ll_option);
            ivIcon = view.findViewById(R.id.iv_icon);
            tvOptionTitle = view.findViewById(R.id.tv_option_title);
            tvOptionText = view.findViewById(R.id.tv_option_text);
            gifTextView = view.findViewById(R.id.gif_voice_play);
        }
    }


    /**
     * 弹出填写答案对话框
     */
    private void popupAnswerEditWindows(String title, String content) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_window_edit_answer, null);
        final Dialog dialog = new Dialog(getContext(), R.style.dialog_bottom_full);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setContentView(view);

        WindowManager.LayoutParams lp = window.getAttributes(); // 获取对话框当前的参数值
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽度占满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.BottomDialog_Animation);//动画
        dialog.show();

        //标题
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(title);
        //评论编辑框
        final EditText etAnswer = view.findViewById(R.id.et_answer);
        //初始化内容
        if (!TextUtils.isEmpty(content)) {
            etAnswer.setText(content);
            etAnswer.setSelection(content.length());
        }

        //确定按钮
        final Button btnConfirm = view.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //确定答案
                if (!TextUtils.isEmpty(etAnswer.getText().toString())) {
                    String text = etAnswer.getText().toString().trim();
                    if (!TextUtils.isEmpty(text)) {
                        confirmAnswer(text, optionsList.get(curSelect));
                    }
                }
            }
        });
        //关闭按钮
        final Button btnClose = view.findViewById(R.id.btn_cancel);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        etAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //没输入，则确定按钮禁用
                if (s.length() == 0) {
                    btnConfirm.setEnabled(false);
                } else {
                    String text = s.toString();
                    //非空
                    if (TextUtils.isEmpty(text.trim())) {
                        btnConfirm.setEnabled(false);
                    } else {
                        btnConfirm.setEnabled(true);
                    }
                }
            }
        });

        //内容为空，则初始使确认按钮失效
        if (TextUtils.isEmpty(content)) {
            btnConfirm.setEnabled(false);
        }

//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface iDialog) {
//                BaseActivity.fixInputMethodManagerLeak(dialog.getContext());
//            }
//        });

    }


    /**
     * 确定填写的答案
     * @param answer
     */
    private void confirmAnswer(String answer, OptionsEntity entity) {
        optionText = answer;

        adapter.notifyDataSetChanged();
//        SoundPlayUtils.play(getContext(),3);
        clickQuestionOption();

        //处理答题数据，并跳转到下一题
        saveReplyInfo(questionInfoEntity.getChildFactorId(), entity, optionText);
//        hiddenSoft();
        SoftInputUtil.closeSoftInput(getActivity());
    }


    private void showAnimBg(TextView tv, final OptionsEntity optionsEntity){
        tv.setVisibility(View.VISIBLE);
//        SoundPlayUtils.play(getContext(),3);
        clickQuestionOption();
        //处理答题数据，并跳转到下一题
//        saveReplyInfo(optionsEntity);
    }

    /**
     * 保存当前题目的答题
     * @param childFactorId 孩子因子ID
     * @param optionsEntity 选项对象
     * @param optionText 选项填写的文本
     */
    private void saveReplyInfo(String childFactorId, OptionsEntity optionsEntity, String optionText) {
        ReplyQuestionActivity activity = (ReplyQuestionActivity)getActivity();
        if (activity != null) {
            //处理答题信息
            activity.saveCurHasedReply(childFactorId, optionsEntity, optionText);
            //跳到下一题
            activity.toNextQuestion();
        }
    }

//    /**
//     * 隐藏软键盘
//     */
//    private void hiddenSoft(){
//        if(getActivity().getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE){
//            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//        }
//    }


    /**
     * 播放
     */
    @Override
    public void play() {
        ReplyQuestionActivity activity = (ReplyQuestionActivity) getActivity();
        //完整播放完
        if (isPlayFullEnd) {
            //语音播放
            try {
                if (activity != null) {
                    //空则初始化播放文本集合
                    if (ArrayListUtil.isEmpty(texts)) {
                        texts = new ArrayList<Pair<String, String>>();
                        //题目
                        String tempStem = mStem;
                        if (!TextUtils.isEmpty(mStem) && !(mStem.substring(mStem.length() - 1)).equals(Dictionary.VOICE_TEXT_END_SYMBOL)) {
                            tempStem += Dictionary.VOICE_TEXT_END_SYMBOL;
                            tempStem += Dictionary.VOICE_TEXT_END_SYMBOL;
                        } else {
                            tempStem += Dictionary.VOICE_TEXT_END_SYMBOL;
                        }
                        texts.add(new Pair<String, String>(tempStem, "-2"));
                        //选项提示
//                    texts.add(new Pair<String, String>("以下是选项" + Dictionary.VOICE_TEXT_END_SYMBOL, "-1"));
                        //选项
                        if (ArrayListUtil.isNotEmpty(optionsList)) {
                            for (int i = 0; i < optionsList.size(); i++) {
                                OptionsEntity option = optionsList.get(i);
//                            texts.add(new Pair<String, String>((i+1) + "。" + option.getContent() + Dictionary.VOICE_TEXT_END_SYMBOL, String.valueOf(i)));
                                texts.add(new Pair<String, String>(option.getContent() + Dictionary.VOICE_TEXT_END_SYMBOL, String.valueOf(i)));
                            }
                        }
                    }
                    //播放语音
                    activity.batchSpeak(texts);
                    //初始化播放完整结束标记为false
                    isPlayFullEnd = false;
                    //切换语音按钮为暂停图标
                    activity.switchToPauseIcon();
                }
            } catch (Exception e) {
                e.printStackTrace();
                //初始化播放完整结束标记为false
                isPlayFullEnd = false;
            }

        } else {
            try {
                if (activity != null) {
                    if (isPause) {
                        //继续播放
                        activity.resume();
                        isPause = false;
                        //切换语音按钮为暂停图标
                        activity.switchToPauseIcon();
                    } else {
                        //暂停
                        activity.pause();
                        isPause = true;
                        //切换语音按钮为播放图标
                        activity.switchToPlayIcon();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 停止
     */
    @Override
    public void stop() {
        //未播放完整就结束
        if (!isPlayFullEnd) {
            //隐藏题目语音提示
            gftVoicePlay.setVisibility(View.INVISIBLE);
            //还原列表选项语音提示
            for (OptionsEntity option : optionsList) {
                option.setVoicePlayingTipShow(false);
            }
            adapter.notifyDataSetChanged();
            //标记播放完整结束
            isPlayFullEnd = true;
        }

        //还原暂停标记
        isPause = false;
        //切换语音按钮为播放图标
        FragmentActivity activity = getActivity();
        if (activity instanceof VoiceButtonUISwitchListener) {
            ((VoiceButtonUISwitchListener)activity).switchToPlayIcon();
        }
    }


    /**
     * 语音结束
     * @param utteranceId 标识ID
     */
    @Override
    public void speechFinish(String utteranceId) {
        if (!TextUtils.isEmpty(utteranceId)) {
            int utteranceIdVal = Integer.parseInt(utteranceId);

            //存在选项
            if (ArrayListUtil.isNotEmpty(optionsList)) {
                //选项的最后一项
                if (utteranceIdVal == optionsList.size() - 1) {
                    optionsList.get(utteranceIdVal).setVoicePlayingTipShow(false);
                    adapter.notifyDataSetChanged();
                    //播放完整结束标记为true
                    isPlayFullEnd = true;
                    //还原暂停标记
                    isPause = false;
                    //切换语音按钮为播放图标
                    FragmentActivity activity = getActivity();
                    if (activity instanceof VoiceButtonUISwitchListener) {
                        ((VoiceButtonUISwitchListener)activity).switchToPlayIcon();
                    }
                }
            }
        }

        //隐藏题目语音提示
        gftVoicePlay.setVisibility(View.INVISIBLE);
    }


    /**
     * 语音开始
     * @param utteranceId 标识ID
     */
    @Override
    public void speechStart(String utteranceId) {
        //还原列表选项语音提示
        for (OptionsEntity option : optionsList) {
            option.setVoicePlayingTipShow(false);
        }

        if (!TextUtils.isEmpty(utteranceId)) {
            int utteranceIdVal = Integer.parseInt(utteranceId);
            //题目
            if (utteranceIdVal == -1 || utteranceIdVal == -2) {
                gftVoicePlay.setVisibility(View.VISIBLE);

            } else {
                //选项列表
                optionsList.get(utteranceIdVal).setVoicePlayingTipShow(true);
            }

        }

        adapter.notifyDataSetChanged();
    }


}
