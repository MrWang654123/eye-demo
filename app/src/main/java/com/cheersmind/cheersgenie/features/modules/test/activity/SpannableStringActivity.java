package com.cheersmind.cheersgenie.features.modules.test.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * SpannableString测试（文本最后一个字后面跟着图标按钮）
 */
public class SpannableStringActivity extends Activity {

    @BindView(R.id.tv_content)
    TextView tvContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spannable_string);
        ButterKnife.bind(this);

        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.question_title);
//        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.btn_voice_play);
//        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.voice_play_normal);
        ImageSpan imgSpan = new ImageSpan(SpannableStringActivity.this,bitmap);
        SpannableString spanString = new SpannableString("icon");
        spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        vh.outline1.append(spanString);
//        vh.outline2.append(spanString);

        String s = "这是一个测试测试测试测试测试测试测试测试测试测试测试测试" +
                "这是一个测试测试测试测试测试测试测试测试测试测试测试测试" +
                " 按钮";
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(s);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ToastUtil.showShort(getApplication(), "点击了");
            }
        }, s.length() - 2, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //得到drawable对象，即所要插入的图片
//        Drawable d = getResources().getDrawable(R.drawable.btn_voice_play);
        StateListDrawable d = (StateListDrawable) getResources().getDrawable(R.drawable.btn_voice_play);
        d.setBounds(0, 0,
                DensityUtil.dip2px(SpannableStringActivity.this, 25),
                DensityUtil.dip2px(SpannableStringActivity.this, 25));
        //用这个drawable对象代替字符串easy
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);

        SpannableString spanString2 = new SpannableString(s);
        spanString2.setSpan(imgSpan, s.length() - 2, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString2.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ToastUtil.showShort(getApplication(), "点击了");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
//                super.updateDrawState(ds);
                System.out.println("--");
            }
        }, s.length() - 2, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvContent.setText(spanString2);
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
