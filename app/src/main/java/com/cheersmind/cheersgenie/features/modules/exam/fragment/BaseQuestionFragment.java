package com.cheersmind.cheersgenie.features.modules.exam.fragment;

import android.support.v4.app.Fragment;
import android.util.Pair;

import com.cheersmind.cheersgenie.main.entity.OptionsEntity;
import com.cheersmind.cheersgenie.main.entity.QuestionInfoEntity;

import java.util.List;

/**
 * 默认的问题Fragment
 */
public class BaseQuestionFragment extends Fragment {

    //问题对象
    QuestionInfoEntity questionInfoEntity;
    //选项集合
    List<OptionsEntity> optionsList;
    //播放的文字集合Pair<String, String>：1、文字；2、ID
    List<Pair<String, String>> texts;


    /**
     * 清空数据
     */
    public void clearData() {
        try {
            //数据释放
            questionInfoEntity = null;
            if (optionsList != null) {
                optionsList.clear();
                optionsList = null;
            }
            if (texts != null) {
                texts.clear();
                texts = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
