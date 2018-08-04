package com.cheersmind.smartbrain.main.fragment.questype;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.entity.OptionsEntity;
import com.cheersmind.smartbrain.main.entity.QuestionInfoChildEntity;
import com.cheersmind.smartbrain.main.entity.QuestionInfoEntity;
import com.cheersmind.smartbrain.main.util.OnMultiClickListener;
import com.cheersmind.smartbrain.main.util.SoundPlayUtils;
import com.cheersmind.smartbrain.main.view.RoundProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018/6/4.
 */

public class QsAccordJudgeFragment extends QuestionTypeBaseFragment {

    View contentView;
    private TextView tvQuesTitle;

    private RoundProgressBar rpBar;
    private LinearLayout llOption;

    private int lastPro = 0;
    private int optionSize = 4;
    private int maxDegree = 100;
    private int baseDegree;

    QuestionInfoEntity questionInfoEntity;
    List<OptionsEntity> optionsList;
    String childFactorId = "";
    QuestionInfoChildEntity childQuestion;
    private int curSelect = -1;//当前选中选项，默认没选中

    List<TextView> tvList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.fragment_accord_judge, container,
                    false);
        }

        initView();
        initData();
        return contentView;
    }

    private void initView(){
        tvQuesTitle = (TextView)contentView.findViewById(R.id.tv_ques_title);
        tvQuesTitle.setVisibility(View.GONE);
        llOption = (LinearLayout)contentView.findViewById(R.id.ll_option);
        llOption.removeAllViews();
        rpBar = (RoundProgressBar)contentView.findViewById(R.id.roundProgressBar);
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
                    baseDegree = maxDegree/(optionSize-1);
                    initOption(curSelect,optionsList,tvList,llOption,tvClick);
                    if(curSelect != -1){
                        //如果这题已经回答过,更新进度
                        updateHasCompleteProgress();
                    }
                }
            }
        }

    }

    /**
     * 如果这题已经回答过
     * @param curSelect
     */
//    private void showHasAnswerProgress(int curSelect){
//        if(curSelect==-1){
//            return;
//        }
//        lastPro = baseDegree * curSelect;
//        rpBar.setProgress(lastPro);
//    }

    private OnMultiClickListener tvClick = new OnMultiClickListener() {
        @Override
        public void onMultiClick(View view) {
            SoundPlayUtils.play(3);
            int tag = (int) view.getTag();
            updateProgress(tag);
        }
    };

    private void updateProgress(int tag){
        int endDegree = optionsList.get(tag-1).getShowValue();
        if(endDegree==0){
            endDegree = (tag-1) * baseDegree;
        }
        if(lastPro == endDegree && lastPro!=0){
            return;
        }

        if(endDegree>95){
            endDegree = 100;//除不尽，满度数矫正
        }

        if(lastPro == endDegree && lastPro==0){
            updateProgressFirst();
        }else{
            updateProgress(lastPro,endDegree);
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

    private void updateProgressFirst(){
        final Integer [] isAdd = new Integer[]{0};
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(isAdd[0]==0){
                    lastPro ++;
                    if(lastPro>=7){
                        isAdd[0] = 1;
                    }
                }else{
                    lastPro --;
                    if(lastPro==0){
                        lastPro = 0;
                        this.cancel();
                    }
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rpBar.setProgress(lastPro);
                    }
                });
            }
        },0,20);
    }

    private void updateProgress(int start, final int end){
        final Timer timer = new Timer();
        int diff = 8;
        if(Math.abs(start-end)>=80){
            diff = 3;
        }else if(Math.abs(start-end)>=50){
            diff = 5;
        }else {
            diff = 8;
        }
        if(start < end){

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(lastPro<end){
                        lastPro = lastPro +1;
                        if(lastPro>end){
                            lastPro = end;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rpBar.setProgress(lastPro);
                            }
                        });
                    }else{
                        lastPro = end;
                        timer.cancel();
                    }
                }
            },0,diff);
        }else{
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(lastPro>end){
                        lastPro = lastPro -1;
                        if(lastPro < end){
                            lastPro = end;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rpBar.setProgress(lastPro);
                            }
                        });
                    }else{
                        lastPro = end;
                        timer.cancel();
                    }
                }
            },0,diff);
        }
    }

    //之前已经回答过，重新进入后初始化
    private void updateHasCompleteProgress(){
        lastPro = baseDegree * curSelect;
        rpBar.setProgress(lastPro);
        for(int i=0;i<tvList.size();i++){
            if(curSelect == i){
                tvList.get(i).setBackgroundResource(R.mipmap.option_bg_select);
            }else{
                tvList.get(i).setBackgroundResource(R.mipmap.option_bg_nor);
            }
        }
    }

}
