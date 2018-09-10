package com.cheersmind.cheersgenie.main.fragment.questype;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.entity.OptionsEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionInfoEntity;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.SoundPlayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2018/6/4.
 */

public class QsTickedCorrectFragment extends QuestionTypeBaseFragment {


    View contentView;
    private TextView tvQuesTitle;
    private GridView gvTick;
    private LinearLayout llOption;

    QuestionInfoEntity questionInfoEntity;
    List<OptionsEntity> optionsList;
    String childFactorId = "";
    QuestionInfoChildEntity childQuestion;
    private int curSelect = -1;//当前选中选项，默认没选中

    List<TextView> tvList = new ArrayList<>();
    private int optionSize;
    List<Integer> kList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.fragment_ticked_corrent, container,
                    false);
        }

        initView();
        initData();
        return contentView;

    }

    private void initView(){
        tvQuesTitle = (TextView)contentView.findViewById(R.id.tv_ques_title);
        tvQuesTitle.setVisibility(View.GONE);
        gvTick = (GridView)contentView.findViewById(R.id.gv_tick);
        gvTick.setAdapter(adapter);
        llOption = (LinearLayout)contentView.findViewById(R.id.ll_option);
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
                    if(curSelect != -1){
                        //如果这题已经回答过,更新进度
                        updateHasCompleteProgress(curSelect);
                    }
                }
            }
        }


    }

    private BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 16;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = View.inflate(getActivity(),R.layout.item_ticked_corrent,null);
                holder = new ViewHolder();
                holder.initView(convertView);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }

            if(isNeedTicked(position)){
                holder.ivSelect.setBackgroundResource(R.mipmap.item_ticked_select);
                showTicketAnim(holder.tvAnim);
            }else{
                holder.ivSelect.setBackgroundResource(R.mipmap.item_ticked_nor);
            }

            return convertView;
        }
    };

    class ViewHolder{
        ImageView ivSelect;
        TextView tvAnim;
        void initView(View view){
            ivSelect = (ImageView)view.findViewById(R.id.iv_select);
            tvAnim = (TextView)view.findViewById(R.id.tv_anim);
        }
    }

    private void showTicketAnim(final TextView tv){
        tv.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.anim_center_to_right);
        tv.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
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
        int num = optionsList.get(tag-1).getShowValue();
        if(num>16){
            num = 16;
        }
        getRandom(num);
        adapter.notifyDataSetChanged();

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
        int num = optionsList.get(tag).getShowValue();
        if(num>16){
            num = 16;
        }
        getRandom(num);
        adapter.notifyDataSetChanged();

        for(int i=0;i<tvList.size();i++){
            if(tag == i){
                tvList.get(i).setBackgroundResource(R.mipmap.option_bg_select);;
            }else{
                tvList.get(i).setBackgroundResource(R.mipmap.option_bg_nor);
            }
        }
    }

    private boolean isNeedTicked(int index){
        for(int i=0;i<kList.size();i++){
            if(kList.get(i) == index){
                return true;
            }
        }
        return false;
    }

    private void getRandom(int num){
//        List<Integer> list = new ArrayList<>();
        kList.clear();
        int intRd = 0; //存放随机数
        int count = 0; //记录生成的随机数个数
        int flag = 0; //是否已经生成过标志
        while(count<num){
            Random rdm = new Random(System.currentTimeMillis());
            intRd = Math.abs(rdm.nextInt())%16;
            for(int i=0;i<count;i++){
                if(kList.get(i)==intRd){
                    flag = 1;
                    break;
                }else{
                    flag = 0;
                }
            }
            if(flag==0){
                kList.add(intRd);
                count++;
            }
        }
    }
}
