package com.cheersmind.cheersgenie.features.modules.exam.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.QuestionOptionsAdapter;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.droidsonroids.gif.GifTextView;

/**
 * 默认的问题Fragment
 */
public class DefaultQuestionFragment extends BaseQuestionFragment implements VoiceControlListener, SoundPlayListener {

    protected Unbinder unbinder;

    //题目文本
    @BindView(R.id.tv_question_title)
    TextView tvQuestionTitle;
    //列表
    @BindView(R.id.rv_question)
    RecyclerView recycleView;

    QuestionOptionsAdapter recyclerAdapter;

    //时间间隔
    private long INTERVAL_TIME = 220;

    //题目
    String mStem;
    //题目音频播放gft提示
    @BindView(R.id.gif_voice_play)
    GifTextView gftVoicePlay;
    //播放完整结束
    boolean isPlayFullEnd = true;
    //是否暂停
    boolean isPause = false;

    //recycler子项的孩子的点击监听
    BaseQuickAdapter.OnItemChildClickListener recyclerItemChildClickListener = new BaseQuickAdapter.OnItemChildClickListener() {

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            switch (view.getId()) {
                //点击答案
                case R.id.ll_option: {
                    OptionsEntity options = recyclerAdapter.getData().get(position);
                    int preSelect = recyclerAdapter.getCurSelect();
                    recyclerAdapter.setCurSelect(position);
                    //问题类型：只选
                    if (options.getType()== Dictionary.QUESTION_TYPE_SELECT_ONLY) {
                        if (position != preSelect) {
                            recyclerAdapter.notifyDataSetChanged();
                        }
//                        SoundPlayUtils.play(getContext(),3);
                        clickQuestionOption();
                        //处理答题数据，并跳转到下一题
                        saveReplyInfo(questionInfoEntity.getChildFactorId(), options, "");
                        //关闭软键盘
//                        SoftInputUtil.closeSoftInput(getActivity());

                    } else if (options.getType()== Dictionary.QUESTION_TYPE_EDIT) {//问题类型：填写
                        //弹出填写答案的对话框
                        String initContent = preSelect == position ? recyclerAdapter.getOptionText() : "";
                        popupAnswerEditWindows(optionsList.get(position).getContent(), initContent);
                    }
                    break;
                }
            }
        }
    };


    @Override
    protected int setContentView() {
        return R.layout.fragment_question_default;
    }

    @Override
    protected void onInitView(View rootView) {
        //绑定ButterKnife
        unbinder = ButterKnife.bind(this, rootView);

        //适配器
        if (recyclerAdapter == null) {
            recyclerAdapter = new QuestionOptionsAdapter(R.layout.item_evaluate_question, null);
        }
        recyclerAdapter.setOnItemChildClickListener(recyclerItemChildClickListener);
        recycleView.setAdapter(recyclerAdapter);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        initData();
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onDestroyView() {
        //记录当前选中项
        Bundle arguments = getArguments();
        if (arguments != null) {
            arguments.putInt("curSelect", recyclerAdapter.getCurSelect());
            arguments.putString("optionText", recyclerAdapter.getOptionText());
        }

        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 初始化数据
     */
    private void initData(){
        //初始化已经选中的项
        Bundle bundle = getArguments();
        if(bundle!=null){
//            int curSelect = bundle.getInt("curSelect", -1);
//            String optionText = bundle.getString("optionText", "");
//            if (curSelect != -1) {
//                //非第一次初始化
//                if (optionsList != null) {
//                    recyclerAdapter.setCurSelect(curSelect);
//                    recyclerAdapter.setOptionText(optionText);
//                    recyclerAdapter.notifyDataSetChanged();
//                }
//
//            } else {
//                //第一次初始化
//                questionInfoEntity = (QuestionInfoEntity) bundle.getSerializable("question_content");
//                if (questionInfoEntity != null) {
//                    optionsList = questionInfoEntity.getOptions();
//                    //如果之前回答过本题,更新对应选项选中状态
//                    QuestionInfoChildEntity childQuestion = questionInfoEntity.getChildQuestion();
//                    if (childQuestion != null) {
//                        for (int i = 0; i < optionsList.size(); i++) {
//                            if (childQuestion.getOptionId().equals(optionsList.get(i).getOptionId())) {
//                                //初始化当前选中项
//                                recyclerAdapter.setCurSelect(i);
//                                break;
//                            }
//                        }
//
//                        //填写的答案
//                        recyclerAdapter.setOptionText(childQuestion.getOptionText());
//                    }
//
//                    recyclerAdapter.setNewData(optionsList);
//                }
//            }

            if (questionInfoEntity == null) {
                questionInfoEntity = (QuestionInfoEntity) bundle.getSerializable("question_content");
            }
            if (questionInfoEntity != null) {
                optionsList = questionInfoEntity.getOptions();
                //如果之前回答过本题,更新对应选项选中状态
                QuestionInfoChildEntity childQuestion = questionInfoEntity.getChildQuestion();
                if (childQuestion != null) {
                    for (int i = 0; i < optionsList.size(); i++) {
                        if (childQuestion.getOptionId().equals(optionsList.get(i).getOptionId())) {
                            //初始化当前选中项
                            recyclerAdapter.setCurSelect(i);
                            break;
                        }
                    }

                    //填写的答案
                    recyclerAdapter.setOptionText(childQuestion.getOptionText());
                }

                recyclerAdapter.setNewData(optionsList);
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





    @Override
    public void clickQuestionOption() {
        if (getActivity() != null && getActivity() instanceof SoundPlayListener) {
            ((SoundPlayListener) getActivity()).clickQuestionOption();
        }
    }


    Dialog answerEditDialog;
    /**
     * 弹出填写答案对话框
     */
    private void popupAnswerEditWindows(String title, String content) {
        if (answerEditDialog == null) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_window_edit_answer, null);
            answerEditDialog = new Dialog(getContext(), R.style.dialog_bottom_full);
            Window window = answerEditDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.setContentView(view);

            WindowManager.LayoutParams lp = window.getAttributes(); // 获取对话框当前的参数值
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽度占满屏幕
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setWindowAnimations(R.style.BottomDialog_Animation);//动画

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
                    answerEditDialog.dismiss();
                    answerEditDialog = null;
                    //确定答案
                    if (!TextUtils.isEmpty(etAnswer.getText().toString())) {
                        String text = etAnswer.getText().toString().trim();
                        if (!TextUtils.isEmpty(text)) {
                            confirmAnswer(text, optionsList.get(recyclerAdapter.getCurSelect()));
                        }
                    }
                }
            });
            //关闭按钮
            final Button btnClose = view.findViewById(R.id.btn_cancel);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerEditDialog.dismiss();
                    answerEditDialog = null;
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
        }

        answerEditDialog.show();
    }


    /**
     * 确定填写的答案
     * @param answer
     */
    private void confirmAnswer(String answer, OptionsEntity entity) {
        recyclerAdapter.setOptionText(answer);
        recyclerAdapter.notifyDataSetChanged();
//        SoundPlayUtils.play(getContext(),3);
        clickQuestionOption();

        //处理答题数据，并跳转到下一题
        saveReplyInfo(questionInfoEntity.getChildFactorId(), entity, answer);
        //关闭软键盘
        SoftInputUtil.closeSoftInput(getActivity());
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
            recyclerAdapter.notifyDataSetChanged();
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
                    recyclerAdapter.notifyDataSetChanged();
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

        recyclerAdapter.notifyDataSetChanged();
    }


}
