package com.cheersmind.cheersgenie.main.fragment.questype;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.activity.QsEvaluateQuestionActivity;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
import com.cheersmind.cheersgenie.main.entity.OptionsEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionInfoChildEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/31 0031.
 */

public class QuestionTypeBaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化答题选项
     * @param curSelect
     * @param optionsList
     * @param tvList
     * @param llOption
     * @param tvClick
     */
    protected void initOption(int curSelect,List<OptionsEntity> optionsList,List<TextView> tvList,LinearLayout llOption,OnMultiClickListener tvClick){
        if(optionsList==null){
            return;
        }
        int optionSize = optionsList.size();
        if(optionSize<=0){
            return;
        }
        tvList.clear();
        List<LinearLayout> layouts = new ArrayList<>();
        int row = optionSize/3;
        if(optionSize%3>0){
            row = row + 1;
        }

        int [] des = DensityUtil.getWindowHW();
        int screenWidth = des[0];
        int btnWidth = (screenWidth - 60)/3;
        for(int i=0;i<row;i++){
            LinearLayout layout = new LinearLayout(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth,
                    100);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setGravity(Gravity.CENTER);
            params.topMargin = 20;
            llOption.addView(layout,params);
            layouts.add(layout);
        }
        for(int i=0;i<optionSize;i++){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(btnWidth,
                    100);
            params.leftMargin = 10;
            TextView tv = new TextView(getActivity());
            tv.setText(optionsList.get(i).getContent());
            tv.setTextSize(14);
            tv.setTextColor(getActivity().getResources().getColor(R.color.color_text_white));
            tv.setTag(i+1);
            if(curSelect == i){
                tv.setBackgroundResource(R.mipmap.option_bg_select);
            }else{
                tv.setBackgroundResource(R.mipmap.option_bg_nor);
            }
            tv.setGravity(Gravity.CENTER);
            tv.setOnClickListener(tvClick);
            tv.setLines(1);
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tvList.add(tv);

            int curRow = i/3;
            layouts.get(curRow).addView(tv,params);

        }
    }

    protected void commitQuestion(final Context context, OptionsEntity optionsEntity, String childFactorId){
        String optionText = "";
        QsEvaluateQuestionActivity activity = (QsEvaluateQuestionActivity)getActivity();
        int costedTime = activity.getConstTimeCount();
        DataRequestService.getInstance().commitQuestionSingle(ChildInfoDao.getDefaultChildId(), optionsEntity.getQuestionId(),optionsEntity.getOptionId(),childFactorId,optionText,costedTime,new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                ToastUtil.showShort(context,"提交失败");
            }

            @Override
            public void onResponse(Object obj) {
                if(obj!=null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(),Map.class);
                    QuestionInfoChildEntity entity = InjectionWrapperUtil.injectMap(dataMap,QuestionInfoChildEntity.class);
                    if(entity!=null){
                        Activity activity = (Activity)context;
                        if(activity instanceof QsEvaluateQuestionActivity){
                            QsEvaluateQuestionActivity evAct = (QsEvaluateQuestionActivity) activity;
                            evAct.setCurFlowers(entity.getFlowers());
                            int pageIndex = evAct.getCurPageIndex();
                            evAct.updateProgress();
                            if(pageIndex == evAct.getFragments().size()-1){
                                if(evAct.getTimeCount()>0){
//                                    evAct.showQuestionCompleteDialog();
                                }else{
//                                    evAct.commitChildFactor(evAct.getConstTimeCount(),false);
                                }
                            }else{
//                                if(questionInfoEntity.getType()==2){
//
//                                }else {
//                                    //自动下一题
//                                    int nextPage = evAct.getCurPageIndex() + 1;
//                                    evAct.setCurPageIndex(nextPage);
//                                    evAct.getVpQuestion().setCurrentItem(nextPage, true);
//                                }

                            }
                        }
                    }
                }

            }
        });
    }
}
