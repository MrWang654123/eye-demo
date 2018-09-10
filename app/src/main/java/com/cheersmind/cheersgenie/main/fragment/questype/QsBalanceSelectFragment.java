package com.cheersmind.cheersgenie.main.fragment.questype;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.entity.OptionsEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionInfoEntity;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.SoundPlayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/4.
 */

public class QsBalanceSelectFragment extends QuestionTypeBaseFragment {

    View contentView;
    private TextView tvQuesTitle;

    private ImageView ivhx;
    private ImageView ivup;

    private RelativeLayout rthx;

    private LinearLayout llOption;
    private TextView tvLeft;
    private TextView tvRight;

    private float lastDegree;
    private float lastMove;
    private int maxDegree = 12;//偏移最大度数
    private float maxMove = 7.0f;//最大单边移动距离
    private int optionSize = 3;

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
            contentView = inflater.inflate(R.layout.fragment_balance_select, container,
                    false);
        }

        initView();
        initData();
        return contentView;

    }

    private void initView(){
        tvQuesTitle = (TextView)contentView.findViewById(R.id.tv_ques_title);
        tvQuesTitle.setVisibility(View.GONE);
        ivhx = (ImageView)contentView.findViewById(R.id.iv_hx);
        ivup = (ImageView)contentView.findViewById(R.id.iv_up);
        rthx = (RelativeLayout)contentView.findViewById(R.id.rt_hx);
        llOption = (LinearLayout)contentView.findViewById(R.id.ll_option);
        tvLeft = (TextView)contentView.findViewById(R.id.tv_left);
        tvRight = (TextView)contentView.findViewById(R.id.tv_right);
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
                    initOption(curSelect,optionsList,tvList,llOption,tvClick);
                    tvLeft.setText(optionsList.get(0).getContent());
                    tvRight.setText(optionsList.get(optionSize-1).getContent());

                    if(curSelect != -1){
                        //如果这题已经回答过,更新进度
                        updateHasCompleteProgress(curSelect+1);
                    }
                }
            }
        }
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
        float half = optionSize * 1.0f / 2;
        float halfD = optionSize / 2;
        float baseDegree = maxDegree * 1.0f / halfD;
        float baseMove = maxMove * 1.0f / halfD;
        float endDegree;
        float endMove;

        if(optionSize%2==0){
            if(tag <= half){
                endDegree = -((half-(tag-1)) * baseDegree);
                endMove = -((half-(tag-1)) * baseMove);
            }else{
                endDegree = (tag-half) * baseDegree;
                endMove = (tag-half) * baseMove;
            }

            if(lastDegree != endDegree){
                rotateAnim(lastDegree, endDegree);
                startTranslateAnimation(ivup, lastMove, endMove);
            }
        }else{

            if(tag == half + 0.5){
                endDegree = 0;
                endMove = 0;
            }else{
                if(tag <= half){
                    endDegree = -((halfD-(tag-1)) * baseDegree);
                    endMove = -((halfD-(tag-1)) * baseMove);
                }else{
                    endDegree = (tag-1-halfD) * baseDegree;
                    endMove = (tag-1-halfD) * baseMove;
                }

            }

        }

        if(lastDegree != endDegree){
            rotateAnim(lastDegree, endDegree);
            startTranslateAnimation(ivup, lastMove, endMove);
        }

        lastDegree = endDegree;
        lastMove = endMove;

        for(int i=0;i<tvList.size();i++){
            if(tag == i+1){
                tvList.get(i).setBackgroundResource(R.mipmap.option_bg_select);;
                commitQuestion(getActivity(),optionsList.get(i),childFactorId);
            }else{
                tvList.get(i).setBackgroundResource(R.mipmap.option_bg_nor);
            }
        }
    }

    private void updateHasCompleteProgress(int tag){
        float half = optionSize * 1.0f / 2;
        float halfD = optionSize / 2;
        float baseDegree = maxDegree * 1.0f / halfD;
        float baseMove = maxMove * 1.0f / halfD;
        float endDegree;
        float endMove;

        if(optionSize%2==0){
            if(tag <= half){
                endDegree = -((half-(tag-1)) * baseDegree);
                endMove = -((half-(tag-1)) * baseMove);
            }else{
                endDegree = (tag-half) * baseDegree;
                endMove = (tag-half) * baseMove;
            }

            if(lastDegree != endDegree){
                rotateAnim(lastDegree, endDegree);
                startTranslateAnimation(ivup, lastMove, endMove);
            }
        }else{

            if(tag == half + 0.5){
                endDegree = 0;
                endMove = 0;
            }else{
                if(tag <= half){
                    endDegree = -((halfD-(tag-1)) * baseDegree);
                    endMove = -((halfD-(tag-1)) * baseMove);
                }else{
                    endDegree = (tag-1-halfD) * baseDegree;
                    endMove = (tag-1-halfD) * baseMove;
                }

            }

        }

        if(lastDegree != endDegree){
            rotateAnim(lastDegree, endDegree);
            startTranslateAnimation(ivup, lastMove, endMove);
        }

        lastDegree = endDegree;
        lastMove = endMove;

        for(int i=0;i<tvList.size();i++){
            if(tag == i+1){
                tvList.get(i).setBackgroundResource(R.mipmap.option_bg_select);;
            }else{
                tvList.get(i).setBackgroundResource(R.mipmap.option_bg_nor);
            }
        }
    }

    private void rotateAnim(float start,float end) {
        Animation anim =new RotateAnimation(start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true); // 设置保持动画最后的状态
        anim.setDuration(300); // 设置动画时间
        anim.setInterpolator(new AccelerateInterpolator()); // 设置插入器
        rthx.startAnimation(anim);
    }

    private void startTranslateAnimation(ImageView mIvImg,float startTra,float endTra) {
        TranslateAnimation anim = new TranslateAnimation(mIvImg.getWidth() * startTra, mIvImg.getWidth() * endTra, 0, 0);
        //设置动画持续时长
        anim.setDuration(300);
        anim.setFillEnabled(true);
        //设置动画结束之后的状态是否是动画的最终状态，true，表示是保持动画结束时的最终状态
        anim.setFillAfter(true);
//        anim.setInterpolator(new AccelerateInterpolator()); // 设置插入器
        //设置动画结束之后的状态是否是动画开始时的状态，true，表示是保持动画开始时的状态
        anim.setFillBefore(true);
        mIvImg.startAnimation(anim);
    }

}
