package com.cheersmind.cheersgenie.features.modules.exam.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.modules.article.activity.ArticleDetailActivity;
import com.cheersmind.cheersgenie.features.modules.base.fragment.BaseFragment;
import com.cheersmind.cheersgenie.features.modules.exam.activity.ReplyQuestionActivity;
import com.cheersmind.cheersgenie.main.entity.OptionsEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionInfoEntity;
import com.cheersmind.cheersgenie.main.fragment.questype.QuestionTypeBaseFragment;
import com.cheersmind.cheersgenie.main.util.SoundPlayUtils;

import java.util.List;

/**
 * 默认的问题Fragment
 */
public class DefaultQuestionFragment extends QuestionTypeBaseFragment {

    private View contentView;
    private TextView tvQuesTitle;
    private ListView lvQuestion;

    //当前选中选项，默认没选中
    private int curSelect = -1;
    //填写的答案
    String optionText;
    //问题对象
    QuestionInfoEntity questionInfoEntity;
    //选项集合
    List<OptionsEntity> optionsList;


    //消息处理器
    @SuppressLint("HandlerLeak")
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };


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
        arguments.putInt("curSelect", curSelect);
        arguments.putString("optionText", optionText);
        super.onDestroyView();
    }


    private void initView(){
        tvQuesTitle = (TextView)contentView.findViewById(R.id.tv_ques_title);
        lvQuestion = (ListView)contentView.findViewById(R.id.lv_question);
        lvQuestion.setAdapter(adapter);
    }

    private void initData(){
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
                    tvQuesTitle.setText(questionInfoEntity.getStem());


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
                viewHolder.ivIcon.setBackgroundResource(R.mipmap.option_choice_select);
                //选中背景
                viewHolder.llOption.setBackgroundResource(R.drawable.shape_corner_f5a400_12dp);
                //选中选项标题
                viewHolder.tvOptionTitle.setTextColor(Color.parseColor("#ffffff"));
                //问题类型：手填
                if (entity.getType()== Dictionary.QUESTION_TYPE_EDIT) {
                    //显示填写答案文本
                    viewHolder.tvOptionText.setVisibility(View.VISIBLE);
                    //设置填写的答案
                    viewHolder.tvOptionText.setText(optionText);
                    //选中的填写答案
                    viewHolder.tvOptionText.setTextColor(Color.parseColor("#ffffff"));
                }

            } else {
                //未选中图标
                viewHolder.ivIcon.setBackgroundResource(R.mipmap.option_choice_nor);
                //未选中背景
                viewHolder.llOption.setBackgroundResource(R.drawable.shape_corner_f4f3ee_12dp);
                //未选中选项标题
                viewHolder.tvOptionTitle.setTextColor(Color.parseColor("#7b7b7b"));
                //问题类型：手填
                if (entity.getType()== Dictionary.QUESTION_TYPE_EDIT) {
                    //选中的填写答案
                    viewHolder.tvOptionText.setTextColor(Color.parseColor("#898989"));
                }
            }

            //选项点击监听
            viewHolder.llOption.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int preSelect = curSelect;
                    curSelect = position;
                    //问题类型：只选
                    if (entity.getType()== Dictionary.QUESTION_TYPE_SELECT_ONLY) {
                        notifyDataSetChanged();
                        SoundPlayUtils.play(3);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SoundPlayUtils.play(4);
                            }
                        }, 200);
                        //处理答题数据，并跳转到下一题
                        saveReplyInfo(questionInfoEntity.getChildFactorId(), entity, "");
                        hiddenSoft();

                    } else if (entity.getType()== Dictionary.QUESTION_TYPE_EDIT) {//问题类型：填写
                        //弹出填写答案的对话框
                        String initContent = preSelect == curSelect ? optionText : "";
                        popupAnswerEditWindows(optionsList.get(curSelect).getContent(), initContent);
                    }
                }
            });

            return convertView;
        }

    };

    class ViewHolder{
        //布局容器
        private LinearLayout llOption;
        //选中图标
        private ImageView ivIcon;
        //选项标题
        private TextView tvOptionTitle;
        //填写的答案
        private TextView tvOptionText;
        void initView(View view){
            llOption = view.findViewById(R.id.ll_option);
            ivIcon = view.findViewById(R.id.iv_icon);
            tvOptionTitle = view.findViewById(R.id.tv_option_title);
            tvOptionText = view.findViewById(R.id.tv_option_text);
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
                confirmAnswer(etAnswer.getText().toString(), optionsList.get(curSelect));
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
                    btnConfirm.setEnabled(true);
                }
            }
        });

    }

    /**
     * 确定填写的答案
     * @param answer
     */
    private void confirmAnswer(String answer, OptionsEntity entity) {
        optionText = answer;

        adapter.notifyDataSetChanged();
        SoundPlayUtils.play(3);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SoundPlayUtils.play(4);
            }
        }, 200);

        //处理答题数据，并跳转到下一题
        saveReplyInfo(questionInfoEntity.getChildFactorId(), entity, optionText);
        hiddenSoft();
    }


    private void showAnimBg(TextView tv, final OptionsEntity optionsEntity){
        tv.setVisibility(View.VISIBLE);
        SoundPlayUtils.play(3);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SoundPlayUtils.play(4);
            }
        }, 200);
        //处理答题数据，并跳转到下一题
//        saveReplyInfo(optionsEntity);
    }

    /**
     * 保存当前题目的答题
     * @param optionsEntity
     */
    private void saveReplyInfo(String childFactorId, OptionsEntity optionsEntity, String optionText) {
        ReplyQuestionActivity activity = (ReplyQuestionActivity)getActivity();
        //处理答题信息
        activity.saveCurHasedReply(childFactorId, optionsEntity, optionText);
        //跳到下一题
        activity.toNextQuestion();
    }

    private void hiddenSoft(){
        if(getActivity().getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
