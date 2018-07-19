package com.cheersmind.smartbrain.main.fragment.questype;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.entity.OptionsEntity;
import com.cheersmind.smartbrain.main.entity.QuestionInfoChildEntity;
import com.cheersmind.smartbrain.main.entity.QuestionInfoEntity;
import com.cheersmind.smartbrain.main.util.OnMultiClickListener;
import com.cheersmind.smartbrain.main.util.SoundPlayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/4.
 */

public class QsFrequencySelectFragment extends QuestionTypeBaseFragment {

    View contentView;
    private TextView tvQuesTitle;

    private TextView tvTop;
    private TextView tvRight;
    private TextView tvBottom;
    private TextView tvLeft;
    private ImageView ivzz;

    private LinearLayout llOption;

    private float lastRa = 0f;
    private float baseRa = 90;
    private int optionSize = 5;

    QuestionInfoEntity questionInfoEntity;
    List<OptionsEntity> optionsList;
    String childFactorId = "";
    QuestionInfoChildEntity childQuestion;
    private int curSelect = -1;//当前选中选项，默认没选中

    List<TextView> tvList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.fragment_frequency_select, container,
                    false);
        }

        initView();
        initData();
        return contentView;

    }

    private void initView(){

        tvQuesTitle = (TextView)contentView.findViewById(R.id.tv_ques_title);
        tvQuesTitle.setVisibility(View.GONE);
        ivzz = (ImageView)contentView.findViewById(R.id.iv_zz);
        tvTop = (TextView)contentView.findViewById(R.id.tv_top);
        tvRight = (TextView)contentView.findViewById(R.id.tv_right);
        tvBottom = (TextView)contentView.findViewById(R.id.tv_bottom);
        tvLeft = (TextView)contentView.findViewById(R.id.tv_left);

        llOption = (LinearLayout)contentView.findViewById(R.id.ll_option);
        llOption.removeAllViews();

    }

    private void initData(){

        Bundle bundle = getArguments();
        if(bundle!=null){
            childFactorId = bundle.getString("child_factor_id");
            questionInfoEntity = (QuestionInfoEntity)bundle.getSerializable("question_content");
            if(questionInfoEntity!=null){
                tvQuesTitle.setText(questionInfoEntity.getStem());


                optionsList = questionInfoEntity.getOptions();
                //如果之前回答过本题,更新对应选项选中状态
                childQuestion = questionInfoEntity.getChildQuestion();
                if(childQuestion!=null){
                    for(int i=0;i<optionsList.size();i++){
                        if(childQuestion.getOptionId() .equals(optionsList.get(i).getOptionId())){
                            curSelect = i;
                        }
                    }
                }

                if(optionsList!=null){
                    optionSize = optionsList.size();
                    baseRa = 360 * 1.0f/optionSize;
                    initOption(curSelect,optionsList,tvList,llOption,tvClick);
                    updateShowValue();
                    if(curSelect != -1){
                        //如果这题已经回答过,更新进度
                        updateProgress(curSelect);
                    }
                }
            }
        }
    }

    private void updateShowValue(){
        if(optionsList.size()>=4){
            String str1 = optionsList.get(0).getContent().replace("／周","");
            String str2 = optionsList.get(1).getContent().replace("／周","");
            String str3 = optionsList.get(2).getContent().replace("／周","");
            String str4 = optionsList.get(3).getContent().replace("／周","");
            tvTop.setText(str1);
            tvRight.setText(str2);
            tvBottom.setText(str3);
            tvLeft.setText(str4);
        }
    }

    private void rotateAnimFirst(){
        Animation anim =new RotateAnimation(0, 5, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1f);
        anim.setFillAfter(true); // 设置保持动画最后的状态
        anim.setFillBefore(true);
        anim.setDuration(200); // 设置动画时间
        anim.setInterpolator(new AccelerateInterpolator()); // 设置插入器
        ivzz.startAnimation(anim);

        final Animation anim1 =new RotateAnimation(5, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1f);
        anim1.setFillAfter(true); // 设置保持动画最后的状态
        anim1.setFillBefore(true);
        anim1.setDuration(200); // 设置动画时间
        anim1.setInterpolator(new AccelerateInterpolator()); // 设置插入器

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivzz.startAnimation(anim1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void rotateAnim(float start,float end) {
        Animation anim =new RotateAnimation(start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1f);
        anim.setFillAfter(true); // 设置保持动画最后的状态
        anim.setFillBefore(true);
        anim.setDuration(300); // 设置动画时间
        anim.setInterpolator(new AccelerateInterpolator()); // 设置插入器
        ivzz.startAnimation(anim);
    }

    private OnMultiClickListener tvClick = new OnMultiClickListener() {
        @Override
        public void onMultiClick(View view) {
            SoundPlayUtils.play(3);
            int tag = (int) view.getTag();
            updateProgress(tag);

        }
    };

    private void updateProgress(int tag){
        float end = (tag-1) * baseRa;
        if(lastRa ==0 && lastRa == end){
            rotateAnimFirst();
        }else{
            rotateAnim(lastRa,end);
        }


        lastRa = (tag-1) * baseRa;

        if(lastRa>355){
            lastRa = 360;//满度数校准
        }

        for(int i=0;i<tvList.size();i++){
            if(tag == i+1){
                tvList.get(i).setBackgroundResource(R.mipmap.option_bg_select);
                commitQuestion(getActivity(),optionsList.get(i),childFactorId);
            }else{
                tvList.get(i).setBackgroundResource(R.mipmap.option_bg_nor);
            }
        }
    }
}
