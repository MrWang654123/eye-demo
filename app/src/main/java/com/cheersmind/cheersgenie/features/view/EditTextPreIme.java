package com.cheersmind.cheersgenie.features.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.cheersmind.cheersgenie.features.interfaces.OnBackPressListener;

/**
 * 监听按键的EditText
 */
public class EditTextPreIme extends android.support.v7.widget.AppCompatEditText {

    public EditTextPreIme(Context context) {
        super(context);
    }

    public EditTextPreIme(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextPreIme(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode && KeyEvent.ACTION_DOWN == event.getAction()) {
            if (backPressListener != null) {
                backPressListener.onBackPress();
            }
        }

        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && KeyEvent.ACTION_DOWN == event.getAction()) {
            //when the softinput display
            //处理事件
        }

        return super.dispatchKeyEventPreIme(event);
    }

    OnBackPressListener backPressListener;

    /**
     * 返回键监听
     * @param backPressListener
     */
    public void setBackPressListener(OnBackPressListener backPressListener) {
        this.backPressListener = backPressListener;
    }

}

