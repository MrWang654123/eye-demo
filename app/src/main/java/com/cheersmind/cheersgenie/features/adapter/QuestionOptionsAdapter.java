package com.cheersmind.cheersgenie.features.adapter;

import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.utils.SoftInputUtil;
import com.cheersmind.cheersgenie.main.entity.OptionsEntity;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import pl.droidsonroids.gif.GifTextView;

/**
 * 问题选项recycler适配器
 */
public class QuestionOptionsAdapter extends BaseQuickAdapter<OptionsEntity, BaseViewHolder> {

    private int curSelect = -1;
    private String optionText;

    public QuestionOptionsAdapter(int layoutResId, @Nullable List<OptionsEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OptionsEntity item) {
        int layoutPosition = helper.getLayoutPosition();
        int headCount = getHeaderLayoutCount();
        int position = layoutPosition - headCount;

        final OptionsEntity entity = item;

        //隐藏填写答案文本
        helper.getView(R.id.tv_option_text).setVisibility(View.GONE);

        //选项标题
        helper.setText(R.id.tv_option_title, entity.getContent());
        //选中索引
        if (curSelect == position) {
            helper.getView(R.id.iv_icon).setVisibility(View.VISIBLE);
            //问题类型：手填
            if (entity.getType()== Dictionary.QUESTION_TYPE_EDIT) {
                //显示填写答案文本
                helper.getView(R.id.tv_option_text).setVisibility(View.VISIBLE);
                //设置填写的答案
                helper.setText(R.id.tv_option_text, optionText);
                //选中的填写答案
                ((TextView)helper.getView(R.id.tv_option_text)).setTextColor(Color.parseColor("#444444"));
            }

        } else {
            helper.getView(R.id.iv_icon).setVisibility(View.INVISIBLE);
            //问题类型：手填
            if (entity.getType()== Dictionary.QUESTION_TYPE_EDIT) {
                //选中的填写答案
                ((TextView)helper.getView(R.id.tv_option_text)).setTextColor(Color.parseColor("#898989"));
            }
        }

//        //选项点击监听
//        viewHolder.llOption.setOnClickListener(new OnMultiClickListener() {
//
//            @Override
//            public void onMultiClick(View view) {
//                int preSelect = curSelect;
//                curSelect = position;
//                //问题类型：只选
//                if (entity.getType()== Dictionary.QUESTION_TYPE_SELECT_ONLY) {
//                    notifyDataSetChanged();
////                        SoundPlayUtils.play(getContext(),3);
//                    clickQuestionOption();
//                    //处理答题数据，并跳转到下一题
//                    saveReplyInfo(questionInfoEntity.getChildFactorId(), entity, "");
////                        hiddenSoft();
//                    SoftInputUtil.closeSoftInput(getActivity());
//
//                } else if (entity.getType()== Dictionary.QUESTION_TYPE_EDIT) {//问题类型：填写
//                    //弹出填写答案的对话框
//                    String initContent = preSelect == curSelect ? optionText : "";
//                    popupAnswerEditWindows(optionsList.get(curSelect).getContent(), initContent);
//                }
//            }
//
//        });

        //语音播放提示
        if (entity.isVoicePlayingTipShow()) {
            SimpleDraweeView gftVoicePlay = helper.getView(R.id.gif_voice_play);
            gftVoicePlay.setVisibility(View.VISIBLE);
            //设置Controller
            DraweeController controller = gftVoicePlay.getController();
            if (controller == null) {
                Uri uri = new Uri.Builder()
                        .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                        .path(String.valueOf(R.drawable.voice_playing))
                        .build();
                controller = Fresco.newDraweeControllerBuilder()
                        //设置uri,加载本地的gif资源
//                            .setUri(Uri.parse("res://"+getPackageName()+"/"+R.drawable.voice_playing))//设置uri
                        .setUri(uri)
                        .setAutoPlayAnimations(true)
                        .build();
                gftVoicePlay.setController(controller);
            }
        } else {
            helper.getView(R.id.gif_voice_play).setVisibility(View.INVISIBLE);
        }


        //布局点击监听
        helper.addOnClickListener(R.id.ll_option);
    }

    public int getCurSelect() {
        return curSelect;
    }

    public void setCurSelect(int curSelect) {
        this.curSelect = curSelect;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

}
