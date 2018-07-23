package com.cheersmind.smartbrain.main.fragment.questype;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.Exception.QSCustomException;
import com.cheersmind.smartbrain.main.activity.QsEvaluateQuestionActivity;
import com.cheersmind.smartbrain.main.dao.ChildInfoDao;
import com.cheersmind.smartbrain.main.entity.OptionsEntity;
import com.cheersmind.smartbrain.main.entity.QuestionInfoChildEntity;
import com.cheersmind.smartbrain.main.entity.QuestionInfoEntity;
import com.cheersmind.smartbrain.main.service.BaseService;
import com.cheersmind.smartbrain.main.service.DataRequestService;
import com.cheersmind.smartbrain.main.util.InjectionWrapperUtil;
import com.cheersmind.smartbrain.main.util.JsonUtil;
import com.cheersmind.smartbrain.main.util.RepetitionClickUtil;
import com.cheersmind.smartbrain.main.util.SoundPlayUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/22.
 */

public class EvaluateQuestionFragment extends Fragment {

    private View contentView;
    private TextView tvQuesTitle;
    private ListView lvQuestion;

//    private SoundPool clickPool;

    private EditText etAnswer;

    private int curSelect = -1;//当前选中选项，默认没选中
    QuestionInfoEntity questionInfoEntity;
    List<OptionsEntity> optionsList;
    String childFactorId = "";

    QuestionInfoChildEntity childQuestion;
    private boolean hasCommit = false;//是否点击选项触发提交，防止重复提交

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        clickPool= new SoundPool(10, AudioManager.STREAM_SYSTEM,0);
//        clickPool.load(getActivity(),R.raw.question_click,1);
//        clickPool.load(getActivity(),R.raw.page_next,2);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.fragment_evaluate_question, container,
                    false);
        }

        initView();
        initData();
        return contentView;
    }

    private void initView(){
        tvQuesTitle = (TextView)contentView.findViewById(R.id.tv_ques_title);
        lvQuestion = (ListView)contentView.findViewById(R.id.lv_question);
        lvQuestion.setAdapter(adapter);
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
                    adapter.notifyDataSetChanged();
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

            if(entity.getType()==2){
                viewHolder.llFill.setVisibility(View.VISIBLE);
                viewHolder.tvAnswerTitle.setVisibility(View.GONE);
                viewHolder.tvOption.setText(entity.getContent());
                etAnswer = viewHolder.etOption;
            }else{

                viewHolder.llFill.setVisibility(View.GONE);
                viewHolder.tvAnswerTitle.setVisibility(View.VISIBLE);
                etAnswer = null;

                viewHolder.tvAnswerTitle.setText(entity.getContent());

                if(curSelect == position){
                    viewHolder.ivIcon.setBackgroundResource(R.mipmap.option_choice_select);
                    viewHolder.tvAnswerbg.setVisibility(View.VISIBLE);
                }else{
                    viewHolder.ivIcon.setBackgroundResource(R.mipmap.option_choice_nor);
                    viewHolder.tvAnswerbg.setVisibility(View.GONE);
                }

            }

            viewHolder.rtSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(hasCommit){ //如果当前题目有出发提交等待提交结果，防止重复提交
                        return;
                    }
                    if(!RepetitionClickUtil.isFastClick()){
                        if(entity.getType()==2){
                            String str = viewHolder.etOption.getText().toString();
                            if(TextUtils.isEmpty(str)){
                                Toast.makeText(getActivity(),"请填写当前选项说明",Toast.LENGTH_SHORT).show();
                            }else{
                                curSelect = position;
                                notifyDataSetChanged();
                                showAnimBg(viewHolder.tvAnswerbg,entity);
                            }
                        }else{
                            curSelect = position;
                            notifyDataSetChanged();
                            showAnimBg(viewHolder.tvAnswerbg,entity);
                        }

                    }

                }
            });

            return convertView;
        }
    };

    class ViewHolder{
        private ImageView ivIcon;
        private TextView tvAnswerTitle;
        private TextView tvAnswerbg;
        private LinearLayout llFill;
        private RelativeLayout rtSelect;
        private TextView tvOption;
        private EditText etOption;
        void initView(View view){
            ivIcon = (ImageView)view.findViewById(R.id.iv_icon);
            tvAnswerTitle = (TextView)view.findViewById(R.id.tv_answer_title);
            tvAnswerbg = (TextView) view.findViewById(R.id.tv_answer_bg);
            rtSelect = (RelativeLayout)view.findViewById(R.id.rt_select);
            llFill = (LinearLayout)view.findViewById(R.id.ll_fill);
            tvOption = (TextView)view.findViewById(R.id.tv_option);
            etOption = (EditText)view.findViewById(R.id.et_option);
        }
    }

    public boolean isHasCommit() {
        return hasCommit;
    }

    public void setHasCommit(boolean hasCommit) {
        this.hasCommit = hasCommit;
    }

    private void showAnimBg(TextView tv, final OptionsEntity optionsEntity){
        tv.setVisibility(View.VISIBLE);
        Animation showAnim = AnimationUtils.loadAnimation(
                getActivity(), R.anim.anim_scale_center);
        showAnim.setDuration(200);
        tv.startAnimation(showAnim);

        showAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                clickPool.play(1,1, 1, 0, 0, 1);
                SoundPlayUtils.play(3);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                clickPool.play(2,1, 1, 0, 0, 1);
                SoundPlayUtils.play(4);
                commitQestion(optionsEntity);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    public void commitQestion(OptionsEntity optionsEntity){
        hasCommit = true;
        String optionText = (etAnswer==null ? "" : etAnswer.getText().toString());
        QsEvaluateQuestionActivity activity = (QsEvaluateQuestionActivity)getActivity();
        int costedTime = activity.getConstTimeCount();
        DataRequestService.getInstance().commitQuestionSingle(ChildInfoDao.getDefaultChildId(), optionsEntity.getQuestionId(),optionsEntity.getOptionId(),childFactorId,optionText,costedTime,new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                Toast.makeText(getActivity(),"提交失败",Toast.LENGTH_SHORT).show();
                hasCommit = false;
            }

            @Override
            public void onResponse(Object obj) {
                hasCommit = false;
                if(obj!=null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(),Map.class);
                    QuestionInfoChildEntity entity = InjectionWrapperUtil.injectMap(dataMap,QuestionInfoChildEntity.class);
                    if(entity!=null){
                        Activity activity = getActivity();
                        if(activity instanceof QsEvaluateQuestionActivity){
                            QsEvaluateQuestionActivity evAct = (QsEvaluateQuestionActivity) activity;
                            evAct.setCurFlowers(entity.getFlowers());
//                            evAct.updateFlowers();
                            int pageIndex = evAct.getCurPageIndex();
                            evAct.updateProgress();
                            if(pageIndex == evAct.getFragments().size()-1){
                                if(evAct.getTimeCount()>0){
//                                    evAct.showSureCommit();
                                }else{
//                                    if(evAct.isHasCommitingFactor()){
//                                        return;
//                                    }
//                                    evAct.setHasCommitingFactor(true);
                                    evAct.commitChildFactor(evAct.getConstTimeCount(),false);
                                }
                            }else{
                                if(questionInfoEntity.getType()==2){

                                }else {
                                    //自动下一题
                                    int nextPage = evAct.getCurPageIndex() + 1;
                                    evAct.setCurPageIndex(nextPage);
                                    evAct.getVpQuestion().setCurrentItem(nextPage, true);
                                }

                            }
                        }
                    }
                }

            }
        });
    }

}
